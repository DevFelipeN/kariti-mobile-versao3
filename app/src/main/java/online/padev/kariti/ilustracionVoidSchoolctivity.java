package online.padev.kariti;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class ilustracionVoidSchoolctivity extends AppCompatActivity {
    ImageButton voltarVoid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilustracion_void_schoolctivity);

        voltarVoid = findViewById(R.id.voltarVoidScholl);
        voltarVoid.setOnClickListener(view -> {
            getOnBackPressedDispatcher();
            finish();
        });
    }
}