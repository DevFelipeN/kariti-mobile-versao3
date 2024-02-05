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
            //Local modificado
            public void onClick(View view) {
                Intent intencion = new Intent(getApplicationContext(), VisualEscolaActivity.class);
                startActivity(intencion);
                Toast.makeText(InicioActivity.this, "Selecione uma Escola", Toast.LENGTH_SHORT).show();
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