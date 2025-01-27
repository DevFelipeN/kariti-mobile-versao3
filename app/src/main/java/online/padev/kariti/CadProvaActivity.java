package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import online.padev.kariti.utilities.Prova;

public class CadProvaActivity extends AppCompatActivity {
    Button datePickerButton;
    Calendar calendar;
    EditText nomeProva;
    TextView qtdQuest, qtdAlter;
    Button btnGerProva;
    Spinner spinnerTurma;
    BancoDados bancoDados;
    Prova prova;
    List<String> listTurmas;
    ImageButton btnVoltar, questMenos, questMais, altMais, altMenos;
    String dataform;
    Integer id_turma;
    TextView titulo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_prova);

        datePickerButton = findViewById(R.id.datePickerButton);
        btnVoltar = findViewById(R.id.imgBtnVoltar);
        btnGerProva = findViewById(R.id.btnGerarProva);
        nomeProva = findViewById(R.id.editTextNomeProva);
        qtdQuest = findViewById(R.id.textViewQuantity);
        qtdAlter = findViewById(R.id.textVieAlter);
        questMais = findViewById(R.id.imageButtonMaisQuest);
        questMenos = findViewById(R.id.imageButtonMenosQuest);
        altMais = findViewById(R.id.imgBtnMaisAlter);
        altMenos = findViewById(R.id.imgBtnMenoAlter);
        spinnerTurma = findViewById(R.id.spinnerTurmaPprova);
        titulo = findViewById(R.id.toolbar_title);

        bancoDados = new BancoDados(this);
        prova = new Prova();

        titulo.setText(String.format("%s","Nova Prova"));

        listTurmas = bancoDados.listarNomesTurmas(); //Obtem a lista das turmas delimitadas pertecentes a escola atual
        if(listTurmas == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            finish();
        }
        listTurmas.add(0, "Selecione a Turma");
        SpinnerAdapter adapter = new SpinnerAdapter(this, listTurmas);
        spinnerTurma.setAdapter(adapter);

        questMais.setOnClickListener(v -> {
            int quest = Integer.parseInt(qtdQuest.getText().toString());
            if(quest < 20)
                quest ++;
            qtdQuest.setText(String.valueOf(quest));
        });
        questMenos.setOnClickListener(v -> {
            int quest = Integer.parseInt(qtdQuest.getText().toString());
            if(quest > 0)
                quest --;
            qtdQuest.setText(String.valueOf(quest));
        });
        altMais.setOnClickListener(v -> {
            int alter = Integer.parseInt(qtdAlter.getText().toString());
            if(alter < 7)
                alter ++;
            qtdAlter.setText(String.valueOf(alter));
        });
        altMenos.setOnClickListener(v -> {
            int alter = Integer.parseInt(qtdAlter.getText().toString());
            if(alter > 0)
                alter --;
            qtdAlter.setText(String.valueOf(alter));
        });
        btnGerProva.setOnClickListener(v -> {

            prova.setNomeProva(nomeProva.getText().toString());
            prova.setNumQuestoes(Integer.parseInt(qtdQuest.getText().toString()));
            prova.setNumAlternativas(Integer.parseInt(qtdAlter.getText().toString()));

            if(prova.getNomeProva().trim().isEmpty()){
                Toast.makeText(CadProvaActivity.this, "Informe o nome da prova!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(spinnerTurma.getSelectedItem() == "Selecione a Turma"){
                Toast.makeText(CadProvaActivity.this, "Selecione uma turma!", Toast.LENGTH_SHORT).show();
                return;
            }
            String nomeTurma = spinnerTurma.getSelectedItem().toString();
            prova.setId_turma(bancoDados.pegarIdTurma(nomeTurma));

            if(datePickerButton.getText().toString().equals("Selecionar Data")){
                Toast.makeText(CadProvaActivity.this, "Selecione uma data!", Toast.LENGTH_SHORT).show();
                return;
            }
            prova.setDataProva(datePickerButton.getText().toString());

            if(prova.getNumQuestoes() == 0){
                Toast.makeText(CadProvaActivity.this, "Informe a quantidade de questões!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(prova.getNumAlternativas() == 0){
                Toast.makeText(CadProvaActivity.this, "Informe a quantidade de alternativas!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(prova.getId_turma() == null){
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                return;
            }

            Boolean verificaProva = bancoDados.verificaExisteProvaPNome(prova.getNomeProva(), prova.getId_turma().toString());
            if(verificaProva == null){
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                return;
            }
            if(verificaProva) {
                Toast.makeText(CadProvaActivity.this, "Esta turma já pussui uma prova cadastrada com o nome, "+prova.getNomeProva(), Toast.LENGTH_SHORT).show();
                return;
            }

            carregarTelaGabarito();
        });
        // Obtém a instância do calendário com a data atual
        calendar = Calendar.getInstance();
        datePickerButton.setOnClickListener(v -> {
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
        });
        btnVoltar.setOnClickListener(v -> finish());
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
    private void carregarTelaGabarito(){
        Intent intent = new Intent(this, GabaritoActivity.class);
        intent.putExtra("prova", prova);
        intent.putExtra("direcao", "novaProva");
        startActivity(intent);
        finish();
    }
}