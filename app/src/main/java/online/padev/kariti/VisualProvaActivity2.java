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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class VisualProvaActivity2 extends AppCompatActivity {
    ImageButton voltar;
    String nomeTurma, nomeProva;
    Integer id_turma, id_prova;
    List<String> listaProvas, listaTurmas;
    RecyclerView recyclerView;
    MyAdapter adapterProvas;
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

        listaProvas = bancoDados.listarNomesProvasPorTurma(id_turma.toString());
        if (listaProvas == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 1", Toast.LENGTH_SHORT).show();
            finish();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterProvas = new MyAdapter(this, listaProvas, this::onItemClick, this::onItemLongClick);
        recyclerView.setAdapter(adapterProvas);

        spinnerTurma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nomeTurma = spinnerTurma.getSelectedItem().toString();
                id_turma = bancoDados.pegarIdTurma(nomeTurma);
                listaProvas.clear();
                listaProvas = bancoDados.listarNomesProvasPorTurma(id_turma.toString());
                if (listaProvas == null){
                    Toast.makeText(VisualProvaActivity2.this, "Falha de comunicação! \n\n Por favor, tente novamente 1", Toast.LENGTH_SHORT).show();
                    finish();
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(VisualProvaActivity2.this));
                adapterProvas = new MyAdapter(VisualProvaActivity2.this, listaProvas, VisualProvaActivity2.this::onItemClick, VisualProvaActivity2.this::onItemLongClick);
                recyclerView.setAdapter(adapterProvas);
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
        nomeProva = listaProvas.get(position);
        id_prova = bancoDados.pegarIdProvaPorTurma(nomeProva, id_turma);
        solicitaExcluirOuEditar(position);
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

    private void solicitaExcluirOuEditar(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deseja excluir ou editar esta prova?")
                .setPositiveButton("Excluir", (dialog, which) -> avisoSeExcluir(position))
                .setNegativeButton("Editar", (dialog, which) -> editarProva());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void editarProva(){
        if(bancoDados.verificaExisteCorrecao(id_prova.toString())){
            avisoProvaNaoEditavel();
        }else {
            Intent intent = new Intent(this, EdicaoProva.class);
            intent.putExtra("id_prova", id_prova);
            startActivity(intent);
        }
    }
    private void avisoProvaNaoEditavel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO")
                .setMessage("Esta prova já foi corrigida.\n\n" +
                        "Não é possivel editar provas já corrigidas!")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void avisoSeExcluir(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO")
                .setMessage("Caso confirme essa ação todos os dados dessa prova incluindo correção, serão excluidos permanentemente! \n\n" +
                        "Deseja realmente excluir essa prova? ")
                .setPositiveButton("SIM", (dialog, which) -> deleteProva(position))
                .setNegativeButton("NÃO", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteProva(int position){
        if (bancoDados.deletarProva(id_prova)){
            listaProvas.remove(nomeProva);
            provaApagada(position);
        }else{
            Toast.makeText(this, "Falha ao tentar excluir essa prova!", Toast.LENGTH_SHORT).show();
        }

    }
    private void provaApagada(int position){
        Toast.makeText(this, "Prova excluida com sucesso!", Toast.LENGTH_SHORT).show();
        if(!listaProvas.isEmpty()){
            adapterProvas.notifyItemRemoved(position);
        }else{
            finish();
        }
    }
}