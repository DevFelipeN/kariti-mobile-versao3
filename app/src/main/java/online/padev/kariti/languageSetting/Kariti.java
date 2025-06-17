package online.padev.kariti.languageSetting;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class Kariti extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(ActivityLocale.wrap(base));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("idioma", "Application iniciado!");
        ActivityLocale.setLocale(this); // Garante idioma correto ao inicializar o app
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    /*
    @Override
    protected void attachBaseContext(Context base) {
        Configuration c = base.getResources().getConfiguration();
        Locale l = new Locale("en");
        Locale.setDefault(l);
        c.setLocale(l);
        //c.setLocale(Locale.ROOT);
        Log.e("idioma","PASSEI AQUIIIII");
        super.attachBaseContext(base.createConfigurationContext(c));
    }

     */
}
