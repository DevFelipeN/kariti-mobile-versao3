package online.padev.kariti;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EdicaoProva extends AppCompatActivity {
    private EditText nomeProva;
    private TextView questoes, alternativas;
    private Spinner turma;
    private ImageButton menosQuestoes, maisQuestoes, menosAlternativas, maisAlternativas;
    private Button proximo, data;
    private Calendar calendar;
    private String dataFormatada, prova, nomeTurma;
    private Integer id_prova, id_turma;
    BancoDados bancoDados;
    private ArrayList<String> listTurma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicao_prova);

        data = findViewById(R.id.NovaData);
        nomeProva = findViewById(R.id.EdicaoProva);
        turma = findViewById(R.id.EdicaoTurma);

        bancoDados = new BancoDados(this);
        Log.e("kariti", "Aqui 3");

        id_prova = getIntent().getExtras().getInt("id_prova");
        prova = getIntent().getExtras().getString("prova");
        id_turma = getIntent().getExtras().getInt("id_turma");
        nomeTurma = bancoDados.pegaNomeTurma(id_turma.toString());
        nomeProva.setText(prova);
        Log.e("kariti", "Aqui 4");

        listTurma = (ArrayList<String>) bancoDados.obterNomeTurmas();
        listTurma.add(0, nomeTurma);
        SpinnerAdapter adapter = new SpinnerAdapter(this, listTurma);
        turma.setAdapter(adapter);
        Log.e("kariti", "Aqui 5");

        calendar = Calendar.getInstance();
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cria um DatePickerDialog com a data atual
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EdicaoProva.this,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            // Atualiza a data no calendário quando o usuário seleciona uma nova data
                            calendar.set(year, monthOfYear, dayOfMonth);
                            // Atualiza o texto do botão com a data selecionada
                            data.setText(formatDate(calendar));
                            dataFormatada = formatDateBanco(calendar);
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

                // Exibe o DatePickerDialog
                datePickerDialog.show();
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
}