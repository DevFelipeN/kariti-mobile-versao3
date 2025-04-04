package online.padev.kariti;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Objects;

import online.padev.kariti.cards.CreatCard;
import online.padev.kariti.entity.Prova;
import online.padev.kariti.database.DataBaseKariti;
import pl.droidsonroids.gif.GifImageView;

public class ProvaGenerateCardRegisteredActivity extends AppCompatActivity {
    ImageButton toGoBack;
    Button btnGenerateCard;
    Integer id_ClassBD, address, id_provaBD;
    String nameProva, nameClass;
    List<String> listProva, listClass, listStudent;
    DataBaseKariti dataBaseKariti;
    Spinner spinnerClass, spinnerProva, spinnerStudent;
    AdapterSpinner adapterClass, adapterProva, adapterStudent;
    TextView title;
    Prova prova;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prova_generate_card_registered);

        toGoBack = findViewById(R.id.imgBtnVoltar);
        spinnerClass = findViewById(R.id.spinnerTurma);
        spinnerProva = findViewById(R.id.spinnerProva);
        spinnerStudent = findViewById(R.id.spinnerAlunos);
        btnGenerateCard = findViewById(R.id.baixarcatoes);
        title = findViewById(R.id.toolbar_title);

        dataBaseKariti = new DataBaseKariti(this);

        title.setText(String.format("%s","Cartões"));

        address = Objects.requireNonNull(getIntent().getExtras()).getInt("endereco");

        listClass = dataBaseKariti.listarTurmasPorProva();
        if(listClass == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            return;
        }
        listClass.add(0,"Selecione a turma");

        if(address.equals(2)){ //para quando a activity que a chamou foi ProvaActivity
            adapterClass = new AdapterSpinner(this, listClass);
            spinnerClass.setAdapter(adapterClass);

        }else if(address.equals(1)) { //para quando a activity que chamou for Gabarito
            id_ClassBD = getIntent().getExtras().getInt("id_turma");
            nameClass = dataBaseKariti.pegarNomeTurma(String.valueOf(id_ClassBD));
            nameProva = getIntent().getExtras().getString("prova");
            if (nameClass == null){
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                return;
            }

            int indexTurma = listClass.indexOf(nameClass); // Identifica a posicão da turma na lista
            adapterClass = new AdapterSpinner(this, listClass);
            spinnerClass.setAdapter(adapterClass);
            if (indexTurma != -1){
                spinnerClass.setSelection(indexTurma);
            }

            //============ Lista todas provas pertecentes a turma selecionada =======================
            listProva = dataBaseKariti.listarNomesProvasPorTurma(String.valueOf(id_ClassBD));
            if (listProva == null){
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                return;
            }

            int indexProva = listProva.indexOf(nameProva);
            adapterProva = new AdapterSpinner(this, listProva);
            spinnerProva.setAdapter(adapterProva);
            spinnerProva.postDelayed(() -> {
                if (indexProva != -1) {
                    spinnerProva.setSelection(indexProva);
                }
            }, 200);


            // ============ Lista todos os alunos pertencentes a turma selecionada =======================================
            listStudent = dataBaseKariti.listarAlunosPorTurma(id_ClassBD.toString());
            if (listStudent == null){
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                return;
            }
            listStudent.add(0, "Todos");
            adapterStudent = new AdapterSpinner(this, listStudent);
            spinnerStudent.setAdapter(adapterStudent);
        }

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0){
                    try {
                        nameClass = spinnerClass.getSelectedItem().toString();
                        id_ClassBD = dataBaseKariti.pegarIdTurma(nameClass);
                        if (id_ClassBD == null) {
                            Toast.makeText(ProvaGenerateCardRegisteredActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        listProva = dataBaseKariti.listarNomesProvasPorTurma(String.valueOf(id_ClassBD));
                        if (listProva == null) {
                            Toast.makeText(ProvaGenerateCardRegisteredActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        adapterProva = new AdapterSpinner(ProvaGenerateCardRegisteredActivity.this, listProva);
                        spinnerProva.setAdapter(adapterProva);

                        listStudent = dataBaseKariti.listarAlunosPorTurma(id_ClassBD.toString());
                        if (listStudent == null) {
                            Toast.makeText(ProvaGenerateCardRegisteredActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        listStudent.add(0, "Todos");
                        adapterStudent = new AdapterSpinner(ProvaGenerateCardRegisteredActivity.this, listStudent);
                        spinnerStudent.setAdapter(adapterStudent);
                    } catch (Exception e){
                        Log.e("kariti", e.toString());
                        Toast.makeText(ProvaGenerateCardRegisteredActivity.this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    int indexClass = listClass.indexOf(nameClass);
                    spinnerClass.setSelection(indexClass);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btnGenerateCard.setOnClickListener(v -> {
            btnGenerateCard.setEnabled(false);
            try {
                requestsPermissionNotify();
                if(spinnerProva.getSelectedItem() != null) {
                    nameProva = spinnerProva.getSelectedItem().toString();
                    //String aluno = spinnerAluno.getSelectedItem().toString();
                    id_provaBD = dataBaseKariti.pegarIdProvaPorTurma(nameProva, id_ClassBD);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
                        solicitaPermissao();
                    }else {
                        generateCard();
                    }

                }else Toast.makeText(this, "Selecione os dados", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Log.e("kariti",e.getMessage());
                Toast.makeText(this, "Ocorreu uma falha de comunicação no Kariti! \n\n Por favor, tente novamente.", Toast.LENGTH_SHORT).show();
            }

        });
        toGoBack.setOnClickListener(view -> finish());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void generateCard(){
        View overlayView = findViewById(R.id.overlayView);
        GifImageView gifLoading = findViewById(R.id.loadingId);
        overlayView.setVisibility(View.VISIBLE);
        gifLoading.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                prova = new Prova(id_provaBD, dataBaseKariti);
                CreatCard creatCard = new CreatCard(prova, dataBaseKariti, this);
                if (creatCard.creatPdfCard()) {
                    runOnUiThread(this::infoDownloadCard);
                } else {
                    runOnUiThread(this::notifyFailureDownload);
                }
            } catch (Exception e) {
                Log.e("kariti", e.toString());
            } finally {
                runOnUiThread(() -> {
                    btnGenerateCard.setEnabled(true);
                    gifLoading.setVisibility(View.GONE);
                    overlayView.setVisibility(View.GONE);
                });
            }
        }).start();
    }

    private void requestsPermissionNotify(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101); // Código de solicitação
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) { // Verifica se o código de solicitação é o esperado
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisão concedida com sucesso", Toast.LENGTH_SHORT).show();
                Log.d("Permissão", "Permissão WRITE_EXTERNAL_STORAGE concedida.");
                generateCard();
            } else {
                // Permissão negada
                Log.d("Permissão", "Permissão WRITE_EXTERNAL_STORAGE negada.");
                permissaoNegada();
                // Informe ao usuário que a permissão é necessária ou tome uma ação adequada
            }
        }
        if (requestCode == 101){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão concedida!", Toast.LENGTH_SHORT).show();
            } else {
                // Permissão negada, exiba uma mensagem explicativa ao usuário
                permissaoDNotificacaoNegada();
            }
        }
    }
    public void permissaoNegada(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("Para realizar o download dos cartões resposta em seu dispositivo, é necessário que conceda a permissão ao Kariti! .");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    public void permissaoDNotificacaoNegada(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENÇÃO");
        builder.setMessage("O Kariti não será capaz de notifica-lo sobre os downloads realizados! .");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void solicitaPermissao(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else{
            generateCard();
        }
    }
    private void infoDownloadCard(){
        if(!isFinishing() && !isDestroyed()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Cartões gerados com sucesso");
            builder.setMessage("Os cartões respostas foram gerados e estão disponíveis na pasta de downloads do seu dispositivo.");
            builder.setPositiveButton("OK", (dialog, which) -> {
                dialog.dismiss();
                orientation();
            });

            builder.show();
        }
    }
    private void notifyFailureDownload(){
        if(!isFinishing() && !isDestroyed()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("KARITI");
            builder.setMessage("Ocorreu uma falha ao tentar gerar os cartões dessa prova, se a falha persistir: \n" +
                    "1 - Verifique se possui armazenamento diponível para realização de downloads" +
                    "2 - Reinicie o Kariti!");
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
        }
    }
    private void orientation(){
        if(!isFinishing() && !isDestroyed()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("ORIENTAÇÃO!");
            builder.setMessage("O preenchimento dos cartões-respostas deve ser feito com caneta de cor escura, preferencialmente de cor preta.");
            builder.setPositiveButton("OK", (dialog, which) -> {
                dialog.dismiss();
                finish();
            });

            builder.show();
        }
    }
}