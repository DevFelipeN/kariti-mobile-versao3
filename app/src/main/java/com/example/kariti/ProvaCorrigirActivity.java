package com.example.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class ProvaCorrigirActivity extends AppCompatActivity {
    Button btnGaleria, btnCamera;
    ImageButton voltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_corrigir);

        btnGaleria = findViewById(R.id.buttonGaleria);
        btnCamera = findViewById(R.id.buttonCamera);
        voltar = findViewById(R.id.imgBtnVoltar);

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaProva();
            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaProva();
            }
        });
    }

    public void telaProva(){

        AlertDialog.Builder builder = new AlertDialog.Builder(ProvaCorrigirActivity.this);
        builder.setTitle("Provas enviadas para correção!")
                .setMessage("Para acompanhar o andamento da correção, selecione a opção 'Visualizar Prova'.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), ProvaActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


//        String mensagem = "Provas enviadas com Sucesso! Para acompanhar o andamento da correção, vá em VERIFICAR PROVA.";
//        Intent intent = new Intent(this, ProvaActivity.class);
//        intent.putExtra("mensagem", mensagem);
//        startActivity(intent);
//        finish();
        Toast.makeText(this, "Provas enviadas para Correção!!", Toast.LENGTH_SHORT).show();
        // Volte para a tela da Prova
//        Intent intent = new Intent(this, ProvaActivity.class);
//        startActivity(intent);
//        finish();
    }
}