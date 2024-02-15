package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CadAlunoActivity extends AppCompatActivity {
    ImageButton voltar;
    EditText nomeCad, emailCad, idAluno;
    Spinner spinnerTurma;
    Button cadastrar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_aluno);


        nomeCad = findViewById(R.id.editTextNomeCad);
        emailCad = findViewById(R.id.editTextEmailCad);
        idAluno = findViewById(R.id.editTextNumberIdAluno);
        cadastrar = findViewById(R.id.buttonCadastrar);
        voltar = findViewById(R.id.btn_voltar_left);


        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaVisulAluno();
            }
        });

    }
    public void mudarParaTelaFormCadAluno(){
        Intent intent = new Intent(this, CadAlunoActivity.class);
        startActivity(intent);
    }
    public void mudarParaTelaVisulAluno(){
        Intent intent = new Intent(this, VisualAlunoActivity.class);
        startActivity(intent);
        List<String> turmas = new ArrayList<>();
        turmas.add("Turma");
        turmas.add("Turma 1");
        turmas.add("Turma 2");
        turmas.add("Turma 3");

        Spinner spinnerTurma = findViewById(R.id.spinner_turma);
        // Criação do ArrayAdapter e configuração do Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, turmas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurma.setAdapter(adapter);
        spinnerTurma.setSelection(0);
    }
}