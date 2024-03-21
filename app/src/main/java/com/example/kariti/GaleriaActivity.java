package com.example.kariti;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class GaleriaActivity extends AppCompatActivity {
    AppCompatButton btnFinalizar;
    ImageButton btnVoltar;
    FloatingActionButton btnAdcionarFoto;
    RecyclerView recyclerView;
    ArrayList<String> nomePhoto = new ArrayList<>();
    ArrayList<String> dataImg = new ArrayList<>();
    ArrayList<byte[]> photoTelaAnterior = new ArrayList<>();
    RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_galeria);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnVoltar = findViewById(R.id.imgBtnVoltar);
        btnFinalizar = findViewById(R.id.buttonFinalizar);
        btnAdcionarFoto = findViewById(R.id.buttonAdicionarFoto);

        // Recebendo o byte array da foto
        byte[] byteArray = getIntent().getByteArrayExtra("photo");

        recyclerView = findViewById(R.id.recyclerViewFotos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nomePhoto.add("101_80_15_5.png");
        dataImg.add("2024-03-2024 20:01");
        photoTelaAnterior.add(byteArray);

        nomePhoto.add("101_80_15_5.png");
        dataImg.add("2024-03-2024 21:02");
        photoTelaAnterior.add(byteArray);

        adapter = new AdapterGaleria(this, nomePhoto, dataImg, photoTelaAnterior);
        recyclerView.setAdapter(adapter);

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GaleriaActivity.this, "Imagem da câmera enviada!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        btnAdcionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
            }
        });
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                // Se a imagem foi capturada pela câmera
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArrayTirada = stream.toByteArray();

                    nomePhoto.add("Foto Tirada");
                    dataImg.add("2024-03-2024 22:01");
                    photoTelaAnterior.add(byteArrayTirada);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
