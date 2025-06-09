package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import online.padev.kariti.adapters.StudentOnDeleteAdapter;
import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.entity.Student;

public class ClassRegistrationActivity extends AppCompatActivity{
    ImageButton back, iconHelp;
    Toolbar toolbar;
    EditText editTextNameClass, editTextStudentAnonymous;
    ImageView lessAnonymous, moreAnonymous;
    ListView listViewStudents;
    Button btnRegistration, btnSelectStudents;
    DataBaseKariti dataBase;
    Spinner spinnerStudent;
    String studentSelected;
    Integer id_class = 0;
    StudentOnDeleteAdapter adapterStudents;
    TextView titleActivity, titleStudents;
    List<String> listStudentSelected = new ArrayList<>(), listStudentsSpinner;
    List<Student> students;

    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_registration);

        toolbar = findViewById(R.id.myToolBarMenu);
        setSupportActionBar(toolbar);
        back = findViewById(R.id.imgBtnVoltaDescola);
        iconHelp = findViewById(R.id.iconHelp);
        listViewStudents = findViewById(R.id.listViewCadTurma);
        titleActivity = findViewById(R.id.toolbar_title);
        titleStudents = findViewById(R.id.textViewAlunos);

        titleActivity.setText(String.format("%s","Nova Turma"));

        editTextNameClass = findViewById(R.id.editTextTurmaCad);
        btnRegistration = findViewById(R.id.buttonCadastrarTurma);
        //spinnerStudent = findViewById(R.id.spinnerBuscAluno);
        btnSelectStudents = findViewById(R.id.btnSelectaStudentClass);
        editTextStudentAnonymous = findViewById(R.id.editTextAlunosAnonimos);
        lessAnonymous = findViewById(R.id.imageViewMenosAnonimos);
        moreAnonymous = findViewById(R.id.imageViewMaisAnonimos);

        listViewStudents.setVisibility(View.GONE);
        titleStudents.setVisibility(View.GONE);

        dataBase = new DataBaseKariti(this);

        students = new ArrayList<>();

        listViewStudents.setVisibility(View.GONE);

        adapterStudents = new StudentOnDeleteAdapter(this, students);
        listViewStudents.setAdapter(adapterStudents);

        listViewStudents.setOnItemClickListener((adapterView, view, i, l) -> {
            students.remove(i);
            if(students.isEmpty()){
                listViewStudents.setVisibility(View.GONE);
            }
            adapterStudents.notifyDataSetChanged();
            Toast.makeText(this, "Aluno removido! ", Toast.LENGTH_SHORT).show();
        });

        btnSelectStudents.setOnClickListener(v -> {
            Intent intent_2 = new Intent(this, StudentSelectedActivity.class);
            intent_2.putExtra("students", (Serializable) students);
            startActivityForResult(intent_2, REQUEST_CODE);
        });

        /*
        listStudentsSpinner = dataBase.listStudentNames(1);


        if (!listStudentsSpinner.isEmpty()){
            listStudentsSpinner.add(0, "Selecionar Alunos");
            listStudentsSpinner.add(1, "Todos");
        }else {
            spinnerStudent.setVisibility(View.GONE);
            listViewStudents.setVisibility(View.GONE);
        }

        AdapterSpinner adapter = new AdapterSpinner(this, listStudentsSpinner);
        spinnerStudent.setAdapter(adapter);
        spinnerStudent.setSelection(0);

        iconHelp.setOnClickListener(view -> dialogHelp());
        spinnerStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    studentSelected = spinnerStudent.getSelectedItem().toString();
                    if(studentSelected.equals("Todos")){
                        listStudentSelected = dataBase.listStudentNames(1);
                        if(listStudentSelected == null){
                            Toast.makeText(ClassRegistrationActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        adapterStudents = new AdapterStudentOnDelete(ClassRegistrationActivity.this, listStudentSelected);
                        listViewStudents.setAdapter(adapterStudents);
                        listViewStudents.setVisibility(View.VISIBLE);
                        titleAnonymous.setVisibility(View.VISIBLE);
                        adapterStudents.notifyDataSetChanged();
                        spinnerStudent.setSelection(1);
                    }else {
                        int i = 0;
                        for (String a : listStudentSelected) {
                            if (studentSelected.equals(a)) {
                                i = 1;
                                Toast.makeText(ClassRegistrationActivity.this, "Aluno já selecionado!", Toast.LENGTH_SHORT).show();
                                spinnerStudent.setSelection(0);
                                break;
                            }
                        }
                        if (i != 1) {
                            listStudentSelected.add(studentSelected);
                            adapterStudents = new AdapterStudentOnDelete(ClassRegistrationActivity.this, listStudentSelected);
                            listViewStudents.setAdapter(adapterStudents);
                            listViewStudents.setVisibility(View.VISIBLE);
                            titleAnonymous.setVisibility(View.VISIBLE);
                            adapterStudents.notifyDataSetChanged();
                            spinnerStudent.setSelection(0);
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        listViewStudents.setOnItemClickListener((adapterView, view, i, l) -> {
            listStudentSelected.remove(i);
            if(listStudentSelected.isEmpty()){
                listViewStudents.setVisibility(View.GONE);
                titleAnonymous.setVisibility(View.GONE);
                spinnerStudent.setSelection(0);
            }
            adapterStudents.notifyDataSetChanged();
            Toast.makeText(ClassRegistrationActivity.this, "Aluno removido! ", Toast.LENGTH_SHORT).show();
        });
        */

        lessAnonymous.setOnClickListener(view -> {
            int numAnonymous = 0;
            if (!editTextStudentAnonymous.getText().toString().trim().isEmpty()){
                numAnonymous = Integer.parseInt(editTextStudentAnonymous.getText().toString());
            }
            if(numAnonymous > 0)
                numAnonymous --;
            editTextStudentAnonymous.setText(String.valueOf(numAnonymous));
        });
        moreAnonymous.setOnClickListener(view -> {
            int numAnonymous = 0;
            if (!editTextStudentAnonymous.getText().toString().trim().isEmpty()){
                numAnonymous = Integer.parseInt(editTextStudentAnonymous.getText().toString());
            }
            numAnonymous ++;
            editTextStudentAnonymous.setText(String.valueOf(numAnonymous));
        });

        btnRegistration.setOnClickListener(v -> {
            btnRegistration.setEnabled(false);
            try {
                String className = editTextNameClass.getText().toString().trim();
                if(className.isEmpty()) {
                    Toast.makeText(ClassRegistrationActivity.this, "Informe o nome da turma!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String numAnonymous = editTextStudentAnonymous.getText().toString().trim();
                if (listStudentSelected.isEmpty() && (numAnonymous.equals("0") || numAnonymous.isEmpty())) {
                    notice();
                    return;
                }
                Boolean checkClassExists = dataBase.checkExistClass(className);
                if (checkClassExists == null){
                    Toast.makeText(ClassRegistrationActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (checkClassExists) {
                    Toast.makeText(ClassRegistrationActivity.this, "Turma já cadastrada! ", Toast.LENGTH_SHORT).show();
                    return;
                }
                id_class = dataBase.insertClass(className);
                if (id_class == null || id_class == -1) {
                    Toast.makeText(ClassRegistrationActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!listStudentSelected.isEmpty()){
                    for (String student : listStudentSelected) {
                        Integer id_student = dataBase.getStudentId(student);
                        if (id_student != null && id_student != -1){
                            if(dataBase.linkStudentToClass(id_class, id_student)){
                                Log.e("kariti","Aluno cadastrado na turma: "+ id_class);
                            }
                        }else Log.e("kariti","Erro ao tentar cadastrar na turma o aluno: "+student);
                    }
                }
                int totAnonymous = 0;
                if (!numAnonymous.isEmpty()) {
                    totAnonymous = Integer.parseInt(numAnonymous);
                }
                if (totAnonymous != 0){
                    int t = numAnonymous.length();
                    for (int x = 1; x <= totAnonymous; x++) {
                        String nameAnonymous = "Aluno "+ String.format("%0"+t+"d",x);
                        Integer id_anonymous = dataBase.insertStudent(nameAnonymous, null, 0);
                        if(id_anonymous != -1){
                            if(dataBase.linkStudentToClass(id_class, id_anonymous)){
                                Log.e("kariti","Aluno anônimo cadastrado na turma: "+ id_class);
                            }
                        }else  Log.e("kariti","Erro ao tentar cadastrar anônimo: "+x);
                    }
                }
                Toast.makeText(this, "Turma cadastrada com Sucesso", Toast.LENGTH_SHORT).show();
                restartVisualClass();
            }catch (Exception e){
                Toast.makeText(this, "Erro: turma não cadastrada corretamente!!", Toast.LENGTH_SHORT).show();
                restartVisualClass();
            } finally {
                btnRegistration.setEnabled(true);
            }
        });
        back.setOnClickListener(view -> restartVisualClass());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                restartVisualClass();
            }
        });
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
                    titleStudents.setVisibility(View.VISIBLE);
                }
            }else{
                restartVisualClass();
            }
        }
    }
    public void restartVisualClass(){
        if(dataBase.checkExistClass()){
            setResult(RESULT_OK);
            finish();
        }else{
            setResult(RESULT_CANCELED);
            finish();
        }
    }
    public void notice(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ClassRegistrationActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Não é possível cadastrar uma turma sem alunos. Por favor, selecione os alunos para essa turma ou, caso preferir, informe a quantidade de alunos anônimos! ")
                .setPositiveButton("OK", (dialog, which) -> Toast.makeText(ClassRegistrationActivity.this, "Selecione os alunos!", Toast.LENGTH_SHORT).show());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void dialogHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Bem vindo(a) ao cadastro de turma! \n\n" +
                "Nesta tela são solicitados alguns dados para cadastrar um nova turma.\n\n" +
                "1 - Nome: deve ser informado o nome da turma *obrigatório* \n\n" +
                "2 - Alunos: podem ser incluídos alunos para essa turma selecionando-os no campo 'Selecione os Alunos', os quais antecipadamente já devem estar cadastrados no KARITI na tela de cadastro de alunos. Todos os alunos selecionados são listados no campo 'Alunos'. Caso selecione algum aluno errado, basta clicar no nome do aluno para remove-lo da lista. \n\n" +
                "3 - Anônimos: caso não deseje cadastrar alunos para essa turma, podem ser incluidos alunos anônimos no campo 'Incluir Alunos Anônimos', informando a quantidade no campo sugerido, sem a necessidade de cadastrar todos ou nenhum aluno como descrito na opção 2. \n\n" +
                "Obs. A Turma não pode ser cadastrada sem alunos.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}