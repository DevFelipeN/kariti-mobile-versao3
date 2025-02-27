package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SchoolDisabledActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    ImageButton back, iconHelp;
    BancoDados dataBase;
    List<String> listDisabledBD;
    TextView textViewTitle;
    DesativadaAdapter adapterDisabled;
    ListView listViewDisabled;
    private Integer id_school;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_disabled);

        back = findViewById(R.id.imgBtnVoltaDescola);
        listViewDisabled = findViewById(R.id.listViewEscDesativadas);
        textViewTitle = findViewById(R.id.toolbar_title);
        iconHelp = findViewById(R.id.iconHelp);

        dataBase = new BancoDados(this);

        textViewTitle.setText(String.format("%s","Desativadas"));

        iconHelp.setOnClickListener(v -> ajuda());

        listDisabledBD = (ArrayList<String>) dataBase.listarEscolas(0);
        if (listDisabledBD == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            finish();
        }

        adapterDisabled = new DesativadaAdapter(this, listDisabledBD, listDisabledBD);
        listViewDisabled.setAdapter(adapterDisabled);

        listViewDisabled.setOnItemLongClickListener((parent, view, position, id) -> {
            // Exibir a caixa de diálogo
            id_school = dataBase.pegarIdEscola(adapterDisabled.getItem(position));
            if (id_school == null || id_school == -1){
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(SchoolDisabledActivity.this);
            builder.setTitle("Atenção!")
                    .setMessage("Qual operação deseja realizar com essa escola? ")
                    .setPositiveButton("Ativar", (dialog, which) -> {
                        if(dataBase.alterarStatusEscola(id_school, 1)){
                            listDisabledBD.remove(position);
                            adapterDisabled.notifyDataSetChanged();
                            Toast.makeText(SchoolDisabledActivity.this, "Escola reativada com sucesso!", Toast.LENGTH_SHORT).show();
                            restartVisualSchool();
                        }else Toast.makeText(this, "Erro de ativação!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Excluir", (dialog, which) -> {
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

    private void ajuda() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Para ATIVAR ou EXCLUIR uma escola, basta pressionar sobre a escola desejada por alguns segundos e selecionar a ação desejada. ");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuExcluirEscola) {
            Toast.makeText(SchoolDisabledActivity.this, "Excluir Escola selecionado: ", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menuAtivarEscola) {
            Toast.makeText(SchoolDisabledActivity.this, "Ativar Escola selecionado", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }
    private void notifyIfDelete(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deseja realmente excluir essa escola?");
        builder.setMessage("Caso confirme essa ação, todos os dados pertencentes a essa escola, serão perdidos!");
        builder.setPositiveButton("SIM", (dialog, which) -> {
            Boolean deletaEscola = dataBase.deletarEscola(id_school);
            if (deletaEscola){
                listDisabledBD.remove(position);
                adapterDisabled.notifyDataSetChanged();
                Toast.makeText(SchoolDisabledActivity.this, "Escola excluida com sucesso", Toast.LENGTH_SHORT).show();
                if(listDisabledBD.isEmpty()){
                    restartVisualSchool();
                }
            }else{
                Toast.makeText(this, "Erro ao tentar excluir escola!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("NÃO", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}