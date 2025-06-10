package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import online.padev.kariti.adapters.AdapterClickableList;
import online.padev.kariti.adapters.StudentsClickableAdapter;
import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.entity.Student;

public class StudentActivity extends AppCompatActivity {
    ImageButton back;
    EditText editTextSearch;
    List<Student> students;
    FloatingActionButton btnRegistration;
    StudentsClickableAdapter adapterStudent;
    TextView titleActivity, textViewNumStudents, descritionAddStudent;
    RecyclerView recyclerView;
    private static final int REQUEST_CODE = 1;
    private Integer id_student;

    DataBaseKariti dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        back = findViewById(R.id.imgBtnVoltar);
        btnRegistration = findViewById(R.id.iconaddaluno);
        editTextSearch = findViewById(R.id.editTextBuscar);
        recyclerView = findViewById(R.id.listSelecAluno);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataBase = new DataBaseKariti(this);
        textViewNumStudents = findViewById(R.id.totalAlunos);
        descritionAddStudent = findViewById(R.id.txtDescricaoAddAluno);

        titleActivity = findViewById(R.id.toolbar_title);
        titleActivity.setText(String.format("%s","Alunos"));

        students = dataBase.listStudentsData(1);
        if (students == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            finish();
        }
        if(students.isEmpty()){
            startRegistrationStudent();
        }

        textViewNumStudents.setText(String.format("%s","Total de Alunos: "+ students.size()));

        adapterStudent = new StudentsClickableAdapter(this, students, this::onItemClick, this::onItemLongClick);
        recyclerView.setAdapter(adapterStudent);

        editTextSearch.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){
                adapterStudent.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable editable){
            }
        });
        //Exibir o texto sobre o botão
        descritionAddStudent.setVisibility(View.VISIBLE);
        descritionAddStudent.setVisibility(View.VISIBLE);
        // Ocultar o texto após 3 segundos
        new Handler().postDelayed(() -> descritionAddStudent.setVisibility(View.INVISIBLE), 10000);
        new Handler().postDelayed(() -> descritionAddStudent.setVisibility(View.INVISIBLE), 10000);

        btnRegistration.setOnClickListener(v -> {
            descritionAddStudent.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> descritionAddStudent.setVisibility(View.INVISIBLE), 3000);
            startRegistrationStudent();
        });
        back.setOnClickListener(view -> {
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
        Intent intent = new Intent(this, StudentEditActivity.class);
        intent.putExtra("student", students.get(position));
        startActivityForResult(intent, REQUEST_CODE);
    }
    public void onItemLongClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StudentActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Deseja realmente excluir esse aluno?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    id_student = students.get(position).getId_student();
                    Boolean checkStudentExists = dataBase.checkIfStudentInClass(id_student);
                    if(checkStudentExists == null){
                        Toast.makeText(StudentActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!checkStudentExists){
                        boolean isDeleteStudent = dataBase.deleteStudent(id_student);
                        if (isDeleteStudent) {
                            Student s = students.remove(position);
                            adapterStudent.deleteStudent(s); // isso garante que o aluno não apareça quando uma nova pesquisa é realizada
                            adapterStudent.notifyItemRemoved(position);
                            if(students.isEmpty()){
                                finish();
                            }
                            textViewNumStudents.setText(String.format("%s","Total de Alunos: "+ students.size()));
                            Toast.makeText(StudentActivity.this, "Aluno Excluido! ", Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(StudentActivity.this, "Erro: aluno não excluido!", Toast.LENGTH_SHORT).show();
                    }else notifyImpossibleDelete();
                })
                .setNegativeButton("Não", (dialog, which) -> {
                    dialog.dismiss();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                List<Student> studentsAux = dataBase.listStudentsData(1);
                adapterStudent.updateData(studentsAux);
                textViewNumStudents.setText(String.format("%s","Total de Alunos: "+ students.size()));
            }else{
                finish();
            }
        }
    }
    private void notifyImpossibleDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(StudentActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Este aluno possui vínculo com uma ou mais turma(s) cadastrada(s), não sendo possível excluir!.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void startRegistrationStudent(){
        Intent intent = new Intent(this, StudentRegistrationActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }
}