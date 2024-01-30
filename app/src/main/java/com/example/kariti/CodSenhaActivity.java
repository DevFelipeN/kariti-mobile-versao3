package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CodSenhaActivity extends AppCompatActivity {
    Button buttonValidarSenha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cod_senha);

        buttonValidarSenha = findViewById(R.id.buttonValidarSenhaw);
        buttonValidarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaLogin();
            }

        });
    }
    public void mudarParaTelaLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}