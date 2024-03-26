package com.example.kariti;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class ProvaCorrigirActivity extends AppCompatActivity {
    Button btnGaleria, btnCamera;
    ImageButton voltar;
    ImageView teste;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_corrigir);

        btnGaleria = findViewById(R.id.buttonGaleria);
        btnCamera = findViewById(R.id.buttonCamera);
        voltar = findViewById(R.id.imgBtnVoltaEscola);
        teste = findViewById(R.id.imageView20);

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Selecione as imagens"), 1);

            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                // Se a imagem foi selecionada da galeria
                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        teste.setImageURI(selectedImageUri);
                        Toast.makeText(ProvaCorrigirActivity.this, "Imagem da galeria enviada!", Toast.LENGTH_LONG).show();
                    }
                }
            } else if (requestCode == 2) {
                // Se a imagem foi capturada pela câmera
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    teste.setImageBitmap(photo);
//                    Toast.makeText(ProvaCorrigirActivity.this, "Imagem da câmera enviada!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(this, GaleriaActivity.class);
                    intent.putExtra("photo", byteArray);
                    startActivity(intent);
                    finish();
                }
            }
        }
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