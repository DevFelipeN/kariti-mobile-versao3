package online.padev.kariti.settings;

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
        ActivityLocale.wrap(this); // Garante idioma correto ao inicializar o app
    }
}
