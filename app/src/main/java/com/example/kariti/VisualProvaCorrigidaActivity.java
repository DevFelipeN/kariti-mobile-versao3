package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
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

import java.net.NetworkInterface;
import java.util.ArrayList;

public class VisualProvaCorrigidaActivity extends AppCompatActivity {
    ImageButton voltar;
    Button btnBaixar;
    ArrayList<String> listIdsAlunos, listAlunos;
    ArrayList<Integer> listAcertos, listNotaDaluno;
    BancoDados bancoDados;
    String provaSelected, idProva, aluno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_prova_corrigida);

        voltar = findViewById(R.id.imgBtnVoltarDcorrecao);
        btnBaixar = findViewById(R.id.buttonBaixarResultado);
        bancoDados = new BancoDados(this);
        listAlunos = new ArrayList<>();

        provaSelected = getIntent().getExtras().getString("prova");
        idProva = String.valueOf(bancoDados.pegaIdProva(provaSelected));
        listIdsAlunos = (ArrayList<String>) bancoDados.listAluno(idProva);
        for(int i = 0; i < listIdsAlunos.size(); i++){
            aluno = bancoDados.pegaNomeAluno(listIdsAlunos.get(i));
            listAlunos.add(aluno);
        }
        listAcertos = (ArrayList<Integer>) bancoDados.listAcertos(idProva);
        listNotaDaluno = (ArrayList<Integer>) bancoDados.listNotaAluno(idProva);

        for(int x = 0; x < listIdsAlunos.size(); x++) {

            TableLayout tableLayout = findViewById(R.id.tableLayout);
            TableRow row = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);

            // Cria uma célula para a nova linha
            TextView cell1 = new TextView(this);
            cell1.setText(listAlunos.get(x));
            //cell1.setGravity(Gravity.);
            row.addView(cell1);

            // Cria outra célula para a nova linha
            TextView cell2 = new TextView(this);
            cell2.setText(listAcertos.get(x).toString());
            cell2.setGravity(Gravity.CENTER);
            row.addView(cell2);

            TextView cell3 = new TextView(this);
            cell3.setText(listNotaDaluno.get(x).toString());
            cell3.setGravity(Gravity.CENTER);
            row.addView(cell3);

            // Adiciona a nova linha à tabela
            tableLayout.addView(row);
        }
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
}