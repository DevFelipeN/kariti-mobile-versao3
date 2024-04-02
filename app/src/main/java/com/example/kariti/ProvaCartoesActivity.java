package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ProvaCartoesActivity extends AppCompatActivity {
    ImageButton voltar;
    Button baixarCartoes;
    Integer id_turma, endereco, idTurmaSelect;
    String prova, turma, turmaSelecionada;
    ArrayList<String> provalist, turmalist, alunolist;
    ArrayList<Integer> listIdAlTurma;
    BancoDados bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_cartoes);

        voltar = findViewById(R.id.imgBtnVoltar);
        Spinner spinnerTurma = findViewById(R.id.spinnerTurma);
        Spinner spinnerProva = findViewById(R.id.spinnerProva);
        Spinner spinnerAluno = findViewById(R.id.spinnerAlunos);
        baixarCartoes = findViewById(R.id.baixarcatoes);

        bancoDados = new BancoDados(this);

        endereco = getIntent().getExtras().getInt("endereco");
        prova = getIntent().getExtras().getString("prova");

        turmalist = (ArrayList<String>) bancoDados.obterNomeTurmas();
        if(endereco.equals(02)){
            turmalist.add(0,"Selecione a turma");
            SpinnerAdapter adapterTurma = new SpinnerAdapter(this, turmalist);
            spinnerTurma.setAdapter(adapterTurma);

            spinnerTurma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position!=0){
                        turmaSelecionada = spinnerTurma.getSelectedItem().toString();
                        idTurmaSelect = bancoDados.pegaIdTurma(turmaSelecionada);
                        provalist = (ArrayList<String>) bancoDados.obterNomeProvas(String.valueOf(idTurmaSelect));
                        provalist.add(0, "Selecione a prova");
                        SpinnerAdapter adapterProva = new SpinnerAdapter(ProvaCartoesActivity.this, provalist);
                        spinnerProva.setAdapter(adapterProva);

                        alunolist = new ArrayList<>();
                        listIdAlTurma = (ArrayList<Integer>) bancoDados.listAlunosDturma(String.valueOf(idTurmaSelect));
                        alunolist.add(0, "Alunos");
                        int num = listIdAlTurma.size();
                        for(int x = 0; x < num; x++){
                            String id_aluno = String.valueOf(listIdAlTurma.get(x));
                            alunolist.add(bancoDados.pegaNomeAluno(id_aluno));
                        }
                        SpinnerAdapter adapterAluno = new SpinnerAdapter(ProvaCartoesActivity.this, alunolist);
                        spinnerAluno.setAdapter(adapterAluno);

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }else if(endereco.equals(01)){
            id_turma = getIntent().getExtras().getInt("id_turma");
            turma = bancoDados.pegaNomeTurma(String.valueOf(id_turma));
            turmalist.add(0, turma);
            SpinnerAdapter adapterTurma = new SpinnerAdapter(this, turmalist);
            spinnerTurma.setAdapter(adapterTurma);

            provalist = (ArrayList<String>) bancoDados.obterNomeProvas(String.valueOf(id_turma));
            provalist.add(0, prova);
            SpinnerAdapter adapterProva = new SpinnerAdapter(ProvaCartoesActivity.this, provalist);
            spinnerProva.setAdapter(adapterProva);

            alunolist = new ArrayList<>();
            listIdAlTurma = (ArrayList<Integer>) bancoDados.listAlunosDturma(String.valueOf(id_turma));
            alunolist.add(0, "Alunos");
            int num = listIdAlTurma.size();
            for(int x = 0; x < num; x++){
                String id_aluno = String.valueOf(listIdAlTurma.get(x));
                alunolist.add(bancoDados.pegaNomeAluno(id_aluno));
            }
            SpinnerAdapter adapterAluno = new SpinnerAdapter(ProvaCartoesActivity.this, alunolist);
            spinnerAluno.setAdapter(adapterAluno);

            spinnerTurma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position!=0){
                        turmaSelecionada = spinnerTurma.getSelectedItem().toString();
                        idTurmaSelect = bancoDados.pegaIdTurma(turmaSelecionada);
                        provalist = (ArrayList<String>) bancoDados.obterNomeProvas(String.valueOf(idTurmaSelect));
                        provalist.add(0, "Selecione a prova");
                        SpinnerAdapter adapterProva = new SpinnerAdapter(ProvaCartoesActivity.this, provalist);
                        spinnerProva.setAdapter(adapterProva);

                        alunolist = new ArrayList<>();
                        listIdAlTurma = (ArrayList<Integer>) bancoDados.listAlunosDturma(String.valueOf(idTurmaSelect));//pegando Ids dos alunos
                        alunolist.add(0, "Alunos");
                        int num = listIdAlTurma.size();
                        for(int x = 0; x < num; x++){
                            String id_aluno = String.valueOf(listIdAlTurma.get(x));
                            alunolist.add(bancoDados.pegaNomeAluno(id_aluno));
                        }
                        SpinnerAdapter adapterAluno = new SpinnerAdapter(ProvaCartoesActivity.this, alunolist);
                        spinnerAluno.setAdapter(adapterAluno);

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        baixarCartoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String provaSelect = spinnerProva.getSelectedItem().toString();
                String turmaSelect = spinnerTurma.getSelectedItem().toString();
                Integer id_prova = bancoDados.pegaIdProva(provaSelect);
                String prof = bancoDados.pegaUsuario(BancoDados.USER_ID);
                Toast.makeText(ProvaCartoesActivity.this, "Informações: "+id_prova+";"+provaSelect+";"+prof+";"+turmaSelect, Toast.LENGTH_SHORT).show();
                //Em Implementação

            }
        });

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}