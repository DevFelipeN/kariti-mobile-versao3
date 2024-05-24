package online.padev.kariti;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import online.padev.kariti.R;

public class ilustracionVoidSchoolctivity extends AppCompatActivity {
    ImageButton voltarVoid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ilustracion_void_schoolctivity);
        voltarVoid = findViewById(R.id.voltarVoidScholl);
        voltarVoid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}