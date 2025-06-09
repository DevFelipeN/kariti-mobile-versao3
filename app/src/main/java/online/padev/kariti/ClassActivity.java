package online.padev.kariti;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import online.padev.kariti.adapters.AdapterClickableSchool;
import online.padev.kariti.database.DataBaseKariti;

public class ClassActivity extends AppCompatActivity {
    ImageButton back;
    FloatingActionButton btnNewClass;
    ListView listViewClass;
    private List<String> listClass;
    TextView title, descriptionNewTurma;
    private Integer id_class;
    DataBaseKariti dataBase;
    private static final int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        back = findViewById(R.id.imgBtnVoltar);
        listViewClass = findViewById(R.id.listViewVisualTurma);
        descriptionNewTurma = findViewById(R.id.txtDescricaoAddTurma);
        title = findViewById(R.id.toolbar_title);
        btnNewClass = findViewById(R.id.iconaddTurma);

        title.setText(String.format("%s","Turmas"));

        dataBase = new DataBaseKariti(this);

        listClass = dataBase.listClassNames();
        if (listClass == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 7", Toast.LENGTH_SHORT).show();
            finish();
        }
        if(listClass.isEmpty()){
            startNewClassActivity();
        }
        AdapterClickableSchool adapterClass = new AdapterClickableSchool(this, listClass, listClass);
        listViewClass.setAdapter(adapterClass);

        listViewClass.setOnItemClickListener((parent, view, position, id) -> {
            id_class = dataBase.getClassId(adapterClass.getItem(position));
            if (id_class == null){
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 7", Toast.LENGTH_SHORT).show();
                return;
            }
            startClassDetails(id_class);
        });

        listViewClass.setOnItemLongClickListener((parent, view, position, id) -> {
            // Exibir a caixa de diálogo
            AlertDialog.Builder builder = new AlertDialog.Builder(ClassActivity.this);
            builder.setTitle("Atenção!")
                    .setMessage("Deseja excluir essa turma?")
                    .setPositiveButton("SIM", (dialog, which) -> {
                        id_class = dataBase.getClassId(listClass.get(position));
                        Boolean checkClassInProva = dataBase.checkIfClassInExam(id_class);
                        if (id_class == null || checkClassInProva == null){
                            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente 7", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!checkClassInProva) {
                            Boolean deleteClass = dataBase.deleteClass(id_class);
                            if (deleteClass) {
                                listClass.remove(position);
                                adapterClass.notifyDataSetChanged();
                                if(listClass.isEmpty()){
                                    finish();
                                }
                                Toast.makeText(ClassActivity.this, "Turma excluida com sucesso! ", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ClassActivity.this, "Algo deu errado, falha ao tentar excluir a turma! ", Toast.LENGTH_SHORT).show();
                            }
                        }else notifyImpossibleDelete();
                    })
                    .setNegativeButton("NÃO", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        });

        //Exibir o texto sobre o botão
        descriptionNewTurma.setVisibility(View.VISIBLE);
        descriptionNewTurma.setVisibility(View.VISIBLE);
        // Ocultar o texto após 3 segundos
        new Handler().postDelayed(() -> descriptionNewTurma.setVisibility(View.INVISIBLE), 10000);
        new Handler().postDelayed(() -> descriptionNewTurma.setVisibility(View.INVISIBLE), 10000);
        btnNewClass.setOnClickListener(v -> startNewClassActivity());
        back.setOnClickListener(view -> {
            getOnBackPressedDispatcher();
            finish();
        });
    }
    private void startClassDetails(Integer id_class) {
        Intent intent = new Intent(this, ClassDetailsActivity.class);
        intent.putExtra("idTurma", id_class);
        startActivityForResult(intent, REQUEST_CODE);
    }
    private void notifyImpossibleDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ClassActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Esta turma possui vínculo com uma ou mais prova(s) cadastrada(s), não sendo possível excluir!.");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                finish();
                startActivity(getIntent());
            }else{
                finish();
            }
        }
    }

    private void startNewClassActivity(){
        Intent intent = new Intent(this, ClassRegistrationActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

}