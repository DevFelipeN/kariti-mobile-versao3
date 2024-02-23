package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class VisualAlunoActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    BancoDados bancoDados;
    EditText pesquisarAlunos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_aluno);
        btnVoltar = findViewById(R.id.btn_voltar_left);
        pesquisarAlunos = findViewById(R.id.editTextBuscarAluno);
        bancoDados = new BancoDados(this);

        SQLiteDatabase database = bancoDados.getReadableDatabase();
        String [] projection = {"nome"};
        Cursor cursor = database.query("aluno", projection, null, null, null, null, null);
        ArrayList<String> alunos = new ArrayList<>();
        //ArrayList<String> idsalunos = new ArrayList<>();
        int nomeColumIndex = cursor.getColumnIndex("nome");
        if (nomeColumIndex != -1){
            while (cursor.moveToNext()){
                String nome = cursor.getString(nomeColumIndex);
                //String idAluno = cursor.getString(1);
                alunos.add(nome);
                //idsalunos.add(idAluno);
            }
        }else{
            Log.e("VisualAlunoActivity", "A coluna 'nome' não foi encontrada no cursor.");
        }
        cursor.close();
        database.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, alunos);
        ListView listView = findViewById(R.id.listAluno);
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
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

}