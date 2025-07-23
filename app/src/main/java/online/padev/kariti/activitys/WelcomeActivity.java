package online.padev.kariti.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.PopupMenu;
import org.opencv.android.OpenCVLoader;

import java.util.Locale;

import online.padev.kariti.R;
import online.padev.kariti.settings.ActivityLocale;
import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.tests.InsertBD;

public class WelcomeActivity extends AppCompatActivity {
    AppCompatImageView languageIcon;
    TextView textViewLanguage;

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
        textViewLanguage = findViewById(R.id.textViewLanguage);
        languageIcon = findViewById(R.id.imageViewLanguage);

        SharedPreferences prefs_initial = getSharedPreferences("settings", MODE_PRIVATE);
        String currentLang_initial = prefs_initial.getString("language", Locale.getDefault().getLanguage());

        if (currentLang_initial.equals("pt")) {
            textViewLanguage.setText(String.format("%s", getString(R.string.language_portuguese)));
        } else if (currentLang_initial.equals("en")) {
            textViewLanguage.setText(String.format("%s", getString(R.string.language_English)));
        }



        if (OpenCVLoader.initDebug()) {
            Log.e("opencv","Sucesso na inicialização do openCV");
        }else{
            Log.e("opencv","Erro ao tentar executar openCV");
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
                    textViewLanguage.setText(String.format("%s", getString(R.string.language_portuguese)));
                    return true;
                } else if (id == R.id.lang_en) {
                    changeLanguage("en");
                    textViewLanguage.setText(String.format("%s", getString(R.string.language_English)));
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
        Intent intent = new Intent(this, ExamFastDefaultActivity.class);
        startActivity(intent);
    }

    public void changeLanguage(String languageCode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString("app_language", languageCode).apply();

        ActivityLocale.setLocale(this, languageCode);
        recreate();  // reinicia a Activity para aplicar o idioma
    }

}
