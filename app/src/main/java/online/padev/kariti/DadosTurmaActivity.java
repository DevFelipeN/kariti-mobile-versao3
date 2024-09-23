package online.padev.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class DadosTurmaActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    ImageButton voltar;
    TextView turmaCad, qtdAnonimos;
    BancoDados bancoDados;
    ListView listView;
    ArrayList<String> listTodosAlunosDaTurma;
    ArrayList<Integer> qtdAlunosAnonimatos, provasPorTurma;
    String id_turma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_turma);

        voltar = findViewById(R.id.imgBtnVoltarDados);
        listView = findViewById(R.id.listViewDados);
        qtdAnonimos = findViewById(R.id.textViewqtdAnonimos);
        turmaCad = findViewById(R.id.textViewTurmaCad);
        bancoDados = new BancoDados(this);

        id_turma = String.valueOf(Objects.requireNonNull(getIntent().getExtras()).getInt("idTurma"));
        String pegaTurma = bancoDados.pegaNomeTurma(id_turma);

        turmaCad.setText(String.format("Turma: %s", pegaTurma));

        listTodosAlunosDaTurma = (ArrayList<String>) bancoDados.listTodosAlunosDaTurma(id_turma);
        qtdAlunosAnonimatos = (ArrayList<Integer>) bancoDados.qtdAlunosAnonimatos(id_turma);
        qtdAnonimos.setText(String.format(" Alunos Anônimos: %s \n Total de alunos: %s",qtdAlunosAnonimatos.size(), listTodosAlunosDaTurma.size()));
        DesativadaAdapter adapter = new DesativadaAdapter(this, listTodosAlunosDaTurma, listTodosAlunosDaTurma);
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
        return true;
    }

    public void telaEditar(){
        boolean status = false;
        //Boolean checkTurmaEmProva = bancoDados.checkTurmaEmProva(Integer.valueOf(id_turma));
        provasPorTurma = (ArrayList<Integer>) bancoDados.listProvasPorTurma(id_turma);
        if(!provasPorTurma.isEmpty()){
            for(int a : provasPorTurma){
                Boolean checkCorrigida = bancoDados.checkCorrigida(String.valueOf(a));
                if(checkCorrigida) {
                    status = true;
                    break;
                }
            }
        }
        if(!status){
            Intent intent = new Intent(this, EditarTurmaActivity.class);
            intent.putExtra("id_turma", id_turma);
            startActivity(intent);
            finish();
        }else{
            avisoNotExluir();
        }
    }
    public void avisoNotExluir(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DadosTurmaActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Esta turma possui vínculo com uma ou mais prova(s) já corrigidas, não sendo possível editar!");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}