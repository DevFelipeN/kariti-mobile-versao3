package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class VisualTurmaActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_turma);

        btnVoltar = findViewById(R.id.imgBtnVoltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        ArrayList<String> teste = new ArrayList<>();
        teste.add("nome1");
        teste.add("nome2");
        teste.add("nome3");
        teste.add("nome4");
        teste.add("nome5");
        teste.add("nome6");
        teste.add("nome7");
        teste.add("nome8");
        ListView listView = findViewById(R.id.listView);
        EscolaAdapter adapter = new EscolaAdapter(this, teste, teste);
        listView.setAdapter(adapter);
    }
}