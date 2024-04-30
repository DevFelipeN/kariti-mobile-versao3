package com.example.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.DialogInterface;
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
    VisualEscolaActivity atualiza;
    ArrayList<String> listeDesativadas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escola_desativada);

        btnVoltar = findViewById(R.id.imgBtnVoltaDescola);
        ListView listView = findViewById(R.id.listViewEscDesativadas);

        bancoDados = new BancoDados(this);
        atualiza = new VisualEscolaActivity();
        iconHelp = findViewById(R.id.iconHelp);

        iconHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelp();
            }
        });

        listeDesativadas = (ArrayList<String>) bancoDados.listDesativadas();
        DesativadaAdapter adapter = new DesativadaAdapter(this, listeDesativadas, listeDesativadas);
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
                                String ids = String.valueOf(bancoDados.pegaIdEscolaDesativada(adapter.getItem(position)));
                                String pegaNome = bancoDados.pegaEscolaDesativada(ids);
                                String pegaBairro = bancoDados.pegaBairroDesativado(ids);
                                Integer id = Integer.valueOf(ids);
                                Boolean deletaEscola = bancoDados.deletarEscola(id);
                                if(deletaEscola){
                                    Boolean reativa = bancoDados.inserirDadosEscola(pegaNome, pegaBairro);
                                    if(reativa){
                                        listeDesativadas.remove(position);
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(EscolaDesativadaActivity.this, "Escola Reativada com Sucesso!", Toast.LENGTH_SHORT).show();
                                        //finish();
                                    } else Toast.makeText(EscolaDesativadaActivity.this, "Erro de ativação!", Toast.LENGTH_SHORT).show();
                                }else Toast.makeText(EscolaDesativadaActivity.this, "Erro de ativação!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Integer ids = bancoDados.pegaIdEscolaDesativada(adapter.getItem(position));
                                Boolean deletaEscola = bancoDados.deletarEscola(ids);
                                if (deletaEscola) {
                                    listeDesativadas.remove(position);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(EscolaDesativadaActivity.this, "Escola Excluida Com Sucesso", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {onBackPressed();}
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