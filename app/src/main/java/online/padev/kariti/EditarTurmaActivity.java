package online.padev.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class EditarTurmaActivity extends AppCompatActivity {
    ImageButton voltar, iconHelpEditTurma;
    ImageView maisAn, menosAn;
    ListView listView;
    EditText editTurma, novosAlAnonimos;
    ArrayList<String> alunosDaTurmaSemAnonimos, alunosSpinner;
    ArrayList<Integer> qtdAlunosAnonimatos;
    String id_turma, pegaTurma, alunosSelecionados;
    BancoDados bancoDados;
    AdapterExclAluno adapter;
    Spinner spinnerBuscAlun;
    Button salvar;
    Integer id_aluno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_turma);

        listView = findViewById(R.id.listViewEditarTurma);
        editTurma = findViewById(R.id.editTextEditTurma);
        maisAn = findViewById(R.id.imageViewMaisNovosAnonimos);
        menosAn = findViewById(R.id.imageViewMenosNovosAnonimos);
        novosAlAnonimos = findViewById(R.id.editTextNovosAlunosAnonimos);
        spinnerBuscAlun = findViewById(R.id.spinnerBuscAlunoNovos);
        salvar = findViewById(R.id.buttonSalvarTurma);
        bancoDados = new BancoDados(this);
        alunosDaTurmaSemAnonimos = new ArrayList<>();
        voltar = findViewById(R.id.imgBtnVoltaDescola);
        iconHelpEditTurma = findViewById(R.id.iconHelp);

        iconHelpEditTurma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelpDetalhes();
            }
        });

        //Lista todos os alunos no Spinner
        alunosSpinner = (ArrayList<String>) bancoDados.obterNomesAlunos();
        alunosSpinner.add(0, "Selecione os Alunos");
        SpinnerAdapter adapterSpinner = new SpinnerAdapter(this, alunosSpinner);
        spinnerBuscAlun.setAdapter(adapterSpinner);
        spinnerBuscAlun.setSelection(0);

        //Mostra a turma a ser editada
        id_turma = Objects.requireNonNull(getIntent().getExtras()).getString("id_turma");
        pegaTurma = bancoDados.pegaNomeTurma(id_turma);
        qtdAlunosAnonimatos = (ArrayList<Integer>) bancoDados.qtdAlunosAnonimatos(id_turma);
        editTurma.setText(pegaTurma);
        novosAlAnonimos.setText(String.format("%s", qtdAlunosAnonimatos.size()));
        informAnonimos(qtdAlunosAnonimatos.size());

        //Lista os aluno cadastrados nesta turma.
        alunosDaTurmaSemAnonimos = (ArrayList<String>) bancoDados.listAlunosDaTurmaSemAnonimos(id_turma);
        adapter = new AdapterExclAluno(this, alunosDaTurmaSemAnonimos);
        listView.setAdapter(adapter);

        //Identifica o aluno selecionado no Spinner e adiciona no listView
        spinnerBuscAlun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    alunosSelecionados = spinnerBuscAlun.getSelectedItem().toString();
                    int n = alunosDaTurmaSemAnonimos.size();
                    int x = 0;
                    if(n == 0)
                        alunosDaTurmaSemAnonimos.add(alunosSelecionados);
                    spinnerBuscAlun.setSelection(0);
                    for(int a = 0; a < n; a++){
                        if(alunosSelecionados.equals(alunosDaTurmaSemAnonimos.get(a))) {
                            x = 1;
                            Toast.makeText(EditarTurmaActivity.this, "Aluno já selecionado!", Toast.LENGTH_SHORT).show();
                            spinnerBuscAlun.setSelection(0);
                            break;
                        }else x = 2;

                    }
                    if (x == 2)
                        alunosDaTurmaSemAnonimos.add(alunosSelecionados);
                    adapter = new AdapterExclAluno(EditarTurmaActivity.this, alunosDaTurmaSemAnonimos);
                    listView.setAdapter(adapter);
                    spinnerBuscAlun.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //Remove aluno do listView, após simples click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    alunosDaTurmaSemAnonimos.remove(i);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(EditarTurmaActivity.this, "Aluno Removido! ", Toast.LENGTH_SHORT).show();
            }
        });
        menosAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int menos = Integer.parseInt(novosAlAnonimos.getText().toString());
                if(menos > 0)
                    menos --;
                novosAlAnonimos.setText(String.valueOf(menos));
            }
        });

        maisAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mais = Integer.parseInt(novosAlAnonimos.getText().toString());
                mais ++;
                novosAlAnonimos.setText(String.valueOf(mais));
            }
        });
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String turmaEditada = editTurma.getText().toString();
                if(!turmaEditada.isEmpty()) {
                    Integer an = Integer.valueOf(novosAlAnonimos.getText().toString());
                    //Editar essa linha após realização de segunda fase da segunda bateria de testes
                    bancoDados.upadateTurma(turmaEditada, an, Integer.valueOf(id_turma)); //Alterando Dados da turma
                    bancoDados.deletaAnonimos(Integer.valueOf(id_turma)); //Deleta todos os alunos Anonimos pertecentes a essa turma da tabela aluno
                    bancoDados.deletarAlunoDturma(Integer.valueOf(id_turma));  //Deleta todos os alunos pertecentes a essa turma

                    if (!alunosDaTurmaSemAnonimos.isEmpty()) {
                        for (int i = 0; i < alunosDaTurmaSemAnonimos.size(); i++) {
                            id_aluno = bancoDados.pegaIdAluno(alunosDaTurmaSemAnonimos.get(i));
                            bancoDados.inserirAlunosNaTurma(Integer.valueOf(id_turma), id_aluno);
                        }
                    }
                    if (!an.equals(0)) {
                        for (int x = 1; x <= an; x++) {
                            String anonimo = "Aluno "+ x;
                            Integer id_anonimo = bancoDados.inserirNovoAluno(anonimo, null, 0);
                            bancoDados.inserirAlunosNaTurma(Integer.valueOf(id_turma), id_anonimo);
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
            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    public void PopMenu(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EditarTurmaActivity.this, "Preparado para implementação", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void informAnonimos(Integer anonimos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("KARITI");
        builder.setMessage("Esta turma possui "+anonimos+" alunos anônimos cadastrados, caso deseje alterar essa quantidade, basta informar um novo valor no campo referente 'Incluir Alunos Anônimos'");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
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
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}