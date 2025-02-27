package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditarTurmaActivity extends AppCompatActivity {
    ImageButton voltar, iconeAjuda;
    ImageView maisAnonimos, menosAnonimos;
    ListView listViewAlunos;
    EditText editTxtTurma, editTxtQtdnonimos;
    List<String> studentsRegisteredClass, studentsSchool;
    String id_turma, nomeTurmaBD, nomeTurmaAtual, alunoSelecionado;
    BancoDados bancoDados;
    AdapterExclAluno adapterStudentsRegistered;
    Spinner spinnerAlunos;
    Button btnSalvar;
    Integer id_aluno, qtdAnonimosBD;
    TextView titulo, titleStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_turma);

        listViewAlunos = findViewById(R.id.listViewEditarTurma);
        editTxtTurma = findViewById(R.id.editTextEditTurma);
        maisAnonimos = findViewById(R.id.imageViewMaisNovosAnonimos);
        menosAnonimos = findViewById(R.id.imageViewMenosNovosAnonimos);
        titleStudent = findViewById(R.id.txtVieAlunos);
        editTxtQtdnonimos = findViewById(R.id.editTextNovosAlunosAnonimos);
        spinnerAlunos = findViewById(R.id.spinnerBuscAlunoNovos);
        btnSalvar = findViewById(R.id.buttonSalvarTurma);
        voltar = findViewById(R.id.imgBtnVoltaDescola);
        iconeAjuda = findViewById(R.id.iconHelp);
        titulo = findViewById(R.id.toolbar_title);

        bancoDados = new BancoDados(this);
        studentsRegisteredClass = new ArrayList<>();

        titulo.setText(String.format("%s","Atualização"));

        id_turma = Objects.requireNonNull(getIntent().getExtras()).getString("id_turma");

        //Lista todos os alunos no Spinner
        studentsSchool = bancoDados.listarNomesAlunos(1);
        if (studentsSchool == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente - 1", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!studentsSchool.isEmpty()){
            studentsSchool.add(0, "Selecionar alunos");
        } else {
            spinnerAlunos.setVisibility(View.GONE);
        }

        SpinnerAdapter adapterSpinner = new SpinnerAdapter(this, studentsSchool);
        spinnerAlunos.setAdapter(adapterSpinner);
        spinnerAlunos.setSelection(0);

        //Mostra a turma a ser editada

        nomeTurmaBD = bancoDados.pegarNomeTurma(id_turma);
        qtdAnonimosBD = bancoDados.pegarQtdAlunosPorStatus(id_turma, 0);

        if (nomeTurmaBD == null || qtdAnonimosBD == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente - 2", Toast.LENGTH_SHORT).show();
            finish();
        }

        editTxtTurma.setText(nomeTurmaBD);
        editTxtQtdnonimos.setText(String.format("%s", qtdAnonimosBD));

        informAnonimos(qtdAnonimosBD);

        //Lista os aluno cadastrados nesta turma.
        studentsRegisteredClass = bancoDados.listarAlunosTurmaPorStatus(id_turma, 1);
        if (studentsRegisteredClass == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente - 3", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (studentsRegisteredClass.isEmpty()){
            titleStudent.setVisibility(View.GONE);
            listViewAlunos.setVisibility(View.GONE);
        }
        adapterStudentsRegistered = new AdapterExclAluno(this, studentsRegisteredClass);
        listViewAlunos.setAdapter(adapterStudentsRegistered);

        //Identifica o aluno selecionado no Spinner e adiciona no listView
        spinnerAlunos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    alunoSelecionado = spinnerAlunos.getSelectedItem().toString();
                    if(studentsRegisteredClass.contains(alunoSelecionado)) {
                        Toast.makeText(EditarTurmaActivity.this, "Aluno já selecionado!", Toast.LENGTH_SHORT).show();
                        spinnerAlunos.setSelection(0);
                        return;
                    }
                    studentsRegisteredClass.add(alunoSelecionado);
                    adapterStudentsRegistered = new AdapterExclAluno(EditarTurmaActivity.this, studentsRegisteredClass);
                    listViewAlunos.setAdapter(adapterStudentsRegistered);
                    titleStudent.setVisibility(View.VISIBLE);
                    listViewAlunos.setVisibility(View.VISIBLE);
                    adapterStudentsRegistered.notifyDataSetChanged();
                    spinnerAlunos.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //Remove aluno do listView, após simples click
        listViewAlunos.setOnItemClickListener((adapterView, view, i, l) -> {
                studentsRegisteredClass.remove(i);
                if (studentsRegisteredClass.isEmpty()){
                    titleStudent.setVisibility(View.GONE);
                    listViewAlunos.setVisibility(View.GONE);
                    spinnerAlunos.setSelection(0);
                }
                adapterStudentsRegistered.notifyDataSetChanged();
                Toast.makeText(EditarTurmaActivity.this, "Aluno Removido! ", Toast.LENGTH_SHORT).show();
        });
        menosAnonimos.setOnClickListener(view -> {
            int numAnonimos = 0;
            if (!editTxtQtdnonimos.getText().toString().trim().isEmpty()){
                numAnonimos = Integer.parseInt(editTxtQtdnonimos.getText().toString());
            }
            if(numAnonimos > 0)
                numAnonimos --;
            editTxtQtdnonimos.setText(String.valueOf(numAnonimos));
        });

        maisAnonimos.setOnClickListener(view -> {
            int numAnonimos = 0;
            if (!editTxtQtdnonimos.getText().toString().trim().isEmpty()){
                numAnonimos = Integer.parseInt(editTxtQtdnonimos.getText().toString());
            }
            numAnonimos ++;
            editTxtQtdnonimos.setText(String.valueOf(numAnonimos));
        });
        btnSalvar.setOnClickListener(view -> {
            btnSalvar.setEnabled(false);
            try {
                nomeTurmaAtual = editTxtTurma.getText().toString().trim();
                if (nomeTurmaAtual.isEmpty()) {
                    Toast.makeText(EditarTurmaActivity.this, "Informe o nome da turma!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String numAnonimos = editTxtQtdnonimos.getText().toString().trim();
                Log.e("kariti", "p1");
                if (studentsRegisteredClass.isEmpty() && (numAnonimos.equals("0") || numAnonimos.isEmpty())) {
                    aviso();
                    return;
                }
                Log.e("kariti", "p2");
                if (!nomeTurmaAtual.equals(nomeTurmaBD)) {
                    Boolean verificaTurma = bancoDados.verificaExisteTurmaPorNome(nomeTurmaAtual);
                    if (verificaTurma == null) {
                        Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente - 3", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (verificaTurma) {
                        Toast.makeText(this, "Turma já cadastrada! ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!bancoDados.alterarDadosTurma(nomeTurmaAtual, Integer.valueOf(id_turma))) {
                        Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Log.e("kariti", "p3");
                if (!bancoDados.deletarAnonimos(Integer.valueOf(id_turma))) {  //Deleta todos os alunos Anonimos pertecentes a essa turma da tabela aluno
                    Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e("kariti", "p4");
                if (!bancoDados.deletarAlunoDeTurma(Integer.valueOf(id_turma))) {  //Deleta todos os alunos pertecentes a essa turma
                    Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.e("kariti", "p5");
                if (!studentsRegisteredClass.isEmpty()) {
                    for (String aluno : studentsRegisteredClass) {
                        id_aluno = bancoDados.pegarIdAluno(aluno);
                        if (id_aluno != null && id_aluno != -1) {
                            if (!bancoDados.cadastrarAlunoNaTurma(Integer.valueOf(id_turma), id_aluno)) {
                                Log.e("kariti", "Erro ao tentar vincular o aluno " + aluno + " a turma com id: " + id_turma);
                            }
                        }
                    }
                }
                Log.e("kariti", "p6");
                int qtdAnonimosAtual = 0;
                if (!numAnonimos.isEmpty()){
                    qtdAnonimosAtual = Integer.parseInt(numAnonimos);
                }
                Log.e("kariti", "p7");
                if (qtdAnonimosAtual != 0) {
                    int tamanho = String.valueOf(qtdAnonimosAtual).length();
                    for (int x = 1; x <= qtdAnonimosAtual; x++) {
                        String anonimo = "Aluno " + String.format("%0" + tamanho + "d", x);
                        Integer id_anonimo = bancoDados.cadastrarAluno(anonimo, null, 0);
                        if (id_anonimo != -1) {
                            if (!bancoDados.cadastrarAlunoNaTurma(Integer.valueOf(id_turma), id_anonimo)) {
                                Log.e("kariti", "Erro ao tentar vincular o aluno anonimo " + anonimo + " a turma com id: " + id_turma);
                            }
                        }
                    }
                }
                Log.e("kariti", "p8");
                Toast.makeText(EditarTurmaActivity.this, "Dados alterados com sucesso!", Toast.LENGTH_SHORT).show();
                recarregarDadosTurma();
            } catch (Exception e){
                Log.e("kariti", e.toString());
            } finally {
                btnSalvar.setEnabled(true);
            }
        });
        iconeAjuda.setOnClickListener(view -> dialogHelpDetalhes());
        voltar.setOnClickListener(view -> recarregarDadosTurma());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                recarregarDadosTurma();
            }
        });
    }
    public void PopMenu(View v){
        v.setOnClickListener(view -> Toast.makeText(EditarTurmaActivity.this, "Preparado para implementação", Toast.LENGTH_SHORT).show());
    }
    public void informAnonimos(Integer anonimos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("KARITI");
        builder.setMessage("Esta turma possui "+anonimos+" alunos anônimos cadastrados, caso deseje alterar essa quantidade, basta informar um novo valor no campo referente 'Incluir Alunos Anônimos'");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void dialogHelpDetalhes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Nesta tela os dados da turma podem sem alterados, seguindo o mesmo padrão de cadastro de turma.\n\n" +
                "1 - Nome: caso deseje alterar o nome, basta informar um novo\n\n" +
                "2 - Alunos: podem ser incluídos novos alunos para essa turma selecionando-os no campo 'Selecione os Alunos', os quais antecipadamente já devem estar cadastrados no KARITI na tela de cadastro de alunos. Caso deseje remover, basta clicar no nome do aluno para remove-lo da turma. \n\n" +
                "3 - Anônimos: caso não deseje cadastrar alunos para essa turma, podem ser incluidos alunos anônimos no campo 'Incluir Alunos Anônimos', informando a quantidade no campo sugerido, sem a necessidade de cadastrar todos ou nenhum aluno como descrito na opção 2. \n\n" +
                "Obs. A Turma não pode ser cadastrada sem alunos.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void aviso(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarTurmaActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Não é possível cadastrar uma turma sem alunos. Por favor, selecione os alunos para essa turma ou, caso preferir, informe a quantidade de alunos anônimos! ")
                .setPositiveButton("OK", (dialog, which) -> Toast.makeText(EditarTurmaActivity.this, "Selecione os alunos!", Toast.LENGTH_SHORT).show());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void recarregarDadosTurma(){
        setResult(RESULT_OK);
        finish();
    }

}