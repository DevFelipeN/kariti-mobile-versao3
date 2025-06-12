package online.padev.kariti;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import org.opencv.android.OpenCVLoader;

import online.padev.kariti.database.DataBaseKariti;

public class WelcomeActivity extends AppCompatActivity {
    DataBaseKariti dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        AppCompatButton btnRegistration = findViewById(R.id.buttonCadastroW);
        AppCompatButton btnLogin = findViewById(R.id.buttonLoginW);
        AppCompatButton btnDefaultPassword = findViewById(R.id.buttonDefaultPassword);

        if (OpenCVLoader.initDebug()) {
            Log.e("opencv","Sucesso na inicialização do openCV");
        }else{
            Log.e("opencv","Erro ao tentar executar openCV");
        }

        dataBase = new DataBaseKariti(this);

        if(dataBase.checkUserEmail("karitimobile@gmail.com") == null) {
            dataBase.insertUser("Master user", "user1", "karitimobile@gmail.com");
        }
        btnRegistration.setOnClickListener(v -> startRegistrationUser());
        btnLogin.setOnClickListener(v -> startLogin());
        btnDefaultPassword.setOnClickListener(v -> startProvaDefault());

        changeLanguage("pt");
    }

    /**
     *Este método carrega a tela de cadastro de usuário
     */
    private void startRegistrationUser(){
        Intent intent = new Intent(this, UserRegistrationActivity.class);
        startActivity(intent);
    }

    /**
     * Este método carrega a tela de Login..
     */
    private void startLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    /**
     * Este método carrega a opção de prova rápida
     */
    private void startProvaDefault(){
        Intent intent = new Intent(this, ProvaFastDefaultActivity.class);
        startActivity(intent);
    }

    public void changeLanguage(String languageCode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("app_language", languageCode).apply();

        ActivityLocale.setLocale(this, languageCode);
        //recreate();  // reinicia a Activity para aplicar o idioma
    }
}
