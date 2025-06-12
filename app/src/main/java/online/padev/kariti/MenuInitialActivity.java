package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import online.padev.kariti.database.DataBaseKariti;

public class MenuInitialActivity extends AppCompatActivity {
    ImageButton back, iconHelp;
    Button btnClass, btnStudent, btnProva;
    TextView textViewSchool;
    DataBaseKariti dataBase;
    private String nameSchool;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ActivityLocale.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_initial);

        back = findViewById(R.id.imgBtnVoltaDescola);
        back.setVisibility(View.VISIBLE);
        iconHelp = findViewById(R.id.iconHelp);

        btnClass = findViewById(R.id.btnTurma);
        btnStudent = findViewById(R.id.buttonAluno);
        btnProva = findViewById(R.id.btnProva);
        textViewSchool = findViewById(R.id.toolbar_title);

        dataBase = new DataBaseKariti(this);

        nameSchool = dataBase.getSchoolName();
        if (nameSchool == null){ //vericação caso ocorra exceções no Banco
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            finish();
        }

        textViewSchool.setText(nameSchool);

        btnClass.setOnClickListener(v -> startClass());
        btnStudent.setOnClickListener(v -> startStudent());
        btnProva.setOnClickListener(v -> startProva());
        iconHelp.setOnClickListener(v -> help());

        back.setOnClickListener(v -> {
            DataBaseKariti.ID_ESCOLA = null;
            finish();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                DataBaseKariti.ID_ESCOLA = null;
                finish();
            }
        });
    }
    private void startClass(){
        Intent intent = new Intent(this, ClassActivity.class);
        startActivity(intent);
    }
    private void startStudent(){
        Intent intent = new Intent(this, StudentActivity.class);
        startActivity(intent);
    }
    private void startProva(){
        Intent intent = new Intent(this, ProvaActivity.class);
        startActivity(intent);
    }

    public void help() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("• Clique na opção \"Aluno\" para cadastrar seus estudantes, independentemente das turmas às quais eles pertencem. Caso não deseje cadastrar os alunos, será possível cadastrar estudantes anônimos (sem definição de nome) em etapa posterior.\n\n\n" +
                "• Clique na opção \"Turma\" para cadastrar as turmas de estudantes e para vincular os estudantes correspondentes (podendo também ser inseridos alunos anônimos nesta etapa).\n\n" +
                "• Após cadastrada a turma e vinculados os alunos correspondentes, clique em \"Prova\" para cadastrar as informações sobre uma prova a ser aplicada, incluindo suas informações básicas e seu gabarito.\n");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

}