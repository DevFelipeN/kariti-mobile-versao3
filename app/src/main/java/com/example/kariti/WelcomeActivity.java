package com.example.kariti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {
    Button botaoCadastro, botaoLogin, esqueciSenha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        botaoCadastro = findViewById(R.id.buttonCadastroW);
        botaoLogin = findViewById(R.id.buttonLoginW);
        esqueciSenha = findViewById(R.id.buttonEsqueciSenhaW);

        botaoCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaWelcome();
            }
        });
        botaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaLogin();
            }
        });
        esqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaEsquciSenha();
            }
        });
    }

    public void mudarParaTelaWelcome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void mudarParaTelaLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void mudarParaTelaEsquciSenha(){
        Intent intent = new Intent(this, CodSenhaActivity.class);
        startActivity(intent);
    }
}
