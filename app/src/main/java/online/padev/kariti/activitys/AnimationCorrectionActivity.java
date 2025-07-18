package online.padev.kariti.activitys;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import online.padev.kariti.R;
import online.padev.kariti.settings.ActivityLocale;

public class AnimationCorrectionActivity extends AppCompatActivity {
    TextView titleActivity;
    public static AnimationCorrectionActivity instanceClosed;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ActivityLocale.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_correction);

        instanceClosed = this;

        ImageButton btnBack = findViewById(R.id.imgBtnVoltar);
        titleActivity = findViewById(R.id.toolbar_title);

        titleActivity.setText(getString(R.string.titleGrading));
        btnBack.setOnClickListener(v -> {
            getOnBackPressedDispatcher();
            finish();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
    protected void onDestroy() {
        super.onDestroy();
        instanceClosed = null;
    }

    public static void close() {
        if (instanceClosed != null) {
            instanceClosed.finish();
        }
    }
}