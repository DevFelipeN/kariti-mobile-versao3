package com.example.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class EscolaDesativadaActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    ImageView menu;
    BancoDados bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escola_desativada);

        btnVoltar = findViewById(R.id.btn_voltar_left);
        menu = findViewById(R.id.imageViewIcon);
        ListView listView = findViewById(R.id.listView);
        bancoDados = new BancoDados(this);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {onBackPressed();}
        });


        SQLiteDatabase database = bancoDados.getReadableDatabase();
        String [] projection = {"nomeScolDesativada", "id_scolDesativadas"};
        Cursor cursor = database.query("escolasDesativadas", projection, null, null, null, null, null);
        ArrayList<String> nomesEscolasDesativadas = new ArrayList<>();
        ArrayList<String> idsEscolasDesativadas = new ArrayList<>();
        int nomeColumIndex = cursor.getColumnIndex("nomeScolDesativada");
        if (nomeColumIndex != -1){
            while (cursor.moveToNext()){
                String nomeEscola = cursor.getString(0);
                String idEscola = cursor.getString(1);
                nomesEscolasDesativadas.add(nomeEscola);
                idsEscolasDesativadas.add(idEscola);
            }
        }else{Log.e("VisualEscolaActivity", "A coluna 'nomeEscola' não foi encontrada no cursor.");}
        cursor.close();
        database.close();

        EscolaAdapter adapter = new EscolaAdapter(this, nomesEscolasDesativadas, idsEscolasDesativadas);
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
                                Integer ids = Integer.valueOf(idsEscolasDesativadas.get(position));
                                Boolean deletaEscola = bancoDados.deletarEscola(ids);
                                if (deletaEscola)
                                        Toast.makeText(EscolaDesativadaActivity.this, "Escola Excluida Com Sucesso", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent = new Intent(getApplicationContext(), EscolaDesativadaActivity.class);
                                startActivity(intent);
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