package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FormCadAlunoActivity extends AppCompatActivity {

    BancoDados bancoDados;
    EditText nomeAluno, emailAluno, cpfAluno;
    Button cadastrarAluno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cad_aluno);

        nomeAluno = findViewById(R.id.editTextNomeAluno);
        emailAluno = findViewById(R.id.editTextEmailAluno);
        cpfAluno = findViewById(R.id.editTextCpfAluno);
        bancoDados = new BancoDados(this);

        Spinner spinnerTurma = findViewById(R.id.spinner_turma);

        cadastrarAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = nomeAluno.getText().toString();
                String email = emailAluno.getText().toString();
                String cpf = cpfAluno.getText().toString();

                if (nome.equals("") || cpf.equals("")){
                    Toast.makeText(FormCadAlunoActivity.this, "Os campos nome e CPF são obrigatórios!", Toast.LENGTH_SHORT).show();
                }else {
                    Boolean checkAluno = bancoDados.checkAluno(cpf);

                    if (!checkAluno){
                        Boolean inserirAluno = bancoDados.inserirDadosAluno(nome, email, cpf);

                        if (inserirAluno){
                            Toast.makeText(FormCadAlunoActivity.this, "Aluno cadastrado!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), CadAlunoActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(FormCadAlunoActivity.this, "Falha no cadastro!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(FormCadAlunoActivity.this, "Aluno já existe!", Toast.LENGTH_SHORT).show();
                    }
                        }
            }
        });

        /*
        List<String> turmas = new ArrayList<>();
        turmas.add("Turma");
        turmas.add("Turma 1");
        turmas.add("Turma 2");
        turmas.add("Turma 3");

        // Criação do ArrayAdapter e configuração do Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, turmas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTurma.setAdapter(adapter);
        spinnerTurma.setSelection(0);*/

    }
}