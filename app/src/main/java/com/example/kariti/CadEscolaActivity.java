package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CadEscolaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_escola);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);
    }
}