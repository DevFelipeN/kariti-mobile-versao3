package com.example.kariti;

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
import android.widget.Toast;

import java.util.ArrayList;

public class CadTurmaActivity extends AppCompatActivity{
    ImageButton voltar;
    Toolbar toolbar;
    EditText nomeTurma, alunosAnonimos;
    ImageView menosAnonimos, maisAnonimos;
    ListView listarAlunos;
    Button cadastrar;
    BancoDados bancoDados;
    Spinner spinnerBuscAluno;
    String alunoSelecionado;
    Boolean checkList;
    Integer id_turma = 0;
    AdapterExclAluno al;
    ArrayList<String> selectedAlunos = new ArrayList<>(), nomesAluno, anonimos;
    Integer idAnonimos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_turma);

        toolbar = findViewById(R.id.myToolBarMenu);
        setSupportActionBar(toolbar);
        voltar = findViewById(R.id.imgBtnVoltar);
        listarAlunos = findViewById(R.id.listViewCadTurma);

        nomeTurma = findViewById(R.id.editTextTurmaCad);
        cadastrar = findViewById(R.id.buttonCadastrarTurma);
        spinnerBuscAluno = findViewById(R.id.spinnerBuscAluno);
        alunosAnonimos = findViewById(R.id.editTextAlunosAnonimos);
        menosAnonimos = findViewById(R.id.imageViewMenosAnonimos);
        maisAnonimos = findViewById(R.id.imageViewMaisAnonimos);
        bancoDados = new BancoDados(this);

        nomesAluno = (ArrayList<String>) bancoDados.obterNomesAlunos();
        nomesAluno.add(0, "Selecione os Alunos");
        SpinnerAdapter adapter = new SpinnerAdapter(this, nomesAluno);
        spinnerBuscAluno.setAdapter(adapter);
        spinnerBuscAluno.setSelection(0);

        spinnerBuscAluno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    alunoSelecionado = spinnerBuscAluno.getSelectedItem().toString();
                    int n = selectedAlunos.size();
                    int i = 0;
                    if(n == 0)
                        selectedAlunos.add(alunoSelecionado);
                    for(int a = 0; a < n; a++){
                        if(alunoSelecionado.equals(selectedAlunos.get(a))) {
                            i = 1;
                            Toast.makeText(CadTurmaActivity.this, "Aluno já selecionado!", Toast.LENGTH_SHORT).show();
                            break;
                        }else i = 2;

                    }
                    if (i == 2)
                        selectedAlunos.add(alunoSelecionado);
                    al = new AdapterExclAluno(CadTurmaActivity.this, selectedAlunos);
                    listarAlunos.setAdapter(al);

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
                if(!turma.equals("")) {
                    if (selectedAlunos.size() != 0 || !alunosAnonimos.getText().toString().equals("0")) {
                        Integer an = Integer.valueOf(alunosAnonimos.getText().toString());
                        Boolean checkTurma = bancoDados.checkTurma(turma);
                        if (!checkTurma) {
                            Boolean cadTurma = bancoDados.inserirTurma(turma, an);
                            if (cadTurma) {
                                id_turma = bancoDados.pegaIdTurma(turma);
                                if (selectedAlunos.size() != 0) {
                                    int num = listarAlunos.getAdapter().getCount();
                                    for (int i = 0; i < num; i++) {
                                        Integer id_aluno = bancoDados.pegaIdAluno(selectedAlunos.get(i));
                                        bancoDados.inserirAlunosNaTurma(id_turma, id_aluno);
                                    }
                                }
                                if (!an.equals(0)) {
                                    anonimos = new ArrayList<>();
                                    for (int x = 1; x <= an; x++) {
                                        String anonimo = "Aluno "+turma+" "+ x;
                                        anonimos.add(anonimo);
                                        bancoDados.inserirDadosAluno(anonimo, null, 0);
                                    }
                                    for (int a = 0; a < anonimos.size(); a++) {
                                        idAnonimos = bancoDados.pegaIdAnonimo(anonimos.get(a));
                                        bancoDados.inserirAlunosNaTurma(id_turma, idAnonimos);
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
}