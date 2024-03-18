package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class DadosTurmaActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    ImageButton voltar;
    ImageView menuPnt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_turma);

        voltar = findViewById(R.id.imgBtnVoltar);
        menuPnt = findViewById(R.id.menu_icon);
        ListView listView = findViewById(R.id.listViewEscolas);

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

    public void PopMenu(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.actuvity_menuturma);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuEditar) {
            telaEditar();
            Toast.makeText(DadosTurmaActivity.this, "Editar Turma selecionado", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menuExcluir) {
            Toast.makeText(DadosTurmaActivity.this, "Excluir Turma selecionado", Toast.LENGTH_SHORT).show();
            return true;
        }else if (id == R.id.menuExclTurmAlun) {
            Toast.makeText(DadosTurmaActivity.this, "Excluir Turma e Aluno selecionado", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    public void telaEditar(){
        Intent intent = new Intent(this, EditarTurmaActivity.class);
        startActivity(intent);
    }
}