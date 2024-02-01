package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CadEscolaActivity extends AppCompatActivity {

    EditText nomeEscola, bairro;
    Button cadastrarEscola;
    BancoDados bancoDados;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_escola);

        nomeEscola = findViewById(R.id.editTextNomeEscola);
        bairro = findViewById(R.id.editTextBairro);
        cadastrarEscola = findViewById(R.id.buttonCad);

        cadastrarEscola.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });
    }
}