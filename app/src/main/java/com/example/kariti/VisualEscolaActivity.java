package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;


import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class VisualEscolaActivity extends AppCompatActivity {
    ImageButton btnVoltar, btnHome;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_escola);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnVoltar = findViewById(R.id.btn_voltar_left);
        btnVoltar.setVisibility(View.VISIBLE);
        btnHome = findViewById(R.id.home_icon);

        ArrayList<String> escolas = new ArrayList<>();
        escolas.add("Escola 1");
        escolas.add("Escola 2");
        escolas.add("Escola 3");
        escolas.add("Escola 4");


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, escolas);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voltarTelaIncial();
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                telaDetalheEscola();

            }
        });
    }

    public void voltarTelaIncial() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }

    public void telaDetalheEscola() {
        Intent intent = new Intent(this, DetalhesEscolaActivity.class);
        startActivity(intent);
    }
}