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

import online.padev.kariti.utilities.Prova;

public class EdicaoProva extends AppCompatActivity {
    private EditText editTextNomeProva;
    private TextView textViewNumQuestoes, textViewNumAlternativas;
    private Spinner spinnerTurma;
    private ImageButton btnMenosQuestoes, btnMaisQuestoes, btnMenosAlternativas, btnMaisAlternativas, btnVoltar;
    private Button btnAvancar, btnData;
    private Calendar calendario;
    private String nomeTurmaAtual;
    private Integer id_provaBD;
    private TextView titulo;
    BancoDados bancoDados;
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
        textViewNumQuestoes = findViewById(R.id.textViewQuantity);
        textViewNumAlternativas = findViewById(R.id.textVieAlter);

        btnMenosQuestoes = findViewById(R.id.imageButtonMenosQuest);
        btnMaisQuestoes = findViewById(R.id.imageButtonMaisQuest);
        btnMenosAlternativas = findViewById(R.id.imgBtnMenoAlter);
        btnMaisAlternativas = findViewById(R.id.imgBtnMaisAlter);
        btnAvancar = findViewById(R.id.btnGerarProva);

        titulo.setText(String.format("%s","Editar Prova"));

        bancoDados = new BancoDados(this);

        id_provaBD = Objects.requireNonNull(getIntent().getExtras()).getInt("id_prova");

        provaBD = new Prova(id_provaBD, bancoDados);
        provaAtual = new Prova();

        editTextNomeProva.setText(String.format("%s", provaBD.getNomeProva()));
        textViewNumQuestoes.setText(String.format("%s", provaBD.getNumQuestoes()));
        textViewNumAlternativas.setText(String.format("%s", provaBD.getNumAlternativas()));
        btnData.setText(provaBD.dateToDisplay());

        listTurma = bancoDados.listarNomesTurmas(); // Lista todas as turmas da escola atual
        int position = listTurma.indexOf(bancoDados.pegarNomeTurma(provaBD.getId_turma().toString()));
        SpinnerAdapter adapter = new SpinnerAdapter(this, listTurma);
        spinnerTurma.setAdapter(adapter);
        spinnerTurma.setSelection(position);

        btnMaisQuestoes.setOnClickListener(view -> {
            int quest = Integer.parseInt(textViewNumQuestoes.getText().toString());
            if(quest < 20)
                quest ++;
            textViewNumQuestoes.setText(String.valueOf(quest));
        });
        btnMenosQuestoes.setOnClickListener(view -> {
            int quest = Integer.parseInt(textViewNumQuestoes.getText().toString());
            if(quest > 0)
                quest --;
            textViewNumQuestoes.setText(String.valueOf(quest));
        });
        btnMaisAlternativas.setOnClickListener(view -> {
            int alter = Integer.parseInt(textViewNumAlternativas.getText().toString());
            if(alter < 7)
                alter ++;
            textViewNumAlternativas.setText(String.valueOf(alter));
        });
        btnMenosAlternativas.setOnClickListener(view -> {
            int alter = Integer.parseInt(textViewNumAlternativas.getText().toString());
            if(alter > 0)
                alter --;
            textViewNumAlternativas.setText(String.valueOf(alter));
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
                provaAtual.setNomeProva(editTextNomeProva.getText().toString());
                provaAtual.setDataProva(btnData.getText().toString());
                provaAtual.setNumQuestoes(Integer.parseInt(textViewNumQuestoes.getText().toString()));
                provaAtual.setNumAlternativas(Integer.parseInt(textViewNumAlternativas.getText().toString()));
                provaAtual.setId_turma(bancoDados.pegarIdTurma(nomeTurmaAtual));

                if (provaAtual.getNomeProva().trim().isEmpty()) { //verifica se o campo prova esta vazio
                    Toast.makeText(EdicaoProva.this, "Informe o nome da Prova!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (provaAtual.isDifferent(provaBD)) { //Verifica se os dados da prova foram alterados
                    if (provaAtual.getNumQuestoes() == 0 || provaAtual.getNumAlternativas() == 0) {
                        Toast.makeText(EdicaoProva.this, "Quantidade de questões e/ou alternativas, não podem ser igual a 0.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!provaAtual.getNomeProva().equals(provaBD.getNomeProva()) || !provaAtual.getId_turma().equals(provaBD.getId_turma())) {
                        Boolean verificaProva = bancoDados.verificaExisteProvaPNome(provaAtual.getNomeProva(), provaAtual.getId_turma().toString());
                        if (verificaProva == null) {
                            Toast.makeText(EdicaoProva.this, "Erro na comunicação, tente novamente!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (verificaProva) {
                            Toast.makeText(EdicaoProva.this, "Esta turma já pussui uma prova cadastrada com esse nome, " + provaAtual.getNomeProva(), Toast.LENGTH_SHORT).show();
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
        builder.setTitle("ATENÇÃO")
                .setMessage("Confirma as alterações realizadas para esta prova? ")
                .setPositiveButton("Confirmar", (dialog, which) -> carregarTelaGabarito())
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void carregarTelaGabarito(){
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
}