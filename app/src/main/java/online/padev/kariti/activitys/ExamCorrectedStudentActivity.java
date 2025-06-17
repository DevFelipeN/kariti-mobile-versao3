package online.padev.kariti.activitys;

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

public class ExamCorrectedStudentActivity extends AppCompatActivity {

    ImageButton back;
    String StudentName, status;
    Integer id_student, id_prova, numQuestions;
    DataBaseKariti dataBaseKariti;
    TextView textViewStudent, textViewNote;
    List<String> responseStudent, gabarito;
    List<Float> peso;
    TextView title;
    float note = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_corrected_student);

        back = findViewById(R.id.imgBtnVoltar);
        textViewStudent = findViewById(R.id.textViewDetalheAluno);
        textViewNote = findViewById(R.id.textViewNotaTotalDetalhe);
        title = findViewById(R.id.toolbar_title);

        dataBaseKariti = new DataBaseKariti(this);

        title.setText(String.format("%s","Detalhes"));

        id_student = Objects.requireNonNull(getIntent().getExtras()).getInt("id_aluno");
        id_prova = getIntent().getExtras().getInt("id_prova");
        StudentName = dataBaseKariti.getStudentName(id_student);
        numQuestions = dataBaseKariti.getNumberQuestions(id_prova.toString());

        if (StudentName == null || numQuestions == null){ //vericação caso ocorra exceções no Banco
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            finish();
        }

        textViewStudent.setText(String.format("%s","Aluno: "+ StudentName));
        //Carrega todas as respostas ordenadas por questao
        responseStudent = dataBaseKariti.listAnswerGivenString(id_prova, id_student); // lista as respostas dos alunos em formato de letras
        gabarito = dataBaseKariti.listAnswerKeyString(id_prova); // lista as respostas do gabarito em formato de letras
        peso = dataBaseKariti.listGradeByQuestion(id_prova);

        if (responseStudent == null || gabarito == null || peso == null){ //vericação caso ocorra exceções no Banco
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
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
            if(gabarito.get(x-1).equals(responseStudent.get(x-1))){
                note += peso.get(x-1);
                status = "CERTA";
            }else {status = "ERRADA";}

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
            cell3.setText(gabarito.get(x-1));
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
        textViewNote.setText(String.format("Nota total obtida: %.2f pontos", note));


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