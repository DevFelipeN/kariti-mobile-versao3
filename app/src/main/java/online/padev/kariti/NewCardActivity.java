package online.padev.kariti;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import online.padev.kariti.dao.Prova;

public class NewCardActivity extends AppCompatActivity {

    EditText qtdQuest, qtdAlter, provaName, className, teacherName;
    Calendar calendar;
    Button datePickerButton, btnGenerateCard;
    ImageButton btnVoltar, questMenos, questMais, altMais, altMenos;
    TextView textViewTitle;
    Prova prova;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_card);

        btnVoltar = findViewById(R.id.imgBtnVoltaDescola);
        provaName = findViewById(R.id.editTextNameProva);
        className = findViewById(R.id.editTextNameClass);
        teacherName = findViewById(R.id.editTextNameTeacher);
        datePickerButton = findViewById(R.id.datePickerNewCard);
        questMais = findViewById(R.id.imageButtonMaisQuest);
        questMenos = findViewById(R.id.imageButtonMenosQuest);
        altMais = findViewById(R.id.imgBtnMaisAlter);
        altMenos = findViewById(R.id.imgBtnMenoAlter);
        qtdQuest = findViewById(R.id.editTextQtdQuests);
        qtdAlter = findViewById(R.id.editTextQtdAlters);
        btnGenerateCard = findViewById(R.id.btnNewCard);
        textViewTitle = findViewById(R.id.toolbar_title);

        textViewTitle.setText(String.format("%s", "KARITI"));

        btnVoltar.setOnClickListener(v -> Toast.makeText(this, "Voltar clicado!", Toast.LENGTH_SHORT).show());

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
            if(provaName.getText().toString().trim().isEmpty()){
                Toast.makeText(this, "Informe o nome da prova!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(qtdQuest.getText().toString().trim().isEmpty() || qtdQuest.getText().toString().equals("0")){
                Toast.makeText(this, "Informe a quantidade de questões!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(qtdAlter.getText().toString().trim().isEmpty() || qtdAlter.getText().toString().equals("0")){
                Toast.makeText(this, "Informe a quantidade de alternativas!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Integer.parseInt(qtdQuest.getText().toString()) > 20){
                dialogLimitMaxQuest();
                return;
            }
            if (Integer.parseInt(qtdAlter.getText().toString()) > 6) {
                dialogLimitMaxAlter();
                return;
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                solicitaPermissao();
            }else {
                generateCard();
            }

        });
    }
    private void generateCard(){
        prova = new Prova();
        prova.setNomeProva(provaName.getText().toString());
        prova.setId_prova(Integer.parseInt(qtdQuest.getText().toString()));
        prova.setNumQuestoes(Integer.parseInt(qtdQuest.getText().toString()));
        prova.setNumAlternativas(Integer.parseInt(qtdAlter.getText().toString()));
        if (!datePickerButton.getText().toString().equals("Selecionar Data")){
            prova.setDataProva(datePickerButton.getText().toString());
        }
        CreatCard creatCard = new CreatCard(prova, teacherName.getText().toString(), className.getText().toString(), this);
        if (creatCard.creatPdfCard()){
            infoDownloadCard();
        } else {
            Toast.makeText(this, "Falha na geração do cartão resposta!", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatDateToDisplay(Calendar calendar) {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    public void dialogLimitMaxQuest(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Atualmente o Kariti oferece suporte para cartões repostas com no máximo 20 questões!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void dialogLimitMaxAlter(){
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
    public void permissaoDNotificacaoNegada(){
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cartão gerado com sucesso");
        builder.setMessage("O cartão resposta foi gerado e está disponível na pasta de downloads do seu dispositvo.\n\n" +
                "Você pode realizar impressão da quantidade de cartões necessários a partir do cartão que acabou de ser gerado!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}