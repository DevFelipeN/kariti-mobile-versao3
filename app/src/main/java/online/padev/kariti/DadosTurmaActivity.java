package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class DadosTurmaActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    ImageButton voltar;
    TextView turmaCadastrada, txtViewQtdAnonimos;
    BancoDados bancoDados;
    ListView listViewAlunos;
    ArrayList<String> listaAlunosDaTurma;
    String id_turma;
    Integer qtdAnonimos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dados_turma);

        voltar = findViewById(R.id.imgBtnVoltarDados);
        listViewAlunos = findViewById(R.id.listViewDados);
        txtViewQtdAnonimos = findViewById(R.id.textViewqtdAnonimos);
        turmaCadastrada = findViewById(R.id.textViewTurmaCad);

        bancoDados = new BancoDados(this);

        id_turma = String.valueOf(Objects.requireNonNull(getIntent().getExtras()).getInt("idTurma"));
        String nomeTurma = bancoDados.pegarNomeTurma(id_turma);
        if (nomeTurma == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            finish();
        }

        turmaCadastrada.setText(String.format("Turma: %s", nomeTurma));

        listaAlunosDaTurma = (ArrayList<String>) bancoDados.listarAlunosPorTurma(id_turma);
        if (listaAlunosDaTurma == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            finish();
        }
        qtdAnonimos = bancoDados.pegarQtdAlunosPorStatus(id_turma, 0);
        if (qtdAnonimos == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            finish();
        }
        txtViewQtdAnonimos.setText(String.format(" Alunos Anônimos: %s \n Total de alunos: %s", qtdAnonimos, listaAlunosDaTurma.size()));
        DesativadaAdapter adapter = new DesativadaAdapter(this, listaAlunosDaTurma, listaAlunosDaTurma);
        listViewAlunos.setAdapter(adapter);

        voltar.setOnClickListener(view -> {
            getOnBackPressedDispatcher();
            finish();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
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
        Boolean provasCorrigidas = bancoDados.verificaExisteCorrecaoPorTurma(id_turma);
        if (provasCorrigidas == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!provasCorrigidas){
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