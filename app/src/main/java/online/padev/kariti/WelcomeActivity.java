package online.padev.kariti;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class WelcomeActivity extends AppCompatActivity {
    BancoDados bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions, 101);

        AppCompatButton botaoCadastro = findViewById(R.id.buttonCadastroW);
        AppCompatButton botaoLogin = findViewById(R.id.buttonLoginW);
        bancoDados = new BancoDados(this);

        if(bancoDados.verificaEmail("karitimobile@gmail.com") == null) {
            bancoDados.cadastrarUsuario("Master user", "user1", "karitimobile@gmail.com");
        }
        botaoCadastro.setOnClickListener(v -> {
            mudarParaTelaCadastro();
        });
        botaoLogin.setOnClickListener(v -> {
            mudarParaTelaLogin();
        });
    }

    /**
     *Este método carrega a tela de cadastro de usuário
     */
    private void mudarParaTelaCadastro(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Este método carrega a tela de Login
     */
    private void mudarParaTelaLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
