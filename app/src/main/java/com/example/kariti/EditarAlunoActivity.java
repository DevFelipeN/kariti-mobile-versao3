package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class EditarAlunoActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    ImageButton voltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_aluno);

        voltar = findViewById(R.id.imgBtnVoltar);

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {onBackPressed();}
        });
    }

    public void popMenuAluno(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.activity_menualuno);
        popupMenu.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuEditarAluno) {
            Toast.makeText(EditarAlunoActivity.this, "Editar Aluno selecionado", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menuExcluirAluno) {
            Toast.makeText(EditarAlunoActivity.this, "Excluir Aluno selecionado", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }
}


