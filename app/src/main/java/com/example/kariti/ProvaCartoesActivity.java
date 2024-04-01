package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ProvaCartoesActivity extends AppCompatActivity {
    ImageButton voltar;
    Integer id_turma;
    String prova, turma;
    ArrayList<String> provalist, turmalist, alunolist;
    ArrayList<Integer> listIdAlTurma;
    BancoDados bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_cartoes);

        voltar = findViewById(R.id.imgBtnVoltar);
        Spinner spinnerTurma = findViewById(R.id.spinnerTurma);
        Spinner spinnerProva = findViewById(R.id.spinnerProva);
        Spinner spinnerAluno = findViewById(R.id.spinnerAlunos);

        bancoDados = new BancoDados(this);

        prova = getIntent().getExtras().getString("prova");
        id_turma = getIntent().getExtras().getInt("id_turma");
        turma = bancoDados.pegaNomeTurma(String.valueOf(id_turma));


        turmalist = (ArrayList<String>) bancoDados.obterNomeTurmas();
        turmalist.add(0, turma);
        SpinnerAdapter adapterTurma = new SpinnerAdapter(this, turmalist);
        spinnerTurma.setAdapter(adapterTurma);

        provalist = (ArrayList<String>) bancoDados.obterNomeProvas(String.valueOf(id_turma));
        provalist.add(0, prova);
        SpinnerAdapter adapterProva = new SpinnerAdapter(this, provalist);
        spinnerProva.setAdapter(adapterProva);

        alunolist = new ArrayList<>();
        listIdAlTurma = (ArrayList<Integer>) bancoDados.listAlunosDturma(String.valueOf(id_turma));
        alunolist.add(0, "Alunos");
        int num = listIdAlTurma.size();
        for(int x = 0; x < num; x++){
            String id_aluno = String.valueOf(listIdAlTurma.get(x));
            alunolist.add(bancoDados.pegaNomeAluno(id_aluno));
        }
        SpinnerAdapter adapterAluno = new SpinnerAdapter(this, alunolist);
        spinnerAluno.setAdapter(adapterAluno);

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}