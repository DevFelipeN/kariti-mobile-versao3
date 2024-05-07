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
    ArrayList<String> listEscola;

    BancoDados bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_escola);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnVoltar = findViewById(R.id.imgBtnVoltaDescola);
        btnEscDesativada = findViewById(R.id.buttonEscDesativada);
        ListView listView = findViewById(R.id.listViewEscolas);
        bancoDados = new BancoDados(this);
        iconHelp = findViewById(R.id.iconHelp);

        if (!haEscolasCadastradas()) {
            Intent intent = new Intent(this, ilustracionVoidSchoolctivity.class);
            startActivity(intent);
            finish();
            return;
        }

        listEscola = (ArrayList<String>) bancoDados.listEscolas();
        EscolaAdapter adapter = new EscolaAdapter(this, listEscola, listEscola);
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
                BancoDados.ID_ESCOLA = bancoDados.pegaIdEscola(adapter.getItem(position));
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
                                String ids = String.valueOf(bancoDados.pegaIdEscola(adapter.getItem(position)));
                                String escola = bancoDados.pegaEscola(ids);
                                String bairro = bancoDados.pegaBairro(ids);
                                Boolean deletDativadas = bancoDados.deletarDasAtivadas(ids);
                                if(deletDativadas){
                                    Boolean inserSlcolDesativada = bancoDados.inserirEscolaDesativada(escola, bairro);
                                    if(inserSlcolDesativada) {
                                        listEscola.remove(position);
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(VisualEscolaActivity.this, "Escola desativada", Toast.LENGTH_SHORT).show();
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
        builder.setMessage("Para desativar uma escola, selecionar a escola desejada e confirmar a ação. " +
                "Posteriormente, você poderá encontrar suas escolas desativadas clicando no botão 'Escolas Desativadas'.");
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

    public boolean haEscolasCadastradas() {
        SQLiteDatabase database = bancoDados.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM escola WHERE id_usuario=" + BancoDados.USER_ID, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

}