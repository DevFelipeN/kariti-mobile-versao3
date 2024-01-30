package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {
    Button entrar, esqueciSenha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        entrar = findViewById(R.id.buttonEntrarL);
        esqueciSenha = findViewById(R.id.buttonEsqueciSenhaL);

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarTelaInicio();
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