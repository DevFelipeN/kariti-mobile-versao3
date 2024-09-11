package online.padev.kariti;

import static android.os.Environment.getExternalStoragePublicDirectory;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VisualProvaCorrigidaActivity extends AppCompatActivity {
    ImageButton voltar;
    Button btnBaixar;
    ArrayList<String> listAlunos, respostasDadas;
    ArrayList<Integer> listIdsAlunos;
    BancoDados bancoDados;
    Integer id_prova, qtdQuestoes;
    String prova, turma;
    TextView provaResult;
    List<String[]> dadosProvaCorrigida;
    TextView titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_prova_corrigida);

        voltar = findViewById(R.id.imgBtnVoltar);
        btnBaixar = findViewById(R.id.buttonBaixarResultado);
        provaResult = findViewById(R.id.textViewProvaResult);
        titulo = findViewById(R.id.toolbar_title);
        bancoDados = new BancoDados(this);
        listAlunos = new ArrayList<>();

        titulo.setText(String.format("%s","Resultado"));

        id_prova = getIntent().getExtras().getInt("id_prova");
        prova = getIntent().getExtras().getString("prova");
        turma = getIntent().getExtras().getString("turma");
        provaResult.setText(prova);

        qtdQuestoes = bancoDados.pegaqtdQuestoes(id_prova.toString());

        ShapeDrawable border = new ShapeDrawable(new RectShape());
        border.getPaint().setColor(0xFF000000); // Cor da borda
        border.getPaint().setStrokeWidth(1); // Largura da borda
        border.getPaint().setStyle(Paint.Style.STROKE);

        listIdsAlunos = (ArrayList<Integer>) bancoDados.listAlunoPorProvaCorrigida(id_prova); //pega todos os alunos com provas corrigidas
        for(int x : listIdsAlunos) { // interage sob esses alunos
            float nota = 0;
            int acertos = 0;
            Integer id_aluno = x;
            if(!bancoDados.checkSituacaoCorrecao(id_prova, id_aluno, -1)) {
                for (int i = 1; i <= qtdQuestoes; i++) {
                    Integer respostaDada = bancoDados.pegaRespostaDada(id_prova, id_aluno, i);
                    Integer respostaGabarito = bancoDados.pegaRespostaQuestao(id_prova, i);
                    if (respostaGabarito.equals(respostaDada)) {
                        nota += bancoDados.pegaNotaQuestao(id_prova, i);
                        acertos += 1;
                    }
                }
            }else{
                nota = -1;
                acertos = -1;
            }

            TableLayout tableLayout = findViewById(R.id.tableLayout);
            TableRow row = new TableRow(this);
            row.setBackground(border);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);

            // Cria uma célula para a nova linha para armazenar nome do aluno
            TextView cell1 = new TextView(this);
            String nomeAluno = editaNomeAluno(bancoDados.pegaAlunoProvaCorrigida(id_aluno));
            cell1.setText(String.format("  %s", nomeAluno));
            //cell1.setGravity(Gravity.CENTER);
            row.addView(cell1);

            // Cria outra célula para a nova linha para armazenar o total de acertos do aluno na prova
            TextView cell2 = new TextView(this);
            if(acertos != -1){
                cell2.setText(String.valueOf(acertos));
            }else{
                cell2.setText("-");}
            cell2.setGravity(Gravity.CENTER);
            cell2.setTextSize(16);
            row.addView(cell2);

            // Cria outra célula para a nova linha para armazenar a nota total do aluno
            TextView cell3 = new TextView(this);
            if(nota != -1){
                cell3.setText(String.valueOf(nota));
            }else{
                cell3.setText("-");
            }
            cell3.setGravity(Gravity.CENTER);
            cell3.setTextSize(16);
            row.addView(cell3);

            // Cria outra célula para a nova linha com botão para exibir detalhamento da nota do aluno
            Button cell4 = new Button(this);
            cell4.setId(id_aluno);
            cell4.setText(String.format("%s","Ver"));
            cell4.setGravity(Gravity.CENTER);
            cell4.setPadding(4,4,4,4);
            row.addView(cell4);

            // Adiciona a nova linha à tabela
            tableLayout.addView(row);

            cell4.setOnClickListener(v -> {
                respostasDadas = (ArrayList<String>) bancoDados.respostasDadas(id_prova, id_aluno);
                if(!bancoDados.checkSituacaoCorrecao(id_prova, id_aluno, -1) && qtdQuestoes.equals(respostasDadas.size())){
                    Intent intent = new Intent(getApplicationContext(), DetalheCorrecao.class);
                    intent.putExtra("id_aluno", v.getId());
                    intent.putExtra("id_prova", id_prova);
                    startActivity(intent);
                }else{
                    informeProvaNaoCorrigida();
                }

            });
        }
        //carregaGabarito();
        btnBaixar.setOnClickListener(v -> {
            if (!VerificaConexaoInternet.verificaConexao(VisualProvaCorrigidaActivity.this)) {
                Toast.makeText(VisualProvaCorrigidaActivity.this, "Sem conexão de rede!", Toast.LENGTH_SHORT).show();
                return;
            }
            dadosProvaCorrigida = new ArrayList<>();
            String prof = bancoDados.pegaUsuario(BancoDados.USER_ID.toString());
            String qtdAlternativas = String.valueOf(bancoDados.pegaqtdAlternativas(id_prova.toString()));
            String nota = String.valueOf(bancoDados.listNota(id_prova.toString()));
            String dataProva = bancoDados.pegaData(id_prova.toString());
            for(int id_aluno: listIdsAlunos) {
                if(!bancoDados.checkSituacaoCorrecao(id_prova, id_aluno, -1)) {
                    String nomeAluno = bancoDados.pegaAlunoProvaCorrigida(id_aluno);
                    String respostasDadas = bancoDados.listRespostasAluno(id_prova.toString(), String.valueOf(id_aluno));
                    //respostasDadas = respostasDadas.replaceAll("(?<=\\d)(?=\\d)", ",");
                    String respostasEsperadas = bancoDados.listRespostasGabarito(id_prova.toString());
                    respostasEsperadas = respostasEsperadas.replaceAll("(?<=\\d)(?=\\d)", ",");
                    String notasQuestoes = bancoDados.listNotaQuestao(id_prova.toString());
                    notasQuestoes = notasQuestoes.replaceAll("(?<=\\d)(?=\\d)", ",");
                    dadosProvaCorrigida.add(new String[]{id_prova.toString(), prova, prof, turma, dataProva, qtdQuestoes.toString(), qtdAlternativas, nota, respostasDadas, respostasEsperadas, String.valueOf(id_aluno), nomeAluno, notasQuestoes});
                }
            }
            try {
                File filecsv  = new File(getExternalFilesDir(null), "/dadosCorrecao.csv");
                String dateCart = new SimpleDateFormat(" HH_mm_ss").format(new Date());
                String filePdf = "Corrigida_" + prova + dateCart + ".pdf";
                GerarCsv.gerar(dadosProvaCorrigida, filecsv);// Gerando e salvando arquivo.csv
                File fSaida = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filePdf);
                BaixarResultadoCorrecao.solicitarResultadoCorrecao(filecsv, new FileOutputStream(fSaida), fSaida, filePdf, (DownloadManager) getSystemService(DOWNLOAD_SERVICE));
                AlertDialog.Builder builder = new AlertDialog.Builder(VisualProvaCorrigidaActivity.this);
                builder.setTitle("Por favor, Aguarde!")
                        .setMessage("Download em execução. Você será notificado quando o arquivo estiver baixado.");
                builder.setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } catch (Exception e) {
                Log.e("Kariti", e.toString());
            }
        });
        voltar.setOnClickListener(v -> {
            getOnBackPressedDispatcher();
            finish();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
    private String editaNomeAluno(String aluno){
        String[] separa = aluno.trim().split("\\s+");
        if(separa.length > 2) {
            return separa[0] + " " + separa[separa.length - 1];
        }else{
            return aluno;
        }
    }
    public void PopMenu(View v){
        v.setOnClickListener(view -> Toast.makeText(VisualProvaCorrigidaActivity.this, "Preparado para implementação", Toast.LENGTH_SHORT).show());
    }
    public void informeProvaNaoCorrigida(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Prova não corrigida corretamente!\n\n" +
                " Veja algumas das causas que podem ter colaborado para este resultado:\n\n" +
                "• Fundo da imagem com ruidos ou diferente do padrão uniforme \n\n" +
                "• Cartão resposta não visível totalmente na imagem\n\n" +
                "• Ambiente com pouca luminosidade\n\n" +
                "• imagem ofuscada\n\n" +
                "• Cartão resposta Rasurado\n\n" +
                "Para ter melhor resultado na correção é essencial que sejam seguidas as orientações destacadas na fase de correção!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public static class Scanner {

    }
}