package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;


import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class VisualEscolaActivity extends AppCompatActivity {
    ImageButton btnVoltar, btnHome;
    private Toolbar toolbar;

    BancoDados bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_escola);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnVoltar = findViewById(R.id.btn_voltar_left);
        btnVoltar.setVisibility(View.VISIBLE);
        btnHome = findViewById(R.id.home_icon);
        bancoDados = new BancoDados(this);

        BancoDados bancoDados = new BancoDados(this);
        SQLiteDatabase database = bancoDados.getReadableDatabase();
        String [] projection = {"nomeEscola"};
        Cursor cursor = database.query("escola", projection, null, null, null, null, null);
        ArrayList<String> escolas = new ArrayList<>();
        int nomeColumIndex = cursor.getColumnIndex("nomeEscola");
        if (nomeColumIndex != -1){
            while (cursor.moveToNext()){
                String nomeEscola = cursor.getString(nomeColumIndex);
                escolas.add(nomeEscola);
            }
        }else{
            Log.e("VisualEscolaActivity", "A coluna 'nomeEscola' não foi encontrada no cursor.");
        }
        cursor.close();
        database.close();

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
                String ids = Long.toString(id+1);
                String escola = bancoDados.pegaEscola(ids);
                Intent intent = new Intent(VisualEscolaActivity.this, DetalhesEscolaActivity.class);
                intent.putExtra("escola", escola);
                startActivity(intent);

            }
        });
    }

    public void voltarTelaIncial() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }
}