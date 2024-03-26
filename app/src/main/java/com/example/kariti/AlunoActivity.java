package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class AlunoActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    Button btnCadAluno, btnVisualizarAluno;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aluno);

        toolbar = findViewById(R.id.myToolBarMenu);
        setSupportActionBar(toolbar);

        btnVoltar = findViewById(R.id.imgBtnVoltaEscola);
        btnCadAluno = findViewById(R.id.buttonCadAluno);
        btnVisualizarAluno = findViewById(R.id.buttonVisuTurma);


        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        btnCadAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaCadastroAluno();
            }
        });
        btnVisualizarAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaVisualizarAluno();
            }
        });

    }
    public void mudarParaTelaCadastroAluno(){
        Intent intent = new Intent(this, CadAlunoActivity.class);
        startActivity(intent);
    }
    public void mudarParaTelaVisualizarAluno(){
        Intent intent = new Intent(this, VisualAlunoActivity.class);
        startActivity(intent);
    }
}