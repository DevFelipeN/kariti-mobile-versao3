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
    EditText nomeAluno, emailAluno, cpfAluno;
    Spinner spinnerTurma;
    Button cadastrar;
    BancoDados bancoDados;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_aluno);

        nomeAluno = findViewById(R.id.editTextNomeCad);
        emailAluno = findViewById(R.id.editTextEmailCad);
        cpfAluno = findViewById(R.id.editTextNumberIdAluno);
        voltar = findViewById(R.id.btn_voltar_left);
        cadastrar = findViewById(R.id.buttonCadastrar);

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        bancoDados = new BancoDados(this);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = nomeAluno.getText().toString();
                String email = emailAluno.getText().toString();
                String cpf = cpfAluno.getText().toString();

                if (nome.equals("")|| email.equals("")||cpf.equals("")){
                    Toast.makeText(CadAlunoActivity.this, "Preecha todos os campos!", Toast.LENGTH_SHORT).show();
                }else {
                    Boolean checkAluno = bancoDados.checkAluno(nome);
                    if (!checkAluno) {
                        Boolean insertAluno = bancoDados.inserirDadosAluno(nome, email, cpf);
                        if (insertAluno) {
                            Toast.makeText(CadAlunoActivity.this, "Aluno cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), VisualAlunoActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(CadAlunoActivity.this, "Falha no cadastro do aluno!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                            Toast.makeText(CadAlunoActivity.this, "Escola já cadastrada!", Toast.LENGTH_SHORT).show();
                        }
                    }
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