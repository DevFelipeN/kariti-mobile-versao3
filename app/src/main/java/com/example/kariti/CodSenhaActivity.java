package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CodSenhaActivity extends AppCompatActivity {
    Button buttonValidarSenha;
    BancoDados bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cod_senha);

        bancoDados = new BancoDados(this);

        buttonValidarSenha = findViewById(R.id.buttonValidarSenhaw);
        buttonValidarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernome = "Felipe";
                String password = "1234";
                String emails = "felipe@teste";
                Boolean insert = bancoDados.insertData(usernome, password, emails);
                if (insert == true) {
                    Toast.makeText(CodSenhaActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(CodSenhaActivity.this, "Erro: Usuário não Registrado! ", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}