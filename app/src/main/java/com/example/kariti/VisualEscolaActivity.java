package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class VisualEscolaActivity extends AppCompatActivity {
    ImageButton btnVoltar, btnHome;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_escola);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnVoltar = findViewById(R.id.btn_voltar_left);
        btnVoltar.setVisibility(View.VISIBLE);btnHome = findViewById(R.id.home_icon);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voltarTelaIncial();
            }
        });

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    public void voltarTelaIncial(){
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }
}