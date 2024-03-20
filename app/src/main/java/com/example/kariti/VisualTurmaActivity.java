package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class VisualTurmaActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    ListView listView;
    BancoDados bancoDados;
    ArrayList<String> listarTurma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_turma);

        btnVoltar = findViewById(R.id.imgBtnVoltar);
        listView = findViewById(R.id.listViewAlunosTurma);

        bancoDados = new BancoDados(this);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        listarTurma = (ArrayList<String>) bancoDados.obterNomeTurmas();
        EscolaAdapter adapter = new EscolaAdapter(this, listarTurma, listarTurma);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String qTurma = listarTurma.get(position);
                Integer idTurma = bancoDados.pegaIdTurma(qTurma);
                telaTeste(idTurma);
            }
        });
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    public void telaTeste(Integer idTurma) {
        Intent intent = new Intent(this, DadosTurmaActivity.class);
        intent.putExtra("idTurma", idTurma);
        startActivity(intent);
    }
}