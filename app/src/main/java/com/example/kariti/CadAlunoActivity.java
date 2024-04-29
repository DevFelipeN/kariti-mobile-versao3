package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class CadAlunoActivity extends AppCompatActivity {
    ImageButton voltar;
    EditText nomeAluno, emailAluno;
    Button cadastrar;
    BancoDados bancoDados;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_aluno);

        nomeAluno = findViewById(R.id.editTextAlunoCad);
        emailAluno = findViewById(R.id.editTextEmailCad);
        voltar = findViewById(R.id.imgBtnVoltaEscola);
        cadastrar = findViewById(R.id.buttonSalvarEdit);
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
                if (nome.equals("")) {
                    Toast.makeText(CadAlunoActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean checkAluno = bancoDados.checkAluno(nome);
                    if (!checkAluno) {
                        if (!email.equals("")) {
                            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {  
                                Boolean insertAluno = bancoDados.inserirDadosAluno(nome, email);
                                if (insertAluno) {
                                    Toast.makeText(CadAlunoActivity.this, "Aluno cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(CadAlunoActivity.this, "Falha no cadastro do aluno!", Toast.LENGTH_SHORT).show();
                                }
                            } else
                                Toast.makeText(CadAlunoActivity.this, "E-mail Inválido!", Toast.LENGTH_SHORT).show();
                        } else {
                            Boolean insertAlunoS = bancoDados.inserirDadosAlunoSemail(nome);
                            if (insertAlunoS) {
                                Toast.makeText(CadAlunoActivity.this, "Aluno cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {Toast.makeText(CadAlunoActivity.this, "Aluno não cadastrado!", Toast.LENGTH_SHORT).show();}
                        }
                    } else{Toast.makeText(CadAlunoActivity.this, "Aluno já cadastrado!", Toast.LENGTH_SHORT).show();}
                }
            }
        });
    }
}