package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class DetalhesEscolaActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    Button btnTurma, btnAluno, btnProva;
    TextView nomeEscola;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_escola);

        btnVoltar = findViewById(R.id.btn_voltar_left);
        btnVoltar.setVisibility(View.VISIBLE);

        btnTurma = findViewById(R.id.btnTurma);
        btnAluno = findViewById(R.id.buttonAluno);
        btnProva = findViewById(R.id.btnProva);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnTurma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irTelaTurma();
            }
        });
        btnAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irTelaAluno();
            }
        });
        btnProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irTelaProva();
            }
        });
    }
    public void irTelaTurma(){
        Intent intent = new Intent(this, TurmaActivity.class);
        startActivity(intent);
    }
    public void irTelaAluno(){
        Intent intent = new Intent(this, AlunoActivity.class);
        startActivity(intent);
    }
    public void irTelaProva(){
        Intent intent = new Intent(this, ProvaActivity.class);
        startActivity(intent);
    }

}