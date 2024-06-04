package online.padev.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import online.padev.kariti.R;

public class TurmaActivity extends AppCompatActivity {
    ImageButton voltar, iconHelTurma;
    Button cadTurma, visuTurma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turma);

        voltar = findViewById(R.id.imgBtnVoltaDescola);
        iconHelTurma = findViewById(R.id.iconHelp);
        cadTurma = findViewById(R.id.buttonCadAluno);
        visuTurma = findViewById(R.id.buttonVisuTurma);

        iconHelTurma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelpDetalhes();
            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cadTurma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaCadTurma();
            }
        });
        visuTurma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaVisualTurma();
            }
        });
    }
    public void telaCadTurma(){
        Intent intent = new Intent(this, CadTurmaActivity.class);
        startActivity(intent);
    }

    public void telaVisualTurma(){
        Intent intent = new Intent(this, VisualTurmaActivity.class);
        startActivity(intent);
    }

    public void dialogHelpDetalhes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Adcionar Mensagem Turma!");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}