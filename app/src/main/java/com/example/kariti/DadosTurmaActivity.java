package com.example.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DadosTurmaActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    ImageButton voltar;
    ImageView menuPnt;
    TextView turmaCad, txtAonimos, qtdAnonimos;
    BancoDados bancoDados;
    ListView listView;
    ArrayList<String> listAlunosDturma = new ArrayList<>();
    ArrayList<Integer> idsAlTurma;
    String id_turma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_turma);

        voltar = findViewById(R.id.imgBtnVoltarDados);
        //menuPnt = findViewById(R.id.menu_icon);
        listView = findViewById(R.id.listViewDados);
        qtdAnonimos = findViewById(R.id.textViewqtdAnonimos);
        turmaCad = findViewById(R.id.textViewTurmaCad);
        txtAonimos = findViewById(R.id.textViewAlunosAnonimos);
        bancoDados = new BancoDados(this);

        id_turma = String.valueOf(getIntent().getExtras().getInt("idTurma"));
        String pegaTurma = bancoDados.pegaNomeTurma(id_turma);
        Integer pegaAnonimos = bancoDados.pegaqtdAnonimos(id_turma);
        turmaCad.setText(pegaTurma);
        qtdAnonimos.setText(pegaAnonimos.toString());


        idsAlTurma = (ArrayList<Integer>) bancoDados.listAlunosDturma(id_turma);
        int num = idsAlTurma.size();
        for(int y = 0; y < num; y++){
            String id_aluno = String.valueOf(idsAlTurma.get(y));
            listAlunosDturma.add(bancoDados.pegaNomeAluno(id_aluno));
        }
        EscolaAdapter adapter = new EscolaAdapter(this, listAlunosDturma, listAlunosDturma);
        listView.setAdapter(adapter);


        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    public void PopMenu(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.actuvity_menuturma);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuEditar) {
                telaEditar();
            return true;
        }
        /*else if (id == R.id.menuExcluir) {
            Toast.makeText(DadosTurmaActivity.this, "Excluir Turma selecionado", Toast.LENGTH_SHORT).show();
            return true;
        }else if (id == R.id.menuExclTurmAlun) {
            Toast.makeText(DadosTurmaActivity.this, "Excluir Turma e Aluno selecionado", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }

         */
        return true;
    }

    public void telaEditar(){
        Boolean checkTurmaEmProva = bancoDados.checkTurmaEmProva(Integer.valueOf(id_turma));
        if(!checkTurmaEmProva) {
            Intent intent = new Intent(this, EditarTurmaActivity.class);
            intent.putExtra("id_turma", id_turma);
            startActivity(intent);
            finish();
        }else avisoNotExluir();
    }
    public void avisoNotExluir(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DadosTurmaActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Esta turma possui vínculo com uma ou mais prova(s) cadastrada(s), não sendo possível Editar!");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}