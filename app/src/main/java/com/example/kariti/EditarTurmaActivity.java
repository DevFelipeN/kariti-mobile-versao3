package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

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
    EditText editTurma, novosAlAnonmos;
    ArrayList<String> editAlTurma, alunosSpinner;
    String id_turma;
    BancoDados bancoDados;
    Spinner spinnerBuscAlun;
    Button salvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_turma);

        listView = findViewById(R.id.listViewAlunosTurma);
        editTurma = findViewById(R.id.editTextEditTurma);
        maisAn = findViewById(R.id.imageViewMaisNovosAnonimos);
        menosAn = findViewById(R.id.imageViewMenosNovosAnonimos);
        novosAlAnonmos = findViewById(R.id.editTextNovosAlunosAnonimos);
        voltar = findViewById(R.id.imgBtnVoltar);
        bancoDados = new BancoDados(this);
        spinnerBuscAlun = findViewById(R.id.spinnerBuscAlunoNovos);
        salvar = findViewById(R.id.buttonSalvarTurma);

        alunosSpinner = (ArrayList<String>) bancoDados.obterNomesAlunos();
        alunosSpinner.add(0, "Selecione os Alunos");
        SpinnerAdapter adapterSpinner = new SpinnerAdapter(this, alunosSpinner);
        spinnerBuscAlun.setAdapter(adapterSpinner);
        spinnerBuscAlun.setSelection(0);

        id_turma = getIntent().getExtras().getString("id_turma");
        String pegaTurma = bancoDados.pegaNomeTurma(id_turma);
        editTurma.setText(pegaTurma);

        editAlTurma = (ArrayList<String>) bancoDados.listAlunosDturma(id_turma);
        AdapterExclAluno adapter = new AdapterExclAluno(this, editAlTurma);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Boolean remove = bancoDados.deletarAlunoDturma(editAlTurma.get(i));
                if(remove) {
                    editAlTurma.remove(i);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(EditarTurmaActivity.this, "Aluno Excluido! ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        menosAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer menos = Integer.valueOf(novosAlAnonmos.getText().toString());
                if(menos > 0)
                    menos --;
                novosAlAnonmos.setText(menos.toString());
            }
        });

        maisAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer mais = Integer.valueOf(novosAlAnonmos.getText().toString());
                mais ++;
                novosAlAnonmos.setText(mais.toString());
                //listView.setAdapter(adapter);
            }
        });
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Em desenvolvimento!
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