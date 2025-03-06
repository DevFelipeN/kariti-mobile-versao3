package online.padev.kariti;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.emails.EnviarCodigo;
import online.padev.kariti.utils.GenerateCodeValidation;
import online.padev.kariti.utils.CheckConnectionInternet;

public class LoginActivity extends AppCompatActivity {

    EditText EditTextEmail, EditTextPassword;
    private String emailInformed, passwordInformed, codeValidation;
    private Integer id_user;
    TextView registerNewConta;
    Button btnAccess, forgetPassword;
    ImageButton btnHidePassword;
    DataBaseKariti dataBase;
    CodeValidationActivity validationCodeActivity;
    EnviarCodigo sendCode;
    GenerateCodeValidation generateCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnAccess = findViewById(R.id.buttonEntrarL);
        forgetPassword = findViewById(R.id.buttonEsqueciSenhaL);
        registerNewConta = findViewById(R.id.textViewCriarConta);
        EditTextEmail = findViewById(R.id.editTextLogin);
        EditTextPassword = findViewById(R.id.editTextSenha);
        btnHidePassword = findViewById(R.id.senhaoculta);

        dataBase = new DataBaseKariti(this);
        sendCode = new EnviarCodigo();
        generateCode = new GenerateCodeValidation();
        validationCodeActivity = new CodeValidationActivity();

        btnAccess.setOnClickListener(v -> {
            btnAccess.setEnabled(false);
            try {
                emailInformed = EditTextEmail.getText().toString().trim();
                passwordInformed = EditTextPassword.getText().toString().trim();
                if (emailInformed.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Informe seu e-mail!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passwordInformed.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Informe a senha!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emailInformed).matches()) {
                    Toast.makeText(this, "O e-mail informado é inválido!", Toast.LENGTH_SHORT).show();
                    return;
                }
                id_user = dataBase.verificaAutenticacao(emailInformed, passwordInformed);
                if (id_user == null || id_user == -1) {
                    Toast.makeText(this, "Usuário e/ou senha inválidos! ", Toast.LENGTH_SHORT).show();
                    return;
                }
                DataBaseKariti.USER_ID = id_user;
                startSchools();
            } catch (Exception e){
                Log.e("kariti", e.toString());
            } finally {
                btnAccess.setEnabled(true);
            }
        });
        forgetPassword.setOnClickListener(v -> {
            forgetPassword.setEnabled(false);
            try {
                if (!CheckConnectionInternet.verificaConexao(this)) {
                    Toast.makeText(this, "Sem conexão de internet!", Toast.LENGTH_SHORT).show();
                    return;
                }
                emailInformed = EditTextEmail.getText().toString().trim();
                if (emailInformed.isEmpty()) {
                    notifyInformedEmail();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(emailInformed).matches()) {
                    Toast.makeText(LoginActivity.this, "O e-mail informado é inválido!", Toast.LENGTH_SHORT).show();
                    return;
                }
                id_user = dataBase.verificaExisteEmail(emailInformed);
                if (id_user == null || id_user == -1) {
                    Toast.makeText(this, "E-mail não encontrado!", Toast.LENGTH_SHORT).show();
                    return;
                }
                codeValidation = generateCode.gerarVerificador();
                if (sendCode.enviaCodigo(emailInformed, codeValidation)) {
                    startCodeValidationActivity();
                } else {
                    Toast.makeText(this, "Email não Enviado!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e){
                Log.e("kariti", e.toString());
            } finally {
                forgetPassword.setEnabled(true);
            }
        });

        EditTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btnHidePassword.setOnClickListener(v -> {
//                Verifica se a senha está visivel ou oculta.
            if(EditTextPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
//                  Se a senha está visivel ou oculta.
                EditTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnHidePassword.setImageResource(R.mipmap.senhaoff);
            } else {
                EditTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                btnHidePassword.setImageResource(R.mipmap.senhaon);
            }
            EditTextPassword.setSelection(EditTextPassword.getText().length());
        });

        registerNewConta.setOnClickListener(v -> startNewUserRegistration());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
    private void startSchools(){
        Intent intent = new Intent(this, SchoolActivity.class);
        startActivity(intent);
        finish();
    }
    private void startNewUserRegistration(){
        Intent intent = new Intent(this, UserRegistrationActivity.class);
        startActivity(intent);
        finish();
    }
    private void notifyInformedEmail(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Esqueceu sua senha?")
                .setMessage("Por favor, informe seu e-mail cadastrado no campo E-mail, em seguida pressione 'Esqueci Minha Senha'")
                .setPositiveButton("Ok", (dialog, which) -> Toast.makeText(LoginActivity.this, "Informe o E-mail! ", Toast.LENGTH_SHORT).show());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /**
     * Este método inicializa a tela de verificação de código, para autenticação de cadastro do usuário.
     */
    private void startCodeValidationActivity(){
        Intent proxima = new Intent(getApplicationContext(), CodeValidationActivity.class);
        proxima.putExtra("identificador", 1);
        proxima.putExtra("id_usuario", id_user);
        proxima.putExtra("email", emailInformed);
        proxima.putExtra("codigo", codeValidation);
        startActivity(proxima);
        finish();
    }
}