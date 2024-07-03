package online.padev.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import online.padev.kariti.R;

public class AlunoActivity extends AppCompatActivity {
    ImageButton btnVoltar, iconHelpInfor;
    Button btnCadAluno, btnVisualizarAluno;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aluno);

        toolbar = findViewById(R.id.myToolBarMenu);
        setSupportActionBar(toolbar);
        btnCadAluno = findViewById(R.id.buttonCadAluno);
        btnVisualizarAluno = findViewById(R.id.buttonVisualAluno);



        /*
        Id do AppBar
         */
        btnVoltar = findViewById(R.id.imgBtnVoltaDescola);
        iconHelpInfor = findViewById(R.id.iconHelp);

        iconHelpInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelpDetalhes();
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        btnCadAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaCadastroAluno();
            }
        });
        btnVisualizarAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaVisualizarAluno();
            }
        });

    }
    public void mudarParaTelaCadastroAluno(){
        Intent intent = new Intent(this, CadAlunoActivity.class);
        startActivity(intent);
    }
    public void mudarParaTelaVisualizarAluno(){
        Intent intent = new Intent(this, VisualAlunoActivity.class);
        startActivity(intent);
    }

    public void dialogHelpDetalhes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Nesta tela você pode cadastrar novos alunos ou visualizar os alunos já cadastrados, selecionando uma das opções sugeridas.\n\n" +
                "IMPORTANTE!\n\n" +
                "O kariti possibilita que seus usuários possam cadastrar provas sem a necessidade de cadastro dos alunos nesta etapa.\n\n" +
                "Para isso, basta selecionar a opção 'Turma -> Cadastrar Turma' informar o nome da turma e quantidade de alunos no campo 'Incluir Alunos Anônimos'.\n" +
                "Dessa forma será criada uma turma com seus alunos anônimos (alunos com identificação unica gerada pelo KARITI).");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void informeAnonimo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Caso seja seu primeiro acesso!" +
                "");
        builder.setMessage("O kariti possibilita que seus usuários possam cadastrar provas sem a necessidade de cadastro dos alunos nesta etapa.\n\n" +
                "Para isso, basta selecionar a opção 'Turma -> Cadastrar Turma' informar o nome da turma e quantidade de alunos no campo 'Incluir Alunos Anônimos'.\n" +
                "Dessa forma será possivel cadastrar as provas para essa turma com alunos anônimos (o KARITI gera uma identificação unica para cada prova)");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}