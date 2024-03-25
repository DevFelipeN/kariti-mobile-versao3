package com.example.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class VisualTurmaActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    ListView listView;
    BancoDados bancoDados;
    ArrayList<String> listarTurma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_turma);

        btnVoltar = findViewById(R.id.imgBtnVoltar);
        listView = findViewById(R.id.listViewTurma);

        bancoDados = new BancoDados(this);


        listarTurma = (ArrayList<String>) bancoDados.obterNomeTurmas();
        EscolaAdapter adapter = new EscolaAdapter(this, listarTurma, listarTurma);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String qTurma = listarTurma.get(position);
                Integer idTurma = bancoDados.pegaIdTurma(qTurma);
                telaTeste(idTurma);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Exibir a caixa de diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(VisualTurmaActivity.this);
                builder.setTitle("Atenção!")
                        .setMessage("Deseja Escluir Esta Turma?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String turma = listarTurma.get(position);
                                Toast.makeText(VisualTurmaActivity.this, "Item: "+turma, Toast.LENGTH_SHORT).show();
                                Boolean deletAluno = bancoDados.deletarTurma(turma);
                                if (deletAluno) {
                                   listarTurma.remove(position);
                                   adapter.notifyDataSetChanged();
                                   Toast.makeText(VisualTurmaActivity.this, "Turma Escluida! ", Toast.LENGTH_SHORT).show();
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
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    public void telaTeste(Integer idTurma) {
        Intent intent = new Intent(this, DadosTurmaActivity.class);
        intent.putExtra("idTurma", idTurma);
        startActivity(intent);
    }
    public void aviso(){
        AlertDialog.Builder builder = new AlertDialog.Builder(VisualTurmaActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Nao foram encontradas turmas cadastradas para esse usuário!");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}