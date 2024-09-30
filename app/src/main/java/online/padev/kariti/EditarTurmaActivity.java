package online.padev.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.Objects;

public class EditarTurmaActivity extends AppCompatActivity {
    ImageButton voltar, iconeAjuda;
    ImageView maisAnonimos, menosAnonimos;
    ListView listViewAlunos;
    EditText editTxtTurma, EditTxtQtdnonimos;
    ArrayList<String> listaAlunosDTurma, alunosSpinner;
    String id_turma, pegaTurma, alunosSelecionados;
    BancoDados bancoDados;
    AdapterExclAluno adapter;
    Spinner spinnerAlunos;
    Button btnSalvar;
    Integer id_aluno, qtdAlunosAnonimatos;
    TextView titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_turma);

        listViewAlunos = findViewById(R.id.listViewEditarTurma);
        editTxtTurma = findViewById(R.id.editTextEditTurma);
        maisAnonimos = findViewById(R.id.imageViewMaisNovosAnonimos);
        menosAnonimos = findViewById(R.id.imageViewMenosNovosAnonimos);
        EditTxtQtdnonimos = findViewById(R.id.editTextNovosAlunosAnonimos);
        spinnerAlunos = findViewById(R.id.spinnerBuscAlunoNovos);
        btnSalvar = findViewById(R.id.buttonSalvarTurma);
        voltar = findViewById(R.id.imgBtnVoltaDescola);
        iconeAjuda = findViewById(R.id.iconHelp);
        titulo = findViewById(R.id.toolbar_title);

        bancoDados = new BancoDados(this);
        listaAlunosDTurma = new ArrayList<>();

        titulo.setText(String.format("%s","Atualização"));

        id_turma = Objects.requireNonNull(getIntent().getExtras()).getString("id_turma");

        //Lista todos os alunos no Spinner
        alunosSpinner = (ArrayList<String>) bancoDados.listarNomesAlunos(1);
        if (adapter == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            finish();
        }
        alunosSpinner.add(0, "Selecione os Alunos");
        SpinnerAdapter adapterSpinner = new SpinnerAdapter(this, alunosSpinner);
        spinnerAlunos.setAdapter(adapterSpinner);
        spinnerAlunos.setSelection(0);

        //Mostra a turma a ser editada

        pegaTurma = bancoDados.pegarNomeTurma(id_turma);
        qtdAlunosAnonimatos = bancoDados.pegarQtdAlunosPorStatus(id_turma, 0);
        editTxtTurma.setText(pegaTurma);
        EditTxtQtdnonimos.setText(String.format("%s", qtdAlunosAnonimatos));
        informAnonimos(qtdAlunosAnonimatos);

        //Lista os aluno cadastrados nesta turma.
        listaAlunosDTurma = (ArrayList<String>) bancoDados.listarAlunosTurmaPorStatus(id_turma, 1);
        adapter = new AdapterExclAluno(this, listaAlunosDTurma);
        listViewAlunos.setAdapter(adapter);

        //Identifica o aluno selecionado no Spinner e adiciona no listView
        spinnerAlunos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    alunosSelecionados = spinnerAlunos.getSelectedItem().toString();
                    int n = listaAlunosDTurma.size();
                    int x = 0;
                    if(n == 0)
                        listaAlunosDTurma.add(alunosSelecionados);
                    spinnerAlunos.setSelection(0);
                    for(int a = 0; a < n; a++){
                        if(alunosSelecionados.equals(listaAlunosDTurma.get(a))) {
                            x = 1;
                            Toast.makeText(EditarTurmaActivity.this, "Aluno já selecionado!", Toast.LENGTH_SHORT).show();
                            spinnerAlunos.setSelection(0);
                            break;
                        }else x = 2;

                    }
                    if (x == 2)
                        listaAlunosDTurma.add(alunosSelecionados);
                    adapter = new AdapterExclAluno(EditarTurmaActivity.this, listaAlunosDTurma);
                    listViewAlunos.setAdapter(adapter);
                    spinnerAlunos.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //Remove aluno do listView, após simples click
        listViewAlunos.setOnItemClickListener((adapterView, view, i, l) -> {
                listaAlunosDTurma.remove(i);
                adapter.notifyDataSetChanged();
                Toast.makeText(EditarTurmaActivity.this, "Aluno Removido! ", Toast.LENGTH_SHORT).show();
        });
        menosAnonimos.setOnClickListener(view -> {
            int menos = Integer.parseInt(EditTxtQtdnonimos.getText().toString());
            if(menos > 0)
                menos --;
            EditTxtQtdnonimos.setText(String.valueOf(menos));
        });

        maisAnonimos.setOnClickListener(view -> {
            int mais = Integer.parseInt(EditTxtQtdnonimos.getText().toString());
            mais ++;
            EditTxtQtdnonimos.setText(String.valueOf(mais));
        });
        btnSalvar.setOnClickListener(view -> {
            String turmaEditada = editTxtTurma.getText().toString();
            if(!turmaEditada.trim().isEmpty()) {
                Integer an = Integer.valueOf(EditTxtQtdnonimos.getText().toString());
                //Editar essa linha após realização de segunda fase da segunda bateria de testes
                bancoDados.alterarDadosTurma(turmaEditada, Integer.valueOf(id_turma)); //Alterando Dados da turma
                bancoDados.deletarAnonimos(Integer.valueOf(id_turma)); //Deleta todos os alunos Anonimos pertecentes a essa turma da tabela aluno
                bancoDados.deletarAlunoDeTurma(Integer.valueOf(id_turma));  //Deleta todos os alunos pertecentes a essa turma

                if (!listaAlunosDTurma.isEmpty()) {
                    for (int i = 0; i < listaAlunosDTurma.size(); i++) {
                        id_aluno = bancoDados.pegarIdAluno(listaAlunosDTurma.get(i));
                        bancoDados.cadastrarAlunoNaTurma(Integer.valueOf(id_turma), id_aluno);
                    }
                }
                if (!an.equals(0)) {
                    for (int x = 1; x <= an; x++) {
                        String anonimo = "Aluno "+ x;
                        Integer id_anonimo = bancoDados.cadastrarAluno(anonimo, null, 0);
                        bancoDados.cadastrarAlunoNaTurma(Integer.valueOf(id_turma), id_anonimo);
                    }
                }
                Toast.makeText(EditarTurmaActivity.this, "Dados Alterados!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), DadosTurmaActivity.class);
                intent.putExtra("idTurma", Integer.valueOf(id_turma));
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(EditarTurmaActivity.this, "Informe o nome da turma!", Toast.LENGTH_SHORT).show();
            }
        });
        voltar.setOnClickListener(view -> {
            getOnBackPressedDispatcher();
            finish();
        });
        iconeAjuda.setOnClickListener(view -> dialogHelpDetalhes());
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

}