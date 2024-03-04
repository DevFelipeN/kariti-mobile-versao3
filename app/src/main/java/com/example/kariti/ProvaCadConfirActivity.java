package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class ProvaCadConfirActivity extends AppCompatActivity {
    ImageButton voltar;
    Button gerarCartao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_cad_confir);

        voltar = findViewById(R.id.imgBtnVoltar);
        gerarCartao = findViewById(R.id.buttonGerarCatao);
        Toast.makeText(this, "Estou aqui!", Toast.LENGTH_SHORT).show();

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaProva();
            }
        });
    }


    public void telaProva() {
        Intent intent = new Intent(this, CadProvaActivity.class);
        startActivity(intent);
        finish();
    }
}