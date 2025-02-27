package online.padev.kariti;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class AnimationCorrectionActivity extends AppCompatActivity {
    TextView titleActivity, informativo;
        static AnimationCorrectionActivity instanciaEncerra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_correction);

        instanciaEncerra = this;

        ImageButton btnVoltar = findViewById(R.id.imgBtnVoltar);
        titleActivity = findViewById(R.id.toolbar_title);
        informativo = findViewById(R.id.textViewInformativo);

        informativo.setText(String.format(" %s ","Correção em andamento...\n\n" +
                "Após a correção, acesse 'Visualizar Provas', em seguida selecione a prova desejada!\n\n" +
                "Por favor, aguarde..."));

        titleActivity.setText(String.format("%s","Corrigindo..."));
        btnVoltar.setOnClickListener(v -> {
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
    protected void onDestroy() {
        super.onDestroy();
        instanciaEncerra = null;
    }

    public static void encerra(String status) {
        if (instanciaEncerra != null) {
            instanciaEncerra.finish();
        }
    }
}