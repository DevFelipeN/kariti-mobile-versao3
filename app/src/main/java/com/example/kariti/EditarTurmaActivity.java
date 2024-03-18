package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class EditarTurmaActivity extends AppCompatActivity {
    ImageButton voltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_turma);

        ListView listView = findViewById(R.id.listViewAlTurma);
        voltar = findViewById(R.id.imgBtnVoltar);

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ArrayList<String> teste = new ArrayList<>();
        teste.add("Antonio");
        teste.add("Carlos");
        teste.add("Gil");
        teste.add("Guilherme");
        teste.add("Miguel");
        teste.add("Paulo");
        teste.add("Toji");
        teste.add("Via");
        ArrayAdapter<String> adapterAlunos = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, teste);
        listView.setAdapter(adapterAlunos);
    }
}