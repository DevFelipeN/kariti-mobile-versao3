package online.padev.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import online.padev.kariti.R;

import java.util.ArrayList;

public class EscolaDesativadaActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    ImageButton btnVoltar, iconHelp;
    ImageView btnMenu;
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

        listeDesativadas = (ArrayList<String>) bancoDados.listEscolas(0);
        if(listeDesativadas.size() == 0){
            Intent intent = new Intent(this, ilustracionVoidSchoolctivity.class);
            startActivity(intent);
            finish();
            return;
        }
        DesativadaAdapter adapter = new DesativadaAdapter(this, listeDesativadas, listeDesativadas);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Exibir a caixa de diálogo
                Integer id_escola = bancoDados.pegaIdEscola(adapter.getItem(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(EscolaDesativadaActivity.this);
                builder.setTitle("Atenção!")
                        .setMessage("Qual operação deseja realizar com essa escola? ")
                        .setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Boolean updadteStatus = bancoDados.upadateStatusEscola(id_escola, 1);
                                if(updadteStatus){
                                    listeDesativadas.remove(position);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(EscolaDesativadaActivity.this, "Escola Reativada com Sucesso!", Toast.LENGTH_SHORT).show();
                                    ativaVisualEscola();
                                }else Toast.makeText(EscolaDesativadaActivity.this, "Erro de ativação!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Implementar verificação, se possui dados como alunos turmas e provas ligadas a essa escola.................................
                                Boolean deletaEscola = bancoDados.deletarEscola(id_escola);
                                if (deletaEscola) {
                                    listeDesativadas.remove(position);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(EscolaDesativadaActivity.this, "Escola excluida com sucesso", Toast.LENGTH_SHORT).show();
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
            public void onClick(View view) {
                ativaVisualEscola();
            }
        });
    }
    public void ativaVisualEscola(){
        Intent intent = new Intent(getApplicationContext(), VisualEscolaActivity.class);
        startActivity(intent);
        finish();
    }

    public void dialogHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Para ATIVAR ou EXCLUIR uma escola, basta pressionar sobre a escola desejada por alguns segundos e selecionar a ação desejada. ");
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