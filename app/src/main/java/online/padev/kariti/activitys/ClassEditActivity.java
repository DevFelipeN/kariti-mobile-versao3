package online.padev.kariti.activitys;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class ClassEditActivity extends AppCompatActivity {
    ImageButton back, iconHelp;
    ImageView moreAnonymous, lessAnonymous;
    ListView listViewStudents;
    EditText editTxtClass, editTxtAnonymous;
    ClassSchool cs;
    List<Student> students;
    List<String> studentsRegisteredClass;
    private String nameClassCurrent, studentSelected;
    DataBaseKariti dataBase;
    StudentOnDeleteAdapter adapterStudents;
    Button selectedStudent;
    Button btnSave;
    private Integer id_student, numAnonymousBD;
    TextView titleActvity, titleStudent;

    private static final int REQUEST_CODE = 1;

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
        titleActvity = findViewById(R.id.toolbar_title);

        dataBase = new DataBaseKariti(this);
        studentsRegisteredClass = new ArrayList<>();

        titleActvity.setText(String.format("%s", "Atualização"));

        cs = (ClassSchool) getIntent().getSerializableExtra("classSchool_id");
        students = (List<Student>) getIntent().getSerializableExtra("students");

        //Mostra a turma a ser editada
        numAnonymousBD = dataBase.getStudentNumber(cs.getClass_id(), 0);
        if (numAnonymousBD == null) {
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente - 2", Toast.LENGTH_SHORT).show();
            finish();
        }

        editTxtClass.setText(cs.getName());
        editTxtAnonymous.setText(String.format("%s", numAnonymousBD));

        notifyAnonymous(numAnonymousBD);

        if (students.isEmpty()) {
            titleStudent.setVisibility(View.GONE);
            listViewStudents.setVisibility(View.GONE);
        }

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
            Toast.makeText(ClassEditActivity.this, "Aluno Removido! ", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ClassEditActivity.this, "Informe o nome da turma!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente - 3", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (checkClassExists) {
                        Toast.makeText(this, "Já existe uma turma com esse nome associado a essa escola! ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!dataBase.updateClassData(nameClassCurrent, cs.getClass_id())) {
                        Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (!dataBase.deleteStudentsFromClass(cs.getClass_id())) {  //Deleta e todos os alunos pertecentes a essa turma
                    Toast.makeText(this, "Falha de na atualização dessa turma! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
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
                        String nameAnonymous = "Aluno "+ String.format("%0"+t+"d",x);
                        Student studentA = new Student(0, nameAnonymous, null);
                        studentsAnonymous.add(studentA);
                    }
                    students.addAll(studentsAnonymous);
                }

                if (!students.isEmpty()) {
                    boolean isInsertion = dataBase.insertStudentsInClass_2(students, cs.getClass_id());
                    if (isInsertion) {
                        Toast.makeText(this, "Dados alterados com sucesso!", Toast.LENGTH_SHORT).show();
                        restartDataClass();
                    } else {
                        Toast.makeText(this, "Falha de na atualização dessa turma! \n\n Por favor, tente novamente!", Toast.LENGTH_SHORT).show();
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
    public void PopMenu(View v){
        v.setOnClickListener(view -> Toast.makeText(ClassEditActivity.this, "Preparado para implementação", Toast.LENGTH_SHORT).show());
    }
    public void notifyAnonymous(Integer anonimos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("KARITI");
        builder.setMessage("Esta turma possui "+anonimos+" alunos anônimos cadastrados, caso deseje alterar essa quantidade, basta informar um novo valor no campo referente 'Incluir Alunos Anônimos'");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void dialogHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Nesta tela os dados da turma podem sem alterados, seguindo o mesmo padrão de cadastro de turma.\n\n" +
                "1 - Nome: caso deseje alterar o nome, basta informar um novo\n\n" +
                "2 - Alunos: podem ser incluídos novos alunos para essa turma selecionando-os no campo 'Selecione os Alunos', os quais antecipadamente já devem estar cadastrados no KARITI na tela de cadastro de alunos. Caso deseje remover, basta clicar no nome do aluno para remove-lo da turma. \n\n" +
                "3 - Anônimos: caso não deseje cadastrar alunos para essa turma, podem ser incluidos alunos anônimos no campo 'Incluir Alunos Anônimos', informando a quantidade no campo sugerido, sem a necessidade de cadastrar todos ou nenhum aluno como descrito na opção 2. \n\n" +
                "Obs. A Turma não pode ser cadastrada sem alunos.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void notice(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ClassEditActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Não é possível cadastrar uma turma sem alunos. Por favor, selecione os alunos para essa turma ou, caso preferir, informe a quantidade de alunos anônimos! ")
                .setPositiveButton("OK", (dialog, which) -> Toast.makeText(ClassEditActivity.this, "Selecione os alunos!", Toast.LENGTH_SHORT).show());
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
                Log.e("Testess", String.valueOf(students.size()));
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