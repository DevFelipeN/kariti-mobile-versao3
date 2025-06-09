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

import java.util.List;

import online.padev.kariti.adapters.AdapterClickableList;
import online.padev.kariti.adapters.AdapterSpinner;
import online.padev.kariti.entity.Exam;
import online.padev.kariti.database.DataBaseKariti;

public class ProvaViewActivity extends AppCompatActivity {
    ImageButton back;
    private String className, nameProva;
    private Integer id_class, id_prova;
    private List<String> listProva, listClass;
    RecyclerView recyclerView;
    AdapterClickableList adapterProva;
    AdapterSpinner adapterSpinnerClass;
    TextView title;
    Spinner spinnerClass;
    DataBaseKariti dataBaseKariti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_view);

        back = findViewById(R.id.imgBtnVoltar);
        recyclerView = findViewById(R.id.listProvas);
        spinnerClass = findViewById(R.id.spinnerTurma2);
        title = findViewById(R.id.toolbar_title);

        title.setText(String.format("%s","Provas"));

        dataBaseKariti = new DataBaseKariti(this);

        listClass = dataBaseKariti.listClassByExam();
        if (listClass == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 1", Toast.LENGTH_SHORT).show();
            finish();
        }

        //listaTurmas.add(0, "Turmas");
        adapterSpinnerClass = new AdapterSpinner(this, listClass);
        spinnerClass.setAdapter(adapterSpinnerClass);
        spinnerClass.setSelection(0);
        className = spinnerClass.getSelectedItem().toString();
        id_class = dataBaseKariti.getClassId(className);
        if (id_class == null){
            Toast.makeText(ProvaViewActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente 5", Toast.LENGTH_SHORT).show();
            finish();
        }

        listProva = dataBaseKariti.listExamNames(id_class.toString());
        if (listProva == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 1", Toast.LENGTH_SHORT).show();
            finish();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterProva = new AdapterClickableList(this, listProva, this::onItemClick, this::onItemLongClick);
        recyclerView.setAdapter(adapterProva);

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                className = spinnerClass.getSelectedItem().toString();
                id_class = dataBaseKariti.getClassId(className);
                listProva.clear();
                listProva = dataBaseKariti.listExamNames(id_class.toString());
                if (listProva == null){
                    Toast.makeText(ProvaViewActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente 1", Toast.LENGTH_SHORT).show();
                    finish();
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(ProvaViewActivity.this));
                adapterProva = new AdapterClickableList(ProvaViewActivity.this, listProva, ProvaViewActivity.this::onItemClick, ProvaViewActivity.this::onItemLongClick);
                recyclerView.setAdapter(adapterProva);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        back.setOnClickListener(view -> {
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
        nameProva = listProva.get(position);
        id_prova = dataBaseKariti.getExamId(nameProva, id_class);
        if (id_prova == null){
            Toast.makeText(ProvaViewActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente 5", Toast.LENGTH_SHORT).show();
            return;
        }
        startCorrectionProva();
    }
    public void onItemLongClick(int position) {
        nameProva = listProva.get(position);
        id_prova = dataBaseKariti.getExamId(nameProva, id_class);
        displayEditOrDelete(position);
    }
    private void startCorrectionProva(){
        Boolean checkIsCorrected = dataBaseKariti.checkIfExamCorrected(id_prova.toString());
        if (checkIsCorrected == null){
            Toast.makeText(ProvaViewActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente 6", Toast.LENGTH_SHORT).show();
            return;
        }
        if(checkIsCorrected){
            Exam exam = new Exam(id_prova, dataBaseKariti);
            Intent intent = new Intent(this, ProvaCorrectedActivity.class);
            intent.putExtra("prova", exam);
            startActivity(intent);
        }else {
            Toast.makeText(this, "Prova não corrigida!", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayEditOrDelete(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deseja excluir ou editar esta prova?")
                .setPositiveButton("EXCLUIR", (dialog, which) -> noticeIfDelete(position))
                .setNegativeButton("EDITAR", (dialog, which) -> editProva());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void editProva(){
        if(dataBaseKariti.checkIfExamCorrected(id_prova.toString())){
            noticeImpossibleEdit();
        }else {
            Intent intent = new Intent(this, ProvaEditActivity.class);
            intent.putExtra("id_prova", id_prova);
            startActivity(intent);
        }
    }
    private void noticeImpossibleEdit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO")
                .setMessage("Esta prova já foi corrigida.\n\n" +
                        "Não é possivel editar provas já corrigidas!")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void noticeIfDelete(int position){
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
        if (dataBaseKariti.deleteExamData(id_prova)){
            listProva.remove(nameProva);
            notifyProvaDeleted(position);
        }else{
            Toast.makeText(this, "Falha ao tentar excluir essa prova!", Toast.LENGTH_SHORT).show();
        }

    }
    private void notifyProvaDeleted(int position){
        Toast.makeText(this, "Prova excluida com sucesso!", Toast.LENGTH_SHORT).show();
        if(!listProva.isEmpty()){
            adapterProva.notifyItemRemoved(position);
        }else{
            finish();
        }
    }
}