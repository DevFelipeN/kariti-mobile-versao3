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

import online.padev.kariti.dao.Gabarito;
import online.padev.kariti.dao.Prova;

public class ProvaFastActivity extends AppCompatActivity {

    ImageButton back, iconHelp;
    Button btnNewCard, btnCorrect;
    TextView textViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_fast);

        back = findViewById(R.id.imgBtnVoltaDescola);
        iconHelp = findViewById(R.id.iconHelp);
        textViewTitle = findViewById(R.id.toolbar_title);
        btnNewCard = findViewById(R.id.buttonNewProvaRapida);
        btnCorrect = findViewById(R.id.buttonCorrigi);

        textViewTitle.setText(String.format("%s", "Prova Rápida"));

        btnNewCard.setOnClickListener(v -> startNewCard());
        btnCorrect.setOnClickListener(v -> startCorrection());
        iconHelp.setOnClickListener(v -> Toast.makeText(this, "Implement Information", Toast.LENGTH_SHORT).show());
        

        back.setOnClickListener(v -> outputController());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                outputController();
            }
        });

    }
    private void startNewCard(){
        Intent intent = new Intent(this, GenerateCardDefaultActivity.class);
        startActivity(intent);
    }
    public void startCorrection(){
        Intent intent = new Intent(getApplicationContext(), CameraxAndOpencv.class);
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
}