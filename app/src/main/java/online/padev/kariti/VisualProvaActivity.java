package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import online.padev.kariti.database.DataBaseKariti;

public class VisualProvaActivity extends AppCompatActivity {
    ImageButton voltar;
    Button bntVisualProva;
    String nomeTurma, nomeProva;
    Integer id_turma, id_prova;
    Spinner spinnerProva, spinnerTurma, spinnerAluno;
    List<String> listaProvas, listaTurmas, listaAlunos;
    TextView titulo;
    DataBaseKariti dataBaseKariti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_prova);

        voltar = findViewById(R.id.imgBtnVoltar);
        bntVisualProva = findViewById(R.id.buttonVisualizarProva);
        spinnerTurma = findViewById(R.id.spinnerTurma1);
        spinnerProva = findViewById(R.id.spinnerProva1);
        spinnerAluno = findViewById(R.id.spinnerAlunos1);
        titulo = findViewById(R.id.toolbar_title);

        titulo.setText(String.format("%s","Provas"));

        dataBaseKariti = new DataBaseKariti(this);

        listaTurmas = (ArrayList<String>) dataBaseKariti.listarTurmasPorProva();
        if (listaTurmas == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 1", Toast.LENGTH_SHORT).show();
            finish();
        }

        listaTurmas.add(0, "Selecione a turma");
        AdapterSpinner adapterTurma = new AdapterSpinner(this, listaTurmas);
        spinnerTurma.setAdapter(adapterTurma);

        spinnerTurma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    nomeTurma = spinnerTurma.getSelectedItem().toString();
                    id_turma = dataBaseKariti.pegarIdTurma(nomeTurma);
                    if (id_turma == null){
                        Toast.makeText(VisualProvaActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente 2", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    listaProvas = (ArrayList<String>) dataBaseKariti.listarNomesProvasPorTurma(id_turma.toString());
                    if (listaProvas == null){
                        Toast.makeText(VisualProvaActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente 3", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    AdapterSpinner adapterProva = new AdapterSpinner(VisualProvaActivity.this, listaProvas);
                    spinnerProva.setAdapter(adapterProva);

                    listaAlunos = (ArrayList<String>) dataBaseKariti.listarAlunosPorTurma(id_turma.toString());
                    if (listaAlunos == null){
                        Toast.makeText(VisualProvaActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente 4", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    listaAlunos.add(0, "Alunos");
                    AdapterSpinner adapterAluno = new AdapterSpinner(VisualProvaActivity.this, listaAlunos);
                    spinnerAluno.setAdapter(adapterAluno);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        bntVisualProva.setOnClickListener(view -> telaVisualProvaSelecionada());
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
    private void telaVisualProvaSelecionada(){
        if(spinnerProva.getSelectedItem() == null){
            return;
        }
        nomeProva = spinnerProva.getSelectedItem().toString();
        id_prova = dataBaseKariti.pegarIdProvaPorTurma(nomeProva, id_turma);
        if (id_prova == null){
            Toast.makeText(VisualProvaActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente 5", Toast.LENGTH_SHORT).show();
            return;
        }
        Boolean verificaProva = dataBaseKariti.verificaExisteCorrecao(id_prova.toString());
        if (verificaProva == null){
            Toast.makeText(VisualProvaActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente 6", Toast.LENGTH_SHORT).show();
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