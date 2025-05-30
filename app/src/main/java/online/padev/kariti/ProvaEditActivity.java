package online.padev.kariti;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import online.padev.kariti.entity.Exam;
import online.padev.kariti.database.DataBaseKariti;

public class ProvaEditActivity extends AppCompatActivity {
    EditText editTextNameProva, editTextNumQuestions, editTextNumAlternatives;
    private Spinner spinnerClass;
    ImageButton btnLessQuestions, btnMoreQuestions, btnLessAlternatives, btnMoreAlternatives, back;
    Button btnNext, btnDate;
    Calendar calendar;
    private String classNameCurrent;
    private Integer id_provaBD;
    TextView title;
    DataBaseKariti dataBaseKariti;
    Exam examBD, examCurrent;
    private int status;
    private List<String> listClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_registration);

        back = findViewById(R.id.imgBtnVoltar);
        btnDate = findViewById(R.id.datePickerButton);
        editTextNameProva = findViewById(R.id.editTextNomeProva);
        spinnerClass = findViewById(R.id.spinnerTurmaPprova);
        title = findViewById(R.id.toolbar_title);
        editTextNumQuestions = findViewById(R.id.editTextQtdQuests);
        editTextNumAlternatives = findViewById(R.id.editTextQtdAlter);

        btnLessQuestions = findViewById(R.id.imageButtonMenosQuest);
        btnMoreQuestions = findViewById(R.id.imageButtonMaisQuest);
        btnLessAlternatives = findViewById(R.id.imgBtnMenoAlter);
        btnMoreAlternatives = findViewById(R.id.imgBtnMaisAlter);
        btnNext = findViewById(R.id.btnGerarProva);

        title.setText(String.format("%s","Editar Prova"));

        dataBaseKariti = new DataBaseKariti(this);

        id_provaBD = Objects.requireNonNull(getIntent().getExtras()).getInt("id_prova");

        examBD = new Exam(id_provaBD, dataBaseKariti);
        examCurrent = new Exam();

        editTextNameProva.setText(String.format("%s", examBD.getNameExam()));
        editTextNumQuestions.setText(String.format("%s", examBD.getNumQuestions()));
        editTextNumAlternatives.setText(String.format("%s", examBD.getNumAlternatives()));
        btnDate.setText(examBD.dateToDisplay());

        listClass = dataBaseKariti.listClassNames(); // Lista todas as turmas da escola atual
        int position = listClass.indexOf(dataBaseKariti.getClassName(examBD.getClass_id().toString()));
        AdapterSpinner adapter = new AdapterSpinner(this, listClass);
        spinnerClass.setAdapter(adapter);
        spinnerClass.setSelection(position);

        btnMoreQuestions.setOnClickListener(view -> {
            int quest = 0;
            if (!editTextNumQuestions.getText().toString().trim().isEmpty()){
                quest = Integer.parseInt(editTextNumQuestions.getText().toString());
            }
            if(quest < 20)
                quest ++;
            editTextNumQuestions.setText(String.valueOf(quest));
        });
        btnLessQuestions.setOnClickListener(view -> {
            int quest = 0;
            if (!editTextNumQuestions.getText().toString().trim().isEmpty()){
                quest = Integer.parseInt(editTextNumQuestions.getText().toString());
            }
            if(quest > 0)
                quest --;
            editTextNumQuestions.setText(String.valueOf(quest));
        });
        btnMoreAlternatives.setOnClickListener(view -> {
            int alter = 0;
            if (!editTextNumAlternatives.getText().toString().trim().isEmpty()) {
                alter = Integer.parseInt(editTextNumAlternatives.getText().toString());
            }
            if(alter < 6)
                alter ++;
            editTextNumAlternatives.setText(String.valueOf(alter));
        });
        btnLessAlternatives.setOnClickListener(view -> {
            int alter = 0;
            if (!editTextNumAlternatives.getText().toString().trim().isEmpty()) {
                alter = Integer.parseInt(editTextNumAlternatives.getText().toString());
            }
            if(alter > 0)
                alter --;
            editTextNumAlternatives.setText(String.valueOf(alter));
        });


        calendar = Calendar.getInstance();
        btnDate.setOnClickListener(v -> {
            // Cria um DatePickerDialog com a data atual
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ProvaEditActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // Atualiza a data no calendário quando o usuário seleciona uma nova data
                        calendar.set(year, monthOfYear, dayOfMonth);
                        // Atualiza o texto do botão com a data selecionada
                        btnDate.setText(formatDateToDisplay(calendar));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            // Exibe o DatePickerDialog
            datePickerDialog.show();
        });

        btnNext.setOnClickListener(v -> {

            try {
                classNameCurrent = spinnerClass.getSelectedItem().toString(); // nome da turma não tem como ser vazio!
                examCurrent.setExam_id(id_provaBD);
                examCurrent.setNameExam(editTextNameProva.getText().toString());
                examCurrent.setDateExam(btnDate.getText().toString());
                examCurrent.setClass_id(dataBaseKariti.getClassId(classNameCurrent));

                if (examCurrent.getNameExam().trim().isEmpty()) { //verifica se o campo prova esta vazio
                    Toast.makeText(ProvaEditActivity.this, "Informe o nome da Prova!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(editTextNumQuestions.getText().toString().trim().isEmpty() || editTextNumQuestions.getText().toString().equals("0")){
                    Toast.makeText(this, "Informe a quantidade de questões!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(editTextNumAlternatives.getText().toString().trim().isEmpty() || editTextNumAlternatives.getText().toString().equals("0")){
                    Toast.makeText(this, "Informe a quantidade de alternativas!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Integer.parseInt(editTextNumQuestions.getText().toString()) > 20){
                    dialogLimitMaxQuest();
                    return;
                }
                if (Integer.parseInt(editTextNumAlternatives.getText().toString()) > 6) {
                    dialogLimitMaxAlter();
                    return;
                }
                examCurrent.setNumQuestions(Integer.parseInt(editTextNumQuestions.getText().toString()));
                examCurrent.setNumAlternatives(Integer.parseInt(editTextNumAlternatives.getText().toString()));

                if (examCurrent.isDifferent(examBD)) { //Verifica se os dados da prova foram alterados
                    if (!examCurrent.getNameExam().equals(examBD.getNameExam()) || !examCurrent.getClass_id().equals(examBD.getClass_id())) {
                        Boolean verificaProva = dataBaseKariti.checkIfExistExam(examCurrent.getNameExam(), examCurrent.getClass_id().toString());
                        if (verificaProva == null) {
                            Toast.makeText(ProvaEditActivity.this, "Erro na comunicação, tente novamente!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (verificaProva) {
                            Toast.makeText(ProvaEditActivity.this, "Esta turma já pussui uma prova cadastrada com esse nome, " + examCurrent.getNameExam(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    status = 1; // isso indica que foram realizadas alterações nos dados da prova
                    confirmeAlteracaoDados();
                } else {
                    status = 0; // isso indica que nem uma alteração foi realizada nos dados da prova
                    carregarTelaGabarito();
                }
            }catch (Exception e){
                Toast.makeText(ProvaEditActivity.this, "Algo de errado ocorreu, tente novamente!", Toast.LENGTH_SHORT).show();
                Log.e("kariti", e.toString());
                finish();
            }
        });

        back.setOnClickListener(view -> {
            getOnBackPressedDispatcher();
            finish();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
    private String formatDateToDisplay(Calendar calendar) {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    private void confirmeAlteracaoDados(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("ATENÇÃO")
                .setMessage("Confirma as alterações realizadas para esta prova? ")
                .setPositiveButton("Confirmar", (dialog, which) -> carregarTelaGabarito())
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void carregarTelaGabarito(){
        Log.e("kariti", "status: "+status);
        Intent intent = new Intent(getApplicationContext(), GabaritoActivity.class);
        if (status == 0){
            intent.putExtra("prova", examBD);
        }else{
            intent.putExtra("prova", examCurrent);
        }
        intent.putExtra("direcao", "edicao");
        intent.putExtra("status", status);
        startActivity(intent);
        finish();
    }

    private void dialogLimitMaxQuest(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Atualmente o Kariti oferece suporte para cartões repostas com no máximo 20 questões!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void dialogLimitMaxAlter(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Atualmente o Kariti oferece suporte para cartões repostas com no máximo 6 alternativas!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}