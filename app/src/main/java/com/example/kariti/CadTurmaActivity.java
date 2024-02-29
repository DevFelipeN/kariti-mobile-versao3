package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class CadTurmaActivity extends AppCompatActivity {
    ImageButton voltar;
    private Toolbar toolbar;
    BancoDados bancoDados;
    EditText pesquisarAlunos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_turma);

        toolbar = findViewById(R.id.myToolBarMenu);
        setSupportActionBar(toolbar);

        voltar = findViewById(R.id.imgBtnVoltar);
        ListView listView = findViewById(R.id.listView);
        pesquisarAlunos = findViewById(R.id.editTextPesquisarAlunos);

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        bancoDados = new BancoDados(this);
        SQLiteDatabase database = bancoDados.getReadableDatabase();
        String [] projection = {"nomeAluno", "id_aluno"};
        Cursor cursor = database.query("aluno", projection, "id_escola="+BancoDados.ID_ESCOLA, null, null, null, null);
        ArrayList<String> alunos = new ArrayList<>();
        ArrayList<String> idAlunos = new ArrayList<>();
        int nomeColumIndex = cursor.getColumnIndex("nomeAluno");
        if (nomeColumIndex != -1){
            while (cursor.moveToNext()){
                String nome = cursor.getString(0);
                String idAluno = cursor.getString(1);
                alunos.add(nome);
                idAlunos.add(idAluno);
            }
        }else{
            Log.e("CadTurmaActivity", "A coluna 'nome' não foi encontrada no cursor.");
        }
        cursor.close();
        database.close();

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alunos);
        listView.setAdapter(adapter);
        pesquisarAlunos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}