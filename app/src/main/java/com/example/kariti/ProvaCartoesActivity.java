package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ProvaCartoesActivity extends AppCompatActivity {
    ImageButton voltar;
    Integer id_turma;
    String prova, turma;
    ArrayList<String> provalist = new ArrayList<>();
    BancoDados bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_cartoes);

        voltar = findViewById(R.id.imgBtnVoltar);
        Spinner spinner = findViewById(R.id.spinnerTurma);
        Spinner spinnerProva = findViewById(R.id.spinnerProva);
        Spinner spinnerAluno = findViewById(R.id.spinnerAlunos);

        bancoDados = new BancoDados(this);

        prova = getIntent().getExtras().getString("prova");
        id_turma = getIntent().getExtras().getInt("id_turma");
        turma = bancoDados.pegaNomeTurma(String.valueOf(id_turma));

        provalist = (ArrayList<String>) bancoDados.obterNomeProvas();
        provalist.add(0, prova);
        SpinnerAdapter adapter = new SpinnerAdapter(this, provalist);
        spinnerProva.setAdapter(adapter);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}