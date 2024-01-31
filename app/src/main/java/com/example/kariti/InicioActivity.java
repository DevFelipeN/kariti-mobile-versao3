package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class InicioActivity extends AppCompatActivity {
    ImageButton imageButtonInicio;
    Button cadEscola, visuEscool;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        imageButtonInicio = (ImageButton) findViewById(R.id.imageButtonInicio);
        cadEscola = (Button) findViewById(R.id.buttonCadSchooli);

        cadEscola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intencion = new Intent(getApplicationContext(), CadEscolaActivity.class);
               startActivity(intencion);
               Toast.makeText(InicioActivity.this, "Casdastro Escola", Toast.LENGTH_SHORT).show();
            }
        });
    }

}