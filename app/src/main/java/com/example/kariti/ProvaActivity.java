package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ProvaActivity extends AppCompatActivity {
    ImageButton voltar;
    Button cadProva;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova);

        voltar = findViewById(R.id.imgBtnVoltar);
        cadProva = findViewById(R.id.buttonCadProva);
        cadProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaCadastroProva();
            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void telaCadastroProva(){
        Intent intent = new Intent(this, CadProvaActivity.class);
        startActivity(intent);
    }
}