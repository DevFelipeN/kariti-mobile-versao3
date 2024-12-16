package online.padev.kariti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VisualProvaActivity2 extends AppCompatActivity {
    ImageButton voltar;
    String nomeTurma, nomeProva;
    Integer id_turma, id_prova;
    ArrayList<String> listaProvas, listaTurmas;
    RecyclerView recyclerView;
    MyAdapter adapterAluno;
    TextView titulo;
    Spinner spinnerTurma;
    BancoDados bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_prova2);

        voltar = findViewById(R.id.imgBtnVoltar);
        recyclerView = findViewById(R.id.listProvas);
        spinnerTurma = findViewById(R.id.spinnerTurma2);
        titulo = findViewById(R.id.toolbar_title);

        titulo.setText(String.format("%s","Provas"));

        bancoDados = new BancoDados(this);

        listaTurmas = (ArrayList<String>) bancoDados.listarTurmasPorProva();
        if (listaTurmas == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 1", Toast.LENGTH_SHORT).show();
            finish();
        }

        //listaTurmas.add(0, "Turmas");
        SpinnerAdapter adapterTurma = new SpinnerAdapter(this, listaTurmas);
        spinnerTurma.setAdapter(adapterTurma);
        spinnerTurma.setSelection(0);
        nomeTurma = spinnerTurma.getSelectedItem().toString();
        id_turma = bancoDados.pegarIdTurma(nomeTurma);
        if (id_turma == null){
            Toast.makeText(VisualProvaActivity2.this, "Falha de comunicação! \n\n Por favor, tente novamente 5", Toast.LENGTH_SHORT).show();
            finish();
        }

        listaProvas = (ArrayList<String>) bancoDados.listarNomesProvasPorTurma(id_turma.toString());
        if (listaProvas == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 1", Toast.LENGTH_SHORT).show();
            finish();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterAluno = new MyAdapter(this, listaProvas, this::onItemClick, this::onItemLongClick);
        recyclerView.setAdapter(adapterAluno);

        spinnerTurma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nomeTurma = spinnerTurma.getSelectedItem().toString();
                id_turma = bancoDados.pegarIdTurma(nomeTurma);
                listaProvas.clear();
                listaProvas = (ArrayList<String>) bancoDados.listarNomesProvasPorTurma(id_turma.toString());
                if (listaProvas == null){
                    Toast.makeText(VisualProvaActivity2.this, "Falha de comunicação! \n\n Por favor, tente novamente 1", Toast.LENGTH_SHORT).show();
                    finish();
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(VisualProvaActivity2.this));
                adapterAluno = new MyAdapter(VisualProvaActivity2.this, listaProvas, VisualProvaActivity2.this::onItemClick, VisualProvaActivity2.this::onItemLongClick);
                recyclerView.setAdapter(adapterAluno);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        voltar.setOnClickListener(view -> {
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
    public void onItemClick(int position) {
        nomeProva = listaProvas.get(position);
        id_prova = bancoDados.pegarIdProvaPorTurma(nomeProva, id_turma);
        if (id_prova == null){
            Toast.makeText(VisualProvaActivity2.this, "Falha de comunicação! \n\n Por favor, tente novamente 5", Toast.LENGTH_SHORT).show();
            return;
        }
        telaVisualProvaSelecionada();

    }
    public void onItemLongClick(int position) {
        //wdwdwdqdqqsq
    }
    private void telaVisualProvaSelecionada(){
        Boolean verificaProva = bancoDados.verificaExisteCorrecao(id_prova.toString());
        if (verificaProva == null){
            Toast.makeText(VisualProvaActivity2.this, "Falha de comunicação! \n\n Por favor, tente novamente 6", Toast.LENGTH_SHORT).show();
            return;
        }
        if(verificaProva){
            Intent intent = new Intent(this, VisualProvaCorrigidaActivity.class);
            intent.putExtra("prova", nomeProva);
            intent.putExtra("id_prova", id_prova);
            intent.putExtra("turma", nomeTurma);
            startActivity(intent);
        }else {
            Toast.makeText(this, "Prova não corrigida!", Toast.LENGTH_SHORT).show();
        }
    }
}