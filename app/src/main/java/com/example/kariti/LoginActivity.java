package com.example.kariti;
import java.util.Random;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;

    Button entrar, esqueciSenha;

    BancoDados bancoDados;

    EnviarEmail enviarEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        entrar = findViewById(R.id.buttonEntrarL);
        esqueciSenha = findViewById(R.id.buttonEsqueciSenhaL);

        email = findViewById(R.id.editTextLogin);
        password = findViewById(R.id.editTextSenha);
        bancoDados = new BancoDados(this);

        enviarEmail = new EnviarEmail();


        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailConf = email.getText().toString();
                String pass = password.getText().toString();
                if(emailConf.equals("")||pass.equals(""))
                    Toast.makeText(LoginActivity.this, "Por favor, preencher todos os campos ", Toast.LENGTH_SHORT).show();
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
                String confEmail = email.getText().toString();
                if(confEmail.equals(""))
                    Toast.makeText(LoginActivity.this, "Favor Informar Email", Toast.LENGTH_SHORT).show();
                else{
                    Boolean verBanco = bancoDados.checkemail(confEmail);
                    if(verBanco==true){
                        String cod = gerarVerificador();
                        Boolean mandaEmail = enviarEmail.enviaCodigo(confEmail, cod);
                        if(mandaEmail==true)
                            Toast.makeText(LoginActivity.this, "Em Desenvolvimento!!!", Toast.LENGTH_SHORT).show();
                            Intent intencion = new Intent(getApplicationContext(), CodSenhaActivity.class);
                            startActivity(intencion);
                    }

                }
            }
        });
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