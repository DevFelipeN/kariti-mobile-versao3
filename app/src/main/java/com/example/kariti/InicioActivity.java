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

    Button cadastrarEscola, visualizarEscola;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        imageButtonInicio = findViewById(R.id.imageButtonInicio);
        cadastrarEscola = findViewById(R.id.buttonCadEscola);
        visualizarEscola = findViewById(R.id.buttonVisualizarEscola);

        imageButtonInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voltarTelaIncial();
            }
        });
        cadastrarEscola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { mudarParaTelaCadEscola();}
        });
        visualizarEscola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(InicioActivity.this, "Bem Vindo ao Kariti", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), CadEscolaActivity.class);
                //Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                Intent intencion = new Intent(getApplicationContext(), CadEscolaActivity.class);
                startActivity(intencion);
                Toast.makeText(InicioActivity.this, "Casdastro Escola", Toast.LENGTH_SHORT).show();
                mudarParaTelaVisulEscola();
            }
        });
    }
    public void voltarTelaIncial(){
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }
    public void mudarParaTelaCadEscola(){
        Intent intent = new Intent(this, CadEscolaActivity.class);
        startActivity(intent);
    }
    public void mudarParaTelaVisulEscola(){
        Intent intent = new Intent(this, VisualEscolaActivity.class);
        startActivity(intent);
    }
}