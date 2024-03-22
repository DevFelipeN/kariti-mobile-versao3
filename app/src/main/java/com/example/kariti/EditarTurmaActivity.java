package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class EditarTurmaActivity extends AppCompatActivity {
    ImageButton voltar;
    ListView listView;
    EditText editTurma;
    ArrayList<String> editAlTurma;
    String id_turma;
    BancoDados bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_turma);

        listView = findViewById(R.id.listViewAlunosTurma);
        editTurma = findViewById(R.id.editTextEditTurma);
        voltar = findViewById(R.id.imgBtnVoltar);
        bancoDados = new BancoDados(this);

        id_turma = getIntent().getExtras().getString("id_turma");
        String pegaTurma = bancoDados.pegaNomeTurma(id_turma);
        editTurma.setText(pegaTurma);

        editAlTurma = (ArrayList<String>) bancoDados.listAlunosDturma(id_turma);
        AdapterExclAluno adapter = new AdapterExclAluno(this, editAlTurma);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(EditarTurmaActivity.this, ""+i, Toast.LENGTH_SHORT).show();
                Boolean remove = bancoDados.deletarAlunoDturma(editAlTurma.get(i));
                if(remove) {
                    editAlTurma.remove(i);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(EditarTurmaActivity.this, "Aluno Excluido! ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}