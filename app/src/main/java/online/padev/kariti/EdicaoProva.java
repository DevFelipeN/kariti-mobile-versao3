package online.padev.kariti;

import android.app.DatePickerDialog;
import android.content.Intent;
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
    private String dataFormatada, dataCadastrada, prova, nomeTurma;
    private Integer id_prova, id_turma;
    private TextView titulo;
    BancoDados bancoDados;
    private Integer qtdQuestoesCadastradas, qtdAlternativasCadastradas;
    private ArrayList<String> listTurma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edicao_prova);

        data = findViewById(R.id.NovaData);
        nomeProva = findViewById(R.id.EdicaoProva);
        turma = findViewById(R.id.EdicaoTurma);
        titulo = findViewById(R.id.toolbar_title);
        questoes = findViewById(R.id.qtdQuestoes);
        alternativas = findViewById(R.id.qtdAlternativas);
        menosQuestoes = findViewById(R.id.menosquest);
        maisQuestoes = findViewById(R.id.maisquest);
        menosAlternativas = findViewById(R.id.menosAlter);
        maisQuestoes = findViewById(R.id.maisAlter);
        proximo = findViewById(R.id.btnEditProximo);

        titulo.setText("Edição");

        bancoDados = new BancoDados(this);

        id_prova = Objects.requireNonNull(getIntent().getExtras()).getInt("id_prova");
        prova = getIntent().getExtras().getString("prova");
        id_turma = getIntent().getExtras().getInt("id_turma");
        nomeTurma = bancoDados.pegaNomeTurma(id_turma.toString());
        qtdQuestoesCadastradas = bancoDados.pegaqtdQuestoes(id_prova.toString());
        qtdAlternativasCadastradas = bancoDados.pegaqtdAlternativas(id_prova.toString());
        dataCadastrada = bancoDados.pegaData(id_prova.toString());

        this.dataCadastrada = formataDataCadastrada(dataCadastrada);

        nomeProva.setText(prova);
        questoes.setText(String.valueOf(qtdQuestoesCadastradas));
        alternativas.setText(String.valueOf(qtdAlternativasCadastradas));
        data.setText(dataCadastrada);

        listTurma = (ArrayList<String>) bancoDados.obterNomeTurmas();
        listTurma.add(0, nomeTurma);
        SpinnerAdapter adapter = new SpinnerAdapter(this, listTurma);
        turma.setAdapter(adapter);

        maisQuestoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quest = Integer.parseInt(questoes.getText().toString());
                if(quest < 20)
                    quest ++;
                questoes.setText(String.valueOf(quest));
            }
        });
        menosQuestoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quest = Integer.parseInt(questoes.getText().toString());
                if(quest > 0)
                    quest --;
                questoes.setText(String.valueOf(quest));
            }
        });
        maisAlternativas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int alter = Integer.parseInt(alternativas.getText().toString());
                if(alter < 7)
                    alter ++;
                alternativas.setText(String.valueOf(alter));
            }
        });
        menosAlternativas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int alter = Integer.parseInt(alternativas.getText().toString());
                if(alter > 0)
                    alter --;
                alternativas.setText(String.valueOf(alter));
            }
        });

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
        proximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String novaProva = nomeProva.getText().toString();
                String novaTurma = turma.getSelectedItem().toString();
                String novaData = data.getText().toString();
                Integer novaQuestao = Integer.valueOf(questoes.getText().toString());
                Integer novaAlternativa = Integer.valueOf(alternativas.getText().toString());
                if(!novaProva.trim().isEmpty()){ //verifica se o campo prova esta vazio
                    if(!novaProva.equals(prova) || !novaTurma.equals(nomeTurma) || !novaData.equals(dataCadastrada) || !novaQuestao.equals(qtdQuestoesCadastradas) || !novaAlternativa.equals(qtdAlternativasCadastradas)){
                        id_turma = bancoDados.pegaIdTurma(novaTurma); //pega o id da turma
                        if(novaQuestao.equals(0) || novaAlternativa.equals(0)){
                            Toast.makeText(EdicaoProva.this, "Quantidade de questões e/ou alternativas, não pode ser igual a 0.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(getApplicationContext(), GabaritoActivity.class);
                        intent.putExtra("id_prova", id_prova);
                        intent.putExtra("status", true);
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(getApplicationContext(), GabaritoActivity.class);
                        intent.putExtra("id_prova", id_prova);
                        intent.putExtra("status", false);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    Toast.makeText(EdicaoProva.this, "Informe o nome da Prova!", Toast.LENGTH_SHORT).show();
                }
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
    private String formataDataCadastrada(String data){
        String[] itens = data.split("-");
        String dataFor = itens[2]+"/"+itens[1]+"/"+itens[0];
        return dataFor;
    }
}