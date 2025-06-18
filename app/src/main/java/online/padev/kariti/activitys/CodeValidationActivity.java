package online.padev.kariti.activitys;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import online.padev.kariti.R;
import online.padev.kariti.settings.ActivityLocale;
import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.emails.SendCodeValidation;
import online.padev.kariti.utils.GenerateCodeValidation;

public class CodeValidationActivity extends AppCompatActivity {
    private  EditText n1, n2, n3, n4;
    private TextView textViewTime, textViewResendCode;
    private String v1, v2, v3, v4, nameUser, password, email, code;
    private GenerateCodeValidation generateCode;
    private SendCodeValidation sendCode;
    private static final long WAITING_TIME = 60000;
    DataBaseKariti dataBase;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ActivityLocale.wrap(newBase));
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_validation);

        TextView textViewCancel = findViewById(R.id.textViewCancelar);
        Button btnValidationCode = findViewById(R.id.buttonValidarSenhaw);
        TextView msgValidation = findViewById(R.id.textViewMsgValidacao);
        textViewTime = findViewById(R.id.textViewTime);
        textViewResendCode = findViewById(R.id.textViewReenviar);

        dataBase = new DataBaseKariti(this);
        generateCode = new GenerateCodeValidation();
        sendCode = new SendCodeValidation();

        //pega os dados mandados por intent de outra activity
        int identifier = getIntent().getExtras().getInt("identificador");
        nameUser = getIntent().getExtras().getString("nome");
        password = getIntent().getExtras().getString("senha");
        email = getIntent().getExtras().getString("email");
        code = getIntent().getExtras().getString("codigo");

        String msg = getString(R.string.longTextEmailSentValidation, email);
        msgValidation.setText(msg);

        startTime(); // Inicia o tempo de 1 minuto

        n1 = findViewById(R.id.editTextNumber);
        n2 = findViewById(R.id.editTextNumber2);
        n3 = findViewById(R.id.editTextNumber3);
        n4 = findViewById(R.id.editTextNumber4);

        n1.requestFocus();

        addTextWatcher(n1, n2, null);
        addTextWatcher(n2, n3, n1);
        addTextWatcher(n3, n4, n2);
        addTextWatcher(n4, null, n3);

        textViewResendCode.setOnClickListener(v -> {
            code = generateCode.gerarVerificador();
            if (sendCode.enviaCodigo(email, code)){
                Toast.makeText(this, getString(R.string.toastEmailResentSuccess), Toast.LENGTH_SHORT).show();
                startTime();
            } else {
                Toast.makeText(this, getString(R.string.toastEmailNotSent), Toast.LENGTH_SHORT).show();
            }
        });

        textViewCancel.setOnClickListener(v -> finish());
        btnValidationCode.setOnClickListener(v -> {
            btnValidationCode.setEnabled(false);
            try {
                //ler o codigo digitado pelo usuário
                v1 = n1.getText().toString();
                v2 = n2.getText().toString();
                v3 = n3.getText().toString();
                v4 = n4.getText().toString();
                String codeInformed = v1 + v2 + v3 + v4;
                if (code.equals(codeInformed)) {
                    if (identifier == 0) {
                        Boolean insertUserBD = dataBase.insertUser(nameUser, password, email);
                        if (insertUserBD == null) {
                            Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (insertUserBD) {
                            startLoginActivity();
                        } else {
                            Toast.makeText(this, getString(R.string.toastRegistrationUserError), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        startUpdatePassword();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.toastInvalidCode), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e){
                Log.e("kariti", e.toString());
            } finally {
                btnValidationCode.setEnabled(true);
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
    private void addTextWatcher(final EditText currentEditText, final EditText nextEditText, final EditText previousEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1 && nextEditText != null) {
                    nextEditText.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0 && previousEditText != null) {
                    previousEditText.requestFocus();
                }
            }
        });
    }

    /**
     * Este método inicializa a tela de Login
     */
    private void startLoginActivity(){
        Toast.makeText(this, getString(R.string.toastRegisterUserSuccess), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Este método inicializa a tela de Atualização de senha
     */
    private void startUpdatePassword(){
        Integer id_user = getIntent().getExtras().getInt("id_usuario");
        String nameUserBD = dataBase.getUserName(id_user);
        Intent intent = new Intent(this, UpdatePasswordActivity.class);
        intent.putExtra("id_usuario", id_user);
        intent.putExtra("nome", nameUserBD);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }
    private void startTime() {
        new CountDownTimer(WAITING_TIME, 1000) {

            public void onTick(long millisUntilFinished) {
                long s = millisUntilFinished / 1000;
                textViewTime.setText(getString(R.string.toastTimeResentCode, s));
            }

            public void onFinish() {
                // Quando o tempo acabar, habilitar o botão para reenviar o código
                textViewTime.setText(String.format("%s",""));

                textViewResendCode.setEnabled(true); // Habilitar o botão
            }

        }.start();
    }
}