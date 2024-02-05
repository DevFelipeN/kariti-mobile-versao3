package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class CadAlunoActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    Button btnCadAluno;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_aluno);

        toolbar = findViewById(R.id.myToolBarMenu);
        setSupportActionBar(toolbar);

        btnVoltar = findViewById(R.id.btn_voltar_left);
        btnCadAluno = findViewById(R.id.buttonCadAluno);


        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnCadAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaCadastroAluno();
            }
        });

    }
    public void mudarParaTelaCadastroAluno(){
        Intent intent = new Intent(this, FormCadAlunoActivity.class);
        startActivity(intent);
    }
}