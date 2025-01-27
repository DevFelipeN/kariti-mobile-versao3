package online.padev.kariti;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import online.padev.kariti.utilities.Gabarito;

public class ViewImage2 extends AppCompatActivity {

    private ImageView imageProcessada;
    private Button encerrar, continuar;
    private TextView textViewNomeAluno, textViewNomeProva, textViewNotaAluno, textViewAcertos, textViewErros, titulo;
    private Integer id_prova, id_aluno;
    float notaAluno;
    int acertos, erros;
    private String nomeAluno, nomeProva;
    private BancoDados bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image2);

        imageProcessada = findViewById(R.id.ImagemProcessada);
        ImageButton voltar = findViewById(R.id.imgBtnVoltar);
        textViewNomeProva = findViewById(R.id.textViewNomeProva);
        textViewNomeAluno = findViewById(R.id.textViewNomeAluno);
        textViewNotaAluno = findViewById(R.id.textViewNotaAluno);
        textViewAcertos = findViewById(R.id.textViewAcertosAluno);
        textViewErros = findViewById(R.id.textViewErrosAluno);
        encerrar = findViewById(R.id.buttonEncerrar);
        titulo = findViewById(R.id.toolbar_title);
        continuar = findViewById(R.id.buttonContinuar);

        bancoDados = new BancoDados(this);

        id_prova = getIntent().getExtras().getInt("id_prova");
        id_aluno = getIntent().getExtras().getInt("id_aluno");

        nomeProva = bancoDados.pegarNomeProva(id_prova);
        nomeAluno = bancoDados.pegaNomeAluno(id_aluno);

        if (nomeProva == null || nomeAluno == null || nomeProva.isEmpty() || nomeAluno.isEmpty()){
            Toast.makeText(this, "Algo deu errado por aqui!", Toast.LENGTH_SHORT).show();
            finish();
        }

        titulo.setText(String.format("%s", "Prova Corrigida"));

        resultCorrecao();

        String filePath = getIntent().getStringExtra("filePath");
        if (filePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            imageProcessada.setImageBitmap(bitmap);
            textViewNomeProva.setText(String.format("%s", "Prova: "+nomeProva));
            textViewNomeAluno.setText(String.format("%s","Aluno: "+nomeAluno));
            textViewNotaAluno.setText(String.format("%s", "Nota: "+ notaAluno));
            textViewAcertos.setText(String.format("%s", "Acertos: "+ acertos));
            textViewErros.setText(String.format("%s","Erros: "+ erros));

        }else{
            newIntent();
        }
        continuar.setOnClickListener(v -> newIntent());
        encerrar.setOnClickListener(v -> finish());
        voltar.setOnClickListener(v -> finish());
    }
    private void newIntent(){
        Intent intent = new Intent(this, CameraxAndOpencv.class);
        startActivity(intent);
        finish();
    }
    private void resultCorrecao(){
        Gabarito gabarito = new Gabarito(bancoDados, id_prova);
        List<Gabarito> listGabarito = gabarito.getGabarito();
        List<String> respostasDadas = bancoDados.listarRespostasDadas(id_prova, id_aluno);

        for (int i = 0; i < listGabarito.size(); i++){
            Gabarito g = listGabarito.get(i); // g contém questao, resposta e nota, respectivamente
            char r = (char) ('A' + Integer.parseInt(String.valueOf(g.getResposta())) - 1);
            if (respostasDadas.get(i).equals(String.valueOf(r))){
                notaAluno += g.getNota();
                acertos += 1;
            }else{
                erros += 1;
            }
        }
    }

}