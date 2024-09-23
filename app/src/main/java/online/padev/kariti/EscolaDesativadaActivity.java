package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class EscolaDesativadaActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    ImageButton btnVoltar, iconeAjuda;
    BancoDados bancoDados;
    ArrayList<String> listDesativadasBD;
    TextView textViewTitulo;
    DesativadaAdapter adapter;
    ListView listViewDesativadas;
    private Integer id_escola;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escola_desativada);

        btnVoltar = findViewById(R.id.imgBtnVoltaDescola);
        listViewDesativadas = findViewById(R.id.listViewEscDesativadas);
        textViewTitulo = findViewById(R.id.toolbar_title);
        iconeAjuda = findViewById(R.id.iconHelp);

        bancoDados = new BancoDados(this);

        textViewTitulo.setText(String.format("%s","Desativadas"));

        iconeAjuda.setOnClickListener(v -> ajuda());

        listDesativadasBD = (ArrayList<String>) bancoDados.listEscolas(0);
        if(listDesativadasBD.isEmpty()){
            ilustracao();
        }
        adapter = new DesativadaAdapter(this, listDesativadasBD, listDesativadasBD);
        listViewDesativadas.setAdapter(adapter);
        listViewDesativadas.setOnItemLongClickListener((parent, view, position, id) -> {
            // Exibir a caixa de diálogo
            id_escola = bancoDados.pegaIdEscola(adapter.getItem(position));
            AlertDialog.Builder builder = new AlertDialog.Builder(EscolaDesativadaActivity.this);
            builder.setTitle("Atenção!")
                    .setMessage("Qual operação deseja realizar com essa escola? ")
                    .setPositiveButton("Ativar", (dialog, which) -> {
                        if(bancoDados.alterarStatusEscola(id_escola, 1)){
                            listDesativadasBD.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(EscolaDesativadaActivity.this, "Escola Reativada com Sucesso!", Toast.LENGTH_SHORT).show();
                            recarregarVisualEscola();
                        }else Toast.makeText(EscolaDesativadaActivity.this, "Erro de ativação!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Excluir", (dialog, which) -> {
                        //Implementar verificação, se possui dados como alunos turmas e provas ligadas a essa escola.................................
                        Boolean deletaEscola = bancoDados.deletarEscola(id_escola);
                        if (deletaEscola){
                            listDesativadasBD.remove(position);
                            adapter.notifyDataSetChanged();
                            if(listDesativadasBD.isEmpty()){
                                finish();
                            }
                            Toast.makeText(EscolaDesativadaActivity.this, "Escola excluida com sucesso", Toast.LENGTH_SHORT).show();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        });
        btnVoltar.setOnClickListener(v -> recarregarVisualEscola());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                recarregarVisualEscola();
            }
        });
    }
    public void recarregarVisualEscola(){
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
            Toast.makeText(EscolaDesativadaActivity.this, "Excluir Escola selecionado: ", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menuAtivarEscola) {
            Toast.makeText(EscolaDesativadaActivity.this, "Ativar Escola selecionado", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }
    private void ilustracao(){
        Intent intent = new Intent(this, ilustracionVoidSchoolctivity.class);
        startActivity(intent);
        finish();
    }
}