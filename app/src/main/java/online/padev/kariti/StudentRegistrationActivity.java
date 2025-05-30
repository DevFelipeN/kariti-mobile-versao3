package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import online.padev.kariti.database.DataBaseKariti;

public class StudentRegistrationActivity extends AppCompatActivity {
    ImageButton back;
    EditText editTextNameStudent, EditTextEmail;
    Button btnSave;
    DataBaseKariti dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        editTextNameStudent = findViewById(R.id.editTextAlunoCad);
        EditTextEmail = findViewById(R.id.editTextEmailCad);
        back = findViewById(R.id.imgBtnVoltaEscola);
        btnSave = findViewById(R.id.buttonSalvarEdit);

        dataBase = new DataBaseKariti(this);

        btnSave.setOnClickListener(view -> {
            btnSave.setEnabled(false);
            try {
                String nameStudent = editTextNameStudent.getText().toString().trim();
                String email = EditTextEmail.getText().toString().trim();
                if (nameStudent.isEmpty()) {
                    Toast.makeText(StudentRegistrationActivity.this, "Informe o nome do aluno", Toast.LENGTH_SHORT).show();
                    return;
                }
                Boolean checkStudent = dataBase.checkExistStudent(nameStudent);
                if (checkStudent == null) {
                    Toast.makeText(this, "Erro de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (checkStudent) {
                    Toast.makeText(StudentRegistrationActivity.this, "Identificamos que esse aluno já está cadastrado!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!email.isEmpty()) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(StudentRegistrationActivity.this, "E-mail do aluno, inválido!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Boolean checkEmail = dataBase.checkStudentEmail(email);
                    if (checkEmail == null) {
                        Toast.makeText(this, "Erro de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (checkEmail) {
                        Toast.makeText(StudentRegistrationActivity.this, "Este e-mail já esta vinculado a um aluno cadastrado!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Integer insertStudent = dataBase.insertStudent(nameStudent, email, 1);
                if (insertStudent != -1) {
                    Toast.makeText(StudentRegistrationActivity.this, "Aluno cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                    restartVisualStudents();
                } else {
                    Toast.makeText(StudentRegistrationActivity.this, "Aluno não cadastrado!", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Log.e("kariti", e.toString());
            }finally {
                btnSave.setEnabled(true);
            }

        });
        back.setOnClickListener(view -> restartVisualStudents());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                restartVisualStudents();
            }
        });
    }
    public void restartVisualStudents(){
        if(dataBase.checkExistStudent()){
            setResult(RESULT_OK);
            finish();
        }else{
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}