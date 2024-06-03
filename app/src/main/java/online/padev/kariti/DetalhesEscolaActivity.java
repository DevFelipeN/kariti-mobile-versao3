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
        builder.setMessage("• Clique em \"Aluno\" para cadastrar seus estudantes, independentemente das turmas às quais eles pertencem. Caso não deseje cadastrar os alunos, será possível cadastrar estudantes anônimos (sem definição de nome) em etapa posterior.\n\n\n" +
                "• Clique em \"Turma\" para cadastrar as turmas de estudantes e para vincular os estudantes correspondentes (podendo também ser inseridos alunos anônimos nesta etapa).\n\n\n" +
                "• Após cadastrada a turma e vinculados os alunos correspondentes, clique em \"Prova\" para cadastrar as informações sobre uma prova a ser aplicada, incluindo suas informações básicas e seu gabarito.\n");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}