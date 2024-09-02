package online.padev.kariti;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AnimacaoCorrecao extends AppCompatActivity {
    TextView titulo, informativo;
    private static AnimacaoCorrecao instanciaEncerra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animacao_correcao);

        instanciaEncerra = this;

        ImageButton btnVoltar = findViewById(R.id.imgBtnVoltar);
        titulo = findViewById(R.id.toolbar_title);
        informativo = findViewById(R.id.textViewInformativo);

        informativo.setText(String.format("%s","Provas enviadas para correção!\n\n" +
                "Em instantes sua prova será corrigida. Após a correção, o resultado" +
                " poderá ser visualizado na opção 'Visualizar Correção'\n\n" +
                "Aguarde"));

        titulo.setText(String.format("%s","Corrigindo"));
        btnVoltar.setOnClickListener(v -> {
            getOnBackPressedDispatcher();
            finish();
        });
    }
    protected void onDestroy() {
        super.onDestroy();
        instanciaEncerra = null;
    }

    public static void encerra() {
        if (instanciaEncerra != null) {
            instanciaEncerra.finish();
        }
    }
}