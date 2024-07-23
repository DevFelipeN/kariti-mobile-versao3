package online.padev.kariti;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EditarProva extends AppCompatActivity {
    private Spinner prova, turma;
    private Button proximo;
    private ArrayList<String> listTurma, lisProva;
    private String turmaSelecionada, provaSelecionada;
    private Integer id_turma, id_prova;
    private TextView titulo;
    BancoDados bancoDados;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_prova);

        prova = findViewById(R.id.spinnerProva2);
        turma = findViewById(R.id.spinnerTurma2);
        proximo = findViewById(R.id.buttonProximo);
        titulo = findViewById(R.id.toolbar_title);

        titulo.setText("Editar Prova");

        bancoDados = new BancoDados(this);

        listTurma = (ArrayList<String>) bancoDados.listTurmasPorProva();
        listTurma.add(0, "Selecione a turma");
        SpinnerAdapter adapterTurma = new SpinnerAdapter(this, listTurma);
        turma.setAdapter(adapterTurma);

        turma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    turmaSelecionada = turma.getSelectedItem().toString();
                    id_turma = bancoDados.pegaIdTurma(turmaSelecionada);

                    lisProva = (ArrayList<String>) bancoDados.listProvasNCorrigidas(id_turma);
                    SpinnerAdapter adapterProva = new SpinnerAdapter(EditarProva.this, lisProva);
                    prova.setAdapter(adapterProva);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        proximo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telaVisualProvaSelecionada();
            }
        });
    }
    public void telaVisualProvaSelecionada(){
        if(prova.getSelectedItem() == null){
            return;
        }
        provaSelecionada = (String) prova.getSelectedItem();
        id_prova = bancoDados.pegaIdProva(provaSelecionada);
        Toast.makeText(this, "Passando aquiiiiiiii", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, EdicaoProva.class);
        intent.putExtra("prova", provaSelecionada);
        intent.putExtra("id_prova", id_prova);
        intent.putExtra("id_turma", id_turma);
        startActivity(intent);
    }
}