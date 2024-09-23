package online.padev.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
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

import online.padev.kariti.R;

import java.util.ArrayList;

public class CadTurmaActivity extends AppCompatActivity{
    ImageButton voltar, iconHelpCadturma;
    Toolbar toolbar;
    EditText nomeTurma, alunosAnonimos;
    ImageView menosAnonimos, maisAnonimos;
    ListView listarAlunos;
    Button cadastrar;
    BancoDados bancoDados;
    Spinner spinnerBuscAluno;
    String alunoSelecionado;
    Integer id_turma = 0;
    AdapterExclAluno al;
    TextView titulo;
    ArrayList<String> selectedAlunos = new ArrayList<>(), nomesAluno;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_turma);

        toolbar = findViewById(R.id.myToolBarMenu);
        setSupportActionBar(toolbar);
        voltar = findViewById(R.id.imgBtnVoltaDescola);
        iconHelpCadturma = findViewById(R.id.iconHelp);
        listarAlunos = findViewById(R.id.listViewCadTurma);
        titulo = findViewById(R.id.toolbar_title);

        titulo.setText(String.format("%s","Cadastro"));

        nomeTurma = findViewById(R.id.editTextTurmaCad);
        cadastrar = findViewById(R.id.buttonCadastrarTurma);
        spinnerBuscAluno = findViewById(R.id.spinnerBuscAluno);
        alunosAnonimos = findViewById(R.id.editTextAlunosAnonimos);
        menosAnonimos = findViewById(R.id.imageViewMenosAnonimos);
        maisAnonimos = findViewById(R.id.imageViewMaisAnonimos);
        bancoDados = new BancoDados(this);

        nomesAluno = (ArrayList<String>) bancoDados.obterNomesAlunos();
        nomesAluno.add(0, "Selecione os Alunos");
        nomesAluno.add(1, "Todos");
        SpinnerAdapter adapter = new SpinnerAdapter(this, nomesAluno);
        spinnerBuscAluno.setAdapter(adapter);
        spinnerBuscAluno.setSelection(0);

        iconHelpCadturma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelpDetalhes();
            }
        });
        spinnerBuscAluno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    alunoSelecionado = spinnerBuscAluno.getSelectedItem().toString();
                    if(alunoSelecionado.equals("Todos")){
                        selectedAlunos = (ArrayList<String>) bancoDados.obterNomesAlunos();
                        al = new AdapterExclAluno(CadTurmaActivity.this, selectedAlunos);
                        listarAlunos.setAdapter(al);
                        al.notifyDataSetChanged();
                        spinnerBuscAluno.setSelection(0);
                    }else {
                        int i = 0;
                        for (int a = 0; a < selectedAlunos.size(); a++) {
                            if (alunoSelecionado.equals(selectedAlunos.get(a))) {
                                i = 1;
                                Toast.makeText(CadTurmaActivity.this, "Aluno já selecionado!", Toast.LENGTH_SHORT).show();
                                spinnerBuscAluno.setSelection(0);
                                break;
                            }
                        }
                        if (i != 1) {
                            selectedAlunos.add(alunoSelecionado);
                            al = new AdapterExclAluno(CadTurmaActivity.this, selectedAlunos);
                            listarAlunos.setAdapter(al);
                            al.notifyDataSetChanged();
                            spinnerBuscAluno.setSelection(0);
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        listarAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               selectedAlunos.remove(i);
               al.notifyDataSetChanged();
               Toast.makeText(CadTurmaActivity.this, "Aluno removido! ", Toast.LENGTH_SHORT).show();
           }
        });
        menosAnonimos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer menos = Integer.valueOf(alunosAnonimos.getText().toString());
                if(menos > 0)
                    menos --;
                alunosAnonimos.setText(menos.toString());
            }
        });
        maisAnonimos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer mais = Integer.valueOf(alunosAnonimos.getText().toString());
                mais ++;
                alunosAnonimos.setText(mais.toString());
            }
        });
        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String turma = nomeTurma.getText().toString();
                if(!turma.trim().isEmpty()) {
                    if (!selectedAlunos.isEmpty() || !alunosAnonimos.getText().toString().equals("0")) {
                        Integer an = Integer.valueOf(alunosAnonimos.getText().toString());
                        Boolean checkTurma = bancoDados.checkTurma(turma);
                        if (!checkTurma) {
                            Boolean cadTurma = bancoDados.inserirTurma(turma, an);
                            if (cadTurma) {
                                id_turma = bancoDados.pegaIdTurma(turma);
                                if (!selectedAlunos.isEmpty()) {
                                    int num = listarAlunos.getAdapter().getCount();
                                    for (int i = 0; i < num; i++) {
                                        Integer id_aluno = bancoDados.pegaIdAluno(selectedAlunos.get(i));
                                        bancoDados.inserirAlunosNaTurma(id_turma, id_aluno);
                                    }
                                }
                                if (!an.equals(0)) {
                                    for (int x = 1; x <= an; x++) {
                                        String anonimo = "Aluno "+ x;
                                        Integer id_anonimo = bancoDados.inserirNovoAluno(anonimo, null, 0);
                                        bancoDados.inserirAlunosNaTurma(id_turma, id_anonimo);
                                    }
                                }
                                Toast.makeText(CadTurmaActivity.this, "Turma cadastrada com Sucesso", Toast.LENGTH_SHORT).show();
                                finish();
                            } else
                                Toast.makeText(CadTurmaActivity.this, "Erro no cadastro da Turma", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(CadTurmaActivity.this, "Turma já cadstradada! ", Toast.LENGTH_SHORT).show();
                    } else aviso();
                }else{
                    Toast.makeText(CadTurmaActivity.this, "Por favor, informe o nome da turma!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }
    public void aviso(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CadTurmaActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Não é possivel cadastrar turma sem incluir aluno. Favor selecionar os alunos pertencentes a essa turma ou informe a quantidade de alunos anônimos se preferir! ")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(CadTurmaActivity.this, "Selecione os alunos!", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void dialogHelpDetalhes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Bem vindo(a) ao cadastro de turma! \n\n" +
                "Nesta tela são solicitados alguns dados para cadastrar um nova turma.\n\n" +
                "1 - Nome: deve ser informado o nome da turma *obrigatório* \n\n" +
                "2 - Alunos: podem ser incluídos alunos para essa turma selecionando-os no campo 'Selecione os Alunos', os quais antecipadamente já devem estar cadastrados no KARITI na tela de cadastro de alunos. Todos os alunos selecionados são listados no campo 'Alunos'. Caso selecione algum aluno errado, basta clicar no nome do aluno para remove-lo da lista. \n\n" +
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