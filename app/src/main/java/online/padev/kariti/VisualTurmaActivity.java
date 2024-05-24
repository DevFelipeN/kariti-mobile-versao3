package online.padev.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import online.padev.kariti.R;

import java.util.ArrayList;

public class VisualTurmaActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    ListView listView;
    BancoDados bancoDados;
    ArrayList<String> listarTurma;
    TextView tituloAppBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_turma);

        btnVoltar = findViewById(R.id.imgBtnVoltar);
        listView = findViewById(R.id.listViewVisualTurma);
        tituloAppBar = findViewById(R.id.toolbar_title);
        tituloAppBar.setText("Turmas");

        bancoDados = new BancoDados(this);

        bancoDados.deletarAlunoDturma(4);

        if (!haTurmasCadastradas()) {
            Intent intent = new Intent(this, ilustracionVoidSchoolctivity.class);
            startActivity(intent);
            finish();
            return;
        }


        listarTurma = (ArrayList<String>) bancoDados.obterNomeTurmas();
        EscolaAdapter adapter = new EscolaAdapter(this, listarTurma, listarTurma);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {;
                Integer idTurma = bancoDados.pegaIdTurma(adapter.getItem(position));
                telaTeste(idTurma);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Exibir a caixa de diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(VisualTurmaActivity.this);
                builder.setTitle("Atenção!")
                        .setMessage("Deseja excluir essa turma?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String turma = listarTurma.get(position);
                                Integer id_turma = bancoDados.pegaIdTurma(turma);
                                Boolean checkEmProva = bancoDados.checkTurmaEmProva(id_turma);
                                if(!checkEmProva) {
                                    Boolean deletTurma = bancoDados.deletarTurma(turma);
                                    bancoDados.deletarAlunoDturma(id_turma);
                                    if (deletTurma) {
                                        listarTurma.remove(position);
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(VisualTurmaActivity.this, "Turma Excluida! ", Toast.LENGTH_SHORT).show();
                                    }
                                }else avisoNotExluir();
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
        finish();
    }
    public void aviso(){
        AlertDialog.Builder builder = new AlertDialog.Builder(VisualTurmaActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Nao foram encontradas turmas cadastradas para esse usuário!");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void avisoNotExluir(){
        AlertDialog.Builder builder = new AlertDialog.Builder(VisualTurmaActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Esta turma possui vínculo com uma ou mais prova(s) cadastrada(s), não sendo possível excluir!.");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean haTurmasCadastradas() {
        SQLiteDatabase database = bancoDados.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM turma WHERE id_turma=" + BancoDados.USER_ID, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

}