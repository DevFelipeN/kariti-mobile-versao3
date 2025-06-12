package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
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

import online.padev.kariti.adapters.AdapterSpinner;
import online.padev.kariti.entity.Exam;
import online.padev.kariti.database.DataBaseKariti;

public class ProvaRegistrationActivity extends AppCompatActivity {
    Button datePickerButton;
    Calendar calendar;
    EditText editTextNameProva, editTextNumQuestion, editTextNumAlternative;
    Button btnRegistrationProva;
    Spinner spinnerClass;
    DataBaseKariti dataBaseKariti;
    Exam exam;
    List<String> listClass;
    ImageButton back, lessQuestions, moreQuestions, moreAlternatives, lessAlternatives;
    private String dateFormatting;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_registration);

        datePickerButton = findViewById(R.id.datePickerButton);
        back = findViewById(R.id.imgBtnVoltar);
        btnRegistrationProva = findViewById(R.id.btnGerarProva);
        editTextNameProva = findViewById(R.id.editTextNomeProva);
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

        title.setText(String.format("%s","Nova Prova"));

        listClass = dataBaseKariti.listClassNames(); //Obtem a lista das turmas delimitadas pertecentes a escola atual
        if(listClass == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            finish();
        }
        listClass.add(0, "Selecione a Turma");
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
        btnRegistrationProva.setOnClickListener(v -> {
            btnRegistrationProva.setEnabled(false);
            try {
                exam.setNameExam(editTextNameProva.getText().toString());

                if (exam.getNameExam().trim().isEmpty()) {
                    Toast.makeText(ProvaRegistrationActivity.this, "Informe o nome da prova!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (spinnerClass.getSelectedItem() == "Selecione a Turma") {
                    Toast.makeText(ProvaRegistrationActivity.this, "Selecione uma turma!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String nameClass = spinnerClass.getSelectedItem().toString();
                exam.setClass_id(dataBaseKariti.getClassId(nameClass));

                if (datePickerButton.getText().toString().equals("Selecionar Data")) {
                    Toast.makeText(ProvaRegistrationActivity.this, "Selecione uma data!", Toast.LENGTH_SHORT).show();
                    return;
                }
                exam.setDateExam(datePickerButton.getText().toString());

                if (editTextNumQuestion.getText().toString().trim().isEmpty() || editTextNumQuestion.getText().toString().equals("0")) {
                    Toast.makeText(this, "Informe a quantidade de questões!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (editTextNumAlternative.getText().toString().trim().isEmpty() || editTextNumAlternative.getText().toString().equals("0")) {
                    Toast.makeText(this, "Informe a quantidade de alternativas!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                    return;
                }

                Boolean checkExistsProva = dataBaseKariti.checkIfExistExam(exam.getNameExam(), exam.getClass_id().toString());
                if (checkExistsProva == null) {
                    Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (checkExistsProva) {
                    Toast.makeText(ProvaRegistrationActivity.this, "Esta turma já pussui uma prova cadastrada com o nome, " + exam.getNameExam(), Toast.LENGTH_SHORT).show();
                    return;
                }
                startGenerateGabarito();
            }catch (Exception e){
                Log.e("kariti", e.toString());
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            } finally {
                btnRegistrationProva.setEnabled(true);
            }
        });
        // Obtém a instância do calendário com a data atual
        calendar = Calendar.getInstance();
        datePickerButton.setOnClickListener(v -> {
            // Cria um DatePickerDialog com a data atual
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ProvaRegistrationActivity.this,
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
    private void startGenerateGabarito(){
        //Configuration c = this.getResources().getConfiguration();
        //Locale l = new Locale("en");
        //Locale.setDefault(l);
        //c.setLocale(l);
        //c.setLocale(Locale.ROOT);
        Intent intent = new Intent(this, GabaritoActivity.class);
        intent.putExtra("prova", exam);
        intent.putExtra("direcao", "novaProva");
        startActivity(intent);
        finish();
    }
    private void notifyLimitMaxQuestions(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Atualmente o Kariti oferece suporte para cartões repostas com no máximo 20 questões!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void notifyLimitMaxAlternatives(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Atualmente o Kariti oferece suporte para cartões repostas com no máximo 6 alternativas!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}