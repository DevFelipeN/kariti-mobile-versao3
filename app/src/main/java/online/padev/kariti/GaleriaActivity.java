package online.padev.kariti;

import static online.padev.kariti.Compactador.listCartoes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import online.padev.kariti.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class GaleriaActivity extends AppCompatActivity {
    AppCompatButton btnFinalizar;
    ImageButton btnVoltar;
    FloatingActionButton btnAdcionarFoto;
    RecyclerView recyclerView;
    ArrayList<String> nomePhoto = new ArrayList<>();
    ArrayList<String> caminhoDaImagem = new ArrayList<>();
    ArrayList<String> dataImg = new ArrayList<>();
    RecyclerView.Adapter adapter;
    String nomeCartao;
    private TextView titulo;
    BancoDados bancoDados;
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
        titulo = findViewById(R.id.toolbar_title);
        btnFinalizar = findViewById(R.id.buttonFinalizar);
        btnAdcionarFoto = findViewById(R.id.buttonAdicionarFoto);
        bancoDados = new BancoDados(this);

        titulo.setText("Correção");

        nomeCartao = Objects.requireNonNull(getIntent().getExtras()).getString("nomeImagem") ;
        if(!Compactador.listCartoes.contains(nomeCartao)) {
            Compactador.listCartoes.add(nomeCartao);
        }

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
                boolean compact = Compactador.compactador();
                if(compact) {
                    listCartoes.clear();
                    try {
                        File fileZip = new File("/storage/emulated/0/Android/media/online.padev.kariti/CameraXApp/saida.zip");
                        File fileJson  = new File(getExternalFilesDir(null), "/json.json");
                        UploadEjson.enviarArquivosP(fileZip, new FileOutputStream(fileJson), getExternalFilesDir(null), bancoDados);
                        telaProva();
                    } catch (Exception e) {
                        Log.e("Kariti", e.toString());
                        //Toast.makeText(GaleriaActivity.this, "Erro: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }else Toast.makeText(GaleriaActivity.this, "Erro de Compactação", Toast.LENGTH_SHORT).show();
            }
        });
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Compactador.listCartoes.isEmpty()){
                    onBackPressed();
                    finish();
                }else{
                    avidoDeCancelamento();
                }
            }
        });
        recyclerView = findViewById(R.id.recyclerViewFotos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        carregarFotos();
        adapter = new AdapterGaleria(this, nomePhoto, dataImg, caminhoDaImagem);
        recyclerView.setAdapter(adapter);
    }

    private void carregarFotos() {
        // Caminho da pasta onde as fotos estão armazenadas
        String caminhoDaPasta = Environment.getExternalStorageDirectory() + "/Android/media/online.padev.kariti/CameraXApp/";
        File diretorio = new File(caminhoDaPasta);
        if (diretorio.exists() && diretorio.isDirectory()) {
            File[] arquivos = diretorio.listFiles();
            if (arquivos != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                for (File arquivo : arquivos) {
                    if (arquivo.isFile() && arquivo.getName().endsWith(".jpg") && listCartoes.contains(arquivo.getName())) {
                        caminhoDaImagem.add(arquivo.getAbsolutePath());
                        nomePhoto.add(arquivo.getName());
                        dataImg.add(sdf.format(arquivo.lastModified()));
                    }
                }
                // Notificar o adaptador que os dados mudaram
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
    public void telaProva(){
        Intent intent = new Intent(getApplicationContext(), AnimacaoCorrecao.class);
        startActivity(intent);
        finish();
    }
    public void onBackPressed() {
        if(Compactador.listCartoes.isEmpty()){
            super.onBackPressed();
            finish();
        }else{
            avidoDeCancelamento();
        };
    }
    public static String leitor(String path) throws IOException {
        BufferedReader buffRead = new BufferedReader(new FileReader(path));
        String linha = "", texto = "";
        while (true) {
            if (linha == null) {
                break;
            }
            texto += linha;
            linha = buffRead.readLine();
        }
        buffRead.close();
        return texto;
    }
    public void avidoDeCancelamento(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO!")
                .setMessage("Caso confirme essa ação, o processo de correção em andamento, será cancelado!\n\n" +
                        "Deseja realmente voltar")
                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Compactador.listCartoes.clear();
                        onBackPressed();
                        finish();
                    }
                })
                .setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Código para lidar com o clique no botão Cancelar, se necessário
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
