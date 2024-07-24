package online.padev.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import online.padev.kariti.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CadProvaActivity extends AppCompatActivity {
    Button datePickerButton;
    Calendar calendar;
    EditText nomeProva;
    TextView qtdQuest, qtdAlter;
    Button btnGerProva;
    Spinner spinnerTurma;
    BancoDados bancoDados;
    ArrayList<String> listTurmaEmProva;
    ImageButton voltar, questMenos, questMais, altMais, altMenos;
    String dataform;
    Integer id_turma;
    private  TextView titulo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_prova);

        datePickerButton = findViewById(R.id.datePickerButton);
        voltar = findViewById(R.id.imgBtnVoltar);
        btnGerProva = findViewById(R.id.btnGerarProva);
        nomeProva = findViewById(R.id.editTextNomeProva);
        qtdQuest = findViewById(R.id.textViewQuantity);
        qtdAlter = findViewById(R.id.textVieAlter);
        questMais = findViewById(R.id.imageButtonMaisQuest);
        questMenos = findViewById(R.id.imageButtonMenosQuest);
        altMais = findViewById(R.id.imgBtnMaisAlter);
        altMenos = findViewById(R.id.imgBtnMenoAlter);
        spinnerTurma = findViewById(R.id.spinnerTurmaPprova);
        bancoDados = new BancoDados(this);
        titulo = findViewById(R.id.toolbar_title);

        titulo.setText("Nova Prova");

        listTurmaEmProva = (ArrayList<String>) bancoDados.obterNomeTurmas();
        listTurmaEmProva.add(0, "Selecione a Turma");
        SpinnerAdapter adapter = new SpinnerAdapter(this, listTurmaEmProva);
        spinnerTurma.setAdapter(adapter);

        questMais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quest = Integer.parseInt(qtdQuest.getText().toString());
                if(quest < 20)
                    quest ++;
                qtdQuest.setText(String.valueOf(quest));
            }
        });
        questMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quest = Integer.parseInt(qtdQuest.getText().toString());
                if(quest > 0)
                    quest --;
                qtdQuest.setText(String.valueOf(quest));
            }
        });
        altMais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int alter = Integer.parseInt(qtdAlter.getText().toString());
                if(alter < 7)
                    alter ++;
                qtdAlter.setText(String.valueOf(alter));
            }
        });
        altMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int alter = Integer.parseInt(qtdAlter.getText().toString());
                if(alter > 0)
                    alter --;
                qtdAlter.setText(String.valueOf(alter));
            }
        });
        btnGerProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = datePickerButton.getText().toString();
                String prova = nomeProva.getText().toString();
                Integer quest = Integer.valueOf(qtdQuest.getText().toString());
                Integer alter = Integer.valueOf(qtdAlter.getText().toString());
                String turma = spinnerTurma.getSelectedItem().toString();
                if(spinnerTurma.getSelectedItem() == "Selecione a Turma"){
                    Toast.makeText(CadProvaActivity.this, "Selecione uma turma!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(data.equals("Selecionar Data")){
                    Toast.makeText(CadProvaActivity.this, "Selecione uma data!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!prova.trim().isEmpty()){
                    id_turma = bancoDados.pegaIdTurma(turma);
                    Boolean existTurma = bancoDados.checkprovasNome(prova, id_turma.toString());
                    if(!existTurma) {
                        if(!quest.equals(0)){
                            if(!alter.equals(0)){
                                Intent intent = new Intent(getApplicationContext(), GabaritoActivity.class);
                                intent.putExtra("nomeProva", prova);
                                intent.putExtra("turma", turma);
                                intent.putExtra("id_turma", id_turma);
                                intent.putExtra("data", data);
                                intent.putExtra("dataForm", dataform);
                                intent.putExtra("quest", quest);
                                intent.putExtra("alter", alter);
                                intent.putExtra("status", false);
                                startActivity(intent);
                                finish();
                            }else
                                Toast.makeText(CadProvaActivity.this, "Informe a quantidade de alternativas!", Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(CadProvaActivity.this, "Informe a quantidade de questões!", Toast.LENGTH_SHORT).show();
                    }else Toast.makeText(CadProvaActivity.this, "Esta turma já pussui uma prova cadastrada com esse nome, "+prova, Toast.LENGTH_SHORT).show();
                }else Toast.makeText(CadProvaActivity.this, "Informe o nome da prova!", Toast.LENGTH_SHORT).show();
            }
        });
        // Obtém a instância do calendário com a data atual
        calendar = Calendar.getInstance();
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cria um DatePickerDialog com a data atual
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CadProvaActivity.this,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            // Atualiza a data no calendário quando o usuário seleciona uma nova data
                            calendar.set(year, monthOfYear, dayOfMonth);
                            // Atualiza o texto do botão com a data selecionada
                            datePickerButton.setText(formatDate(calendar));
                            dataform = formatDateBanco(calendar);
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

                // Exibe o DatePickerDialog
                datePickerDialog.show();
            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}