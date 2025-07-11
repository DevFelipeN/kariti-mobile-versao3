package online.padev.kariti.activitys;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import online.padev.kariti.R;
import online.padev.kariti.adapters.StudentOnDeleteAdapter;
import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.entity.ClassSchool;
import online.padev.kariti.entity.Student;
import online.padev.kariti.settings.ActivityLocale;

public class ClassEditActivity extends AppCompatActivity {
    ImageButton back, iconHelp;
    ImageView moreAnonymous, lessAnonymous;
    ListView listViewStudents;
    EditText editTxtClass, editTxtAnonymous;
    ClassSchool cs;
    List<Student> students;
    List<String> studentsRegisteredClass;
    private String nameClassCurrent;
    DataBaseKariti dataBase;
    StudentOnDeleteAdapter adapterStudents;
    Button selectedStudent;
    Button btnSave;
    TextView titleActivity, titleStudent;

    private static final int REQUEST_CODE = 1;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ActivityLocale.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_edit);

        listViewStudents = findViewById(R.id.listViewEditarTurma);
        editTxtClass = findViewById(R.id.editTextEditTurma);
        moreAnonymous = findViewById(R.id.imageViewMaisNovosAnonimos);
        lessAnonymous = findViewById(R.id.imageViewMenosNovosAnonimos);
        titleStudent = findViewById(R.id.txtVieAlunos);
        editTxtAnonymous = findViewById(R.id.editTextNovosAlunosAnonimos);
        selectedStudent = findViewById(R.id.buscAlunoNovos);
        btnSave = findViewById(R.id.buttonSalvarTurma);
        back = findViewById(R.id.imgBtnVoltaDescola);
        iconHelp = findViewById(R.id.iconHelp);
        titleActivity = findViewById(R.id.toolbar_title);

        dataBase = new DataBaseKariti(this);
        studentsRegisteredClass = new ArrayList<>();

        titleActivity.setText(getString(R.string.titleEditClass));

        cs = (ClassSchool) getIntent().getSerializableExtra("classSchool_id");
        students = (List<Student>) getIntent().getSerializableExtra("students");

        //Mostra a turma a ser editada
        Integer numAnonymousBD = dataBase.getStudentNumber(cs.getClass_id(), 0);
        if (numAnonymousBD == null) {
            Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            finish();
        }

        editTxtClass.setText(cs.getName());
        editTxtAnonymous.setText(String.format("%s", numAnonymousBD));

        notifyAnonymous(numAnonymousBD);

        if (students.isEmpty()) {
            titleStudent.setVisibility(View.GONE);
            listViewStudents.setVisibility(View.GONE);
        }

        Boolean studentExist = dataBase.checkExistStudent();
        if (studentExist != null){
            if(!studentExist){
                selectedStudent.setVisibility(View.GONE);
            }
        } else Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();

        adapterStudents = new StudentOnDeleteAdapter(this, students);
        listViewStudents.setAdapter(adapterStudents);

        selectedStudent.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudentSelectedActivity.class);
            intent.putExtra("students", (Serializable) students);
            startActivityForResult(intent, REQUEST_CODE);
        });

        //Remove aluno do listView, após simples click
        listViewStudents.setOnItemClickListener((adapterView, view, i, l) -> {
            students.remove(i);
            if (students.isEmpty()) {
                titleStudent.setVisibility(View.GONE);
                listViewStudents.setVisibility(View.GONE);
            }
            adapterStudents.notifyDataSetChanged();
            Toast.makeText(this, getString(R.string.toastStudentRemoved), Toast.LENGTH_SHORT).show();
        });

        lessAnonymous.setOnClickListener(view -> {
            int numAnonymous = 0;
            if (!editTxtAnonymous.getText().toString().trim().isEmpty()) {
                numAnonymous = Integer.parseInt(editTxtAnonymous.getText().toString());
            }
            if (numAnonymous > 0)
                numAnonymous--;
            editTxtAnonymous.setText(String.valueOf(numAnonymous));
        });

        moreAnonymous.setOnClickListener(view -> {
            int numAnonymous = 0;
            if (!editTxtAnonymous.getText().toString().trim().isEmpty()) {
                numAnonymous = Integer.parseInt(editTxtAnonymous.getText().toString());
            }
            numAnonymous++;
            editTxtAnonymous.setText(String.valueOf(numAnonymous));
        });

        btnSave.setOnClickListener(view -> {
            btnSave.setEnabled(false);
            try {
                nameClassCurrent = editTxtClass.getText().toString().trim();
                if (nameClassCurrent.isEmpty()) {
                    Toast.makeText(this, getString(R.string.toastEnterNameClass), Toast.LENGTH_SHORT).show();
                    return;
                }
                String numAnonymousCurrent = editTxtAnonymous.getText().toString().trim();
                if (students.isEmpty() && (numAnonymousCurrent.equals("0") || numAnonymousCurrent.isEmpty())) {
                    notice();
                    return;
                }
                if (!nameClassCurrent.equals(cs.getName())) {
                    Boolean checkClassExists = dataBase.checkExistClass(nameClassCurrent);
                    if (checkClassExists == null) {
                        Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (checkClassExists) {
                        Toast.makeText(this, getString(R.string.toastClassAlreadyRegister), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!dataBase.updateClassData(nameClassCurrent, cs.getClass_id())) {
                        Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (!dataBase.deleteStudentsFromClass(cs.getClass_id())) {  //Deleta e todos os alunos pertecentes a essa turma
                    Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                    return;
                }

                int totAnonymous = 0;
                if (!numAnonymousCurrent.isEmpty()) {
                    totAnonymous = Integer.parseInt(numAnonymousCurrent);
                }
                if (totAnonymous != 0){
                    int t = numAnonymousCurrent.length();
                    List<Student> studentsAnonymous = new ArrayList<>();
                    for (int x = 1; x <= totAnonymous; x++) {
                        String nameAnonymous = getString(R.string.textAnonymous, String.format("%0"+t+"d",x));
                        Student studentA = new Student(0, nameAnonymous, null);
                        studentsAnonymous.add(studentA);
                    }
                    students.addAll(studentsAnonymous);
                }

                if (!students.isEmpty()) {
                    boolean isInsertion = dataBase.insertStudentsInClass_2(students, cs.getClass_id());
                    if (isInsertion) {
                        Toast.makeText(this, getString(R.string.toastStudentSuccessUpdate), Toast.LENGTH_SHORT).show();
                        restartDataClass();
                    } else {
                        Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                Log.e("kariti", e.toString());
            } finally {
                btnSave.setEnabled(true);
            }
        });
        iconHelp.setOnClickListener(view -> dialogHelp());
        back.setOnClickListener(view -> restartDataClass());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                restartDataClass();
            }
        });
    }

    public void notifyAnonymous(int an) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name_capital_letter));
        builder.setMessage(getString(R.string.longTextNumAnonymousInClass, an));
        builder.setPositiveButton(getString(R.string.okDescription), (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void dialogHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.titleHelp));
        builder.setMessage(getString(R.string.longTextHelpRegisterClass));
        builder.setPositiveButton(getString(R.string.okDescription), (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void notice(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ClassEditActivity.this);
        builder.setTitle(getString(R.string.titleAttention))
                .setMessage(getString(R.string.longTextClassNoStudent))
                .setPositiveButton(getString(R.string.okDescription), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                students.clear();
                students.addAll((List<Student>) data.getSerializableExtra("students"));
                adapterStudents.notifyDataSetChanged();
                if (!students.isEmpty()){
                    listViewStudents.setVisibility(View.VISIBLE);
                    titleStudent.setVisibility(View.VISIBLE);
                }else {
                    if (listViewStudents.getVisibility() == View.VISIBLE){
                        listViewStudents.setVisibility(View.GONE);
                        titleStudent.setVisibility(View.GONE);
                    }
                }
            }else{
                restartDataClass();
            }
        }
    }

    public void restartDataClass(){
        setResult(RESULT_OK);
        finish();
    }

}