package online.padev.kariti;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

import online.padev.kariti.cards.CorrectionReportCard;
import online.padev.kariti.entity.Gabarito;
import online.padev.kariti.entity.Prova;
import online.padev.kariti.entity.Student;
import online.padev.kariti.database.DataBaseKariti;

public class ProvaCorrectedActivity extends AppCompatActivity {
    ImageButton toGoBack;
    Button btnGenerateCorrectionReport;
    List<String> answersGiven;
    String className, filePdf;
    TextView txtViewProva;
    List<Gabarito> gabarito;
    List<Student> students;
    Prova prova;
    TextView title;
    DataBaseKariti dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_corrected);

        toGoBack = findViewById(R.id.imgBtnVoltar);
        btnGenerateCorrectionReport = findViewById(R.id.buttonBaixarResultado);
        txtViewProva = findViewById(R.id.textViewProvaResult);
        title = findViewById(R.id.toolbar_title);

        dataBase = new DataBaseKariti(this);

        title.setText(String.format("%s","Provas Corrigidas"));

        prova = (Prova) getIntent().getSerializableExtra("prova");

        className = dataBase.pegarNomeTurma(prova.getId_class().toString());

        txtViewProva.setText(String.format("%s","Prova: "+prova.getNameProva()));

        gabarito = dataBase.listarDadosGabarito(prova.getId_prova());
        students = dataBase.listStudentExamCorrect(prova.getId_class());

        if (gabarito == null || students == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 7", Toast.LENGTH_SHORT).show();
            finish();
        }

        ShapeDrawable border = new ShapeDrawable(new RectShape());
        border.getPaint().setColor(0xFF000000); // Cor da borda
        border.getPaint().setStrokeWidth(1); // Largura da borda
        border.getPaint().setStyle(Paint.Style.STROKE);

        for(Student student : students) { // interage sob esses alunos
            float nota = 0;
            int acertos = 0;
            Boolean checkStatusCorrection = dataBase.verificaSituacaoCorrecao(prova.getId_prova(), student.getId_student(), -1);
            if(checkStatusCorrection == null){
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 8", Toast.LENGTH_SHORT).show();
                finish();
            }
            answersGiven = dataBase.listarRespostasDadas(prova.getId_prova(), student.getId_student()); // lista as respostas dos alunos em formato de letras
            incrementResponse(); //caso quantidade de respostadas dadas, menor que o esperado, incrementa!
            if(!checkStatusCorrection) {
                for (int i = 0; i < prova.getNumQuestions(); i++) {
                    Gabarito g = gabarito.get(i);
                    char correctResponse = (char) ('A' + g.getResponse() - 1);
                    if (String.valueOf(correctResponse).equals(answersGiven.get(i))) {
                        nota += g.getNote();
                        acertos += 1;
                    }
                }
            }else{
                nota = -1;
                acertos = -1;
            }

            TableLayout tableLayout = findViewById(R.id.tableLayout);
            TableRow row = new TableRow(this);
            row.setBackground(border);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);

            // Cria uma célula para a nova linha para armazenar nome do aluno
            TextView cell1 = new TextView(this);
            String nomeAlunoEdit = editNameStudent(student.getNameStudent());
            cell1.setText(String.format("  %s", nomeAlunoEdit));
            //cell1.setGravity(Gravity.CENTER);
            row.addView(cell1);

            // Cria outra célula para a nova linha para armazenar o total de acertos do aluno na prova
            TextView cell2 = new TextView(this);
            if(acertos != -1){
                cell2.setText(String.valueOf(acertos));
            }else{
                cell2.setText("-");}
            cell2.setGravity(Gravity.CENTER);
            cell2.setTextSize(16);
            row.addView(cell2);

            // Cria outra célula para a nova linha para armazenar a nota total do aluno
            TextView cell3 = new TextView(this);
            if(nota != -1){
                cell3.setText(String.valueOf(nota));
            }else{
                cell3.setText("-");
            }
            cell3.setGravity(Gravity.CENTER);
            cell3.setTextSize(16);
            row.addView(cell3);

            // Cria outra célula para a nova linha com botão para exibir detalhamento da nota do aluno
            Button cell4 = new Button(this);
            cell4.setId(student.getId_student());
            cell4.setText(String.format("%s","VER"));
            cell4.setGravity(Gravity.CENTER);
            cell4.setPadding(0,0,0,0);
            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT, // Largura ajustável ao conteúdo
                    TableRow.LayoutParams.WRAP_CONTENT  // Altura ajustável ao conteúdo
            );
            cell4.setLayoutParams(params);
            row.addView(cell4);

            // Adiciona a nova linha à tabela
            tableLayout.addView(row);

            cell4.setOnClickListener(v -> {
                Boolean checkStatusCorrection2 = dataBase.verificaSituacaoCorrecao(prova.getId_prova(), v.getId(), -1);
                if(checkStatusCorrection2 == null){
                    Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 8", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!checkStatusCorrection2){
                    Intent intent = new Intent(this, ProvaCorrectedStudentActivity.class);
                    intent.putExtra("id_aluno", v.getId());
                    intent.putExtra("id_prova", prova.getId_prova());
                    startActivity(intent);
                }else{
                    notifyCorrectionNonExistent();
                }

            });
        }
        btnGenerateCorrectionReport.setOnClickListener(v -> {
            btnGenerateCorrectionReport.setEnabled(false);
            requestPermissionNotify();
            try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                    requestPermissionStorage();
                }else {
                    dialogGenerateReport();
                }

            }catch (Exception e) {
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 8", Toast.LENGTH_SHORT).show();
            } finally {
                btnGenerateCorrectionReport.setEnabled(true);
            }

        });
        toGoBack.setOnClickListener(view -> {
           finish();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
    private void dialogGenerateReport(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Deseja gerar o relatório de correção de:")
                .setItems(new String[]{"Todos os alunos", "Somente de alunos com prova corrigida"}, (dialog, which) -> {
                    if (which == 0){
                       generateCorrectionReport(0);
                    } else if (which == 1){
                        generateCorrectionReport(1);
                    }

                });
        builder.show();
    }



    private void generateCorrectionReport(int typeReport){
        CorrectionReportCard createReport = new CorrectionReportCard(this, dataBase, prova.getId_prova());
        boolean requestStatus = createReport.generateCorrectionReport(typeReport);
        if (requestStatus){
            notifySucessDownload();
        } else {
            notifyFailureDownload();
        }

    }
    private String editNameStudent(String aluno){
        String[] separa = aluno.trim().split("\\s+");
        //String novoNome = "";
        if(separa.length > 2) {
            return separa[0] + " " + separa[separa.length - 1];
        }else{
            return aluno;
        }
    }
    private void incrementResponse(){
        int numRespostas = answersGiven.size(); //Quantidade de respostas cadastradas no BD
        if (numRespostas < prova.getNumQuestions()){ //Caso quantidade de respostas dadas menor q de questões
            for (int a = numRespostas; a < prova.getNumQuestions(); a++){
                answersGiven.add(a, "-"); //Aumenta o tamanho da lista até o tamanho da questões
            }
        }
    }
    public void PopMenu(View v){
        v.setOnClickListener(view -> Toast.makeText(ProvaCorrectedActivity.this, "Preparado para implementação", Toast.LENGTH_SHORT).show());
    }


    public void notifyCorrectionNonExistent(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Esta prova não foi corrigida. Veja algumas das causas que podem ter colaborado para este resultado:\n\n" +
                "• Ambiente com pouca luminosidade\n\n" +
                "• Imagem ofuscada\n\n" +
                "• Cartão resposta Rasurado");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) { // Verifica se o código de solicitação é o esperado
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisão concedida com sucesso", Toast.LENGTH_SHORT).show();
                Log.d("Permissão", "Permissão WRITE_EXTERNAL_STORAGE concedida.");
                dialogGenerateReport();
            } else {
                // Permissão negada
                Log.d("Permissão", "Permissão WRITE_EXTERNAL_STORAGE negada.");
                notifyPermissionStorageDenied();
                // Informe ao usuário que a permissão é necessária ou tome uma ação adequada
            }
        }
        if (requestCode == 101){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão concedida!", Toast.LENGTH_SHORT).show();
            } else {
                // Permissão negada, exiba uma mensagem explicativa ao usuário
                notifyPermissionToNotifyDenied();
            }
        }
    }
    public void notifyPermissionStorageDenied(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Para realizar o download do resultado de correção em seu dispositivo, é necessário que conceda permissão ao Kariti! .");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    public void notifyPermissionToNotifyDenied(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("O Kariti não será capaz de notifica-lo sobre os downloads realizados! .");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void requestPermissionNotify(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101); // Código de solicitação
            }
        }
    }
    private void requestPermissionStorage(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else{
            dialogGenerateReport();
        }
    }
    private void notifyFailureDownload(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("KARITI");
        builder.setMessage("Ocorreu uma falha ao tentar gerar o relatório dessa prova, se a falha persistir: \n\n" +
                "1 - Verifique se possui armazenamento diponível para realização de downloads \n\n" +
                "2 - Reinicie o Kariti");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void notifySucessDownload(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Relatório Gerado");
        builder.setMessage("Um relatório de correção dessa prova foi gerado e está disponível na pasta de downloads do seu dispositivo!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

}