package online.padev.kariti;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import online.padev.kariti.entity.Gabarito;
import online.padev.kariti.entity.Prova;

public class ProvaFastDefaultActivity extends AppCompatActivity {

    ImageButton back, iconHelp;
    Button btnNewCard, btnCorrect;
    TextView textViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_fast_default);

        back = findViewById(R.id.imgBtnVoltaDescola);
        iconHelp = findViewById(R.id.iconHelp);
        textViewTitle = findViewById(R.id.toolbar_title);
        btnNewCard = findViewById(R.id.buttonNewProvaRapida);
        btnCorrect = findViewById(R.id.buttonCorrigi);

        textViewTitle.setText(String.format("%s", "Prova Rápida"));

        btnNewCard.setOnClickListener(v -> startNewCard());
        btnCorrect.setOnClickListener(v -> startCorrection());
        iconHelp.setOnClickListener(v -> help());
        

        back.setOnClickListener(v -> outputController());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                outputController();
            }
        });

    }
    private void startNewCard(){
        Intent intent = new Intent(this, ProvaGenerateCardDefaultActivity.class);
        startActivity(intent);
    }
    private void startCorrection(){
        Intent intent = new Intent(getApplicationContext(), CameraxAndOpencvActivity.class);
        startActivity(intent);
    }

    private void outputController(){
        if (Gabarito.gabaritoDefault != null && !Gabarito.gabaritoDefault.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ATENÇÃO!")
                    .setMessage("Você possui um gabarito criado, ao confirmar essa ação o gabarito será perdido!\n\n" +
                            "Deseja realmente sair?")
                    .setPositiveButton("SIM", (dialog, which) -> {
                        Gabarito.gabaritoDefault.clear();
                        Prova.numQuestsDefault = 0;
                        Prova.numAlternativesDefault = 0;
                        finish();
                    })
                    .setNegativeButton("NÃO", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else finish();
    }

    private void help() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Bem vindo(a) ao menu inicial de prova rápida. \n Aqui você pode gerar cartões respostas e corrigir seus cartões de maneira fácil e prática! \n\n" +
                "• Na primeira opção você pode gerar um cartão, informando a quantidade de questões e alternativas da sua prova, em seguida imprimir a quantidade necessária para os alunos.\n\n" +
                "• Na segunda opção você pode realizar a correção do cartão gerado na opção anterior, apenas apontando a câmera do Kariti para o cartão que por sua vez realiza a correção e exibe o resultado na sua tela.\n\n" +
                "• Ao início de cada seção de correção será solicitado o preenchimento do gabarito da sua prova, com ele preenchido todos os cartões referentes a ele podem ser corrigidos sequencialmente. ");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}