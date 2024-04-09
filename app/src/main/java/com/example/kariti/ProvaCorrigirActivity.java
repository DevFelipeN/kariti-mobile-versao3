package com.example.kariti;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class ProvaCorrigirActivity extends AppCompatActivity {
    Button btnGaleria, btnCamera;
    ImageButton voltar;
    ImageView teste;
    TextView txt;
    String nomeDaFoto;
    BancoDados bancoDados;
    Integer qtdQuestoes, qtdAlternativas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_corrigir);

        btnGaleria = findViewById(R.id.buttonGaleria);
        btnCamera = findViewById(R.id.buttonCamera);
        voltar = findViewById(R.id.imgBtnVoltarDcorrecao);
        teste = findViewById(R.id.imageView20);
        txt = findViewById(R.id.textView7);

        bancoDados = new BancoDados(this);


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
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 2);
                iniciarScannerQRCode(); // primeiro o qrcode
            }
        });
    }
    private void iniciarScannerQRCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(false);       // rotação do scanner
        intentIntegrator.setPrompt("Escaneie o QR code");
        intentIntegrator.setBeepEnabled(true);              // som ao scanear
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE); //especifica o qrcode
        intentIntegrator.initiateScan();                    //iniciar o scan
    }

    private void tirarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2); // Iniciar a captura da foto
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
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

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
                    /*
                    teste.setImageBitmap(photo); //Mostra a imagem na Activity
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    Intent intent = new Intent(this, GaleriaActivity.class);
                    intent.putExtra("photo", byteArray);
                    intent.putExtra("nomeFotoAnterior", nomeDaFoto);
                    salvarImagemNaGaleria(photo); // Salvar imagem na galeria
                    startActivity(intent);
                    finish();
                     */
                }
            }
        } if (result != null && result.getContents() != null) {
            String qrCodeConteudo = result.getContents(); // Conteúdo do QR Code

            qrCodeConteudo = qrCodeConteudo.replaceAll("#", "");
            String[] partes = qrCodeConteudo.split("\\."); // partes do valor do QRCODE
            Boolean existProva = bancoDados.checkprovaId(partes[0]);
            if(!existProva.equals(false)) {
                qtdQuestoes = bancoDados.pegaqtdQuestoes(partes[0]);
                qtdAlternativas = bancoDados.pegaqtdAlternativas(partes[1]);
            }else{
                qtdQuestoes = 0;
                qtdAlternativas = 0;
            }
            nomeDaFoto = partes[0] + "_" + partes[1] + "_" + qtdQuestoes + "_" + qtdAlternativas + ".png";

            // junção das partes


            txt.setText(nomeDaFoto);
            tirarFoto();
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