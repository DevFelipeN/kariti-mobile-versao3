package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class CadAlunoActivity extends AppCompatActivity {
    ImageButton voltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_aluno);

        voltar = findViewById(R.id.btn_voltar_left);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Spinner spinnerTurma = findViewById(R.id.spinner_turma);

        btnCadAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaFormCadAluno();
            }
        });
        btnVisualizarAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaVisulaizarAluno();
            }
        });

    }
    public void mudarParaTelaFormCadAluno(){
        Intent intent = new Intent(this, FormCadAlunoActivity.class);
        startActivity(intent);
    }
    public void mudarParaTelaVisulaizarAluno(){
        Intent intent = new Intent(this, VisualAlunoActivity.class);
        startActivity(intent);
        List<String> turmas = new ArrayList<>();
        turmas.add("Turma");
        turmas.add("Turma 1");
        turmas.add("Turma 2");
        turmas.add("Turma 3");

        // Criação do ArrayAdapter e configuração do Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, turmas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurma.setAdapter(adapter);
        spinnerTurma.setSelection(0);
    }
}