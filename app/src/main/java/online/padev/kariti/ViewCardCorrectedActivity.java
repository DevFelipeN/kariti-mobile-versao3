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

import online.padev.kariti.entity.Gabarito;
import online.padev.kariti.database.DataBaseKariti;

public class ViewCardCorrectedActivity extends AppCompatActivity {

    ImageView imageViewCorrected;
    Button btnClose, btnContinue;
    TextView textViewNameStudent, textViewNameProva, textViewNote, textViewNumCorrect, textViewNumIncorrect, titleActivity;
    private Integer id_prova, id_student;
    float noteStudent;
    int numCorrect, numIncorrect;
    private String nameStudent, nameProva;
    private DataBaseKariti dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_card_corrected);

        imageViewCorrected = findViewById(R.id.ImagemProcessada);
        ImageButton back = findViewById(R.id.imgBtnVoltar);
        textViewNameProva = findViewById(R.id.textViewNomeProva);
        textViewNameStudent = findViewById(R.id.textViewNomeAluno);
        textViewNote = findViewById(R.id.textViewNotaAluno);
        textViewNumCorrect = findViewById(R.id.textViewAcertosAluno);
        textViewNumIncorrect = findViewById(R.id.textViewErrosAluno);
        btnClose = findViewById(R.id.buttonEncerrar);
        titleActivity = findViewById(R.id.toolbar_title);
        btnContinue = findViewById(R.id.buttonContinuar);

        dataBase = new DataBaseKariti(this);

        titleActivity.setText(String.format("%s", "Prova Corrigida"));

        int status = getIntent().getExtras().getInt("status");

        if (status == 0){ // Entra nesta estrutura caso o resultado do cartão a ser mostrado, seja de uma prova cadastrada

            id_prova = getIntent().getExtras().getInt("id_prova");
            id_student = getIntent().getExtras().getInt("id_aluno");

            nameProva = dataBase.pegarNomeProva(id_prova);
            nameStudent = dataBase.pegaNomeAluno(id_student);

            if (nameProva == null || nameStudent == null || nameProva.isEmpty() || nameStudent.isEmpty()){
                Toast.makeText(this, "Algo deu errado por aqui!", Toast.LENGTH_SHORT).show();
                finish();
            }
            textViewNameProva.setText(String.format("%s", "Prova: "+ nameProva));
            textViewNameStudent.setText(String.format("%s","Aluno: "+ nameStudent));

            resultCorrectedBD();

        } else {
            resultCorrectedDefault();
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
            imageViewCorrected.setImageBitmap(bitmap);
            textViewNote.setText(String.format("%s", "Nota: "+ noteStudent));
            textViewNumCorrect.setText(String.format("%s", "Acertos: "+ numCorrect));
            textViewNumIncorrect.setText(String.format("%s","Erros: "+ numIncorrect));
        }else{
            startCamera();
        }

        btnContinue.setOnClickListener(v -> startCamera());
        btnClose.setOnClickListener(v -> finish());
        back.setOnClickListener(v -> finish());

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

    private void startCamera(){
        Intent intent = new Intent(this, CameraxAndOpencv.class);
        startActivity(intent);
        finish();
    }

    private void resultCorrectedBD(){
        List<Gabarito> listGabarito = dataBase.listarDadosGabarito(id_prova);
        List<String> responseStudent = dataBase.listarRespostasDadas(id_prova, id_student);

        for (int i = 0; i < listGabarito.size(); i++){
            Gabarito g = listGabarito.get(i); // g contém questao, resposta e nota, respectivamente
            char r = (char) ('A' + Integer.parseInt(String.valueOf(g.getResponse())) - 1);
            if (responseStudent.get(i).equals(String.valueOf(r))){
                noteStudent += g.getNote();
                numCorrect += 1;
            }else{
                numIncorrect += 1;
            }
        }
    }

    private void resultCorrectedDefault(){
        HashMap<Integer, Integer> gabaritoResult = (HashMap<Integer, java.lang.Integer>) getIntent().getSerializableExtra("resultGabarito");
        String gabarito = getIntent().getExtras().getString("gabarito");

        for (Map.Entry<Integer, Integer> entry : gabaritoResult.entrySet()){
            int responseStudent = entry.getValue();
            int responseGabarito = gabarito.charAt(entry.getKey() - 1) - '0';
            if (responseStudent == responseGabarito){
                noteStudent += 1;
                numCorrect += 1;
            }else{
                numIncorrect += 1;
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