package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import online.padev.kariti.database.DataBaseKariti;

public class ClassEditActivity extends AppCompatActivity {
    ImageButton back, iconHelp;
    ImageView moreAnonymous, lessAnonymous;
    ListView listViewStudents;
    EditText editTxtClass, editTxtAnonymous;
    List<String> studentsRegisteredClass, studentsSchool;
    private String id_Class, nameClassBD, nameClassCurrent, studentSelected;
    DataBaseKariti dataBase;
    AdapterStudentOnDelete adapterStudentsRegistered;
    Spinner spinnerStudent;
    Button btnSave;
    private Integer id_student, numAnonymousBD;
    TextView titleActvity, titleStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        listViewStudents = findViewById(R.id.listViewEditarTurma);
        editTxtClass = findViewById(R.id.editTextEditTurma);
        moreAnonymous = findViewById(R.id.imageViewMaisNovosAnonimos);
        lessAnonymous = findViewById(R.id.imageViewMenosNovosAnonimos);
        titleStudent = findViewById(R.id.txtVieAlunos);
        editTxtAnonymous = findViewById(R.id.editTextNovosAlunosAnonimos);
        spinnerStudent = findViewById(R.id.spinnerBuscAlunoNovos);
        btnSave = findViewById(R.id.buttonSalvarTurma);
        back = findViewById(R.id.imgBtnVoltaDescola);
        iconHelp = findViewById(R.id.iconHelp);
        titleActvity = findViewById(R.id.toolbar_title);

        dataBase = new DataBaseKariti(this);
        studentsRegisteredClass = new ArrayList<>();

        titleActvity.setText(String.format("%s","Atualização"));

        id_Class = Objects.requireNonNull(getIntent().getExtras()).getString("id_turma");

        //Lista todos os alunos no Spinner
        studentsSchool = dataBase.listarNomesAlunos(1);
        if (studentsSchool == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente - 1", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!studentsSchool.isEmpty()){
            studentsSchool.add(0, "Selecionar alunos");
        } else {
            spinnerStudent.setVisibility(View.GONE);
        }

        AdapterSpinner adapterSpinner = new AdapterSpinner(this, studentsSchool);
        spinnerStudent.setAdapter(adapterSpinner);
        spinnerStudent.setSelection(0);

        //Mostra a turma a ser editada

        nameClassBD = dataBase.pegarNomeTurma(id_Class);
        numAnonymousBD = dataBase.pegarQtdAlunosPorStatus(id_Class, 0);

        if (nameClassBD == null || numAnonymousBD == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente - 2", Toast.LENGTH_SHORT).show();
            finish();
        }

        editTxtClass.setText(nameClassBD);
        editTxtAnonymous.setText(String.format("%s", numAnonymousBD));

        notifyAnonymous(numAnonymousBD);

        //Lista os aluno cadastrados nesta turma.
        studentsRegisteredClass = dataBase.listarAlunosTurmaPorStatus(id_Class, 1);
        if (studentsRegisteredClass == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente - 3", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (studentsRegisteredClass.isEmpty()){
            titleStudent.setVisibility(View.GONE);
            listViewStudents.setVisibility(View.GONE);
        }
        adapterStudentsRegistered = new AdapterStudentOnDelete(this, studentsRegisteredClass);
        listViewStudents.setAdapter(adapterStudentsRegistered);

        //Identifica o aluno selecionado no Spinner e adiciona no listView
        spinnerStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    studentSelected = spinnerStudent.getSelectedItem().toString();
                    if(studentsRegisteredClass.contains(studentSelected)) {
                        Toast.makeText(ClassEditActivity.this, "Aluno já selecionado!", Toast.LENGTH_SHORT).show();
                        spinnerStudent.setSelection(0);
                        return;
                    }
                    studentsRegisteredClass.add(studentSelected);
                    adapterStudentsRegistered = new AdapterStudentOnDelete(ClassEditActivity.this, studentsRegisteredClass);
                    listViewStudents.setAdapter(adapterStudentsRegistered);
                    titleStudent.setVisibility(View.VISIBLE);
                    listViewStudents.setVisibility(View.VISIBLE);
                    adapterStudentsRegistered.notifyDataSetChanged();
                    spinnerStudent.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //Remove aluno do listView, após simples click
        listViewStudents.setOnItemClickListener((adapterView, view, i, l) -> {
                studentsRegisteredClass.remove(i);
                if (studentsRegisteredClass.isEmpty()){
                    titleStudent.setVisibility(View.GONE);
                    listViewStudents.setVisibility(View.GONE);
                    spinnerStudent.setSelection(0);
                }
                adapterStudentsRegistered.notifyDataSetChanged();
                Toast.makeText(ClassEditActivity.this, "Aluno Removido! ", Toast.LENGTH_SHORT).show();
        });
        lessAnonymous.setOnClickListener(view -> {
            int numAnonymous = 0;
            if (!editTxtAnonymous.getText().toString().trim().isEmpty()){
                numAnonymous = Integer.parseInt(editTxtAnonymous.getText().toString());
            }
            if(numAnonymous > 0)
                numAnonymous --;
            editTxtAnonymous.setText(String.valueOf(numAnonymous));
        });

        moreAnonymous.setOnClickListener(view -> {
            int numAnonymous = 0;
            if (!editTxtAnonymous.getText().toString().trim().isEmpty()){
                numAnonymous = Integer.parseInt(editTxtAnonymous.getText().toString());
            }
            numAnonymous ++;
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
                if (studentsRegisteredClass.isEmpty() && (numAnonymousCurrent.equals("0") || numAnonymousCurrent.isEmpty())) {
                    notice();
                    return;
                }
                if (!nameClassCurrent.equals(nameClassBD)) {
                    Boolean checkClassExists = dataBase.verificaExisteTurmaPorNome(nameClassCurrent);
                    if (checkClassExists == null) {
                        Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente - 3", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (checkClassExists) {
                        Toast.makeText(this, "Turma já cadastrada! ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!dataBase.alterarDadosTurma(nameClassCurrent, Integer.valueOf(id_Class))) {
                        Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (!dataBase.deletarAnonimos(Integer.valueOf(id_Class))) {  //Deleta todos os alunos Anonimos pertecentes a essa turma da tabela aluno
                    Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!dataBase.deletarAlunoDeTurma(Integer.valueOf(id_Class))) {  //Deleta todos os alunos pertecentes a essa turma
                    Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!studentsRegisteredClass.isEmpty()) {
                    for (String student : studentsRegisteredClass) {
                        id_student = dataBase.pegarIdAluno(student);
                        if (id_student != null && id_student != -1) {
                            if (!dataBase.cadastrarAlunoNaTurma(Integer.valueOf(id_Class), id_student)) {
                                Log.e("kariti", "Erro ao tentar vincular o aluno " + student + " a turma com id: " + id_Class);
                            }
                        }
                    }
                }
                int anonymousCurrent = 0;
                if (!numAnonymousCurrent.isEmpty()){
                    anonymousCurrent = Integer.parseInt(numAnonymousCurrent);
                }
                if (anonymousCurrent != 0) {
                    int t = numAnonymousCurrent.length();
                    for (int x = 1; x <= anonymousCurrent; x++) {
                        String nameAnonymous = "Aluno " + String.format("%0" + t + "d", x);
                        Integer id_anonymous = dataBase.cadastrarAluno(nameAnonymous, null, 0);
                        if (id_anonymous != -1) {
                            if (!dataBase.cadastrarAlunoNaTurma(Integer.valueOf(id_Class), id_anonymous)) {
                                Log.e("kariti", "Erro ao tentar vincular o aluno anonimo " + nameAnonymous + " a turma com id: " + id_Class);
                            }
                        }
                    }
                }
                Toast.makeText(ClassEditActivity.this, "Dados alterados com sucesso!", Toast.LENGTH_SHORT).show();
                restartDataClass();
            } catch (Exception e){
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
    public void restartDataClass(){
        setResult(RESULT_OK);
        finish();
    }

}