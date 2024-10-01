package online.padev.kariti;

import static android.os.Environment.getExternalStoragePublicDirectory;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ProvaCartoesActivity extends AppCompatActivity {
    ImageButton voltar;
    Button baixarCartoes;
    Integer id_turma, endereco, idTurmaSelect;
    String prova, turma, turmaSelecionada, idAluno;
    ArrayList<String> listagemProvas, turmalist, alunolist;
    List<String[]> dados;
    ArrayList<Integer> listIdsAlunos;
    BancoDados bancoDados;
    Spinner spinnerTurma, spinnerProva, spinnerAluno;
    TextView titulo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_cartoes);

        voltar = findViewById(R.id.imgBtnVoltar);
        spinnerTurma = findViewById(R.id.spinnerTurma);
        spinnerProva = findViewById(R.id.spinnerProva);
        spinnerAluno = findViewById(R.id.spinnerAlunos);
        baixarCartoes = findViewById(R.id.baixarcatoes);
        titulo = findViewById(R.id.toolbar_title);
        bancoDados = new BancoDados(this);

        titulo.setText(String.format("%s","Cartões"));

        endereco = Objects.requireNonNull(getIntent().getExtras()).getInt("endereco");
        prova = getIntent().getExtras().getString("prova");
        turmalist = (ArrayList<String>) bancoDados.listarTurmasPorProva();

        if(endereco.equals(2)){ //para quando a activity que a chamou foi ProvaActivity
            turmalist.add(0,"Selecione a turma");
            SpinnerAdapter adapterTurma = new SpinnerAdapter(this, turmalist);
            spinnerTurma.setAdapter(adapterTurma);

            spinnerTurma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position!=0){
                        turmaSelecionada = spinnerTurma.getSelectedItem().toString();
                        idTurmaSelect = bancoDados.pegarIdTurma(turmaSelecionada);

                        listagemProvas = (ArrayList<String>) bancoDados.listarNomesProvasPorTurma(String.valueOf(idTurmaSelect));
                        SpinnerAdapter adapterProva = new SpinnerAdapter(ProvaCartoesActivity.this, listagemProvas);
                        spinnerProva.setAdapter(adapterProva);

                        alunolist = (ArrayList<String>) bancoDados.listarAlunosPorTurma(idTurmaSelect.toString());
                        alunolist.add(0, "Alunos");
                        SpinnerAdapter adapterAluno = new SpinnerAdapter(ProvaCartoesActivity.this, alunolist);
                        spinnerAluno.setAdapter(adapterAluno);
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }else if(endereco.equals(1)){ //para quando a activity que chamou for Gabarito
            id_turma = getIntent().getExtras().getInt("id_turma");
            turma = bancoDados.pegarNomeTurma(String.valueOf(id_turma));
            turmalist.add(0, turma);
            SpinnerAdapter adapterTurma = new SpinnerAdapter(this, turmalist);
            spinnerTurma.setAdapter(adapterTurma);
            //Lista todas provas pertecentes a turma selecionada
            listagemProvas = (ArrayList<String>) bancoDados.listarNomesProvasPorTurma(String.valueOf(id_turma));
            listagemProvas.add(0, prova);
            SpinnerAdapter adapterProva = new SpinnerAdapter(ProvaCartoesActivity.this, listagemProvas);
            spinnerProva.setAdapter(adapterProva);
            //Lista todos os alunos pertecentes a turma selecionada
            alunolist = (ArrayList<String>) bancoDados.listarAlunosPorTurma(id_turma.toString());
            alunolist.add(0, "Alunos");
            SpinnerAdapter adapterAluno = new SpinnerAdapter(ProvaCartoesActivity.this, alunolist);
            spinnerAluno.setAdapter(adapterAluno);

            spinnerTurma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position!=0){
                        turmaSelecionada = spinnerTurma.getSelectedItem().toString();
                        idTurmaSelect = bancoDados.pegarIdTurma(turmaSelecionada);
                        //Lista todas provas pertecentes a turma selecionada
                        listagemProvas = (ArrayList<String>) bancoDados.listarNomesProvasPorTurma(String.valueOf(idTurmaSelect));
                        listagemProvas.add(0, "Selecione a prova");
                        SpinnerAdapter adapterProva = new SpinnerAdapter(ProvaCartoesActivity.this, listagemProvas);
                        spinnerProva.setAdapter(adapterProva);
                        //Lista todos os alunos pertencentes a turma selecionada
                        alunolist = (ArrayList<String>) bancoDados.listarAlunosPorTurma(idTurmaSelect.toString());
                        alunolist.add(0, "Alunos");
                        SpinnerAdapter adapterAluno = new SpinnerAdapter(ProvaCartoesActivity.this, alunolist);
                        spinnerAluno.setAdapter(adapterAluno);

                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        baixarCartoes.setOnClickListener(v -> {
            if (isOnline()) {
                if(spinnerProva.getSelectedItem() != null) {
                    String nomeProva = spinnerProva.getSelectedItem().toString();
                    String nomeTurma = spinnerTurma.getSelectedItem().toString();
                    id_turma = bancoDados.pegarIdTurma(nomeTurma);
                    String id_prova = String.valueOf(bancoDados.pegarIdProva(nomeProva, id_turma));
                    String id_usuario = String.valueOf(BancoDados.USER_ID);
                    String prof = bancoDados.pegarNomeUsuario(id_usuario);
                    String data = bancoDados.pegarDataProva(id_prova);
                    String nota = String.valueOf(bancoDados.pegarNotaProva(id_prova));
                    String questoes = String.valueOf(bancoDados.pegarQtdQuestoes(id_prova));
                    String alternativas = String.valueOf(bancoDados.pegarQtdAlternativas(id_prova));

                    dados = new ArrayList<>();

                    String idTurma = String.valueOf(bancoDados.pegarIdTurma(nomeTurma));
                    listIdsAlunos = (ArrayList<Integer>) bancoDados.listarIdsAlunosPorTurma(idTurma);
                    int qtdProvas = listIdsAlunos.size();
                    dados.add(new String[]{"ID_PROVA", "NOME_PROVA", "NOME_PROFESSOR", "NOME_TURMA", "DATA_PROVA", "NOTA_PROVA", "QTD_QUESTOES", "QTD_ALTERNATIVAS", "ID_ALUNO", "NOME_ALUNO"});
                    for (int x = 0; x < qtdProvas; x++) {
                        idAluno = String.valueOf(listIdsAlunos.get(x));
                        String aluno = bancoDados.pegaNomeAluno(listIdsAlunos.get(x));
                        dados.add(new String[]{id_prova, nomeProva, prof, nomeTurma, data, nota, questoes, alternativas, idAluno, aluno});
                    }
                    try {
                        //File filecsv = null;
                        //filecsv = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "dadosProva.csv");
                        String dateCart = new SimpleDateFormat(" HH_mm_ss").format(new Date());
                        String filePdf = nomeProva + dateCart + ".pdf";
                        File filecsv  = new File(getExternalFilesDir(null), "/dadosProva.csv");
                        GerarCsv.gerar(dados, filecsv);// Gerando e salvando arquivo.csv
                        File fSaida = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filePdf);
                        BaixarModeloCartao.solicitarCartoesResposta(filecsv, new FileOutputStream(fSaida), fSaida, filePdf, (DownloadManager) getSystemService(DOWNLOAD_SERVICE));
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProvaCartoesActivity.this);
                        builder.setTitle("Por favor, Aguarde!")
                                .setMessage("Download em execução. Você será notificado quando o arquivo estiver baixado.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    } catch (Exception e) {
                        Log.e("Kariti", e.toString());
                        pedirPermissao();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            // Para Android 11 (API 30) e superior, usar MANAGE_EXTERNAL_STORAGE
                            if (!Environment.isExternalStorageManager()) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                startActivity(intent);
                            }
                        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                            // Para Android 6.0 (API 23) até Android 9 (API 28), solicitar WRITE_EXTERNAL_STORAGE
                            if (ContextCompat.checkSelfPermission(ProvaCartoesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(ProvaCartoesActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                            //Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }
                    }
                }else Toast.makeText(ProvaCartoesActivity.this, "Selecione os dados", Toast.LENGTH_SHORT).show();
            }else Toast.makeText(ProvaCartoesActivity.this, "Sem conexão de rede!", Toast.LENGTH_SHORT).show();
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    public void pedirPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kariti");
        builder.setMessage("Permitir que o Kariti faça download dos cartões no seu dispositivo.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}