package online.padev.kariti.activitys;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import online.padev.kariti.R;
import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.settings.ActivityLocale;

public class StudentRegistrationActivity extends AppCompatActivity {
    ImageButton back;
    EditText editTextNameStudent, EditTextEmail;
    Button btnSave;
    DataBaseKariti dataBase;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ActivityLocale.wrap(newBase));
    }

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
                    Toast.makeText(StudentRegistrationActivity.this, getString(R.string.toastInfoNameStudent), Toast.LENGTH_SHORT).show();
                    return;
                }
                Boolean checkStudent = dataBase.checkExistStudent(nameStudent);
                if (checkStudent == null) {
                    Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (checkStudent) {
                    Toast.makeText(this, getString(R.string.toastStudentAlreadyRegister), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!email.isEmpty()) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(StudentRegistrationActivity.this, getString(R.string.toastInvalidEmail), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Boolean checkEmail = dataBase.checkStudentEmail(email);
                    if (checkEmail == null) {
                        Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (checkEmail) {
                        Toast.makeText(StudentRegistrationActivity.this, getString(R.string.toastEmailAlreadyLinkStudent), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Integer insertStudent = dataBase.insertStudent(nameStudent, email, 1);
                if (insertStudent != -1) {
                    Toast.makeText(StudentRegistrationActivity.this, getString(R.string.toastStudentSuccessRegistration), Toast.LENGTH_SHORT).show();
                    restartVisualStudents();
                } else {
                    Toast.makeText(StudentRegistrationActivity.this, getString(R.string.toastStudentFailedRegistration), Toast.LENGTH_SHORT).show();
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