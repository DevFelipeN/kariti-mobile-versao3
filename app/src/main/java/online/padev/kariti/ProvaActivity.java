package online.padev.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import online.padev.kariti.R;

public class ProvaActivity extends AppCompatActivity {
    ImageButton voltar;
    Button cadProva, gerarCartao, corrigirProva, visuProva;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova);

        TextView textView = findViewById(R.id.textViewConcluido); // Seu TextView

        // Verificar se há uma mensagem extra no Intent
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("mensagem")) {
            String mensagem = intent.getStringExtra("mensagem");
            textView.setText(mensagem); // Define o texto do TextView com a mensagem
            textView.setVisibility(View.VISIBLE); // Exibe o TextView
        } else {
            textView.setVisibility(View.GONE); // Oculta o TextView se não houver mensagem
        }


        voltar = findViewById(R.id.imgBtnVoltar);
        cadProva = findViewById(R.id.buttonCadProva);
        gerarCartao = findViewById(R.id.buttonGerarCatao);
        corrigirProva = findViewById(R.id.buttonCorrigirProva);
        visuProva = findViewById(R.id.buttonVisuProva);
        cadProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaCadastroProva();
            }
        });
        gerarCartao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaGerarCartao();
            }
        });
        corrigirProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaCorrigirProva();
            }
        });
        visuProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaVisuProva();
            }
        });

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void telaCadastroProva(){
        Intent intent = new Intent(this, CadProvaActivity.class);
        startActivity(intent);
    }
    public void telaGerarCartao(){
        Intent intent = new Intent(this, ProvaCartoesActivity.class);
        intent.putExtra("endereco", 02);
        startActivity(intent);
    }
    public void telaCorrigirProva(){
        Intent intent = new Intent(this, ProvaCorrigirActivity.class);
        startActivity(intent);
        //Intent intent = new Intent(getApplicationContext(), CameraxActivity.class);
        //intent.putExtra("nomeImagem", "2_1_10_5.jpg");
        //startActivity(intent);
    }
    public void telaVisuProva(){
        Intent intent = new Intent(this, VisualProvaActivity.class);
        startActivity(intent);
    }
}