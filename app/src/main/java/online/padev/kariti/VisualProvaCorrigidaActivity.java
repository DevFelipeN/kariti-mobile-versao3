package online.padev.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import online.padev.kariti.R;

import java.util.ArrayList;
import java.util.Objects;

public class VisualProvaCorrigidaActivity extends AppCompatActivity {
    ImageButton voltar;
    Button btnBaixar;
    ArrayList<String> listAlunos;
    ArrayList<Integer> listIdsAlunos, listAcertos, listNotaDaluno, listQuestoes;
    BancoDados bancoDados;
    Integer id_prova;
    String prova;
    TextView provaResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_prova_corrigida);

        voltar = findViewById(R.id.imgBtnVoltar);
        btnBaixar = findViewById(R.id.buttonBaixarResultado);
        provaResult = findViewById(R.id.textViewProvaResult);
        bancoDados = new BancoDados(this);
        listAlunos = new ArrayList<>();

        prova = Objects.requireNonNull(getIntent().getExtras()).getString("prova");
        id_prova = getIntent().getExtras().getInt("id_prova");
        provaResult.setText(prova);
        listIdsAlunos = (ArrayList<Integer>) bancoDados.listAluno(id_prova);
        for(int x = 0; x < listIdsAlunos.size(); x++) {
            float nota = 0;
            int acertos = 0;
            Integer id_aluno = listIdsAlunos.get(x);
            listQuestoes = (ArrayList<Integer>) bancoDados.listQuestoes(id_prova, id_aluno);
            for(int i = 0; i < listQuestoes.size(); i++){
                Integer questao = listQuestoes.get(i);
                Integer respostaDada = bancoDados.pegaRespostaDada(id_prova, id_aluno, questao);
                Integer respostaGabarito = bancoDados.pegaRespostaQuestao(id_prova, questao);
                if(respostaDada.equals(respostaGabarito)){
                    nota += bancoDados.pegaNotaQuestao(id_prova, questao);
                    acertos += 1;
                }
            }

            TableLayout tableLayout = findViewById(R.id.tableLayout);
            TableRow row = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);

            // Cria uma célula para a nova linha para armazenar nome do aluno
            TextView cell1 = new TextView(this);
            cell1.setText(bancoDados.pegaAlunoProvaCorrigida(id_aluno.toString()));
            cell1.setGravity(Gravity.CENTER);
            row.addView(cell1);

            // Cria outra célula para a nova linha para armazenar o total de acertos do aluno na prova
            TextView cell2 = new TextView(this);
            cell2.setText(String.valueOf(acertos));
            cell2.setGravity(Gravity.CENTER);
            row.addView(cell2);

            // Cria outra célula para a nova linha para armazenar a nota total do aluno
            TextView cell3 = new TextView(this);
            cell3.setText(String.valueOf(nota));
            cell3.setGravity(Gravity.CENTER);
            row.addView(cell3);
            // Adiciona a nova linha à tabela
            tableLayout.addView(row);
        }
        carregaGabarito();
        btnBaixar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnectedOrConnecting()){
            return true;
        }else{
            return false;
        }
    }
    public void PopMenu(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(VisualProvaCorrigidaActivity.this, "Preparado para implementação", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void carregaGabarito(){
        String gabarito = bancoDados.mostraGabarito(id_prova);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Gabarito");
        builder.setMessage(gabarito);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
        });
        builder.show();

    }

}