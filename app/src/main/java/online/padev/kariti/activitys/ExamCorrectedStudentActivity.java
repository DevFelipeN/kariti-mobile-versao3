package online.padev.kariti.activitys;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Objects;

import online.padev.kariti.R;
import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.settings.ActivityLocale;

public class ExamCorrectedStudentActivity extends AppCompatActivity {

    ImageButton back;
    String studentName, status;
    Integer id_student, exam_id, numQuestions;
    DataBaseKariti dataBaseKariti;
    TextView textViewStudent, textViewNote;
    List<String> responseStudent, answerKey;
    List<Float> peso;
    TextView title;
    float note = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ActivityLocale.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_corrected_student);

        back = findViewById(R.id.imgBtnVoltar);
        textViewStudent = findViewById(R.id.textViewDetalheAluno);
        textViewNote = findViewById(R.id.textViewNotaTotalDetalhe);
        title = findViewById(R.id.toolbar_title);

        dataBaseKariti = new DataBaseKariti(this);

        title.setText(getString(R.string.descriptionDetails));

        id_student = Objects.requireNonNull(getIntent().getExtras()).getInt("id_aluno");
        exam_id = getIntent().getExtras().getInt("id_prova");
        studentName = dataBaseKariti.getStudentName(id_student);
        numQuestions = dataBaseKariti.getNumberQuestions(exam_id.toString());

        if (studentName == null || numQuestions == null){ //vericação caso ocorra exceções no Banco
            Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            finish();
        }

        textViewStudent.setText(getString(R.string.descriptionStudent, studentName));
        //Carrega todas as respostas ordenadas por questao
        responseStudent = dataBaseKariti.listAnswerGivenString(exam_id, id_student); // lista as respostas dos alunos em formato de letras
        answerKey = dataBaseKariti.listAnswerKeyString(exam_id); // lista as respostas do gabarito em formato de letras
        peso = dataBaseKariti.listGradeByQuestion(exam_id);

        if (responseStudent == null || answerKey == null || peso == null){ //vericação caso ocorra exceções no Banco
            Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            finish();
        }

        ShapeDrawable border = new ShapeDrawable(new RectShape());
        border.getPaint().setColor(0xFF000000); // Cor da borda
        border.getPaint().setStrokeWidth(1); // Largura da borda
        border.getPaint().setStyle(Paint.Style.STROKE);

        int numResponseStudent = responseStudent.size(); //Quantidade de respostas cadastradas no BD
        if (numResponseStudent < numQuestions){ //Caso quantidade de respostas dadas menor q de questões
            for (int a = numResponseStudent; a < numQuestions; a++){
                responseStudent.add(a, "-"); //Aumenta o tamanho da lista até o tamanho da questões
            }
        }

        for(int x = 1; x <= numQuestions; x++) {
            if(answerKey.get(x-1).equals(responseStudent.get(x-1))){
                note += peso.get(x-1);
                status = getString(R.string.descriptionCorrect);
            }else {status = getString(R.string.descriptionIncorrect);}

            TableLayout tableLayout = findViewById(R.id.tableLayoutDetalheCorrecao);
            TableRow row = new TableRow(this);
            row.setBackground(border);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);

            // Cria uma célula para a nova linha para armazenar a questão
            TextView cell1 = new TextView(this);
            cell1.setText(String.valueOf(x));
            cell1.setGravity(Gravity.CENTER);
            cell1.setTextSize(16);
            row.addView(cell1);

            // Cria outra célula para a nova linha para armazenar a resposta marcada pelo aluno
            TextView cell2 = new TextView(this);
            cell2.setText(responseStudent.get(x-1));
            cell2.setGravity(Gravity.CENTER);
            cell2.setTextSize(16);
            row.addView(cell2);

            // Cria uma célula para a nova linha para armazenar a resposta do gabarito
            TextView cell3 = new TextView(this);
            cell3.setText(answerKey.get(x-1));
            cell3.setGravity(Gravity.CENTER);
            cell3.setTextSize(16);
            row.addView(cell3);

            // Cria outra célula para a nova linha para armazenar o status de acertos do aluno
            TextView cell4 = new TextView(this);
            cell4.setText(status);
            cell4.setTextSize(14);
            cell4.setGravity(Gravity.CENTER);
            row.addView(cell4);

            // Cria outra célula para a nova linha para armazenar o peso da questão
            TextView cell5 = new TextView(this);
            cell5.setText(String.valueOf(peso.get(x-1)));
            cell5.setGravity(Gravity.CENTER);
            cell5.setTextSize(16);
            row.addView(cell5);

            // Adiciona a nova linha à tabela
            tableLayout.addView(row);
        }
        textViewNote.setText(getString(R.string.txtViewNotaTotal, note));


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
}