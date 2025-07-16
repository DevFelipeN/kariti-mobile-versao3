package online.padev.kariti.activitys;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import online.padev.kariti.R;
import online.padev.kariti.adapters.AdapterSpinner;
import online.padev.kariti.entity.Exam;
import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.settings.ActivityLocale;

public class ExamRegistrationActivity extends AppCompatActivity {
    Button datePickerButton;
    Calendar calendar;
    EditText editTextNameExam, editTextNumQuestion, editTextNumAlternative;
    Button btnRegistrationExam;
    Spinner spinnerClass;
    DataBaseKariti dataBaseKariti;
    Exam exam;
    List<String> listClass;
    ImageButton back, lessQuestions, moreQuestions, moreAlternatives, lessAlternatives;
    private String dateFormatting;
    TextView title;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ActivityLocale.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_registration);

        datePickerButton = findViewById(R.id.datePickerButton);
        back = findViewById(R.id.imgBtnVoltar);
        btnRegistrationExam = findViewById(R.id.btnGerarProva);
        editTextNameExam = findViewById(R.id.editTextNomeProva);
        editTextNumQuestion = findViewById(R.id.editTextQtdQuests);
        editTextNumAlternative = findViewById(R.id.editTextQtdAlter);
        moreQuestions = findViewById(R.id.imageButtonMaisQuest);
        lessQuestions = findViewById(R.id.imageButtonMenosQuest);
        moreAlternatives = findViewById(R.id.imgBtnMaisAlter);
        lessAlternatives = findViewById(R.id.imgBtnMenoAlter);
        spinnerClass = findViewById(R.id.spinnerTurmaPprova);
        title = findViewById(R.id.toolbar_title);

        dataBaseKariti = new DataBaseKariti(this);
        exam = new Exam();

        title.setText(getString(R.string.titleNewExam));

        listClass = dataBaseKariti.listClassNames(); //Obtem a lista das turmas delimitadas pertecentes a escola atual
        if(listClass == null){
            Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            finish();
        }
        listClass.add(0, getString(R.string.titleSelectClass));
        AdapterSpinner adapterClass = new AdapterSpinner(this, listClass);
        spinnerClass.setAdapter(adapterClass);

        moreQuestions.setOnClickListener(v -> {
            int quest = 0;
            if (!editTextNumQuestion.getText().toString().trim().isEmpty()){
                quest = Integer.parseInt(editTextNumQuestion.getText().toString());
            }
            if(quest < 20)
                quest ++;
            editTextNumQuestion.setText(String.valueOf(quest));
        });
        lessQuestions.setOnClickListener(v -> {
            int quest = 0;
            if (!editTextNumQuestion.getText().toString().trim().isEmpty()){
                quest = Integer.parseInt(editTextNumQuestion.getText().toString());
            }
            if(quest > 0)
                quest --;
            editTextNumQuestion.setText(String.valueOf(quest));
        });
        moreAlternatives.setOnClickListener(v -> {
            int alter = 0;
            if (!editTextNumAlternative.getText().toString().trim().isEmpty()) {
                alter = Integer.parseInt(editTextNumAlternative.getText().toString());
            }
            if(alter < 6)
                alter ++;
            editTextNumAlternative.setText(String.valueOf(alter));
        });
        lessAlternatives.setOnClickListener(v -> {
            int alter = 0;
            if (!editTextNumAlternative.getText().toString().trim().isEmpty()) {
                alter = Integer.parseInt(editTextNumAlternative.getText().toString());
            }
            if(alter > 0)
                alter --;
            editTextNumAlternative.setText(String.valueOf(alter));
        });
        btnRegistrationExam.setOnClickListener(v -> {
            btnRegistrationExam.setEnabled(false);
            try {
                exam.setNameExam(editTextNameExam.getText().toString());

                if (exam.getNameExam().trim().isEmpty()) {
                    Toast.makeText(this, getString(R.string.editTextNomeExam), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (spinnerClass.getSelectedItem() == getString(R.string.titleSelectClass)) {
                    Toast.makeText(this, getString(R.string.toastSelectAclass), Toast.LENGTH_SHORT).show();
                    return;
                }
                String nameClass = spinnerClass.getSelectedItem().toString();
                exam.setClass_id(dataBaseKariti.getClassId(nameClass));

                if (datePickerButton.getText().toString().equals(getString(R.string.buttonData))) {
                    Toast.makeText(this, getString(R.string.toastSelectDate), Toast.LENGTH_SHORT).show();
                    return;
                }
                exam.setDateExam(datePickerButton.getText().toString());

                if (editTextNumQuestion.getText().toString().trim().isEmpty() || editTextNumQuestion.getText().toString().equals("0")) {
                    Toast.makeText(this, getString(R.string.toastInfoNumQuestions), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editTextNumAlternative.getText().toString().trim().isEmpty() || editTextNumAlternative.getText().toString().equals("0")) {
                    Toast.makeText(this, getString(R.string.toastInfoNumAlternatives), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Integer.parseInt(editTextNumQuestion.getText().toString()) > 20) {
                    notifyLimitMaxQuestions();
                    return;
                }
                if (Integer.parseInt(editTextNumAlternative.getText().toString()) > 6) {
                    notifyLimitMaxAlternatives();
                    return;
                }

                exam.setNumQuestions(Integer.parseInt(editTextNumQuestion.getText().toString()));
                exam.setNumAlternatives(Integer.parseInt(editTextNumAlternative.getText().toString()));

                if (exam.getClass_id() == null) {
                    Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                    return;
                }

                Boolean checkExistsExam = dataBaseKariti.checkIfExistExam(exam.getNameExam(), exam.getClass_id().toString());
                if (checkExistsExam == null) {
                    Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (checkExistsExam) {
                    Toast.makeText(this, getString(R.string.toastExistExamInClass, exam.getNameExam()), Toast.LENGTH_SHORT).show();
                    return;
                }
                startGenerateAnswerKey();
            }catch (Exception e){
                Log.e("kariti", e.toString());
                Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            } finally {
                btnRegistrationExam.setEnabled(true);
            }
        });
        // Obtém a instância do calendário com a data atual
        calendar = Calendar.getInstance();
        datePickerButton.setOnClickListener(v -> {
            // Cria um DatePickerDialog com a data atual
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ExamRegistrationActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // Atualiza a data no calendário quando o usuário seleciona uma nova data
                        calendar.set(year, monthOfYear, dayOfMonth);
                        // Atualiza o texto do botão com a data selecionada
                        datePickerButton.setText(formatDate(calendar));
                        dateFormatting = formatDateBanco(calendar);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            // Exibe o DatePickerDialog
            datePickerDialog.show();
        });
        back.setOnClickListener(v -> finish());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
    private String formatDate(Calendar calendar) {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }
    private String formatDateBanco(Calendar calendar) {
        String dateFormat = "yyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }
    private void startGenerateAnswerKey(){
        Intent intent = new Intent(this, AnswerKeyActivity.class);
        intent.putExtra("prova", exam);
        intent.putExtra("direcao", "novaProva");
        startActivity(intent);
        finish();
    }
    private void notifyLimitMaxQuestions(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.titleAttention));
        builder.setMessage(getString(R.string.longTextLimitQuestions, Exam.MAX_QUESTIONS));
        builder.setPositiveButton(getString(R.string.okDescription), (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void notifyLimitMaxAlternatives(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.titleAttention));
        builder.setMessage(getString(R.string.longTextLimitAlternatives, Exam.MAX_ALTERNATIVES));
        builder.setPositiveButton(getString(R.string.okDescription), (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}