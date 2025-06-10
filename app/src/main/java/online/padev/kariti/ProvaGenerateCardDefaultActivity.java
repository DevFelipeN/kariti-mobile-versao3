package online.padev.kariti;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import online.padev.kariti.cards.CreatCard;
import online.padev.kariti.entity.Exam;
import pl.droidsonroids.gif.GifImageView;

public class ProvaGenerateCardDefaultActivity extends AppCompatActivity {

    EditText qtdQuest, qtdAlter, provaName, className, teacherName;
    Calendar calendar;
    Button datePickerButton, btnGenerateCard;
    ImageButton btnVoltar, questMenos, questMais, altMais, altMenos, iconHelp;
    TextView textViewTitle;
    Exam exam;
    GifImageView gifLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_generate_card_default);

        btnVoltar = findViewById(R.id.imgBtnVoltaDescola);
        iconHelp = findViewById(R.id.iconHelp);
        provaName = findViewById(R.id.editTextNameProva);
        className = findViewById(R.id.editTextNameClass);
        teacherName = findViewById(R.id.editTextNameTeacher);
        datePickerButton = findViewById(R.id.datePickerNewCard);
        questMais = findViewById(R.id.imgButtonMaisQuestNewCard);
        questMenos = findViewById(R.id.imgButtonMenosQuestNewCard);
        altMais = findViewById(R.id.imgBtnMaisAlterNewCard);
        altMenos = findViewById(R.id.imgBtnMenoAlterNewCard);
        qtdQuest = findViewById(R.id.editTextQtdQuestNewCard);
        qtdAlter = findViewById(R.id.editTextQtdAlterNewCard);
        btnGenerateCard = findViewById(R.id.btnNewCard);
        textViewTitle = findViewById(R.id.toolbar_title);
        gifLoading = findViewById(R.id.loadingId);

        textViewTitle.setText(String.format("%s", "Nova Prova"));

        btnVoltar.setOnClickListener(v -> finish());
        iconHelp.setOnClickListener(v -> help());

        questMais.setOnClickListener(v -> {
            int quest = 0;
            if (!qtdQuest.getText().toString().trim().isEmpty()){
                quest = Integer.parseInt(qtdQuest.getText().toString());
            }
            if(quest < 20)
                quest ++;
            qtdQuest.setText(String.valueOf(quest));
        });
        questMenos.setOnClickListener(v -> {
            int quest = 0;
            if (!qtdQuest.getText().toString().trim().isEmpty()){
                quest = Integer.parseInt(qtdQuest.getText().toString());
            }
            if(quest > 0)
                quest --;
            qtdQuest.setText(String.valueOf(quest));
        });
        altMais.setOnClickListener(v -> {
            int alter = 0;
            if (!qtdAlter.getText().toString().trim().isEmpty()) {
                alter = Integer.parseInt(qtdAlter.getText().toString());
            }
            if(alter < 6)
                alter ++;
            qtdAlter.setText(String.valueOf(alter));
        });
        altMenos.setOnClickListener(v -> {
            int alter = 0;
            if (!qtdAlter.getText().toString().trim().isEmpty()) {
                alter = Integer.parseInt(qtdAlter.getText().toString());
            }
            if(alter > 0)
                alter --;
            qtdAlter.setText(String.valueOf(alter));
        });

        calendar = Calendar.getInstance();
        datePickerButton.setOnClickListener(v -> {
            // Cria um DatePickerDialog com a data atual
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // Atualiza a data no calendário quando o usuário seleciona uma nova data
                        calendar.set(year, monthOfYear, dayOfMonth);
                        // Atualiza o texto do botão com a data selecionada
                        datePickerButton.setText(formatDateToDisplay(calendar));
                        //dataform = formatDateBanco(calendar);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            // Exibe o DatePickerDialog
            datePickerDialog.show();
        });

        btnGenerateCard.setOnClickListener(v -> {
            solicitaPermissaoNotificacao();
            btnGenerateCard.setEnabled(false);
            try {
                if (qtdQuest.getText().toString().trim().isEmpty() || qtdQuest.getText().toString().equals("0")) {
                    Toast.makeText(this, "Informe a quantidade de questões!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (qtdAlter.getText().toString().trim().isEmpty() || qtdAlter.getText().toString().equals("0")) {
                    Toast.makeText(this, "Informe a quantidade de alternativas!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Integer.parseInt(qtdQuest.getText().toString()) > 20) {
                    dialogLimitMaxQuest();
                    return;
                }
                if (Integer.parseInt(qtdAlter.getText().toString()) > 6) {
                    dialogLimitMaxAlter();
                    return;
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    solicitaPermissao();
                } else {
                    generateCard();
                }
            } catch (Exception e) {
                Log.e("kariti", e.toString());
            } finally {
                btnGenerateCard.setEnabled(true);
                //finish();
            }
        });
    }
    private void generateCard(){
        View overlayView = findViewById(R.id.overlayView);
        overlayView.setVisibility(View.VISIBLE);
        gifLoading.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                exam = new Exam();
                exam.setNameExam(provaName.getText().toString().trim());
                exam.setExam_id(Integer.parseInt(qtdQuest.getText().toString()));
                exam.setNumQuestions(Integer.parseInt(qtdQuest.getText().toString()));
                exam.setNumAlternatives(Integer.parseInt(qtdAlter.getText().toString()));
                if (!datePickerButton.getText().toString().equals("Selecionar Data")) {
                    exam.setDateExam(datePickerButton.getText().toString());
                }
                CreatCard creatCard = new CreatCard(exam, teacherName.getText().toString(), className.getText().toString(), this);
                boolean generateSituation = creatCard.creatPdfCard();
                if (generateSituation) {
                    runOnUiThread(this::infoDownloadCard);
                } else {
                    runOnUiThread(this::notifyFailureDownload);
                }
            } catch (Exception e){
                Log.e("kariti", e.toString());
            } finally {
                runOnUiThread(() -> {
                    gifLoading.setVisibility(View.GONE);
                    overlayView.setVisibility(View.GONE);
                });
            }
        }).start();
    }

    private String formatDateToDisplay(Calendar calendar) {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    private void dialogLimitMaxQuest(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Atualmente o Kariti oferece suporte para cartões repostas com no máximo 20 questões!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void dialogLimitMaxAlter(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Atualmente o Kariti oferece suporte para cartões repostas com no máximo 6 alternativas!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void solicitaPermissaoNotificacao(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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

    private void permissaoDNotificacaoNegada(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("O Kariti não será capaz de notifica-lo sobre os downloads realizados!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void permissaoNegada(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Para realizar o download dos cartões resposta em seu dispositivo, é necessário que conceda a permissão ao Kariti!");
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
        if(!isFinishing() && !isDestroyed()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Cartão gerado com sucesso");
            builder.setMessage("O cartão resposta foi gerado e está disponível na pasta de downloads do seu dispositvo.\n\n" +
                    "Você pode realizar impressão da quantidade de cartões necessários a partir do cartão que acabou de ser gerado!");
            builder.setPositiveButton("OK", (dialog, which) -> {
                dialog.dismiss();
                orientation();
            });
            builder.show();
        }
    }
    private void notifyFailureDownload(){
        if(!isFinishing() && !isDestroyed()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("KARITI");
            builder.setMessage("Ocorreu uma falha ao tentar gerar o cartão resposta, se a falha persistir: \n" +
                    "1 - Verifique se possui armazenamento diponível para realização de downloads" +
                    "2 - Reinicie o Kariti!");
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
        }
    }
    private void orientation(){
        if(!isFinishing() && !isDestroyed()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("ORIENTAÇÃO!");
            builder.setMessage("O preenchimento dos cartões-respostas deve ser feito com caneta de cor escura, preferencialmente de cor preta.");
            builder.setPositiveButton("OK", (dialog, which) -> {
                dialog.dismiss();
                finish();
            });

            builder.show();
        }
    }

    private void help() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Nessa sessão você pode gerar cartões de resposta. \n\n" +
                "• Para gerar um cartão de resposta basta informar a quantidade de questões e alternativas de sua prova.\n\n" +
                "• Informações como nome da prova, nome da turma, nome do professor e data, são opcionais.\n\n" +
                "• Após solicitada a ação de Gerar Cartão, o Kariti realiza o download do cartão de resposta na pasta de downlods do dispositivo.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}