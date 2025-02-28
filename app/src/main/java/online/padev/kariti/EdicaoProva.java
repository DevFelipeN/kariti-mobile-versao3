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

import online.padev.kariti.entity.Prova;
import online.padev.kariti.database.DataBaseKariti;

public class EdicaoProva extends AppCompatActivity {
    private EditText editTextNomeProva, qtdQuest, qtdAlter;
    private Spinner spinnerTurma;
    private ImageButton btnMenosQuestoes, btnMaisQuestoes, btnMenosAlternativas, btnMaisAlternativas, btnVoltar;
    private Button btnAvancar, btnData;
    private Calendar calendario;
    private String nomeTurmaAtual;
    private Integer id_provaBD;
    private TextView titulo;
    DataBaseKariti dataBaseKariti;
    Prova provaBD, provaAtual;
    private int status;
    private List<String> listTurma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_prova);

        btnVoltar = findViewById(R.id.imgBtnVoltar);
        btnData = findViewById(R.id.datePickerButton);
        editTextNomeProva = findViewById(R.id.editTextNomeProva);
        spinnerTurma = findViewById(R.id.spinnerTurmaPprova);
        titulo = findViewById(R.id.toolbar_title);
        qtdQuest = findViewById(R.id.editTextQtdQuests);
        qtdAlter = findViewById(R.id.editTextQtdAlter);

        btnMenosQuestoes = findViewById(R.id.imageButtonMenosQuest);
        btnMaisQuestoes = findViewById(R.id.imageButtonMaisQuest);
        btnMenosAlternativas = findViewById(R.id.imgBtnMenoAlter);
        btnMaisAlternativas = findViewById(R.id.imgBtnMaisAlter);
        btnAvancar = findViewById(R.id.btnGerarProva);

        titulo.setText(String.format("%s","Editar Prova"));

        dataBaseKariti = new DataBaseKariti(this);

        id_provaBD = Objects.requireNonNull(getIntent().getExtras()).getInt("id_prova");

        provaBD = new Prova(id_provaBD, dataBaseKariti);
        provaAtual = new Prova();

        editTextNomeProva.setText(String.format("%s", provaBD.getNameProva()));
        qtdQuest.setText(String.format("%s", provaBD.getNumQuestions()));
        qtdAlter.setText(String.format("%s", provaBD.getNumAlternatives()));
        btnData.setText(provaBD.dateToDisplay());

        listTurma = dataBaseKariti.listarNomesTurmas(); // Lista todas as turmas da escola atual
        int position = listTurma.indexOf(dataBaseKariti.pegarNomeTurma(provaBD.getId_class().toString()));
        AdapterSpinner adapter = new AdapterSpinner(this, listTurma);
        spinnerTurma.setAdapter(adapter);
        spinnerTurma.setSelection(position);

        btnMaisQuestoes.setOnClickListener(view -> {
            int quest = 0;
            if (!qtdQuest.getText().toString().trim().isEmpty()){
                quest = Integer.parseInt(qtdQuest.getText().toString());
            }
            if(quest < 20)
                quest ++;
            qtdQuest.setText(String.valueOf(quest));
        });
        btnMenosQuestoes.setOnClickListener(view -> {
            int quest = 0;
            if (!qtdQuest.getText().toString().trim().isEmpty()){
                quest = Integer.parseInt(qtdQuest.getText().toString());
            }
            if(quest > 0)
                quest --;
            qtdQuest.setText(String.valueOf(quest));
        });
        btnMaisAlternativas.setOnClickListener(view -> {
            int alter = 0;
            if (!qtdAlter.getText().toString().trim().isEmpty()) {
                alter = Integer.parseInt(qtdAlter.getText().toString());
            }
            if(alter < 6)
                alter ++;
            qtdAlter.setText(String.valueOf(alter));
        });
        btnMenosAlternativas.setOnClickListener(view -> {
            int alter = 0;
            if (!qtdAlter.getText().toString().trim().isEmpty()) {
                alter = Integer.parseInt(qtdAlter.getText().toString());
            }
            if(alter > 0)
                alter --;
            qtdAlter.setText(String.valueOf(alter));
        });


        calendario = Calendar.getInstance();
        btnData.setOnClickListener(v -> {
            // Cria um DatePickerDialog com a data atual
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EdicaoProva.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // Atualiza a data no calendário quando o usuário seleciona uma nova data
                        calendario.set(year, monthOfYear, dayOfMonth);
                        // Atualiza o texto do botão com a data selecionada
                        btnData.setText(formatDateToDisplay(calendario));
                    },
                    calendario.get(Calendar.YEAR),
                    calendario.get(Calendar.MONTH),
                    calendario.get(Calendar.DAY_OF_MONTH)
            );
            // Exibe o DatePickerDialog
            datePickerDialog.show();
        });

        btnAvancar.setOnClickListener(v -> {

            try {
                nomeTurmaAtual = spinnerTurma.getSelectedItem().toString(); // nome da turma não tem como ser vazio!
                provaAtual.setId_prova(id_provaBD);
                provaAtual.setNameProva(editTextNomeProva.getText().toString());
                provaAtual.setDateProva(btnData.getText().toString());
                provaAtual.setId_class(dataBaseKariti.pegarIdTurma(nomeTurmaAtual));

                if (provaAtual.getNameProva().trim().isEmpty()) { //verifica se o campo prova esta vazio
                    Toast.makeText(EdicaoProva.this, "Informe o nome da Prova!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(qtdQuest.getText().toString().trim().isEmpty() || qtdQuest.getText().toString().equals("0")){
                    Toast.makeText(this, "Informe a quantidade de questões!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(qtdAlter.getText().toString().trim().isEmpty() || qtdAlter.getText().toString().equals("0")){
                    Toast.makeText(this, "Informe a quantidade de alternativas!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Integer.parseInt(qtdQuest.getText().toString()) > 20){
                    dialogLimitMaxQuest();
                    return;
                }
                if (Integer.parseInt(qtdAlter.getText().toString()) > 6) {
                    dialogLimitMaxAlter();
                    return;
                }
                provaAtual.setNumQuestions(Integer.parseInt(qtdQuest.getText().toString()));
                provaAtual.setNumAlternatives(Integer.parseInt(qtdAlter.getText().toString()));

                if (provaAtual.isDifferent(provaBD)) { //Verifica se os dados da prova foram alterados
                    if (!provaAtual.getNameProva().equals(provaBD.getNameProva()) || !provaAtual.getId_class().equals(provaBD.getId_class())) {
                        Boolean verificaProva = dataBaseKariti.verificaExisteProvaPNome(provaAtual.getNameProva(), provaAtual.getId_class().toString());
                        if (verificaProva == null) {
                            Toast.makeText(EdicaoProva.this, "Erro na comunicação, tente novamente!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (verificaProva) {
                            Toast.makeText(EdicaoProva.this, "Esta turma já pussui uma prova cadastrada com esse nome, " + provaAtual.getNameProva(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EdicaoProva.this, "Algo de errado ocorreu, tente novamente!", Toast.LENGTH_SHORT).show();
                Log.e("kariti", e.toString());
                finish();
            }
        });

        btnVoltar.setOnClickListener(view -> {
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
            intent.putExtra("prova", provaBD);
        }else{
            intent.putExtra("prova", provaAtual);
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