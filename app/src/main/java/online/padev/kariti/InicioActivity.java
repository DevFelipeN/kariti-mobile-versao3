package online.padev.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import online.padev.kariti.R;

public class InicioActivity extends AppCompatActivity {
    ImageButton imageButtonInicio, iconHelInicio;
    BancoDados bancoDados;

    Button cadastrarEscola, visualizarEscola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        imageButtonInicio = findViewById(R.id.imageButtonInicio);
        cadastrarEscola = findViewById(R.id.buttonCadEscola);
        visualizarEscola = findViewById(R.id.buttonVisualizarEscola);
        bancoDados = new BancoDados(this);
        iconHelInicio = findViewById(R.id.iconHelpLogout);
        /*

        if(!bancoDados.checkEscola("Escola Teste1")) {
            bancoDados.inserirDadosEscola("Escola Teste1", "centro", 1);
            bancoDados.inserirDadosEscola("Escola Desativada1", "centro", 0);
        }
         */
        iconHelInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelpDetalhes();
            }
        });

        imageButtonInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voltarTelaIncial();
            }
        });
        cadastrarEscola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { mudarParaTelaCadEscola();}
        });
        visualizarEscola.setOnClickListener(new View.OnClickListener() {
            @Override
            //Local modificado
            public void onClick(View view) {
                Intent intencion = new Intent(getApplicationContext(), VisualEscolaActivity.class);
                startActivity(intencion);
                Toast.makeText(InicioActivity.this, "Selecione uma Escola", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void voltarTelaIncial(){
        BancoDados.USER_ID = null;
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
        Toast.makeText(InicioActivity.this, "Usuário desconectado", Toast.LENGTH_SHORT).show();
    }
    public void mudarParaTelaCadEscola(){
        Intent intent = new Intent(this, CadEscolaActivity.class);
        startActivity(intent);
    }
    public void mudarParaTelaVisulEscola(){
        Intent intent = new Intent(this, VisualEscolaActivity.class);
        startActivity(intent);
    }

    public void dialogHelpDetalhes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("Olá, nesta tela são sugeridas duas opções importantes para funcionalidade do KARITI. Para acesso completo ao app, é necessário cadastrar a(s) escola(s) em que atua. Após essa etapa, basta selecionar a opção 'Selecionar Escola' e clicar na desejada para acessar as demais funcionalidades da aplicação! ");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}