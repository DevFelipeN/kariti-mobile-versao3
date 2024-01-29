package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button entrar, esqueciSenha;
    BancoDados bancoDados;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        entrar = findViewById(R.id.buttonEntrarL);
        esqueciSenha = findViewById(R.id.buttonEsqueciSenhaL);
        username = findViewById(R.id.editTextLogin);
        password = findViewById(R.id.editTextSenha);
        bancoDados = new BancoDados(this);

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                if(user.equals("")||pass.equals(""))
                    Toast.makeText(LoginActivity.this, "Por favor preencher todos os campos", Toast.LENGTH_SHORT).show();
                else{
                    Boolean checkuser = bancoDados.checkuser(user); //Verificando no banco de Dados se Nome e senha existem
                    if(checkuser==true){
                        Boolean checkuserpass = bancoDados.checkuserpassword(user, pass);
                        if(checkuserpass==true) {
                            Toast.makeText(LoginActivity.this, "Login Realizado com Sucesso! ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(new Intent(getApplicationContext(), InicioActivity.class));
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Senha Inválida! ", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(LoginActivity.this, "Usuário não existe!", Toast.LENGTH_SHORT).show();
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
    }
    public void mudarTelaInicio(){
        Intent intent = new Intent(this, InicioActivity.class);
        startActivity(intent);
    }
    public void mudarTelaSenhaEsquecida(){
        Intent intent = new Intent(this, CodSenhaActivity.class);
        startActivity(intent);
    }
}