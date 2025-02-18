package online.padev.kariti;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class QuickCardActivity extends AppCompatActivity {

    ImageButton voltar, iconeAjuda;
    Button btnNewCard, btnCorrect;
    TextView textViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_card);

        voltar = findViewById(R.id.imgBtnVoltaDescola);
        iconeAjuda = findViewById(R.id.iconHelp);
        textViewTitle = findViewById(R.id.toolbar_title);
        btnNewCard = findViewById(R.id.buttonNewProvaRapida);
        btnCorrect = findViewById(R.id.buttonCorrigi);

        textViewTitle.setText(String.format("%s", "Prova Rápida"));

        btnNewCard.setOnClickListener(v -> startNewCard());
        btnCorrect.setOnClickListener(v -> startCorrection());
        iconeAjuda.setOnClickListener(v -> Toast.makeText(this, "Implement Information", Toast.LENGTH_SHORT).show());
        

        voltar.setOnClickListener(v -> finish());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

    }
    private void startNewCard(){
        Intent intent = new Intent(this, NewCardActivity.class);
        startActivity(intent);
    }
    public void startCorrection(){
        Intent intent = new Intent(getApplicationContext(), CameraxAndOpencv.class);
        startActivity(intent);
    }
}