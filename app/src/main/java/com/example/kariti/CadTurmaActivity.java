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
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class CadTurmaActivity extends AppCompatActivity {
    private ImageButton voltar;

    private ArrayAdapter<String> listViewAdapter;
    private Toolbar toolbar;
    private EditText pesquisarAlunos, nomeTurma;
    private Button buttonIncluirAlunos, cadastrar;
    private BancoDados bancoDados;
    ListView listarAlunos;
    private List<String> selectedAlunos = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_turma);

        toolbar = findViewById(R.id.myToolBarMenu);
        setSupportActionBar(toolbar);

        voltar = findViewById(R.id.imgBtnVoltar);
        listarAlunos = findViewById(R.id.listView);
        pesquisarAlunos = findViewById(R.id.editTextPesquisarAlunos);
        nomeTurma = findViewById(R.id.editTextTurma);
        cadastrar = findViewById(R.id.buttonCadastrarTurma);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        Spinner spinnerBuscAluno = findViewById(R.id.spinnerBuscAluno);
        bancoDados = new BancoDados(this);
        ArrayList<String> nomesAluno = (ArrayList<String>) bancoDados.obterNomesAlunos();
        SpinnerAdapter adapter = new SpinnerAdapter(this, nomesAluno);
        spinnerBuscAluno.setAdapter(adapter);
        spinnerBuscAluno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String alunoSelecionado = (String) parent.getItemAtPosition(position);
                if (!selectedAlunos.contains(alunoSelecionado)) {
                    selectedAlunos.add(alunoSelecionado);
                    listViewAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        listViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listarAlunos.setAdapter(listViewAdapter);
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}