package com.example.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class VisualProvaActivity extends AppCompatActivity {
    ImageButton voltar;
    Button visualProva;
    String turmaSelecionada, provaSelected;
    Integer id_turma;
    Spinner spinnerProva, spinnerTurma, spinnerAluno;
    BancoDados bancoDados;
    ArrayList<Integer> listIdAlTurma;
    ArrayList<String> provalist, turmalist, alunolist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_prova);

        voltar = findViewById(R.id.imgBtnVoltar);
        visualProva = findViewById(R.id.buttonVisualizarProva);
        spinnerTurma = findViewById(R.id.spinnerTurma1);
        spinnerProva = findViewById(R.id.spinnerProva1);
        spinnerAluno = findViewById(R.id.spinnerAlunos1);

        bancoDados = new BancoDados(this);

        turmalist = (ArrayList<String>) bancoDados.obterNomeTurmas();
        turmalist.add(0, "Selecione a turma");
        SpinnerAdapter adapterTurma = new SpinnerAdapter(this, turmalist);
        spinnerTurma.setAdapter(adapterTurma);

        spinnerTurma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    turmaSelecionada = spinnerTurma.getSelectedItem().toString();
                    id_turma = bancoDados.pegaIdTurma(turmaSelecionada);

                    provalist = (ArrayList<String>) bancoDados.obterNomeProvas(String.valueOf(id_turma));
                    SpinnerAdapter adapterProva = new SpinnerAdapter(VisualProvaActivity.this, provalist);
                    spinnerProva.setAdapter(adapterProva);

                    alunolist = new ArrayList<>();
                    listIdAlTurma = (ArrayList<Integer>) bancoDados.listAlunosDturma(String.valueOf(id_turma));
                    int num = listIdAlTurma.size();
                    for (int x = 0; x < num; x++) {
                        String id_aluno = String.valueOf(listIdAlTurma.get(x));
                        alunolist.add(bancoDados.pegaNomeAluno(id_aluno));
                    }
                    SpinnerAdapter adapterAluno = new SpinnerAdapter(VisualProvaActivity.this, alunolist);
                    spinnerAluno.setAdapter(adapterAluno);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        visualProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaVisualProvaSelecionada();
            }
        });
    }
    public void telaVisualProvaSelecionada(){
        provaSelected = spinnerProva.getSelectedItem().toString();
/*
        bancoDados.inserirResultCorrecao(1,4, 3, 7);
        bancoDados.inserirResultCorrecao(1,5, 2, 5);
        bancoDados.inserirResultCorrecao(1,6, 3, 6);
        bancoDados.inserirResultCorrecao(1,7, 4, 9);
 */
        Intent intent = new Intent(this, VisualProvaCorrigidaActivity.class);
        intent.putExtra("prova", provaSelected);
        startActivity(intent);
    }
    public void avisoCamposNulos(){
        AlertDialog.Builder builder = new AlertDialog.Builder(VisualProvaActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Selecione as opções desejadas ");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}