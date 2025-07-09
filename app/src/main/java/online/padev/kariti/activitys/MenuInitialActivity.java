package online.padev.kariti.activitys;

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

import online.padev.kariti.R;
import online.padev.kariti.settings.ActivityLocale;
import online.padev.kariti.database.DataBaseKariti;

public class MenuInitialActivity extends AppCompatActivity {
    ImageButton back, iconHelp;
    Button btnClass, btnStudent, btnExam;
    TextView textViewSchool;
    DataBaseKariti dataBase;

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
        btnExam = findViewById(R.id.btnProva);
        textViewSchool = findViewById(R.id.toolbar_title);

        dataBase = new DataBaseKariti(this);

        String nameSchool = dataBase.getSchoolName();
        if (nameSchool == null){ //vericação caso ocorra exceções no Banco
            Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            finish();
        }

        textViewSchool.setText(nameSchool);

        btnClass.setOnClickListener(v -> startClass());
        btnStudent.setOnClickListener(v -> startStudent());
        btnExam.setOnClickListener(v -> startExam());
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
    private void startExam(){
        Intent intent = new Intent(this, ExamActivity.class);
        startActivity(intent);
    }

    public void help() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.titleHelp));
        builder.setMessage(getString(R.string.longTextHelpMenu));
        builder.setPositiveButton(getString(R.string.okDescription), (dialog, which) -> dialog.dismiss());
        builder.show();
    }

}