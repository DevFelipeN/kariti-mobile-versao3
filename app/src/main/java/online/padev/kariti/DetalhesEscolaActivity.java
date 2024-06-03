package online.padev.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import online.padev.kariti.R;

public class DetalhesEscolaActivity extends AppCompatActivity {
    ImageButton btnVoltar, iconHelpDetalhes;
    Button btnTurma, btnAluno, btnProva;
    TextView nomeEscola;

    BancoDados bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_escola);

        btnVoltar = findViewById(R.id.imgBtnVoltaEscola);
        btnVoltar.setVisibility(View.VISIBLE);
        iconHelpDetalhes = findViewById(R.id.iconHelpDetalhesSchool);

        btnTurma = findViewById(R.id.btnTurma);
        btnAluno = findViewById(R.id.buttonAluno);
        btnProva = findViewById(R.id.btnProva);
        nomeEscola = findViewById(R.id.textViewNomeEscola);
        bancoDados = new BancoDados(this);

        String escola = bancoDados.pegaEscola(String.valueOf(BancoDados.ID_ESCOLA));
        nomeEscola.setText(escola);

        iconHelpDetalhes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelpDetalhes();
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BancoDados.ID_ESCOLA = null;
                onBackPressed();
            }
        });
        btnTurma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irTelaTurma();
            }
        });
        btnAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irTelaAluno();
            }
        });
        btnProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irTelaProva();
            }
        });
    }
    public void irTelaTurma(){
        Intent intent = new Intent(this, TurmaActivity.class);
        startActivity(intent);
    }
    public void irTelaAluno(){
        Intent intent = new Intent(this, AlunoActivity.class);
        startActivity(intent);
    }
    public void irTelaProva(){
        Intent intent = new Intent(this, ProvaActivity.class);
        startActivity(intent);
    }

    public void dialogHelpDetalhes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("1 - Aluno \n" +
                "Nesta etapa podem ser cadastrados e visualizados os alunos no app, independente da turma, a qual, pertecem! \n" +
                "2 - Turma \n" +
                "Nesta etapa, são cadastradas as turmas e selecionados os alunos pertencentes a essa turma, os quais, já devem antecipadamente está cadastrados na etapa anterior. Caso não deseje selecionar os alunos cadastrados ou nao deseje cadastrar os alunos," +
                " é possivel cadastrar alunos anônimos na turma, informando a quandidade no campo 'Incluir Alunos Anônimos' \n " +
                "3 - Prova \n" +
                "Nesta etapa são realizadas todas as operações com as provas a serem cadastradas no app.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}