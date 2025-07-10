package online.padev.kariti.activitys;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

import online.padev.kariti.R;
import online.padev.kariti.adapters.ClassListAdapter;
import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.entity.ClassSchool;
import online.padev.kariti.settings.ActivityLocale;

public class ClassActivity extends AppCompatActivity {
    ImageButton back;
    FloatingActionButton btnNewClass;
    ListView listViewClass;
    private List<ClassSchool> listClass;
    TextView title, descriptionNewClass;
    DataBaseKariti dataBase;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ActivityLocale.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        back = findViewById(R.id.imgBtnVoltar);
        listViewClass = findViewById(R.id.listViewVisualTurma);
        descriptionNewClass = findViewById(R.id.txtDescricaoAddTurma);
        title = findViewById(R.id.toolbar_title);
        btnNewClass = findViewById(R.id.iconaddTurma);

        title.setText(getString(R.string.textViewTitle));

        dataBase = new DataBaseKariti(this);

        listClass = dataBase.listClassSchoolData();
        if (listClass == null){
            Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            finish();
        }
        if(listClass.isEmpty()){
            startNewClassActivity();
        }

        ClassListAdapter adapterClass = new ClassListAdapter(this, listClass);
        listViewClass.setAdapter(adapterClass);

        listViewClass.setOnItemClickListener((parent, view, position, id) -> {
            ClassSchool classSchool = adapterClass.getItem(position);
            if (classSchool == null){
                Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                return;
            }
            startClassDetails(classSchool);
        });

        listViewClass.setOnItemLongClickListener((parent, view, position, id) -> {
            // Exibir a caixa de diálogo
            AlertDialog.Builder builder = new AlertDialog.Builder(ClassActivity.this);
            builder.setTitle(getString(R.string.titleAttention))
                    .setMessage(getString(R.string.longTextNotifyDeleteClass))
                    .setPositiveButton(getString(R.string.yes_description), (dialog, which) -> {
                        ClassSchool classSchool = adapterClass.getItem(position);
                        Boolean checkClassInExam = dataBase.checkIfClassInExam(classSchool.getClass_id());
                        if (checkClassInExam == null){
                            Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!checkClassInExam) {
                            boolean deleteClass = dataBase.deleteClass(classSchool.getClass_id());
                            if (deleteClass) {
                                listClass.remove(position);
                                adapterClass.notifyDataSetChanged();
                                if(listClass.isEmpty()){
                                    finish();
                                }
                                Toast.makeText(this, getString(R.string.toastClassDelete), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(this, getString(R.string.toastClassNoDeleted), Toast.LENGTH_SHORT).show();
                            }
                        }else notifyImpossibleDelete();
                    })
                    .setNegativeButton(getString(R.string.not_description), (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        });

        //Exibir o texto sobre o botão
        descriptionNewClass.setVisibility(View.VISIBLE);
        descriptionNewClass.setVisibility(View.VISIBLE);
        // Ocultar o texto após 3 segundos
        new Handler().postDelayed(() -> descriptionNewClass.setVisibility(View.INVISIBLE), 10000);
        new Handler().postDelayed(() -> descriptionNewClass.setVisibility(View.INVISIBLE), 10000);
        btnNewClass.setOnClickListener(v -> startNewClassActivity());
        back.setOnClickListener(view -> {
            getOnBackPressedDispatcher();
            finish();
        });
    }
    private void startClassDetails(ClassSchool cs) {
        Intent intent = new Intent(this, ClassDetailsActivity.class);
        intent.putExtra("classSchool", cs);
        startActivityForResult(intent, REQUEST_CODE);
    }
    private void notifyImpossibleDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ClassActivity.this);
        builder.setTitle(getString(R.string.titleAttention))
                .setMessage(getString(R.string.longTextImpossibleDeletedClass));
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