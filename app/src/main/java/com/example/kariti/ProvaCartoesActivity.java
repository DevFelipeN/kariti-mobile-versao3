package com.example.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
    List<String[]> dados;
    ArrayList<Integer> listIdAlTurma, listIdsAlunos;
    BancoDados bancoDados;
    GerarCsv gerarCsv;
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
        gerarCsv = new GerarCsv();

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
                String nomeProva = spinnerProva.getSelectedItem().toString();
                String id_prova = String.valueOf(bancoDados.pegaIdProva(nomeProva));
                String nomeTurma = spinnerTurma.getSelectedItem().toString();
                String id_usuario = String.valueOf(BancoDados.USER_ID);
                String prof = bancoDados.pegaUsuario(id_usuario);
                String data =  bancoDados.pegaData(id_prova);
                String nota = String.valueOf(bancoDados.listNota(id_prova));
                String questoes = String.valueOf(bancoDados.pegaqtdQuestoes(id_prova));
                String alternativas = String.valueOf(bancoDados.pegaqtdAlternativas(id_prova));

                dados = new ArrayList<>();

                String idTurma = String.valueOf(bancoDados.pegaIdTurma(nomeTurma));
                listIdsAlunos = (ArrayList<Integer>) bancoDados.listAlunosDturma(idTurma);
                int qtdProvas = listIdsAlunos.size();
                for(int x = 0;  x < qtdProvas; x++) {
                    String id_aluno = String.valueOf(listIdsAlunos.get(x));
                    String aluno = bancoDados.pegaNomeAluno(String.valueOf(listIdsAlunos.get(x)));
                    dados.add(new String[]{id_prova, nomeProva, prof, nomeTurma, data, nota, questoes, alternativas, id_aluno, aluno});
                }
                Boolean teste = gerarCsv.dadosProva(dados);
                if (teste)
                    Toast.makeText(ProvaCartoesActivity.this, "Sucesso!!!!", Toast.LENGTH_SHORT).show();

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