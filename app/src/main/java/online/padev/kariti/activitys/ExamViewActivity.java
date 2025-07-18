package online.padev.kariti.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import online.padev.kariti.R;
import online.padev.kariti.adapters.AdapterClickableList;
import online.padev.kariti.adapters.AdapterSpinner;
import online.padev.kariti.entity.Exam;
import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.settings.ActivityLocale;

public class ExamViewActivity extends AppCompatActivity {
    ImageButton back;
    private String className, examName;
    private Integer id_class, exam_id;
    private List<String> listExams, listClass;
    RecyclerView recyclerView;
    AdapterClickableList adapterExam;
    AdapterSpinner adapterSpinnerClass;
    TextView title;
    Spinner spinnerClass;
    DataBaseKariti dataBaseKariti;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ActivityLocale.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_view);

        back = findViewById(R.id.imgBtnVoltar);
        recyclerView = findViewById(R.id.listProvas);
        spinnerClass = findViewById(R.id.spinnerTurma2);
        title = findViewById(R.id.toolbar_title);

        title.setText(getString(R.string.textViewExams));

        dataBaseKariti = new DataBaseKariti(this);

        listClass = dataBaseKariti.listClassByExam();
        if (listClass == null){
            Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            finish();
        }

        //listaTurmas.add(0, "Turmas");
        adapterSpinnerClass = new AdapterSpinner(this, listClass);
        spinnerClass.setAdapter(adapterSpinnerClass);
        spinnerClass.setSelection(0);
        className = spinnerClass.getSelectedItem().toString();
        id_class = dataBaseKariti.getClassId(className);
        if (id_class == null){
            Toast.makeText(ExamViewActivity.this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            finish();
        }

        listExams = dataBaseKariti.listExamNames(id_class.toString());
        if (listExams == null){
            Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            finish();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterExam = new AdapterClickableList(this, listExams, this::onItemClick, this::onItemLongClick);
        recyclerView.setAdapter(adapterExam);

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                className = spinnerClass.getSelectedItem().toString();
                id_class = dataBaseKariti.getClassId(className);
                listExams.clear();
                listExams = dataBaseKariti.listExamNames(id_class.toString());
                if (listExams == null){
                    Toast.makeText(ExamViewActivity.this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                    finish();
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(ExamViewActivity.this));
                adapterExam = new AdapterClickableList(ExamViewActivity.this, listExams, ExamViewActivity.this::onItemClick, ExamViewActivity.this::onItemLongClick);
                recyclerView.setAdapter(adapterExam);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        back.setOnClickListener(view -> {
            getOnBackPressedDispatcher();
            finish();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
    public void onItemClick(int position) {
        examName = listExams.get(position);
        exam_id = dataBaseKariti.getExamId(examName, id_class);
        if (exam_id == null){
            Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            return;
        }
        startCorrectionExam();
    }
    public void onItemLongClick(int position) {
        examName = listExams.get(position);
        exam_id = dataBaseKariti.getExamId(examName, id_class);
        displayEditOrDelete(position);
    }
    private void startCorrectionExam(){
        Boolean checkIsCorrected = dataBaseKariti.checkIfExamCorrected(exam_id.toString());
        if (checkIsCorrected == null){
            Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            return;
        }
        if(checkIsCorrected){
            Exam exam = new Exam(exam_id, dataBaseKariti);
            Intent intent = new Intent(this, ExamCorrectedActivity.class);
            intent.putExtra("prova", exam);
            startActivity(intent);
        }else {
            Toast.makeText(this, getString(R.string.toastExamNoCorrected), Toast.LENGTH_SHORT).show();
        }
    }

    private void displayEditOrDelete(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.optionsEditOrDelete))
                .setPositiveButton(getString(R.string.menuDelete), (dialog, which) -> noticeIfDelete(position))
                .setNegativeButton(getString(R.string.buttonEdit), (dialog, which) -> editExam());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void editExam(){
        if(dataBaseKariti.checkIfExamCorrected(exam_id.toString())){
            noticeImpossibleEdit();
        }else {
            Intent intent = new Intent(this, ExamEditActivity.class);
            intent.putExtra("id_prova", exam_id);
            startActivity(intent);
        }
    }
    private void noticeImpossibleEdit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.titleAttention))
                .setMessage(getString(R.string.longTextExamCorrected))
                .setPositiveButton(getString(R.string.okDescription), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void noticeIfDelete(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.titleAttention))
                .setMessage(getString(R.string.longTextIfDeleteExam))
                .setPositiveButton(getString(R.string.yes_description), (dialog, which) -> deleteExam(position))
                .setNegativeButton(getString(R.string.not_description), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteExam(int position){
        if (dataBaseKariti.deleteExamData(exam_id)){
            listExams.remove(examName);
            notifyExamDeleted(position);
        }else{
            Toast.makeText(this, getString(R.string.toastFailedDeleteExam), Toast.LENGTH_SHORT).show();
        }

    }
    private void notifyExamDeleted(int position){
        Toast.makeText(this, getString(R.string.toastSuccessDeleteExam), Toast.LENGTH_SHORT).show();
        if(!listExams.isEmpty()){
            adapterExam.notifyItemRemoved(position);
        }else{
            finish();
        }
    }
}