package com.example.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class VisualAlunoActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    BancoDados bancoDados;
    EditText pesquisarAlunos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_aluno);
        btnVoltar = findViewById(R.id.imgBtnVoltaEscola);
        pesquisarAlunos = findViewById(R.id.editTextBuscar);
        ListView listView = findViewById(R.id.listSelecAluno);
        bancoDados = new BancoDados(this);

        SQLiteDatabase database = bancoDados.getReadableDatabase();
        String [] projection = {"nomeAluno", "id_aluno"};
        Cursor cursor = database.query("aluno", projection, "id_escola="+BancoDados.ID_ESCOLA, null, null, null, null);
        ArrayList<String> alunos = new ArrayList<>();
        ArrayList<String> idsAlunos = new ArrayList<>();
        int nomeColumIndex = cursor.getColumnIndex("nomeAluno");
        if (nomeColumIndex != -1){
            while (cursor.moveToNext()){
                String nome = cursor.getString(0);
                String idAluno = cursor.getString(1);
                alunos.add(nome);
                idsAlunos.add(idAluno);
            }
        }else{
            Log.e("VisualAlunoActivity", "A coluna 'nome' não foi encontrada no cursor.");
        }
        cursor.close();
        database.close();


        EscolaAdapter adapter = new EscolaAdapter(this, alunos, idsAlunos);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Exibir a caixa de diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(VisualAlunoActivity.this);
                builder.setTitle("Atenção!")
                        .setMessage("Deseja Realmente Excluir Aluno?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Integer ids = Integer.valueOf(idsAlunos.get(position));
                                Boolean deletAluno = bancoDados.deletarAluno(ids);
                                if (deletAluno) {
                                    alunos.remove(position);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(VisualAlunoActivity.this, "Aluno Excluido! ", Toast.LENGTH_SHORT).show();
                                }
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Integer id = Integer.valueOf(idsAlunos.get(i));
                Intent intent = new Intent(getApplicationContext(), EditarAlunoActivity.class);
                intent.putExtra("id_aluno", id);
                startActivity(intent);
                finish();
            }
        });

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
                finish();
            }
        });
    }
}