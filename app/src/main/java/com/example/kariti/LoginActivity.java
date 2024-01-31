package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {
<<<<<<< HEAD
=======

    EditText username, email, password;
>>>>>>> e9a3947cb742eadcbe75f010dac36271213b0e2d
    Button entrar, esqueciSenha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        entrar = findViewById(R.id.buttonEntrarL);
        esqueciSenha = findViewById(R.id.buttonEsqueciSenhaL);
<<<<<<< HEAD
=======
        email = findViewById(R.id.editTextLogin);
        password = findViewById(R.id.editTextSenha);
        bancoDados = new BancoDados(this);
>>>>>>> e9a3947cb742eadcbe75f010dac36271213b0e2d

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
<<<<<<< HEAD
                mudarTelaInicio();
=======
                String emailConf = email.getText().toString();
                String pass = password.getText().toString();
                if(emailConf.equals("")||pass.equals(""))
                    Toast.makeText(LoginActivity.this, "Por favor preencher todos os campos", Toast.LENGTH_SHORT).show();
                else{
                    Boolean checkemail = bancoDados.checkemail(emailConf); //Verificando no banco se existe email
                    if(checkemail==true){
                        Boolean checkemailpass = bancoDados.checkemailpass(emailConf, pass);
                        if(checkemailpass==true) {
                            Toast.makeText(LoginActivity.this, "Bem Vindo ao Kariti", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(new Intent(getApplicationContext(), InicioActivity.class));
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Senha Inválida! ", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(LoginActivity.this, "Usuário não existe!", Toast.LENGTH_SHORT).show();
                    }
                }
>>>>>>> e9a3947cb742eadcbe75f010dac36271213b0e2d
            }
        });
        esqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarTelaSenhaEsquecida();
            }
        });
    }
    //Funções de mudança de Tela
    public void mudarTelaInicio(){
        Intent intent = new Intent(this, InicioActivity.class);
        startActivity(intent);
    }
    public void mudarTelaSenhaEsquecida(){
        Intent intent = new Intent(this, CodSenhaActivity.class);
        startActivity(intent);
    }
}