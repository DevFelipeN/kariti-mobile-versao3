package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;

import android.widget.ImageButton;
public class CadEscolaActivity extends AppCompatActivity {
    ImageButton btnVoltar, btnHome;
    private Toolbar toolbar;
    EditText nomeEscola, bairr0;
    Button cadastrarEscola;
    BancoDados bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_escola);

        nomeEscola = findViewById(R.id.editTextNomeEscola);
        bairr0 = findViewById(R.id.editTextBairro);
        cadastrarEscola = findViewById(R.id.button);

        bancoDados = new BancoDados(this); // --> Conexão com o banco de dados.
        cadastrarEscola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = nomeEscola.getText().toString();
                String bairro = bairr0.getText().toString();

                if (nome.equals("") || bairro.equals("")) {
                    Toast.makeText(CadEscolaActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean checkEscola = bancoDados.checkEscola(nome);
                    if (!checkEscola) {
                        Boolean insertEscola = bancoDados.inserirDadosEscola(nome, bairro);
                        if (insertEscola) {
                            Toast.makeText(CadEscolaActivity.this, "Escola cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(CadEscolaActivity.this, "Falha no cadastro da escola!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CadEscolaActivity.this, "Escola já cadastrada!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnVoltar = findViewById(R.id.btn_voltar_left);
        btnVoltar.setVisibility(View.VISIBLE);

        btnHome = findViewById(R.id.home_icon);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voltarTelaIncial();
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    public void voltarTelaIncial(){
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);

        nomeEscola = findViewById(R.id.editTextNomeEscola);
        bairr0 = findViewById(R.id.editTextBairro);
        cadastrarEscola = findViewById(R.id.buttonAlterar);

        cadastrarEscola.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            }
        });
    }
}