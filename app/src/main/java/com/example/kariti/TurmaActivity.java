package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class TurmaActivity extends AppCompatActivity {
    ImageButton voltar;
    Button cadTurma, visuTurma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turma);

        voltar = findViewById(R.id.imgBtnVoltar);
        cadTurma = findViewById(R.id.buttonCadAluno);
        visuTurma = findViewById(R.id.buttonVisuTurma);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cadTurma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaCadTurma();
            }
        });
        visuTurma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaVisualTurma();
            }
        });
    }
    public void telaCadTurma(){
        Intent intent = new Intent(this, CadTurmaActivity.class);
        startActivity(intent);
    }

    public void telaVisualTurma(){
        Intent intent = new Intent(this, VisualTurmaActivity.class);
        startActivity(intent);
    }
}