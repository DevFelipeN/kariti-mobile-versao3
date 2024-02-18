package com.example.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class EscolaDesativadaActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    ImageView menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escola_desativada);

        btnVoltar = findViewById(R.id.btn_voltar_left);
        menu = findViewById(R.id.imageViewIcon);
        ListView listView = findViewById(R.id.listView);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ArrayList<String> apenasUmTeste = new ArrayList<>();
        apenasUmTeste.add("nomeEscola1");
        apenasUmTeste.add("nomeEscola2");
        apenasUmTeste.add("nomeEscola3");
        apenasUmTeste.add("nomeEscola4");
        DesativadaAdapter adapter = new DesativadaAdapter(this, apenasUmTeste);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Exibir a caixa de diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(EscolaDesativadaActivity.this);
                builder.setTitle("Atenção!")
                        .setMessage("Deseja realmente excluir a escola?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // excluir
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // cancelou
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });
    }
}