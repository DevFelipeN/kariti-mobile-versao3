package online.padev.kariti;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class QuickTestActivity extends AppCompatActivity {

    ImageButton voltar, iconeAjuda;
    Button btnNewCard, btnCorrect;
    TextView textViewTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_rapida);

        voltar = findViewById(R.id.imgBtnVoltaDescola);
        iconeAjuda = findViewById(R.id.iconHelp);
        textViewTitle = findViewById(R.id.toolbar_title);
        btnNewCard = findViewById(R.id.buttonNewProvaRapida);
        btnCorrect = findViewById(R.id.buttonCorrigi);

        textViewTitle.setText(String.format("%s", "Prova Rápida"));

        btnNewCard.setOnClickListener(v -> Toast.makeText(this, "New Card", Toast.LENGTH_SHORT).show());
        btnCorrect.setOnClickListener(v -> Toast.makeText(this, "New Correction", Toast.LENGTH_SHORT).show());
        iconeAjuda.setOnClickListener(v -> Toast.makeText(this, "Implement Information", Toast.LENGTH_SHORT).show());
        

        voltar.setOnClickListener(v -> finish());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

    }
}