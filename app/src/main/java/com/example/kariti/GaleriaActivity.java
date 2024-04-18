package com.example.kariti;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    String diretorio, nomeCartao;
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

        nomeCartao = getIntent().getExtras().getString("nomeImagem") ;
        Compactador.listCartoes.add(nomeCartao);

        btnAdcionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GaleriaActivity.this, ProvaCorrigirActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean teste = Compactador.compactador();
                if(teste)
                    Toast.makeText(GaleriaActivity.this, " Arquivo compactado (^_^) ", Toast.LENGTH_SHORT).show();
            }
        });
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Compactador.listCartoes.clear();
                onBackPressed();
            }
        });/*
        Recebendo o byte array e nome da foto TELA ANTERIOR
        byte[] byteArray = getIntent().getByteArrayExtra("photo");
        String nomeFotoAnterior = getIntent().getStringExtra("nomeFotoAnterior");

        recyclerView = findViewById(R.id.recyclerViewFotos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        nomePhoto.add("nomeFotoAnterior");
        dataImg.add("2024-03-2024 20:01");
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
         */
    }
    public void onBackPressed() {
        Compactador.listCartoes.clear();
        super.onBackPressed();
    }
}
