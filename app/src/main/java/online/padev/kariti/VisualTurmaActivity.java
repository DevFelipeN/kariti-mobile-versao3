package online.padev.kariti;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class VisualTurmaActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    FloatingActionButton btnCadTurma;
    ListView listViewTurma;
    ArrayList<String> listaTurma;
    TextView titulo, descricaoTurma;
    Integer id_turma;
    BancoDados bancoDados;
    private static final int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_turma2);

        btnVoltar = findViewById(R.id.imgBtnVoltar);
        listViewTurma = findViewById(R.id.listViewVisualTurma);
        descricaoTurma = findViewById(R.id.txtDescricaoAddTurma);
        titulo = findViewById(R.id.toolbar_title);
        btnCadTurma = findViewById(R.id.iconaddTurma);

        titulo.setText(String.format("%s","Turmas"));

        bancoDados = new BancoDados(this);

        listaTurma = (ArrayList<String>) bancoDados.listarNomesTurmas();
        if (listaTurma == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 7", Toast.LENGTH_SHORT).show();
            finish();
        }
        if(listaTurma.isEmpty()){
            carregarTelaCadTurma();
        }
        EscolaAdapter adapter = new EscolaAdapter(this, listaTurma, listaTurma);
        listViewTurma.setAdapter(adapter);

        listViewTurma.setOnItemClickListener((parent, view, position, id) -> {
            id_turma = bancoDados.pegarIdTurma(adapter.getItem(position));
            if (id_turma == null){
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 7", Toast.LENGTH_SHORT).show();
                return;
            }
            telaDadosTurma(id_turma);
        });

        listViewTurma.setOnItemLongClickListener((parent, view, position, id) -> {
            // Exibir a caixa de diálogo
            AlertDialog.Builder builder = new AlertDialog.Builder(VisualTurmaActivity.this);
            builder.setTitle("Atenção!")
                    .setMessage("Deseja excluir essa turma?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        id_turma = bancoDados.pegarIdTurma(listaTurma.get(position));
                        Boolean verificaTurma = bancoDados.verificaExisteTurmaEmProva(id_turma);
                        if (id_turma == null || verificaTurma == null){
                            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 7", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!verificaTurma) {
                            Boolean deletaTurma = bancoDados.deletarTurma(id_turma);
                            if (deletaTurma) {
                                listaTurma.remove(position);
                                adapter.notifyDataSetChanged();
                                if(listaTurma.isEmpty()){
                                    finish();
                                }
                                Toast.makeText(VisualTurmaActivity.this, "Turma excluida com sucesso! ", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(VisualTurmaActivity.this, "Algo deu errado, falha ao tentar excluir a turma! ", Toast.LENGTH_SHORT).show();
                            }
                        }else avisoNotExluir();
                    })
                    .setNegativeButton("Não", (dialog, which) -> {
                        // cancelou
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        });

        //Exibir o texto sobre o botão
        descricaoTurma.setVisibility(View.VISIBLE);
        descricaoTurma.setVisibility(View.VISIBLE);
        // Ocultar o texto após 3 segundos
        new Handler().postDelayed(() -> descricaoTurma.setVisibility(View.INVISIBLE), 10000);
        new Handler().postDelayed(() -> descricaoTurma.setVisibility(View.INVISIBLE), 10000);
        btnCadTurma.setOnClickListener(v -> carregarTelaCadTurma());
        btnVoltar.setOnClickListener(view -> {
            getOnBackPressedDispatcher();
            finish();
        });
    }
    private void telaDadosTurma(Integer idTurma) {
        Intent intent = new Intent(this, DadosTurmaActivity.class);
        intent.putExtra("idTurma", idTurma);
        startActivityForResult(intent, REQUEST_CODE);
    }
    private void avisoNotExluir(){
        AlertDialog.Builder builder = new AlertDialog.Builder(VisualTurmaActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Esta turma possui vínculo com uma ou mais prova(s) cadastrada(s), não sendo possível excluir!.");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                finish();
                startActivity(getIntent());
            }else{
                finish();
            }
        }
    }

    private void carregarTelaCadTurma(){
        Intent intent = new Intent(this, CadTurmaActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

}