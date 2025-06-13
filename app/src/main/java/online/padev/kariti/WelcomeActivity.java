package online.padev.kariti;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import org.opencv.android.OpenCVLoader;

import java.util.Locale;

import online.padev.kariti.database.DataBaseKariti;

public class WelcomeActivity extends AppCompatActivity {
    DataBaseKariti dataBase;
    AppCompatImageView languageIcon;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ActivityLocale.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        AppCompatButton btnRegistration = findViewById(R.id.buttonCadastroW);
        AppCompatButton btnLogin = findViewById(R.id.buttonLoginW);
        AppCompatButton btnDefaultPassword = findViewById(R.id.buttonDefaultPassword);
        languageIcon = findViewById(R.id.imageViewLanguage);

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

        languageIcon.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenuInflater().inflate(R.menu.activity_menu_language, popup.getMenu());
            Menu menu = popup.getMenu();
            menu.setGroupCheckable(0, true, true);

            SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
            String currentLang = prefs.getString("language", Locale.getDefault().getLanguage()); // padrão pt

            if (currentLang.equals("pt")) {
                menu.findItem(R.id.lang_pt).setChecked(true);
            } else if (currentLang.equals("en")) {
                menu.findItem(R.id.lang_en).setChecked(true);
            }

            popup.setOnMenuItemClickListener(item -> {
                item.setChecked(true);
                int id = item.getItemId();
                if (id == R.id.lang_pt) {
                    changeLanguage("pt");
                    return true;
                } else if (id == R.id.lang_en) {
                    changeLanguage("en");
                    return true;
                }
                return false;
            });
            popup.show();
        });
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
        recreate();  // reinicia a Activity para aplicar o idioma
    }

}
