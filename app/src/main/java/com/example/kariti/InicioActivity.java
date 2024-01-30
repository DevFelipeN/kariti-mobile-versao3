package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class InicioActivity extends AppCompatActivity {
    ImageButton imageButtonInicio;
    Button cadEscola;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        imageButtonInicio = findViewById(R.id.imageButtonInicio);
        cadEscola = findViewById(R.id.buttonCadSchooli);

        imageButtonInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voltarTelaIncial();
            }
        });
        cadEscola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaDeCadastroEscola();
            }
        });
    }
    public void voltarTelaIncial(){
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }
    public void telaDeCadastroEscola(){
        Intent intent = new Intent(this, CadEscolaActivity.class);
        startActivity(intent);
    }
}