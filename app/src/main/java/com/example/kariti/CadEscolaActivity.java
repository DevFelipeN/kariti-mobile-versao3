package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CadEscolaActivity extends AppCompatActivity {

    EditText nScool;
    EditText bairro;
    Button cadScool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_escola);



        nScool = (EditText) findViewById(R.id.editTextNameScool);
        bairro = (EditText) findViewById(R.id.editTextBairro);
        cadScool = (Button) findViewById(R.id.buttonCad);
        cadScool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CadEscolaActivity.this, "Teste " + nScool.getText(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}