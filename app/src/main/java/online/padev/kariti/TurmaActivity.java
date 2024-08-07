package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class TurmaActivity extends AppCompatActivity {
    ImageButton voltar, iconeAjuda;
    Button btnCadastrarTurma, btnVisualizarTurma;
    TextView textViewTitulo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turma);

        voltar = findViewById(R.id.imgBtnVoltaDescola);
        iconeAjuda = findViewById(R.id.iconHelp);
        btnCadastrarTurma = findViewById(R.id.buttonCadAluno);
        btnVisualizarTurma = findViewById(R.id.buttonVisuTurma);
        textViewTitulo = findViewById(R.id.toolbar_title);

        textViewTitulo.setText(String.format("%s","Turma"));

        btnCadastrarTurma.setOnClickListener(v -> carregarTelaCadastrarTurma());
        btnVisualizarTurma.setOnClickListener(v -> carregarTelaVisualTurma());
        iconeAjuda.setOnClickListener(v -> ajuda());

        voltar.setOnClickListener(v -> {
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
    private void carregarTelaCadastrarTurma(){
        Intent intent = new Intent(this, CadTurmaActivity.class);
        startActivity(intent);
    }

    private void carregarTelaVisualTurma(){
        Intent intent = new Intent(this, VisualTurmaActivity.class);
        startActivity(intent);
    }

    private void ajuda() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Olá, nesta tela você pode selecionar 'Cadastrar Turma' para cadastrar uma nova turma com seus respectivos alunos ou 'Visualizar Turma' para exibir as turmas já cadastradas.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

}