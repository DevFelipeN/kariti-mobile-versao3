package online.padev.kariti;

import static online.padev.kariti.utils.CompactImage.controllerImageOrig;
import static online.padev.kariti.utils.CompactImage.controllerImageWarp;
import static online.padev.kariti.utils.CompactImage.controllerImageWarpPaint;
import static online.padev.kariti.utils.ZIpDirectory.createDirectoryZip;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import online.padev.kariti.emails.SendImageTester;
import online.padev.kariti.entity.Gabarito;
import online.padev.kariti.entity.Prova;
import online.padev.kariti.utils.CompactImage;
import pl.droidsonroids.gif.GifImageView;

public class ProvaFastDefaultActivity extends AppCompatActivity {

    ImageButton back, iconHelp;
    Button btnNewCard, btnCorrect;
    TextView textViewTitle;
    GifImageView gifLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_fast_default);

        back = findViewById(R.id.imgBtnVoltaDescola);
        iconHelp = findViewById(R.id.iconHelp);
        textViewTitle = findViewById(R.id.toolbar_title);
        btnNewCard = findViewById(R.id.buttonNewProvaRapida);
        btnCorrect = findViewById(R.id.buttonCorrigi);
        gifLoading = findViewById(R.id.loadingId);

        textViewTitle.setText(String.format("%s", "Prova Rápida"));

        btnNewCard.setOnClickListener(v -> startNewCard());
        btnCorrect.setOnClickListener(v -> controllerCorrection());
        iconHelp.setOnClickListener(v -> help());
        

        back.setOnClickListener(v -> outputController());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                outputController();
            }
        });

    }
    private void startNewCard(){
        Intent intent = new Intent(this, ProvaGenerateCardDefaultActivity.class);
        startActivity(intent);
    }
    private void startCorrection(){
        Intent intent = new Intent(getApplicationContext(), CameraxAndOpencvActivity.class);
        startActivity(intent);
    }

    private void controllerCorrection(){
        if (Gabarito.gabaritoDefault.isEmpty()){
            notifyCorrectionOrganization();
        }else {
            notifyGabaritoSave();
        }
    }

    private void outputController(){
        if (Gabarito.gabaritoDefault != null && !Gabarito.gabaritoDefault.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ATENÇÃO!")
                    .setMessage("Você possui um gabarito criado, ao confirmar essa ação o gabarito será perdido!\n\n" +
                            "Deseja realmente sair?")
                    .setPositiveButton("SIM", (dialog, which) -> {
                        dialog.dismiss();
                        saveImagesTester();
                        //Gabarito.gabaritoDefault.clear();
                        //Prova.numQuestsDefault = 0;
                        //Prova.numAlternativesDefault = 0;
                        //finish();
                    })
                    .setNegativeButton("NÃO", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else if (!controllerImageWarp.isEmpty()) {
            saveImagesTester();
        } else {
            finish();
        }
    }

    private void help() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Bem vindo(a) ao menu inicial de prova rápida. \n Aqui você pode gerar cartões respostas e corrigir seus cartões de maneira fácil e prática! \n\n" +
                "• Na primeira opção você pode gerar um cartão, informando a quantidade de questões e alternativas da sua prova, em seguida imprimir a quantidade necessária para os alunos.\n\n" +
                "• Na segunda opção você pode realizar a correção do cartão gerado na opção anterior, apenas apontando a câmera do Kariti para o cartão que por sua vez realiza a correção e exibe o resultado na sua tela.\n\n" +
                "• Ao início de cada seção de correção será solicitado o preenchimento do gabarito da sua prova, com ele preenchido todos os cartões referentes a ele podem ser corrigidos sequencialmente. ");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void notifyCorrectionOrganization(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Capture a imagem do cartão de cima, sobre superfície plana e com boa luminosidade");
        builder.setPositiveButton("OK", (dialog, which) -> startCorrection());
        builder.show();
    }

    private void notifyGabaritoSave(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Você pussui um gabarito cadastrado. Deseja manter ou alterar o gabarito?");
        builder.setPositiveButton("Alterar", (dialog, which) -> {
            Gabarito.gabaritoDefault.clear();
            Prova.numQuestsDefault = 0;
            Prova.numAlternativesDefault = 0;
            Toast.makeText(this, "Capture o novo cartão!", Toast.LENGTH_SHORT).show();
            startCorrection();
        });
        builder.setNegativeButton("Manter", ((dialog, which) -> startCorrection()));
        builder.show();
    }

    private void saveImagesTester(){
        View overlayView = findViewById(R.id.overlayView);
        overlayView.setVisibility(View.VISIBLE);
        gifLoading.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                if (!controllerImageOrig.isEmpty()) {
                    String nameFileZip = "Prova"+Prova.numQuestsDefault + "_" + Prova.numAlternativesDefault + dataHoraAtual();
                    File fileZip = createDirectoryZip(nameFileZip, this);
                    File fileImages = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CameraXopenCV");
                    boolean isCompact = CompactImage.compact(fileImages, fileZip.getAbsolutePath());
                    if (isCompact){
                        Log.e("testerV3", nameFileZip+" compactado com sucesso!");
                        boolean isSend = SendImageTester.sendInZip(fileZip, nameFileZip+".zip");
                        if (isSend){
                            Log.e("testerV3", nameFileZip+" Enviado com sucesso!");
                        }else {
                            Log.e("testerV3", " Erro no envio das imagens!");
                        }
                    }else {
                        Log.e("testerV3", " Erro na compactação das imagens!");
                    }

                } else {
                    finish();
                }
            } catch (Exception e){
                Log.e("testerV3", " Erro (Exception) na compactação ou envio das imagens!");
            } finally {
                runOnUiThread(() -> { // Atualiza a UI na Main Thread
                    controllerImageOrig.clear();
                    controllerImageWarpPaint.clear();
                    controllerImageWarp.clear();
                    Gabarito.gabaritoDefault.clear();
                    Prova.numQuestsDefault = 0;
                    Prova.numAlternativesDefault = 0;
                    gifLoading.setVisibility(View.GONE); // Esconde o GIF quando o processo termina
                    dialogFinallyTester();
                });
            }
        }).start();
    }

    private String dataHoraAtual(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault());
        Date date = new Date();
        return sdf.format(date);
    }
    private void dialogFinallyTester() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Teste Ativo!");
        builder.setMessage("As imagens desse teste foram salvas e enviadas ao seu e-mail.\n\n" +
                "Por favor verifique se recebeu!! ");
        builder.setPositiveButton("OK", (dialog, which) -> finish());
        builder.show();
    }
}