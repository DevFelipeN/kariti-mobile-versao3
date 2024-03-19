package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
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
    ArrayList<String> nomesAluno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_turma);

        toolbar = findViewById(R.id.myToolBarMenu);
        setSupportActionBar(toolbar);
        voltar = findViewById(R.id.imgBtnVoltar);
        listarAlunos = findViewById(R.id.listViewAlTurma);
        nomeTurma = findViewById(R.id.editTextTurma);
        cadastrar = findViewById(R.id.buttonCadastrarTurma);
        spinnerBuscAluno = findViewById(R.id.spinnerBuscAluno);
        alunosAnonimos = findViewById(R.id.editTextAlunosAnonimos);
        menosAnonimos = findViewById(R.id.imageViewMenosAnonimos);
        maisAnonimos = findViewById(R.id.imageViewMaisAnonimos);
        bancoDados = new BancoDados(this);

        nomesAluno = (ArrayList<String>) bancoDados.obterNomesAlunos();
        SpinnerAdapter adapter = new SpinnerAdapter(this, nomesAluno);
        spinnerBuscAluno.setAdapter(adapter);

        spinnerBuscAluno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                alunoSelecionado = spinnerBuscAluno.getSelectedItem().toString();
                selectedAlunos.add(alunoSelecionado);
                al = new AdapterExclAluno(CadTurmaActivity.this, selectedAlunos);
                listarAlunos.setAdapter(al);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        listarAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               //Em desenvolvimento......
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
                Toast.makeText(CadTurmaActivity.this, "Turma cadastrada", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), TurmaActivity.class);
                startActivity(intent);
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