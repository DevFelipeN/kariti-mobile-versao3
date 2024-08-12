package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.ImageButton;

public class CadEscolaActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    EditText editTextNomeEscola, editTextBairro;
    Button btCadastrarEscola;
    BancoDados bancoDados;
    TextView textViewTitulo;
    private String nomeEscola, bairroEscola;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_escola);

        btnVoltar = findViewById(R.id.imgBtnVoltar);
        editTextNomeEscola = findViewById(R.id.editTextNomeEscola);
        editTextBairro = findViewById(R.id.editTextBairro);
        btCadastrarEscola = findViewById(R.id.button);
        textViewTitulo = findViewById(R.id.toolbar_title);

        bancoDados = new BancoDados(this);

        textViewTitulo.setText(String.format("%s","Cadastrar Escola"));
        
        btCadastrarEscola.setOnClickListener(v -> {
            nomeEscola = editTextNomeEscola.getText().toString();
            bairroEscola = editTextBairro.getText().toString();
            if (nomeEscola.trim().isEmpty()) {
                Toast.makeText(CadEscolaActivity.this, "Informe o nome da escola!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (bancoDados.verificaEscola(nomeEscola)) {
                informacao();
                return;
            }
            if (bancoDados.inserirDadosEscola(nomeEscola, bairroEscola, 1)){
                Toast.makeText(CadEscolaActivity.this, "Escola cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                if(getIntent().getExtras().getString("status").equals("primeiroAcesso")){
                    carregarVisualEscola();
                }else{
                    recarregarVisualEscola();
                }
            }else{
                Toast.makeText(CadEscolaActivity.this, "Falha no cadastro da escola!", Toast.LENGTH_SHORT).show();
            }
        });
        //btnVoltar.setVisibility(View.VISIBLE);
        btnVoltar.setOnClickListener(v -> {
            if(getIntent().getExtras().getString("status").equals("primeiroAcesso")){
                getOnBackPressedDispatcher();
                finish();
            }else{
                recarregarVisualEscola();
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(getIntent().getExtras().getString("status").equals("primeiroAcesso")){
                    getOnBackPressedDispatcher();
                    finish();
                }else{
                    recarregarVisualEscola();
                }
            }
        });
    }
    private void carregarVisualEscola(){
        Intent intent = new Intent(this, VisualEscolaActivity.class);
        startActivity(intent);
        finish();
    }
    private void recarregarVisualEscola(){
        setResult(RESULT_OK);
        finish();
    }
    private void informacao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atenção!")
                .setMessage("Verificamos que esta escola encontra-se cadastrada em nosso sistema. Caso não esteja em sua lista principal, recomendamos reativa-la em 'Escolas Desativadas'")
                .setPositiveButton("Ok", (dialog, which) -> {
                    //Fecha
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}