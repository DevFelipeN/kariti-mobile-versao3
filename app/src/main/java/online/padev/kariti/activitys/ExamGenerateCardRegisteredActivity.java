package online.padev.kariti.activitys;

import android.Manifest;
import android.content.Context;
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

import online.padev.kariti.R;
import online.padev.kariti.adapters.AdapterSpinner;
import online.padev.kariti.cards.CreatCard;
import online.padev.kariti.entity.Exam;
import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.settings.ActivityLocale;
import pl.droidsonroids.gif.GifImageView;

public class ExamGenerateCardRegisteredActivity extends AppCompatActivity {
    ImageButton toGoBack;
    Button btnGenerateCard;
    Integer id_ClassBD, address, exam_idBD;
    String nameExam, nameClass;
    List<String> listExams, listClass, listStudent;
    DataBaseKariti dataBaseKariti;
    Spinner spinnerClass, spinnerExam, spinnerStudent;
    AdapterSpinner adapterClass, adapterExam, adapterStudent;
    TextView title;
    Exam exam;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ActivityLocale.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_generate_card_registered);

        toGoBack = findViewById(R.id.imgBtnVoltar);
        spinnerClass = findViewById(R.id.spinnerTurma);
        spinnerExam = findViewById(R.id.spinnerProva);
        spinnerStudent = findViewById(R.id.spinnerAlunos);
        btnGenerateCard = findViewById(R.id.baixarcatoes);
        title = findViewById(R.id.toolbar_title);

        dataBaseKariti = new DataBaseKariti(this);

        title.setText(getString(R.string.titleDownloadCards));

        address = Objects.requireNonNull(getIntent().getExtras()).getInt("endereco");

        listClass = dataBaseKariti.listClassByExam();
        if(listClass == null){
            Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            return;
        }
        listClass.add(0,getString(R.string.titleSelectClass));

        if(address.equals(2)){ //para quando a activity que a chamou foi ProvaActivity
            adapterClass = new AdapterSpinner(this, listClass);
            spinnerClass.setAdapter(adapterClass);

        }else if(address.equals(1)) { //para quando a activity que chamou for Gabarito
            id_ClassBD = getIntent().getExtras().getInt("id_turma");
            nameClass = dataBaseKariti.getClassName(String.valueOf(id_ClassBD));
            nameExam = getIntent().getExtras().getString("prova");
            if (nameClass == null){
                Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                return;
            }

            int indexClass = listClass.indexOf(nameClass); // Identifica a posicão da turma na lista
            adapterClass = new AdapterSpinner(this, listClass);
            spinnerClass.setAdapter(adapterClass);
            if (indexClass != -1){
                spinnerClass.setSelection(indexClass);
            }

            //============ Lista todas provas pertecentes a turma selecionada =======================
            listExams = dataBaseKariti.listExamNames(String.valueOf(id_ClassBD));
            if (listExams == null){
                Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                return;
            }

            int indexExam = listExams.indexOf(nameExam);
            adapterExam = new AdapterSpinner(this, listExams);
            spinnerExam.setAdapter(adapterExam);
            spinnerExam.postDelayed(() -> {
                if (indexExam != -1) {
                    spinnerExam.setSelection(indexExam);
                }
            }, 200);


            // ============ Lista todos os alunos pertencentes a turma selecionada =======================================
            listStudent = dataBaseKariti.listStudentNames(id_ClassBD.toString());
            if (listStudent == null){
                Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                return;
            }
            listStudent.add(0, getString(R.string.descriptionAll));
            adapterStudent = new AdapterSpinner(this, listStudent);
            spinnerStudent.setAdapter(adapterStudent);
        }

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    try {
                        nameClass = spinnerClass.getSelectedItem().toString();
                        id_ClassBD = dataBaseKariti.getClassId(nameClass);
                        if (id_ClassBD == null) {
                            Toast.makeText(ExamGenerateCardRegisteredActivity.this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        listExams = dataBaseKariti.listExamNames(String.valueOf(id_ClassBD));
                        if (listExams == null) {
                            Toast.makeText(ExamGenerateCardRegisteredActivity.this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        adapterExam = new AdapterSpinner(ExamGenerateCardRegisteredActivity.this, listExams);
                        spinnerExam.setAdapter(adapterExam);

                        listStudent = dataBaseKariti.listStudentNames(id_ClassBD.toString());
                        if (listStudent == null) {
                            Toast.makeText(ExamGenerateCardRegisteredActivity.this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        listStudent.add(0, getString(R.string.descriptionAll));
                        adapterStudent = new AdapterSpinner(ExamGenerateCardRegisteredActivity.this, listStudent);
                        spinnerStudent.setAdapter(adapterStudent);
                    } catch (Exception e){
                        Log.e("kariti", e.toString());
                        Toast.makeText(ExamGenerateCardRegisteredActivity.this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    int indexClass = listClass.indexOf(nameClass);
                    spinnerClass.setSelection(indexClass);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btnGenerateCard.setOnClickListener(v -> {
            btnGenerateCard.setEnabled(false);
            try {
                requestsPermissionNotify();
                if(spinnerExam.getSelectedItem() != null) {
                    nameExam = spinnerExam.getSelectedItem().toString();
                    //String aluno = spinnerAluno.getSelectedItem().toString();
                    exam_idBD = dataBaseKariti.getExamId(nameExam, id_ClassBD);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                        requestsPermission();
                    }else {
                        generateCard();
                    }

                }else Toast.makeText(this, getString(R.string.toastSelectDataS), Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Log.e("kariti",e.getMessage());
                Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
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
        View overlayView = findViewById(R.id.overlayView);
        GifImageView gifLoading = findViewById(R.id.loadingId);
        overlayView.setVisibility(View.VISIBLE);
        gifLoading.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                exam = new Exam(exam_idBD, dataBaseKariti);
                CreatCard creatCard = new CreatCard(exam, dataBaseKariti, this);
                if (creatCard.creatPdfCard()) {
                    runOnUiThread(this::infoDownloadCard);
                } else {
                    runOnUiThread(this::notifyFailureDownload);
                }
            } catch (Exception e) {
                Log.e("kariti", e.toString());
            } finally {
                runOnUiThread(() -> {
                    btnGenerateCard.setEnabled(true);
                    gifLoading.setVisibility(View.GONE);
                    overlayView.setVisibility(View.GONE);
                });
            }
        }).start();
    }

    private void requestsPermissionNotify(){
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
                Toast.makeText(this, getString(R.string.toastPermissionGranted), Toast.LENGTH_SHORT).show();
                //Log.d("Permissão", "Permissão WRITE_EXTERNAL_STORAGE concedida.");
                generateCard();
            } else {
                // Permissão negada
                //Log.d("Permissão", "Permissão WRITE_EXTERNAL_STORAGE negada.");
                permissionDenied();
                // Informe ao usuário que a permissão é necessária ou tome uma ação adequada
            }
        }
        if (requestCode == 101){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.toastPermissionGranted), Toast.LENGTH_SHORT).show();
            } else {
                // Permissão negada, exiba uma mensagem explicativa ao usuário
                permissionNotifyDenied();
            }
        }
    }
    public void permissionDenied(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.titleAttention));
        builder.setMessage(getString(R.string.longTextRequestsPermission));
        builder.setPositiveButton(getString(R.string.okDescription), (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    public void permissionNotifyDenied(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.titleAttention));
        builder.setMessage(getString(R.string.longTextNoNotifyDownload));
        builder.setPositiveButton(getString(R.string.okDescription), (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void requestsPermission(){
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
            builder.setTitle(getString(R.string.app_name_capital_letter));
            builder.setMessage(getString(R.string.longTextSuccessDownload));
            builder.setPositiveButton(getString(R.string.okDescription), (dialog, which) -> {
                dialog.dismiss();
                orientation();
            });

            builder.show();
        }
    }
    private void notifyFailureDownload(){
        if(!isFinishing() && !isDestroyed()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(getString(R.string.app_name_capital_letter));
            builder.setMessage(getString(R.string.longTextGenerateCardError));
            builder.setPositiveButton(getString(R.string.okDescription), (dialog, which) -> dialog.dismiss());
            builder.show();
        }
    }
    private void orientation(){
        if(!isFinishing() && !isDestroyed()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(getString(R.string.titleGuidance));
            builder.setMessage(getString(R.string.longTextGuidanceMarkedCards));
            builder.setPositiveButton(getString(R.string.okDescription), (dialog, which) -> {
                dialog.dismiss();
                finish();
            });
            builder.show();
        }
    }
}