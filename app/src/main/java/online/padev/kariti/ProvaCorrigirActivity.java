package online.padev.kariti;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
public class ProvaCorrigirActivity extends AppCompatActivity {
    Button btnCamera;
    ImageButton voltar;
    String nomeDaFoto;
    BancoDados bancoDados;
    Integer qtdQuestoes, qtdAlternativas;
    TextView titulo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_corrigir);

        btnCamera = findViewById(R.id.buttonCamera);
        voltar = findViewById(R.id.imgBtnVoltar);
        titulo = findViewById(R.id.toolbar_title);

        bancoDados = new BancoDados(this);

        titulo.setText("Correção");
        iniciarScannerQRCode();

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarScannerQRCode(); // primeiro o qrcode
            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
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
    }
    //metodo que exibe o scanner na tela
    private void iniciarScannerQRCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setOrientationLocked(false);// rotação do scanner
        intentIntegrator.setPrompt("Escaneie o QR CODE da Prova");
        intentIntegrator.setBeepEnabled(true);              // som ao scanear
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE); //especifica o qrcode
        intentIntegrator.initiateScan();                     //iniciar o scan
    }
    //metodo que recupera o dados capturados no Qrcode
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String qrCodeConteudo = result.getContents(); // Conteúdo do QR Code
            qrCodeConteudo = qrCodeConteudo.replaceAll("#", "");
            String[] partes = qrCodeConteudo.split("\\."); // partes do valor do QRCODE
            String id_prova = partes[0];
            Boolean existProva = bancoDados.checkprovaId(id_prova);
            if(!existProva.equals(false)) {
                qtdQuestoes = bancoDados.pegaqtdQuestoes(id_prova);
                qtdAlternativas = bancoDados.pegaqtdAlternativas(id_prova);
            }else{
                Toast.makeText(this, "Prova não cadastrada!", Toast.LENGTH_SHORT).show();
                return;
            }
            nomeDaFoto = partes[0] + "_" + partes[1] + "_" + qtdQuestoes + "_" + qtdAlternativas + ".jpg";
            Intent intent = new Intent(ProvaCorrigirActivity.this, CameraxActivity.class);
            intent.putExtra("nomeImagem", nomeDaFoto);
            startActivity(intent);
            finish();
        }else {
            onRestart();
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
    public void onBackPressed() {
        if(Compactador.listCartoes.isEmpty()){
            super.onBackPressed();
            finish();
        }else{
            avidoDeCancelamento();
        }
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