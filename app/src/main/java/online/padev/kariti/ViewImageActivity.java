package online.padev.kariti;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import online.padev.kariti.correction.CoreKariti;
import online.padev.kariti.dao.Gabarito;
import online.padev.kariti.dao.Prova;

public class ViewImageActivity extends AppCompatActivity {

    private ImageView imageProcessada;
    private Button encerrar, continuar;
    private TextView textViewNomeAluno, textViewNomeProva, textViewNotaAluno, textViewAcertos, textViewErros, titulo;
    private Integer id_prova, id_aluno;
    float notaAluno;
    int acertos, erros, typeMessage;
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

        titulo.setText(String.format("%s", "Prova Corrigida"));

        int status = getIntent().getExtras().getInt("status");

        if (status == 0){ // Entra nesta estrutura caso o resultado do cartão a ser mostrado, seja de uma prova cadastrada

            id_prova = getIntent().getExtras().getInt("id_prova");
            id_aluno = getIntent().getExtras().getInt("id_aluno");

            nomeProva = bancoDados.pegarNomeProva(id_prova);
            nomeAluno = bancoDados.pegaNomeAluno(id_aluno);

            if (nomeProva == null || nomeAluno == null || nomeProva.isEmpty() || nomeAluno.isEmpty()){
                Toast.makeText(this, "Algo deu errado por aqui!", Toast.LENGTH_SHORT).show();
                finish();
            }
            textViewNomeProva.setText(String.format("%s", "Prova: "+nomeProva));
            textViewNomeAluno.setText(String.format("%s","Aluno: "+nomeAluno));

            resultCorrecaoBD();

        } else {
            resultCorrecaoDefault();
        }


        String filePath = getIntent().getStringExtra("filePath");
        File file = new File(filePath);
        if (file.exists()){
            Log.e("kariti", "diretório existe!!");
        }else {
            Log.e("kariti", "diretório NÃO existe!!");
        }
        Log.e("kariti", "FilePath: "+filePath);
        if (filePath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            imageProcessada.setImageBitmap(bitmap);
            textViewNotaAluno.setText(String.format("%s", "Nota: "+ notaAluno));
            textViewAcertos.setText(String.format("%s", "Acertos: "+ acertos));
            textViewErros.setText(String.format("%s","Erros: "+ erros));
        }else{
            newIntent();
        }

        continuar.setOnClickListener(v -> newIntent());
        encerrar.setOnClickListener(v -> finish());
        voltar.setOnClickListener(v -> finish());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        deleteAllImages();
    }

    private void newIntent(){
        Intent intent = new Intent(this, CameraxAndOpencv.class);
        startActivity(intent);
        finish();
    }

    private void resultCorrecaoBD(){
        List<Gabarito> listGabarito = bancoDados.listarDadosGabarito(id_prova);
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

    private void resultCorrecaoDefault(){
        HashMap<Integer, Integer> gabaritoResult = (HashMap<Integer, java.lang.Integer>) getIntent().getSerializableExtra("resultGabarito");
        String gabarito = getIntent().getExtras().getString("gabarito");

        for (Map.Entry<Integer, Integer> entry : gabaritoResult.entrySet()){
            int respostaDada = entry.getValue();
            int respostaGabarito = gabarito.charAt(entry.getKey() - 1) - '0';
            if (respostaDada == respostaGabarito){
                notaAluno += 1;
                acertos += 1;
            }else{
                erros += 1;
            }
        }
    }
    private void deleteAllImages() {
        File externalDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CameraXopenCV");

        if (externalDir.exists() && externalDir.isDirectory()) {
            File[] files = externalDir.listFiles(); // Lista todos os arquivos no diretório

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.delete()) {
                        Log.e("kariti", "Diretório limpo: " + file.getName());
                    } else {
                        Log.e("kariti", "Erro ao tentar limpar diretório: " + file.getName());
                    }
                }
            }
        }
    }


}