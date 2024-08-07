package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class InicioActivity extends AppCompatActivity {
    ImageButton imageButtonInicio, iconHelInicio;
    BancoDados bancoDados;
    TextView titulo;
    Button cadastrarEscola, visualizarEscola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        imageButtonInicio = findViewById(R.id.imageButtonInicio);
        cadastrarEscola = findViewById(R.id.buttonCadEscola);
        visualizarEscola = findViewById(R.id.buttonVisualizarEscola);
        iconHelInicio = findViewById(R.id.iconHelpLogout);
        titulo = findViewById(R.id.toolbar_title);

        bancoDados = new BancoDados(this);

        titulo.setText(String.format("%s","Inicio"));

        iconHelInicio.setOnClickListener(v -> ajuda());
        imageButtonInicio.setOnClickListener(v -> voltarTelaIncial());
        cadastrarEscola.setOnClickListener(v -> mudarParaTelaCadEscola());
        visualizarEscola.setOnClickListener(v -> mudarParaTelaVisualEscola());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                voltarTelaIncial();
            }
        });
    }
    public void voltarTelaIncial(){
        BancoDados.USER_ID = null;
        finish();
        Toast.makeText(InicioActivity.this, "Usuário desconectado", Toast.LENGTH_SHORT).show();
    }
    public void mudarParaTelaCadEscola(){
        Intent intent = new Intent(this, CadEscolaActivity.class);
        startActivity(intent);
    }
    public void mudarParaTelaVisualEscola(){
        Intent intent = new Intent(this, VisualEscolaActivity.class);
        startActivity(intent);
    }
    public void ajuda() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Olá, nesta tela são sugeridas duas opções importantes para funcionalidade do KARITI. Para acesso completo ao app, é necessário cadastrar a(s) escola(s) em que atua. Após essa etapa, basta selecionar a opção 'Selecionar Escola' e clicar no campo com a escola desejada para acessar as demais funcionalidades da aplicação! ");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}