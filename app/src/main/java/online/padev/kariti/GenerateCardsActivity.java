package online.padev.kariti;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Objects;

import online.padev.kariti.cards.CreatCard;
import online.padev.kariti.dao.Prova;

public class GenerateCardsActivity extends AppCompatActivity {
    ImageButton toGoBack;
    Button btnGenerateCards;
    Integer id_turmaBD, address, id_provaBD;
    String nameProva, className;
    List<String> listProvas, listTurmas, listAlunos;
    BancoDados bancoDados;
    Spinner spinnerTurma, spinnerProva, spinnerAluno;
    TextView title;
    Prova prova;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_cartoes);

        toGoBack = findViewById(R.id.imgBtnVoltar);
        spinnerTurma = findViewById(R.id.spinnerTurma);
        spinnerProva = findViewById(R.id.spinnerProva);
        spinnerAluno = findViewById(R.id.spinnerAlunos);
        btnGenerateCards = findViewById(R.id.baixarcatoes);
        title = findViewById(R.id.toolbar_title);

        bancoDados = new BancoDados(this);

        title.setText(String.format("%s","Cartões"));

        address = Objects.requireNonNull(getIntent().getExtras()).getInt("endereco");

        listTurmas = bancoDados.listarTurmasPorProva();
        if(listTurmas == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            return;
        }
        listTurmas.add(0,"Selecione a turma");

        if(address.equals(2)){ //para quando a activity que a chamou foi ProvaActivity
            SpinnerAdapter adapterTurma = new SpinnerAdapter(this, listTurmas);
            spinnerTurma.setAdapter(adapterTurma);

        }else if(address.equals(1)) { //para quando a activity que chamou for Gabarito
            id_turmaBD = getIntent().getExtras().getInt("id_turma");
            className = bancoDados.pegarNomeTurma(String.valueOf(id_turmaBD));
            nameProva = getIntent().getExtras().getString("prova");
            if (className == null){
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                return;
            }

            int indexTurma = listTurmas.indexOf(className); // Identifica a posicão da turma na lista
            SpinnerAdapter adapterTurma = new SpinnerAdapter(this, listTurmas);
            spinnerTurma.setAdapter(adapterTurma);
            if (indexTurma != -1){
                spinnerTurma.setSelection(indexTurma);
            }

            //============ Lista todas provas pertecentes a turma selecionada =======================
            listProvas = bancoDados.listarNomesProvasPorTurma(String.valueOf(id_turmaBD));
            if (listProvas == null){
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                return;
            }

            int indexProva = listProvas.indexOf(nameProva);
            SpinnerAdapter adapterProva = new SpinnerAdapter(this, listProvas);
            spinnerProva.setAdapter(adapterProva);
            spinnerProva.postDelayed(() -> {
                if (indexProva != -1) {
                    spinnerProva.setSelection(indexProva);
                }
            }, 200);


            // ============ Lista todos os alunos pertencentes a turma selecionada =======================================
            listAlunos = bancoDados.listarAlunosPorTurma(id_turmaBD.toString());
            if (listAlunos == null){
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                return;
            }
            listAlunos.add(0, "Todos");
            SpinnerAdapter adapterAluno = new SpinnerAdapter(this, listAlunos);
            spinnerAluno.setAdapter(adapterAluno);
        }

        spinnerTurma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    try {
                        className = spinnerTurma.getSelectedItem().toString();
                        id_turmaBD = bancoDados.pegarIdTurma(className);
                        if (id_turmaBD == null) {
                            Toast.makeText(GenerateCardsActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        listProvas = bancoDados.listarNomesProvasPorTurma(String.valueOf(id_turmaBD));
                        if (listProvas == null) {
                            Toast.makeText(GenerateCardsActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SpinnerAdapter adapterProva = new SpinnerAdapter(GenerateCardsActivity.this, listProvas);
                        spinnerProva.setAdapter(adapterProva);

                        listAlunos = bancoDados.listarAlunosPorTurma(id_turmaBD.toString());
                        if (listAlunos == null) {
                            Toast.makeText(GenerateCardsActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        listAlunos.add(0, "Todos");
                        SpinnerAdapter adapterAluno = new SpinnerAdapter(GenerateCardsActivity.this, listAlunos);
                        spinnerAluno.setAdapter(adapterAluno);
                    } catch (Exception e){
                        Log.e("kariti", e.toString());
                        Toast.makeText(GenerateCardsActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    int indexClass = listTurmas.indexOf(className);
                    spinnerTurma.setSelection(indexClass);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btnGenerateCards.setOnClickListener(v -> {
            btnGenerateCards.setEnabled(false);
            try {
                solicitaPermissaoNotificacao();
                if(spinnerProva.getSelectedItem() != null) {
                    nameProva = spinnerProva.getSelectedItem().toString();
                    //String aluno = spinnerAluno.getSelectedItem().toString();
                    id_provaBD = bancoDados.pegarIdProvaPorTurma(nameProva, id_turmaBD);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                        solicitaPermissao();
                    }else {
                        generateCard();
                    }

                }else Toast.makeText(this, "Selecione os dados", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Log.e("kariti",e.getMessage());
                Toast.makeText(this, "Ocorreu uma falha de comunicação no Kariti! \n\n Por favor, tente novamente.", Toast.LENGTH_SHORT).show();
            }finally {
                btnGenerateCards.setEnabled(true);
            }

        });
        toGoBack.setOnClickListener(view -> finish());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void generateCard(){
        prova = new Prova(id_provaBD, bancoDados);
        CreatCard creatCard = new CreatCard(prova, bancoDados, this);
        if (creatCard.creatPdfCard()){
            infoDownloadCard();
        } else {
            notifyFailureDownload();
        }
    }

    private void solicitaPermissaoNotificacao(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101); // Código de solicitação
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) { // Verifica se o código de solicitação é o esperado
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisão concedida com sucesso", Toast.LENGTH_SHORT).show();
                Log.d("Permissão", "Permissão WRITE_EXTERNAL_STORAGE concedida.");
                generateCard();
            } else {
                // Permissão negada
                Log.d("Permissão", "Permissão WRITE_EXTERNAL_STORAGE negada.");
                permissaoNegada();
                // Informe ao usuário que a permissão é necessária ou tome uma ação adequada
            }
        }
        if (requestCode == 101){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão concedida!", Toast.LENGTH_SHORT).show();
            } else {
                // Permissão negada, exiba uma mensagem explicativa ao usuário
                permissaoDNotificacaoNegada();
            }
        }
    }
    public void permissaoNegada(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Para realizar o download dos cartões resposta em seu dispositivo, é necessário que conceda a permissão ao Kariti! .");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    public void permissaoDNotificacaoNegada(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("O Kariti não será capaz de notifica-lo sobre os downloads realizados! .");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void solicitaPermissao(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else{
            generateCard();
        }
    }
    private void infoDownloadCard(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Cartões gerados com sucesso");
        builder.setMessage("Os cartões respostas foram gerados e estão disponíveis na pasta de downloads do seu dispositivo.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });

        builder.show();
    }
    private void notifyFailureDownload(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("KARITI");
        builder.setMessage("Ocorreu uma falha ao tentar gerar os cartões dessa prova, se a falha persistir: \n" +
                "1 - Verifique se possui armazenamento diponível para realização de downloads" +
                "2 - Reinicie o Kariti!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}