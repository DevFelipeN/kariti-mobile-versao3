package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class CadTurmaActivity extends AppCompatActivity {
    private ImageButton voltar;
    private Toolbar toolbar;
    private EditText pesquisarAlunos, nomeTurma, alunosAnonimos;
    ImageView menosAnonimos, maisAnonimos;
    private Button buttonIncluirAlunos, cadastrar;
    BancoDados bancoDados;
    Spinner spinnerBuscAluno;
    private ArrayList<String> selectedAlunos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_turma);

        toolbar = findViewById(R.id.myToolBarMenu);
        setSupportActionBar(toolbar);
        voltar = findViewById(R.id.imgBtnVoltar);
        ListView listarAlunos = findViewById(R.id.listViewAlTurma);
        pesquisarAlunos = findViewById(R.id.editTextPesquisarAlunos);
        nomeTurma = findViewById(R.id.editTextTurma);
        cadastrar = findViewById(R.id.buttonCadastrarTurma);
        spinnerBuscAluno = findViewById(R.id.spinnerBuscAluno);
        alunosAnonimos = findViewById(R.id.editTextAlunosAnonimos);
        menosAnonimos = findViewById(R.id.imageViewMenosAnonimos);
        maisAnonimos = findViewById(R.id.imageViewMaisAnonimos);
        bancoDados = new BancoDados(this);

        ArrayList<String> nomesAluno = (ArrayList<String>) bancoDados.obterNomesAlunos();
        SpinnerAdapter adapter = new SpinnerAdapter(this, nomesAluno);
        spinnerBuscAluno.setAdapter(adapter);
        AdapterExclAluno al = new AdapterExclAluno(this, nomesAluno);
        listarAlunos.setAdapter(al);
        spinnerBuscAluno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String alunoSelecionado = (String) parent.getItemAtPosition(position);
                Toast.makeText(CadTurmaActivity.this, "Clicado: "+position, Toast.LENGTH_SHORT).show();
                if (!selectedAlunos.contains(alunoSelecionado)) {
                    selectedAlunos.add(alunoSelecionado);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
                alunosAnonimos.setText(mais.toString());
            }
        });
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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