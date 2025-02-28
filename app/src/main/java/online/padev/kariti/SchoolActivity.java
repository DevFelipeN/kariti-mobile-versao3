package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.emails.EnviarBackup;

public class SchoolActivity extends AppCompatActivity {
    ImageButton iconExit, iconHelp;
    FloatingActionButton btnSchoolDisabled, btnRegistrationSchool;
    TextView titleActivity, txtDescriptionDisabled, txtDescriptionNewSchool, backupBD;
    ListView listViewSchools;
    AdapterClickableSchool adapterSchool;
    private List<String> listSchoolsBD;
    DataBaseKariti dataBase;
    private static final int REQUEST_CODE = 1;
    private Integer id_school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);

        iconExit = findViewById(R.id.imageButtonInicio);
        btnSchoolDisabled = findViewById(R.id.iconarquivadas);
        listViewSchools = findViewById(R.id.listViewEscolas);
        iconHelp = findViewById(R.id.iconHelpLogout);
        btnRegistrationSchool = findViewById(R.id.iconmaisescolas);
        titleActivity = findViewById(R.id.toolbar_title);
        txtDescriptionDisabled = findViewById(R.id.txtDescricaoDesativadas);
        txtDescriptionNewSchool = findViewById(R.id.txtDescricaoNovaEscola);
        backupBD = findViewById(R.id.textBackupBD);

        dataBase = new DataBaseKariti(this);

        titleActivity.setText(String.format("%s","Acessar com:"));

        listSchoolsBD = dataBase.listarEscolas(1); //carrega todas as escolas ativadas para o usuario logado
        if(listSchoolsBD == null){
            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
            finish();
        }
        if(listSchoolsBD.isEmpty()){
            if(!dataBase.listarEscolas(0).isEmpty()){
                startSchoolsDisabled();
            }else{
                registrationNewSchool();
            }
        }
        adapterSchool = new AdapterClickableSchool(this, listSchoolsBD, listSchoolsBD);
        listViewSchools.setAdapter(adapterSchool);

        listViewSchools.setOnItemClickListener((parent, view, position, id) -> {
            DataBaseKariti.ID_ESCOLA = dataBase.pegarIdEscola(adapterSchool.getItem(position));
            if (DataBaseKariti.ID_ESCOLA == null || DataBaseKariti.ID_ESCOLA == -1){
                Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                return;
            }
            startMenuInitial();
        });
        listViewSchools.setOnItemLongClickListener((parent, view, position, id) -> {
            // Exibir a caixa de diálogo
            AlertDialog.Builder builder = new AlertDialog.Builder(SchoolActivity.this);
            builder.setTitle("Atenção!")
                    .setMessage("Deseja desativar essa escola?")
                    .setPositiveButton("SIM", (dialog, which) -> {
                        id_school = dataBase.pegarIdEscola(adapterSchool.getItem(position));
                        if (id_school == null || id_school == -1){
                            Toast.makeText(this, "Falha de comunicação! \n\n Por favor, tente novamente", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(dataBase.alterarStatusEscola(id_school,0)){
                            listSchoolsBD.remove(position);
                            adapterSchool.notifyDataSetChanged();
                            if(listSchoolsBD.isEmpty()) {
                                finish();
                            }
                            Toast.makeText(SchoolActivity.this, "Escola desativada", Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(SchoolActivity.this, "Erro: escola não desativada!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("NÃO", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            // Retorna true para indicar que o evento de long press foi consumido
            return true;
        });

        //Exibir o texto sobre o botão
        txtDescriptionNewSchool.setVisibility(View.VISIBLE);
        txtDescriptionDisabled.setVisibility(View.VISIBLE);
        // Ocultar o texto após 3 segundos
        new Handler().postDelayed(() -> txtDescriptionNewSchool.setVisibility(View.INVISIBLE), 10000);
        new Handler().postDelayed(() -> txtDescriptionDisabled.setVisibility(View.INVISIBLE), 10000);

        btnRegistrationSchool.setOnClickListener(v -> {
            txtDescriptionNewSchool.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> txtDescriptionNewSchool.setVisibility(View.INVISIBLE), 3000);
            registrationNewSchool();
        });

        btnSchoolDisabled.setOnClickListener(v -> {
            txtDescriptionDisabled.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> txtDescriptionDisabled.setVisibility(View.INVISIBLE), 3000);
            startSchoolsDisabled();
        });
        iconHelp.setOnClickListener(v -> help());
        iconExit.setOnClickListener(v -> exit());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exit();
            }
        });
        backupBD.setOnClickListener(v -> dialogBackup());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            finish();
            startActivity(getIntent());
        }
    }
    private void exit(){
        DataBaseKariti.USER_ID = null;
        finish();
        Toast.makeText(SchoolActivity.this, "Usuário desconectado", Toast.LENGTH_SHORT).show();
    }
    private void help() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("• Para continuar navegando nas funcionalidades do app, clique no campo com a escola desejada. \n\n" +
                "• Cada escola possui suas informações que são restritas a outras. \n\n" +
                "• Para desativar uma escola, basta selecionar a escola desejada e confirmar a ação. " +
                "Posteriormente, você poderá encontrar suas escolas desativadas clicando no botão 'Escolas Desativadas'.");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void startMenuInitial(){
        Intent intent = new Intent(this, MenuInitialActivity.class);
        startActivity(intent);
    }
    private void startSchoolsDisabled() {
        if(!dataBase.listarEscolas(0).isEmpty()) {
            Intent intent = new Intent(this, SchoolDisabledActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }else{
            notifySchoolDisabledNonExistent();
        }
    }
    private void registrationNewSchool() {
        // Inflar o layout customizado
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.cadastrar_escola_dialog, null);

        // Inicializar os elementos do layout
        FloatingActionButton cancelFlut = dialogView.findViewById(R.id.btnvoltarflutuante);
        EditText editTextSchool = dialogView.findViewById(R.id.editTextNomeEscolaDialog);
        Button btnRegistrationSchool = dialogView.findViewById(R.id.buttonDialog);

        // Criar o AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(dialogView);
        // Mostrar o diálogo
        AlertDialog dialog = builder.create();
        dialog.show();

        btnRegistrationSchool.setOnClickListener(v -> {
            String nameSchool = editTextSchool.getText().toString().trim();
            if (!nameSchool.isEmpty()){
                Boolean checkExistsSchool = dataBase.verificaExisteEscola(nameSchool);
                if(checkExistsSchool == null){
                    Toast.makeText(this, "Falha na comunicação, tente novamente!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkExistsSchool){
                    if (dataBase.cadastrarEscola(nameSchool, 1)) {
                        listSchoolsBD.add(nameSchool);
                        Collections.sort(listSchoolsBD);
                        adapterSchool.notifyDataSetChanged();
                        dialog.dismiss();
                        Toast.makeText(this, "Escola cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Erro: Escola não cadastrada!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Atenção: Escola já cadastrada!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Informe o nome da escola!", Toast.LENGTH_SHORT).show();
            }
        });
        cancelFlut.setOnClickListener(v -> {
            if(listSchoolsBD.isEmpty()) finish();
            dialog.dismiss();//Fecha o diálogo
        });
    }
    private void notifySchoolDisabledNonExistent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("KARITI");
        builder.setMessage("Aqui você encontra todas as suas escolas desativas.\n\n" +
                "Obs. Você não possui escolas desativadas até o momento!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        // Criando o diálogo
        AlertDialog dialog = builder.create();

        // Exibindo o diálogo
        dialog.show();
        // Mudando a cor do botão "OK" depois de mostrar o diálogo
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(this, R.color.azul)
        );
    }
    private void dialogBackup(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.backup_bd, null);

        // Inicializar os elementos do layout
        FloatingActionButton closedBackup = dialogView.findViewById(R.id.btnBackupBD);
        Button buttonYes = dialogView.findViewById(R.id.btnYes);
        Button buttonNot = dialogView.findViewById(R.id.btnNot);

        // Criar o AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(dialogView);
        // Mostrar o diálogo
        AlertDialog dialog = builder.create();
        dialog.show();

        buttonYes.setOnClickListener(v -> {
            if (!VerificaConexaoInternet.verificaConexao(this)) {
                Toast.makeText(this, "Sem conexão de internet!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
            if (startBackup()){
                Toast.makeText(this, "Backup realizado com sucesso!!", Toast.LENGTH_SHORT).show();
                backupGuidance();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Falha na realização do backup!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        buttonNot.setOnClickListener(v -> dialog.dismiss());

        closedBackup.setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * Este método controla todo o processo de backup do banco de dados
     * para envio por email do usuário atual logado na aplicação
     * @return retorna um valor booleano indicando se todo o processo obteve sucesso
     */
    private boolean startBackup(){
        File dbFile = getDatabasePath("base_dados.db");
        String email = dataBase.pegarEmailUsuario(DataBaseKariti.USER_ID);
        if (email == null){
            return false;
        }
        // fecha qualquer execução do banco em aberto
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
        db.close();

        if (!dbFile.exists()) {
            Log.e("kariti", "Banco de dados não encontrado!");
            return false;
        }

        //Cria um diretorio para salvar o Json internamente
        File jsonFile = getOutputJson();
        if (!jsonFile.exists()){
            Log.e("kariti", "Erro na criação de arquivo Json!");
            return false;
        }

        // Criar o Jason contendo a versão do banco de dados
        try {
            if (!createJson(jsonFile)){
                Log.e("kariti", "Falha na criaçao do Json!");
                return false;
            }
        }catch (JSONException e){
            Log.e("kariti", "Falha na criaçao do Json!");
            return false;
        }

        File fileZip = createDirectoreZip();

        // Cria um ZIP contendo o JSON e o Banco de Dados
        try (FileOutputStream fos = new FileOutputStream(fileZip);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            addFileInZip(dbFile, "base_dados.db", zipOut);
            addFileInZip(jsonFile, "version_db.json", zipOut);
        }catch (IOException e){
            Log.e("kariti", e.toString());
            return false;
        }

        EnviarBackup enviarBackup = new EnviarBackup();
        return enviarBackup.enviaBackup(email, fileZip);
    }

    /**
     * Este método adiciona arquivos a um zip
     * @param file caminho do arquivo a ser adicionado no zip
     * @param nomeNoZip nome do arquivo no zip
     * @param zipOut saida onde será criado o zip
     * @throws IOException
     */
    private void addFileInZip(File file, String nomeNoZip, ZipOutputStream zipOut) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(nomeNoZip);
            zipOut.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zipOut.write(buffer, 0, length);
            }

            zipOut.closeEntry();
        }
    }

    /**
     * Este método cria um diretório para armazenamento de um arquivo Json
     * @return retorna o diretório criado
     */
    private File getOutputJson(){
        File fileJson = new File(getCacheDir(), "versionBackup.json");
        if (!fileJson.exists()) {
            try {
                // Tenta criar o arquivo
                if (fileJson.createNewFile()) {
                    Log.e("kariti","Diretorio criado");
                } else {
                    Log.i("kariti", "Arquivo já existe.");
                }
            } catch (IOException e) {
                Log.e("kariti", "Erro ao criar diretorio!");
            }
        }
        return fileJson;
    }

    /**
     * Este método cria um diretório para armazenamento de um arquivo zip
     * @return retorna o diretório criado
     */
    public File createDirectoreZip() {
        try {
            File fileZip = new File(getCacheDir(), "backup_kariti.zip");
            if (!fileZip.exists()) {
                try {
                    // Tenta criar o arquivo
                    if (fileZip.createNewFile()) {
                        Log.e("kariti","Diretorio criado");
                    } else {
                        Log.i("kariti", "Arquivo já existe.");
                    }
                } catch (IOException e) {
                    Log.e("kariti", "Erro ao criar diretorio!");
                }
            }
            return fileZip;
        }catch (Exception e){
            Log.e("circles", e.toString());
            return null;
        }
    }

    /**
     * Este método cria um json com a informação da versão do banco de dados atual
     * @param jsonFile diretório criado para armazenamento do json
     * @return returna um valor booleano referente ao resultado da operação
     * @throws JSONException indica que este metodo pode lançar uma exceção que deve ser tratada
     */
    private boolean createJson(File jsonFile) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("version_db", dataBase.getDatabaseVersion());

        // Salvar JSON no armazenamento interno
        try (FileWriter writer = new FileWriter(jsonFile)){
            writer.write(jsonObject.toString());
            writer.flush();
        }catch (Exception e){
            Log.e("kariti", e.toString());
            return false;
        }
        return true;
    }

    private void backupGuidance(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("KARITI")
                .setMessage("Seus dados do Kariti foram enviados para seu e-mail \n\n" +
                        "Para utilizar seus dados do Kariti em outro dispositivo siga as orientações do e-mail\n\n" +
                        "Certifique-se de receber o e-mail antes de desinstalar o Kariti desse dispositivo.")
                .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}