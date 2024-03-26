package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class VisualProvaActivity extends AppCompatActivity {
    ImageButton voltar;
    Button visualProva;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_prova);

        voltar = findViewById(R.id.imgBtnVoltaEscola);
        visualProva = findViewById(R.id.buttonVisualizarProva);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        visualProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaVisuProvaSelecionada();
            }
        });

        Spinner spinner = findViewById(R.id.spinnerTurma);
        Spinner spinnerProva = findViewById(R.id.spinnerProva);
        Spinner spinnerAluno = findViewById(R.id.spinnerAlunos);

        List<String> options = new ArrayList<>();
        options.add("Turma:"); // Item fixo
        options.add("Turma 1");
        options.add("Turma 2");

        List<String> optionsProva = new ArrayList<>();
        optionsProva.add("Prova:"); // Item fixo
        optionsProva.add("Prova 1");
        optionsProva.add("Prova 2");

        List<String> optionsAluno = new ArrayList<>();
        optionsAluno.add("Aluno:"); // Item fixo
        optionsAluno.add("Aluno 1");
        optionsAluno.add("Aluno 2");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayAdapter<String> adapterProva = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionsProva);
        adapterProva.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProva.setAdapter(adapterProva);

        ArrayAdapter<String> adapterAluno = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionsAluno);
        adapterAluno.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAluno.setAdapter(adapterAluno);
    }

    public void telaVisuProvaSelecionada(){
        Intent intent = new Intent(this, VisualProvaCorrigidaActivity.class);
        startActivity(intent);
    }
}