package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import online.padev.kariti.database.DataBaseKariti;

public class ClassDetailsActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    ImageButton back;
    TextView textViewClassName, txtViewNumAnonymous;
    DataBaseKariti dataBase;
    ListView listViewStudents;
    List<String> listStudentsClass;
    String id_class;
    Integer numAnonymousBD;
    private static final int REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        back = findViewById(R.id.imgBtnVoltarDados);
        listViewStudents = findViewById(R.id.listViewDados);
        txtViewNumAnonymous = findViewById(R.id.textViewqtdAnonimos);
        textViewClassName = findViewById(R.id.textViewTurmaCad);

        dataBase = new DataBaseKariti(this);

        id_class = String.valueOf(Objects.requireNonNull(getIntent().getExtras()).getInt("idTurma"));
        String className = dataBase.pegarNomeTurma(id_class);
        if (className == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            finish();
        }

        textViewClassName.setText(String.format("Turma: %s", className));

        listStudentsClass = dataBase.listarAlunosPorTurma(id_class);
        if (listStudentsClass == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            finish();
        }
        numAnonymousBD = dataBase.pegarQtdAlunosPorStatus(id_class, 0);
        if (numAnonymousBD == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            finish();
        }
        txtViewNumAnonymous.setText(String.format(" Alunos Anônimos: %s \n Total de alunos: %s", numAnonymousBD, listStudentsClass.size()));
        AdapterDisabledSchool adapterStudents = new AdapterDisabledSchool(this, listStudentsClass, listStudentsClass);
        listViewStudents.setAdapter(adapterStudents);

        back.setOnClickListener(view -> {
            restartVisualClass();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                restartVisualClass();
            }
        });
    }
    public void PopMenu(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.actuvity_menuturma);
        popupMenu.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuEditar) {
            startEditClass();
            return true;
        }
        return true;
    }

    private void startEditClass(){
        Boolean provasCorreciton = dataBase.verificaExisteCorrecaoPorTurma(id_class);
        if (provasCorreciton == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!provasCorreciton){
            Intent intent = new Intent(this, ClassEditActivity.class);
            intent.putExtra("id_turma", id_class);
            startActivityForResult(intent, REQUEST_CODE);
        }else{
            notifyImpossibilityEdit();
        }
    }
    public void notifyImpossibilityEdit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ClassDetailsActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Esta turma possui vínculo com uma ou mais prova(s) já corrigidas, não sendo possível editar!");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            finish();
            startActivity(getIntent());
        }
    }
    public void restartVisualClass(){
        setResult(RESULT_OK);
        finish();
    }
}