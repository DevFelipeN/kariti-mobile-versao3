package com.example.kariti;
import java.util.Random;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText username, email, password;

    Button entrar, esqueciSenha;
    ImageButton ocultarSenha;

    BancoDados bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        entrar = findViewById(R.id.buttonEntrarL);
        esqueciSenha = findViewById(R.id.buttonEsqueciSenhaL);

        email = findViewById(R.id.editTextLogin);
        password = findViewById(R.id.editTextSenha);
        bancoDados = new BancoDados(this);


        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailConf = email.getText().toString();
                String pass = password.getText().toString();
                if(emailConf.equals("")||pass.equals(""))
                    //Toast.makeText(LoginActivity.this, "Por favor preencher todos os campos", Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, "Code: " + LoginActivity.gerarVerificador(), Toast.LENGTH_SHORT).show();
                else{
                    Boolean checkemail = bancoDados.checkemail(emailConf); //Verificando no banco se existe email
                    if(checkemail==true){
                        Boolean checkemailpass = bancoDados.checkemailpass(emailConf, pass);
                        if(checkemailpass==true) {
                            Toast.makeText(LoginActivity.this, "Bem Vindo ao Kariti", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Usuário e/ou senha inválidos! ", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(LoginActivity.this, "Usuário e/ou senha inválidos!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        esqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mudarTelaSenhaEsquecida();
            }
        });

        ocultarSenha = findViewById(R.id.senhaoculta);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ocultarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Verifica se a senha está visivel ou oculta.
                if(password.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
//                  Se a senha está visivel ou oculta.
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ocultarSenha.setImageResource(R.mipmap.senhaoff);
                } else {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ocultarSenha.setImageResource(R.mipmap.senhaon);
                }
                password.setSelection(password.getText().length());
            }
        });
    }
    //Funções de mudança de Tela
    /*public void mudarTelaInicio(){
        Intent intent = new Intent(this, InicioActivity.class);
        startActivity(intent);
    }*/
    public void mudarTelaSenhaEsquecida(){
        Intent intent = new Intent(this, CodSenhaActivity.class);
        startActivity(intent);
    }

    public static String gerarVerificador(){
        Random r = new Random();
        String saida = "";
        for(int i = 0; i < 4; i++) {
            saida += "" + r.nextInt(10);
        }
        return saida;
    }
}