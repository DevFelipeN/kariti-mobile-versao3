package com.example.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;


import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class VisualEscolaActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    Button btnEscDesativada;
    ImageView iconHelp;
    private Toolbar toolbar;

    BancoDados bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_escola);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnVoltar = findViewById(R.id.imgBtnVoltar);
        btnEscDesativada = findViewById(R.id.buttonEscDesativada);
        bancoDados = new BancoDados(this);
        iconHelp = findViewById(R.id.iconHelp);

        SQLiteDatabase database = bancoDados.getReadableDatabase();
        String [] projection = {"nomeEscola", "id_escola"};
        Cursor cursor = database.query("escola", projection, "id_usuario="+BancoDados.USER_ID, null, null, null, null);
        ArrayList<String> nomesEscolas = new ArrayList<>();
        ArrayList<String> idsEscolas = new ArrayList<>();
        int nomeColumIndex = cursor.getColumnIndex("nomeEscola");
        if (nomeColumIndex != -1){
            while (cursor.moveToNext()){
                String nomeEscola = cursor.getString(0);
                String idEscola = cursor.getString(1);
                nomesEscolas.add(nomeEscola);
                idsEscolas.add(idEscola);
            }
        }else{
            Log.e("VisualEscolaActivity", "A coluna 'nomeEscola' não foi encontrada no cursor.");
        }
        cursor.close();
        database.close();


        ListView listView = findViewById(R.id.listViewAlunosTurma);
        EscolaAdapter adapter = new EscolaAdapter(this, nomesEscolas, idsEscolas);
        listView.setAdapter(adapter);

        btnEscDesativada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaEscolaDesativada();
            }
        });
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        iconHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelp();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BancoDados.ID_ESCOLA = Integer.valueOf(idsEscolas.get(position));
                Intent intent = new Intent(VisualEscolaActivity.this, DetalhesEscolaActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Exibir a caixa de diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(VisualEscolaActivity.this);
                builder.setTitle("Atenção!")
                        .setMessage("Deseja desativar a escola?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String ids = idsEscolas.get(position);
                                String escola = bancoDados.pegaEscola(ids);
                                String bairro = bancoDados.pegaBairro(ids);
                                Boolean deletDativadas = bancoDados.deletarDasAtivadas(ids);
                                if(deletDativadas){
                                    Boolean inserSlcolDesativada = bancoDados.inserirEscolaDesativada(escola, bairro);
                                    if(inserSlcolDesativada) {
                                        nomesEscolas.remove(position);
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(VisualEscolaActivity.this, "Escola Desativada Com Sucesso", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Código para lidar com o clique no botão Cancelar, se necessário
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                // Retorna true para indicar que o evento de long press foi consumido
                return true;
            }
        });

    }

    public void dialogHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Para arquivar uma escola, basta pressionar sobre a escola desejada e confirmar a ação. " +
                "Posteriormente, você poderá encontrar suas escolas arquivadas clicando no botão 'Escolas Desativadas'.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void telaEscolaDesativada() {
        Intent intent = new Intent(this, EscolaDesativadaActivity.class);
        startActivity(intent);
    }
}