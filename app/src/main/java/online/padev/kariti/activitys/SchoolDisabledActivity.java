package online.padev.kariti.activitys;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import online.padev.kariti.R;
import online.padev.kariti.adapters.ListNotActionAdapter;
import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.settings.ActivityLocale;

public class SchoolDisabledActivity extends AppCompatActivity{
    ImageButton back, iconHelp;
    DataBaseKariti dataBase;
    List<String> listDisabledBD;
    TextView textViewTitle;
    ListNotActionAdapter adapterDisabled;
    ListView listViewDisabled;
    private Integer id_school;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ActivityLocale.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_disabled);

        back = findViewById(R.id.imgBtnVoltaDescola);
        listViewDisabled = findViewById(R.id.listViewEscDesativadas);
        textViewTitle = findViewById(R.id.toolbar_title);
        iconHelp = findViewById(R.id.iconHelp);

        dataBase = new DataBaseKariti(this);

        textViewTitle.setText(getString(R.string.titleSchoolDeactivated));

        iconHelp.setOnClickListener(v -> help());

        listDisabledBD = dataBase.listSchoolNames(0);
        if (listDisabledBD == null){
            Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            finish();
        }

        adapterDisabled = new ListNotActionAdapter(this, listDisabledBD, listDisabledBD);
        listViewDisabled.setAdapter(adapterDisabled);

        listViewDisabled.setOnItemLongClickListener((parent, view, position, id) -> {
            // Exibir a caixa de diálogo
            id_school = dataBase.getSchoolId(adapterDisabled.getItem(position));
            if (id_school == null){
                Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(SchoolDisabledActivity.this);
            builder.setTitle(getString(R.string.titleAttention))
                    .setMessage(getString(R.string.longTextOperationSchool))
                    .setPositiveButton(getString(R.string.menuActivate), (dialog, which) -> {
                        if(dataBase.updateSchool(id_school, 1)){
                            listDisabledBD.remove(position);
                            adapterDisabled.notifyDataSetChanged();
                            Toast.makeText(this, getString(R.string.toastSchoolReactivatedSuccess), Toast.LENGTH_SHORT).show();
                            restartVisualSchool();
                        }else Toast.makeText(this, getString(R.string.toastSchoolReactivatedError), Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(getString(R.string.menuDelete), (dialog, which) -> {
                        //Implementar verificação, se possui dados como alunos turmas e provas ligadas a essa escola.................................
                        notifyIfDelete(position);
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        });
        back.setOnClickListener(v -> restartVisualSchool());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                restartVisualSchool();
            }
        });
    }
    public void restartVisualSchool(){
        setResult(RESULT_OK);
        finish();
    }

    private void help() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.titleHelp));
        builder.setMessage(getString(R.string.longTextReactivatedOrDelete));
        builder.setPositiveButton(getString(R.string.okDescription), (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void notifyIfDelete(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.longTitleSchoolDelete));
        builder.setMessage(getString(R.string.longTextInfoIfDelete));
        builder.setPositiveButton(getString(R.string.yes_description), (dialog, which) -> {
            if (dataBase.deleteSchool(id_school)){
                listDisabledBD.remove(position);
                adapterDisabled.notifyDataSetChanged();
                Toast.makeText(SchoolDisabledActivity.this, getString(R.string.toastSchoolSuccessDelete), Toast.LENGTH_SHORT).show();
                if(listDisabledBD.isEmpty()){
                    restartVisualSchool();
                }
            }else{
                Toast.makeText(this, getString(R.string.toastSchoolFailedDelete), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(getString(R.string.not_description), (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}