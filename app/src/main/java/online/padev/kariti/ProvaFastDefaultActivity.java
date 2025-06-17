package online.padev.kariti;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import online.padev.kariti.entity.Answer_key;
import online.padev.kariti.entity.Exam;
import pl.droidsonroids.gif.GifImageView;

public class ProvaFastDefaultActivity extends AppCompatActivity {

    ImageButton back, iconHelp;
    Button btnNewCard, btnCorrect;
    TextView textViewTitle;
    GifImageView gifLoading;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ActivityLocale.wrap(newBase));
    }

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

        textViewTitle.setText(getString(R.string.textTitleQuickTest));

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
        if (Answer_key.answerkeyDefault.isEmpty()){
            notifyCorrectionOrganization();
        }else {
            notifyGabaritoSave();
        }
    }

    private void outputController(){
        if (Answer_key.answerkeyDefault != null && !Answer_key.answerkeyDefault.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.titleAttention))
                    .setMessage(getString(R.string.longTextAnswerKeyRegister))
                    .setPositiveButton(getString(R.string.yes_description), (dialog, which) -> {
                        dialog.dismiss();
                        Answer_key.answerkeyDefault.clear();
                        Exam.numQuestsDefault = 0;
                        Exam.numAlternativesDefault = 0;
                        finish();
                    })
                    .setNegativeButton(getString(R.string.not_description), (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else
            finish();
    }

    private void help() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.titleHelp));
        builder.setMessage(getString(R.string.longTextHelpQuickTest));
        builder.setPositiveButton(getString(R.string.okDescription), (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void notifyCorrectionOrganization(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.titleAttention));
        builder.setMessage(getString(R.string.longTextOrientationCapture));
        builder.setPositiveButton(getString(R.string.okDescription), (dialog, which) -> startCorrection());
        builder.show();
    }

    private void notifyGabaritoSave(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.titleAttention));
        builder.setMessage(getString(R.string.longTextAnswerKeyIfAlter));
        builder.setPositiveButton(getString(R.string.alterarDescription), (dialog, which) -> {
            Answer_key.answerkeyDefault.clear();
            Exam.numQuestsDefault = 0;
            Exam.numAlternativesDefault = 0;
            Toast.makeText(this, getString(R.string.toastCaptureNewCard), Toast.LENGTH_SHORT).show();
            startCorrection();
        });
        builder.setNegativeButton(getString(R.string.manterDescription), ((dialog, which) -> startCorrection()));
        builder.show();
    }
}