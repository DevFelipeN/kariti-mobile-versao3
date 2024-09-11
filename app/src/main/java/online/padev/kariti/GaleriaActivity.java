package online.padev.kariti;

import static online.padev.kariti.Compactador.listCartoes;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class GaleriaActivity extends AppCompatActivity {
    AppCompatButton btnFinalizar;
    ImageButton btnVoltar;
    FloatingActionButton btnAdcionarFoto;
    RecyclerView recyclerView;
    ArrayList<String> nomePhoto = new ArrayList<>();
    ArrayList<String> caminhoDaImagem = new ArrayList<>();
    ArrayList<String> dataImg = new ArrayList<>();
    RecyclerView.Adapter adapter;
    String nomeCartaoScaneado, nomeCartaoCaturado;
    Integer qtdQuestoes, qtdAlternativas;
    TextView titulo;
    BancoDados bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        bancoDados = new BancoDados(this);
        Scanner scanner = new Scanner(this);

        if(Compactador.listCartoes.isEmpty()){ //Se a lista de cartões tiver vazia, então inicia pelo QR Code
            scanner.iniciarScanner();//Inicia o Scanner
        }

        btnVoltar = findViewById(R.id.imgBtnVoltar);
        titulo = findViewById(R.id.toolbar_title);
        btnFinalizar = findViewById(R.id.buttonFinalizar);
        btnAdcionarFoto = findViewById(R.id.buttonAdicionarFoto);


        if(!listCartoes.isEmpty()){//Caso a lista de cartões não esteja vazia
            nomeCartaoCaturado = getIntent().getExtras().getString("nomeImagem");//receba o nome do cartão enviado por intent de CameraX
            if(!Compactador.listCartoes.contains(nomeCartaoCaturado)){//Caso esse cartão ainda não esteja na lista de cartoes.
                Compactador.listCartoes.add(nomeCartaoCaturado);//Inserir nome do novo cartão na lista
            }
        }

        titulo.setText(String.format("%s","Correção"));
        
        btnAdcionarFoto.setOnClickListener(view -> {
            scanner.iniciarScanner();//Inicia o Scanner
        });
        btnFinalizar.setOnClickListener(v -> {
            boolean compact = Compactador.compactador();
            if(compact) {
                listCartoes.clear();
                try {
                    File fileZip = new File("/storage/emulated/0/Android/media/online.padev.kariti/CameraXApp/saida.zip");
                    File fileJson  = new File(getExternalFilesDir(null), "/json.json");
                    UploadEjson.enviarArquivosP(fileZip, new FileOutputStream(fileJson), getExternalFilesDir(null), bancoDados);
                    iniciaAnimacaoCorrecao();
                } catch (Exception e) {
                    Log.e("Kariti", e.toString());
                }
            }else Toast.makeText(GaleriaActivity.this, "Erro de Compactação", Toast.LENGTH_SHORT).show();
        });

        recyclerView = findViewById(R.id.recyclerViewFotos);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        carregarFotos();
        adapter = new AdapterGaleria(this, nomePhoto, dataImg, caminhoDaImagem);
        recyclerView.setAdapter(adapter);

        btnVoltar.setOnClickListener(view -> {
            if(listCartoes.isEmpty()){
                getOnBackPressedDispatcher();
                finish();
            }else{
                avidoDeCancelamento();
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(listCartoes.isEmpty()){
                    finish();
                }else{
                    avidoDeCancelamento();
                };
            }
        });
    }

    private void carregarFotos() {
        File diretorio = new File("/storage/emulated/0/Android/media/online.padev.kariti/CameraXApp/");
        if (diretorio.exists() && diretorio.isDirectory()){
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
    /**
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {  // Se o conteúdo do QR Code não for nulo
                String qrCodeConteudo = result.getContents(); // Conteúdo do QR Code
                qrCodeConteudo = qrCodeConteudo.replaceAll("#", "");
                String[] partes = qrCodeConteudo.split("\\."); // partes do valor do QRCODE
                String id_prova = partes[0];
                if(bancoDados.checkprovaId(id_prova)) {
                    qtdQuestoes = bancoDados.pegaqtdQuestoes(id_prova);
                    qtdAlternativas = bancoDados.pegaqtdAlternativas(id_prova);
                }else{
                    Toast.makeText(this, "Prova não cadastrada!", Toast.LENGTH_SHORT).show();
                    return;
                }
                nomeCartaoScaneado = partes[0] + "_" + partes[1] + "_" + qtdQuestoes + "_" + qtdAlternativas + ".jpg";
                Intent intent = new Intent(this, CameraxActivity.class);
                intent.putExtra("nomeImagem", nomeCartaoScaneado);
                startActivity(intent);
                finish();
            } else {
                // Caso o usuário tenha cancelado o scanner ou não tenha lido um QR Code
                if(listCartoes.isEmpty()){
                    finish();
                }
                Toast.makeText(this, "Leitura do QR Code cancelada.", Toast.LENGTH_SHORT).show();
            }
        }else {
            // Tratamento adicional para resultados que não sejam do IntentIntegrator
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void iniciaAnimacaoCorrecao(){
        Intent intent = new Intent(getApplicationContext(), AnimacaoCorrecao.class);
        startActivity(intent);
        finish();
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
                .setPositiveButton("SIM", (dialog, which) -> {
                    Compactador.listCartoes.clear();
                    getOnBackPressedDispatcher();
                    finish();
                })
                .setNegativeButton("NÃO", (dialog, which) -> {
                    // Código para lidar com o clique no botão Cancelar, se necessário
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
