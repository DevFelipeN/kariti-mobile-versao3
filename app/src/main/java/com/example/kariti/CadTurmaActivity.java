package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class CadTurmaActivity extends AppCompatActivity{
    private ImageButton voltar;
    private Toolbar toolbar;
    private EditText nomeTurma, alunosAnonimos;
    ImageView menosAnonimos, maisAnonimos;
    ListView listarAlunos;
    private Button cadastrar;
    BancoDados bancoDados;
    Spinner spinnerBuscAluno;
    String alunoSelecionado;
    AdapterExclAluno al;
    private ArrayList<String> selectedAlunos = new ArrayList<>();
    ArrayList<String> nomesAluno, selecionados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_turma);

        toolbar = findViewById(R.id.myToolBarMenu);
        setSupportActionBar(toolbar);
        voltar = findViewById(R.id.imgBtnVoltar);
        listarAlunos = findViewById(R.id.listViewAlTurma);
        nomeTurma = findViewById(R.id.editTextEditTurma);
        cadastrar = findViewById(R.id.buttonCadastrarTurma);
        spinnerBuscAluno = findViewById(R.id.spinnerBuscAluno);
        alunosAnonimos = findViewById(R.id.editTextAlunosAnonimos);
        menosAnonimos = findViewById(R.id.imageViewMenosAnonimos);
        maisAnonimos = findViewById(R.id.imageViewMaisAnonimos);
        bancoDados = new BancoDados(this);

        nomesAluno = (ArrayList<String>) bancoDados.obterNomesAlunos();
        nomesAluno.add(0, "Selecione os Alunos");
        SpinnerAdapter adapter = new SpinnerAdapter(this, nomesAluno);

        spinnerBuscAluno.setAdapter(adapter);

        spinnerBuscAluno.setSelection(0);

        spinnerBuscAluno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                alunoSelecionado = spinnerBuscAluno.getSelectedItem().toString();
                selectedAlunos.add(alunoSelecionado);
                al = new AdapterExclAluno(CadTurmaActivity.this, selectedAlunos);
                listarAlunos.setAdapter(al);
            }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        listarAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               Toast.makeText(CadTurmaActivity.this, "Implementar ação para apagar! ", Toast.LENGTH_SHORT).show();
           }
        });


        menosAnonimos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer menos = Integer.valueOf(alunosAnonimos.getText().toString());
                if(menos > 0)
                    menos --;
                alunosAnonimos.setText(menos.toString());
            }
        });

        maisAnonimos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer mais = Integer.valueOf(alunosAnonimos.getText().toString());
                mais ++;
                selectedAlunos.add("Aluno "+mais);
                alunosAnonimos.setText(mais.toString());
                listarAlunos.setAdapter(al);
            }
        });
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String turma = nomeTurma.getText().toString();
                Boolean checkTurma = bancoDados.checkTurma(turma);
                if(!checkTurma) {
                    Integer id_turma = 0;
                    Boolean cadTurma = bancoDados.inserirTurma(turma);
                    if (cadTurma) {
                        id_turma = bancoDados.pegaIdTurma(turma);
                    } else
                        Toast.makeText(CadTurmaActivity.this, "Erro no cadastro da Turma", Toast.LENGTH_SHORT).show();
                    int num = listarAlunos.getAdapter().getCount();
                    selecionados = (ArrayList<String>) selectedAlunos;
                    for (int i = 0; i < num; i++) {
                        bancoDados.inserirAlunosNaTurma(selecionados.get(i), id_turma);
                    }
                    Toast.makeText(CadTurmaActivity.this, "Turma cadastrada com Sucesso", Toast.LENGTH_SHORT).show();
                    finish();
                }else Toast.makeText(CadTurmaActivity.this, "Turma já cadstradada! ", Toast.LENGTH_SHORT).show();

            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}