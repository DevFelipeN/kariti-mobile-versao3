package online.padev.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import online.padev.kariti.R;

public class ProvaActivity extends AppCompatActivity {
    ImageButton voltar;
    Button cadProva, gerarCartao, corrigirProva, visuProva;
    ArrayList<String> listeTurmas;
    BancoDados bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova);

        TextView textView = findViewById(R.id.textViewConcluido); // Seu TextView
        bancoDados = new BancoDados(this);

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
        if(listTurma().equals(false)) {
            Intent intent = new Intent(this, CadProvaActivity.class);
            startActivity(intent);
        }else aviso();
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
    public Boolean listTurma(){
        listeTurmas = (ArrayList<String>) bancoDados.obterNomeTurmas();
        if(listeTurmas.isEmpty()){
            return true;
        }else return false;
    }
    public void aviso(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atenção!");
        builder.setMessage("Não existem turmas cadastradas. " +
                "Para realizar o cadastro de provas, realize o cadastro das turma em que atua e/ou que será aplicada a prova.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}