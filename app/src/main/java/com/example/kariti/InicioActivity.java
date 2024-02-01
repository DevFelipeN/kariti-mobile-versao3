package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class InicioActivity extends AppCompatActivity {
    ImageButton imageButtonInicio;
    Button cadastrarEscola, visualizarEscola;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        imageButtonInicio = (ImageButton) findViewById(R.id.imageButtonInicio);
        cadastrarEscola = (Button) findViewById(R.id.buttonCadEscola);
        visualizarEscola = (Button) findViewById(R.id.buttonVisualizarEscola);


        cadastrarEscola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { mudarParaTelaCadEscola();}
        });
    }

    public void mudarParaTelaCadEscola(){
        Intent intent = new Intent(this, CadEscolaActivity.class);
        startActivity(intent);
    }
}