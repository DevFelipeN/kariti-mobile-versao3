package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

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

public class EditarTurmaActivity extends AppCompatActivity {
    ImageButton voltar;
    ImageView maisAn, menosAn;
    ListView listView;
    EditText editTurma, novosAlAnonimos;
    ArrayList<String> editAlTurma, alunosSpinner, selecionados;
    ArrayList<Integer> idsAlTurma;
    String id_turma, pegaTurma, alunosSelecionados;
    BancoDados bancoDados;
    AdapterExclAluno adapter;
    Spinner spinnerBuscAlun;
    Button salvar;
    Integer qtdAnonimos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_turma);

        listView = findViewById(R.id.listViewEditarTurma);
        editTurma = findViewById(R.id.editTextEditTurma);
        maisAn = findViewById(R.id.imageViewMaisNovosAnonimos);
        menosAn = findViewById(R.id.imageViewMenosNovosAnonimos);
        novosAlAnonimos = findViewById(R.id.editTextNovosAlunosAnonimos);
        voltar = findViewById(R.id.imgBtnVoltar);
        spinnerBuscAlun = findViewById(R.id.spinnerBuscAlunoNovos);
        salvar = findViewById(R.id.buttonSalvarTurma);
        bancoDados = new BancoDados(this);
        editAlTurma = new ArrayList<>();

        //Lista todos os alunos no Spinner
        alunosSpinner = (ArrayList<String>) bancoDados.obterNomesAlunos();
        alunosSpinner.add(0, "Selecione os Alunos");
        SpinnerAdapter adapterSpinner = new SpinnerAdapter(this, alunosSpinner);
        spinnerBuscAlun.setAdapter(adapterSpinner);
        spinnerBuscAlun.setSelection(0);

        //Mostra a turma a ser editada
        id_turma = getIntent().getExtras().getString("id_turma");
        pegaTurma = bancoDados.pegaNomeTurma(id_turma);
        qtdAnonimos = bancoDados.pegaqtdAnonimos(id_turma);
        editTurma.setText(pegaTurma);
        novosAlAnonimos.setText(qtdAnonimos.toString());
        Toast.makeText(this, "Anonimos: "+qtdAnonimos, Toast.LENGTH_SHORT).show();

        //Lista os aluno cadastrados nesta turma.
        idsAlTurma = (ArrayList<Integer>) bancoDados.listAlunosDturma(id_turma);
        int num = idsAlTurma.size();
        for(int y = 1; y <= num; y++){
            editAlTurma.add(bancoDados.pegaAluno(String.valueOf(y)));
        }
        adapter = new AdapterExclAluno(this, editAlTurma);
        listView.setAdapter(adapter);

        //Identifica o aluno selecionado no Spinner e adiciona no listView
        spinnerBuscAlun.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    alunosSelecionados = spinnerBuscAlun.getSelectedItem().toString();
                    editAlTurma.add(alunosSelecionados);
                    adapter = new AdapterExclAluno(EditarTurmaActivity.this, editAlTurma);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Remove aluno do listView após simples click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    editAlTurma.remove(i);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(EditarTurmaActivity.this, "Aluno Removido! ", Toast.LENGTH_SHORT).show();
            }
        });

        menosAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer menos = Integer.valueOf(novosAlAnonimos.getText().toString());
                if(menos > 0)
                    menos --;
                novosAlAnonimos.setText(menos.toString());
            }
        });

        maisAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer mais = Integer.valueOf(novosAlAnonimos.getText().toString());
                mais ++;
                novosAlAnonimos.setText(mais.toString());
            }
        });
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if(!editTurma.getText().toString().equals(pegaTurma)) {
                    bancoDados.upadateTurma(editTurma.getText().toString(), Integer.valueOf(id_turma));
                }
                bancoDados.deletarAlunoDturma(Integer.valueOf(id_turma));
                int num = listView.getAdapter().getCount();
                selecionados = (ArrayList<String>) editAlTurma;
                for (int i = 0; i < num; i++) {
                    bancoDados.inserirAlunosNaTurma(selecionados.get(i), Integer.valueOf(id_turma));
                }
                Integer qtdAlunosAnonimos = Integer.valueOf(novosAlAnonimos.getText().toString());
                if (qtdAlunosAnonimos > 0){
                    for (int x = 1; x <=qtdAlunosAnonimos; x++){
                        String an = "Alunos 0"+x;
                        bancoDados.inserirAlunosNaTurma(an, Integer.valueOf(id_turma));
                    }
                }

                 */
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

}