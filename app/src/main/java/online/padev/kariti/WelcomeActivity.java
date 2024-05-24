package online.padev.kariti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import online.padev.kariti.R;

public class WelcomeActivity extends AppCompatActivity {
    BancoDados bancoDados;
    AppCompatButton botaoCadastro, botaoLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

        requestPermissions(permissions, 101);

        botaoCadastro = findViewById(R.id.buttonCadastroW);
        botaoLogin = findViewById(R.id.buttonLoginW);
        bancoDados = new BancoDados(this);

        if(bancoDados.checkemail("karitimobile@gmail.com") == null) {
            bancoDados.insertData("Master user", "user1", "karitimobile@gmail.com");
        }

        botaoCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaWelcome();
            }
        });
        botaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaLogin();
            }
        });
    }

    public void mudarParaTelaWelcome(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void mudarParaTelaLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
