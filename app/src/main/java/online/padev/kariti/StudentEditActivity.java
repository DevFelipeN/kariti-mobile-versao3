package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.entity.Student;

public class StudentEditActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    ImageButton back;
    EditText editTxtNameStudent, editTxtEmail;
    Button btnSave;
    Student student;
    DataBaseKariti dataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_edit);

        editTxtNameStudent = findViewById(R.id.editTextAlunoCadastrado);
        editTxtEmail = findViewById(R.id.editTextEmailCadastrado);
        btnSave = findViewById(R.id.buttonSalvarEditAluno);
        back = findViewById(R.id.imgBtnVoltaEscola);

        dataBase = new DataBaseKariti(this);

        student = (Student) getIntent().getSerializableExtra("student");

        noticeEdit();

        editTxtNameStudent.setText(student.getNameStudent()); //Mostra o nome do aluno
        editTxtEmail.setText(student.getEmail()); //Mostra o e-mail do aluno

        btnSave.setOnClickListener(view -> {
            String nameStudentCurrent = editTxtNameStudent.getText().toString().trim();
            String emailCurrent = editTxtEmail.getText().toString().trim();
            if (nameStudentCurrent.equals(student.getNameStudent()) && emailCurrent.equals(student.getEmail())){
                Toast.makeText(this, "Sem alterações realizadas", Toast.LENGTH_SHORT).show();
                return;
            }
            if(nameStudentCurrent.isEmpty()){
                Toast.makeText(StudentEditActivity.this, "Informe o nome do aluno!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!student.getNameStudent().equals(nameStudentCurrent)){
                Boolean checkStudentExists = dataBase.checkExistStudent(nameStudentCurrent);
                if (checkStudentExists == null) {
                    Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (checkStudentExists) {
                    Toast.makeText(this, "Já existe um aluno com esse nome, cadastrado nesta escola!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (!emailCurrent.equals(student.getEmail()) && !emailCurrent.isEmpty()) {
                if (!Patterns.EMAIL_ADDRESS.matcher(emailCurrent).matches()) {
                    Toast.makeText(this, "E-mail inválido", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Boolean updateStudentBD = dataBase.updateStudentData(nameStudentCurrent, emailCurrent, student.getId_student());
            if (updateStudentBD == null){
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                return;
            }
            if (updateStudentBD) {
                Toast.makeText(StudentEditActivity.this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                restartStudentActivity();
            } else {
                Toast.makeText(StudentEditActivity.this, "Erro: alteração não realizada!", Toast.LENGTH_SHORT).show();
            }
        });
        back.setOnClickListener(view -> restartStudentActivity());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                restartStudentActivity();
            }
        });
    }
    private void restartStudentActivity(){
        setResult(RESULT_OK);
        finish();
    }

    public void popMenuAluno(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.activity_menualuno);
        popupMenu.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuExcluirAluno) {
            AlertDialog.Builder builder = new AlertDialog.Builder(StudentEditActivity.this);
            builder.setTitle("Atenção!")
                    .setMessage("Deseja realmente excluir o aluno?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        boolean deletarAluno = dataBase.deleteStudent(student.getId_student());
                        if (deletarAluno) {
                            Toast.makeText(this, "Aluno excluido com sucesso", Toast.LENGTH_SHORT).show();
                            restartStudentActivity();
                        }else{
                            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton("Não", (dialog, which) -> {
                        dialog.dismiss();
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        } else {
            return false;
        }
    }
    private void noticeEdit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(StudentEditActivity.this);
        builder.setTitle("Ajuda")
                .setMessage("Olá, caso deseje alterar as informações desse aluno, basta informar os novos dados nos campos e clicar em salvar.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}


