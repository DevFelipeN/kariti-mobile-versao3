package com.example.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class EscolaDesativadaActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    ImageButton btnVoltar;
    ImageView btnMenu, iconHelp;
    BancoDados bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escola_desativada);

        btnVoltar = findViewById(R.id.imgBtnVoltar);
        btnMenu = findViewById(R.id.imageViewIcon);
        ListView listView = findViewById(R.id.listView);
        bancoDados = new BancoDados(this);
        iconHelp = findViewById(R.id.iconHelp);

        iconHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelp();
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {onBackPressed();}
        });

        SQLiteDatabase database = bancoDados.getReadableDatabase();
        String [] projection = {"nomeScolDesativada", "id_scolDesativadas"};
        Cursor cursor = database.query("escolasDesativadas", projection, "id_usuario="+BancoDados.USER_ID, null, null, null, null);
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

        DesativadaAdapter adapter = new DesativadaAdapter(this, nomesEscolasDesativadas, idsEscolasDesativadas);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Exibir a caixa de diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(EscolaDesativadaActivity.this);
                builder.setTitle("Atenção!")
                        .setMessage("Qual operação deseja realizar")
                        .setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String ids = idsEscolasDesativadas.get(position);
                                String pegaNome = bancoDados.pegaEscolaDesativada(ids);
                                String pegaBairro = bancoDados.pegaBairroDesativado(ids);
                                Integer id = Integer.valueOf(ids);
                                Boolean deletaEscola = bancoDados.deletarEscola(id);
                                Boolean reativa = bancoDados.inserirDadosEscola(pegaNome, pegaBairro);
                                if (deletaEscola!=false && reativa!=false) {
                                    Toast.makeText(EscolaDesativadaActivity.this, "Escola reativada com Sucesso!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), VisualEscolaActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else
                                    Toast.makeText(EscolaDesativadaActivity.this, "Erro de ativação!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Integer ids = Integer.valueOf(idsEscolasDesativadas.get(position));
                                Boolean deletaEscola = bancoDados.deletarEscola(ids);
                                if (deletaEscola)
                                    Toast.makeText(EscolaDesativadaActivity.this, "Escola Excluida Com Sucesso", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });
    }

    public void dialogHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Para ATIVAR ou EXCLUIR uma escola, basta pressionar sobre a escola desejada e seleconar a ação. ");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void menuAtivaExcl(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.activity_menuescola);
        popupMenu.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuExcluirEscola) {
            Toast.makeText(EscolaDesativadaActivity.this, "Excluir Escola selecionado: ", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menuAtivarEscola) {
            Toast.makeText(EscolaDesativadaActivity.this, "Ativar Escola selecionado", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }
}