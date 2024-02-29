package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class CadTurmaActivity extends AppCompatActivity {
    ImageButton voltar;
    private Toolbar toolbar;
    EditText pesquisarAlunos, nomeTurma;
    Button buttonIncluirAlunos, cadastrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_turma);

        toolbar = findViewById(R.id.myToolBarMenu);
        setSupportActionBar(toolbar);

        voltar = findViewById(R.id.imgBtnVoltar);
//        ListView listView = findViewById(R.id.listView);
        pesquisarAlunos = findViewById(R.id.editTextPesquisarAlunos);
        buttonIncluirAlunos = findViewById(R.id.buttonIncluirAluno);
        nomeTurma = findViewById(R.id.editTextTurma);
        cadastrar = findViewById(R.id.buttonCadastrarTurma);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        buttonIncluirAlunos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {irParaVisualAluno();}
        });

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = nomeTurma.getText().toString();
                Button incluirAlunos = buttonIncluirAlunos;
            }
        });
    }
    public void irParaVisualAluno(){
        Intent intent = new Intent(this, VisualAlunoActivity.class);
        startActivity(intent);
    }
}