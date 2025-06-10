package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import online.padev.kariti.adapters.ListNotActionAdapter;
import online.padev.kariti.adapters.ListStudentInClassAdapter;
import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.entity.ClassSchool;
import online.padev.kariti.entity.Student;

public class ClassDetailsActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    ImageButton back;
    TextView textViewClassName, txtViewNumAnonymous, titleActivity;
    DataBaseKariti dataBase;
    ListView listViewStudents;
    ClassSchool cs;
    ListStudentInClassAdapter adapterStudents;
    List<Student> studentsAnonymous, studentsIdentified;
    List<Student> students = new ArrayList<>();
    private static final int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        back = findViewById(R.id.imgBtnVoltarDados);
        listViewStudents = findViewById(R.id.listViewDados);
        txtViewNumAnonymous = findViewById(R.id.textViewqtdAnonimos);
        textViewClassName = findViewById(R.id.textViewTurmaCad);
        titleActivity = findViewById(R.id.toolbar_titlePopUp);

        titleActivity.setText(String.format("%s","Turma"));

        dataBase = new DataBaseKariti(this);

        cs = (ClassSchool) getIntent().getSerializableExtra("classSchool");

        textViewClassName.setText(String.format("%s", cs.getName()));

        studentsIdentified = dataBase.listStudentsData(cs.getClass_id(), 1);
        studentsAnonymous = dataBase.listStudentsData(cs.getClass_id(), 0);

        if (studentsIdentified == null || studentsAnonymous == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            finish();
        }

        students.addAll(studentsIdentified);
        students.addAll(studentsAnonymous);

        txtViewNumAnonymous.setText(String.format(" Alunos Identificados: %s \n Alunos Anônimos: %s \n Total de alunos: %s", studentsIdentified.size(), studentsAnonymous.size(), students.size()));
        adapterStudents = new ListStudentInClassAdapter(this, students);
        listViewStudents.setAdapter(adapterStudents);

        back.setOnClickListener(view -> restartVisualClass());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                restartVisualClass();
            }
        });
    }
    public void PopMenu(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.actuvity_menuturma);
        popupMenu.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuEditar) {
            startEditClass();
            return true;
        }
        return true;
    }

    private void startEditClass(){
        Boolean examCorrection = dataBase.checkCorrectedByClass(cs.getClass_id());
        if (examCorrection == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!examCorrection){
            Intent intent = new Intent(this, ClassEditActivity.class);
            intent.putExtra("classSchool_id", cs);
            intent.putExtra("students", (Serializable) studentsIdentified);
            startActivityForResult(intent, REQUEST_CODE);
        }else{
            notifyImpossibilityEdit();
        }
    }
    public void notifyImpossibilityEdit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ClassDetailsActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Esta turma possui vínculo com uma ou mais prova(s) já corrigidas, não sendo possível editar!");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                students.clear();
                studentsIdentified.clear();
                studentsAnonymous.clear();
                studentsIdentified = dataBase.listStudentsData(cs.getClass_id(), 1);
                studentsAnonymous = dataBase.listStudentsData(cs.getClass_id(), 0);
                students.addAll(studentsIdentified);
                students.addAll(studentsAnonymous);
                adapterStudents.notifyDataSetChanged();
                txtViewNumAnonymous.setText(String.format(" Alunos Identificados: %s \n Alunos Anônimos: %s \n Total de alunos: %s", studentsIdentified.size(), studentsAnonymous.size(), students.size()));
            } else {
                restartVisualClass();
            }
        }
    }
    public void restartVisualClass(){
        setResult(RESULT_OK);
        finish();
    }
}