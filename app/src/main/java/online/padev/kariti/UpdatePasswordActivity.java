package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import online.padev.kariti.database.DataBaseKariti;

public class UpdatePasswordActivity extends AppCompatActivity{
    private Integer id_user;
    String nameUser, email, newPassword, confirmedNewPassword;
    EditText editTextNewPassword, editTextConfirmedNewPassword;
    TextView textViewDescription, titleActivity;
    Button btnUpdate;
    DataBaseKariti dataBase;
    ImageButton hidePassword, hidePassword2, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        textViewDescription = findViewById(R.id.descricaoNovaSenha);
        editTextNewPassword = findViewById(R.id.editTextNovaAttSenha);
        editTextConfirmedNewPassword = findViewById(R.id.editTextConfirmAttSenha);
        btnUpdate = findViewById(R.id.buttonAttSenha);
        back = findViewById(R.id.imgBtnVoltar);
        hidePassword2 = findViewById(R.id.imgButtonSenhaOFF);
        hidePassword = findViewById(R.id.senhaoculta);
        titleActivity = findViewById(R.id.toolbar_title);

        dataBase = new DataBaseKariti(this);

        id_user = getIntent().getExtras().getInt("id_usuario");
        nameUser = getIntent().getExtras().getString("nome");
        email = getIntent().getExtras().getString("email");

        titleActivity.setText(String.format("%s","Nova senha"));
        textViewDescription.setText(String.format("Olá, %s!\n Informe uma senha segura para acesso ao KARITI.", nameUser));

        btnUpdate.setOnClickListener(v -> {
            btnUpdate.setEnabled(false);
            try {
                newPassword = editTextNewPassword.getText().toString().trim();
                confirmedNewPassword = editTextConfirmedNewPassword.getText().toString().trim();
                if (newPassword.isEmpty() || confirmedNewPassword.isEmpty()) {
                    Toast.makeText(this, "Informe a senha nos dois campos!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newPassword.equals(confirmedNewPassword)) {
                    Toast.makeText(this, "Senhas divergentes!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dataBase.alterarSenha(newPassword, id_user)) {
                    startLoginActivity();
                } else {
                    Toast.makeText(this, "Erro de comunicação!\n\n Por favor, tente novamente!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e){
                Log.e("kariti", e.toString());
            } finally {
                btnUpdate.setEnabled(true);
            }
        });
        back.setOnClickListener(v -> finish());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        editTextNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        hidePassword.setOnClickListener(v -> {
//           Verifica se a senha está visivel ou oculta.
            if(editTextNewPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
//                  Se a senha está visivel ou oculta.
                editTextNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                hidePassword.setImageResource(R.mipmap.senhaoff);
            } else {
                editTextNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                hidePassword.setImageResource(R.mipmap.senhaon);
            }
            editTextNewPassword.setSelection(editTextNewPassword.getText().length());
        });

        editTextConfirmedNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        hidePassword2.setOnClickListener(v -> {
//          Verifica se a senha está visivel ou oculta.
            if(editTextConfirmedNewPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
//                  Se a senha está visivel ou oculta.
                editTextConfirmedNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                hidePassword2.setImageResource(R.mipmap.senhaoff);
            } else {
                editTextConfirmedNewPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                hidePassword2.setImageResource(R.mipmap.senhaon);
            }
            editTextConfirmedNewPassword.setSelection(editTextConfirmedNewPassword.getText().length());
        });
    }

    /**
     * Método usado para inicializar a tela de login.
     */
    private void startLoginActivity(){
        Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}