package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class VisualEscolaActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    Button btnEscolaDesativada;
    ImageButton iconeAjuda;
    TextView titulo;
    ListView listViewEscolas;
    EscolaAdapter adapter;
    private ArrayList<String> listEscolaBD;
    BancoDados bancoDados;
    private static final int REQUEST_CODE = 1;

    Integer id_escola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_escola);

        btnVoltar = findViewById(R.id.imgBtnVoltaDescola);
        btnEscolaDesativada = findViewById(R.id.buttonEscDesativada);
        listViewEscolas = findViewById(R.id.listViewEscolas);
        iconeAjuda = findViewById(R.id.iconHelp);
        titulo = findViewById(R.id.toolbar_title);

        bancoDados = new BancoDados(this);

        titulo.setText(String.format("%s","Escolas"));

        listEscolaBD = (ArrayList<String>) bancoDados.listEscolas(1); //carrega todas as escolas ativadas para o usuario logado
        if(listEscolaBD.isEmpty()){
            if(!bancoDados.listEscolas(0).isEmpty()){
                carregaEscolasDesativadas();
            }else{
                ilustracao();
            }
        }
        adapter = new EscolaAdapter(this, listEscolaBD, listEscolaBD);
        listViewEscolas.setAdapter(adapter);

        //parei aqui

        btnEscolaDesativada.setOnClickListener(v -> telaEscolaDesativada());
        iconeAjuda.setOnClickListener(v -> ajuda());
        listViewEscolas.setOnItemClickListener((parent, view, position, id) -> {
            BancoDados.ID_ESCOLA = bancoDados.pegaIdEscola(adapter.getItem(position));
            carregarDetalhesEscola();
        });
        listViewEscolas.setOnItemLongClickListener((parent, view, position, id) -> {
            // Exibir a caixa de diálogo
            AlertDialog.Builder builder = new AlertDialog.Builder(VisualEscolaActivity.this);
            builder.setTitle("Atenção!")
                    .setMessage("Deseja desativar essa escola?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        id_escola = bancoDados.pegaIdEscola(adapter.getItem(position));
                        if(bancoDados.alterarStatusEscola(id_escola,0)){
                            listEscolaBD.remove(position);
                            adapter.notifyDataSetChanged();
                            if(listEscolaBD.isEmpty()) {
                                finish();
                            }
                            Toast.makeText(VisualEscolaActivity.this, "Escola desativada", Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(VisualEscolaActivity.this, "Erro: escola não desativada!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Não", (dialog, which) -> {
                        // Código para lidar com o clique no botão cancelar, se necessário
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Retorna true para indicar que o evento de long press foi consumido
            return true;
        });
        btnVoltar.setOnClickListener(v -> {
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            finish();
            startActivity(getIntent());
        }
    }
    private void ajuda() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("• Para continuar navegando nas funcionalidades do app, clique no campo com a escola desejada. \n\n" +
                "• Cada escola possui suas informações que são restritas a outras. \n\n" +
                "• Para desativar uma escola, basta selecionar a escola desejada e confirmar a ação. " +
                "Posteriormente, você poderá encontrar suas escolas desativadas clicando no botão 'Escolas Desativadas'.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void carregarDetalhesEscola(){
        Intent intent = new Intent(this, DetalhesEscolaActivity.class);
        startActivity(intent);
    }

    private void telaEscolaDesativada() {
        Intent intent = new Intent(this, EscolaDesativadaActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }
    private void ilustracao(){
        Intent intent = new Intent(this, ilustracionVoidSchoolctivity.class);
        startActivity(intent);
        finish();
    }
    private void carregaEscolasDesativadas() {
        Intent intent = new Intent(this, EscolaDesativadaActivity.class);
        startActivity(intent);
        finish();
    }
}