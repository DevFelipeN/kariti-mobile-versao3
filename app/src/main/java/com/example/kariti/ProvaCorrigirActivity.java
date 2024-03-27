package com.example.kariti;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

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
        voltar = findViewById(R.id.imgBtnVoltarDcorrecao);
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

    // Método para salvar a imagem no armazenamento externo e atualizar a galeria
    private void salvarImagemNaGaleria(Bitmap bitmap) {
        String fileName = System.currentTimeMillis() + ".png";
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            try {
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                if (fos != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    Toast.makeText(this, "Imagem salva na galeria!", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString());
            if (!directory.exists())
                directory.mkdirs();

            File file = new File(directory, fileName);
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                Toast.makeText(this, "Imagem salva na galeria!", Toast.LENGTH_SHORT).show();

                // Atualizar a galeria
                MediaScannerConnection.scanFile(this, new String[]{file.getPath()}, new String[]{"image/png"}, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                    teste.setImageBitmap(photo);
                    salvarImagemNaGaleria(photo); // Salvar imagem na galeria
                }
            }
        }
    }

    public void PopMenu(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProvaCorrigirActivity.this, "Preparado para implementação", Toast.LENGTH_SHORT).show();
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

        Toast.makeText(this, "Provas enviadas para Correção!!", Toast.LENGTH_SHORT).show();
    }
}