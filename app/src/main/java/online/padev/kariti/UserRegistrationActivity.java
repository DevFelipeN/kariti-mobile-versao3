package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import online.padev.kariti.emails.EnviarCodigo;

public class UserRegistrationActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextConfirmedPassword;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 100;
    private ImageButton hidePassword;
    private ImageButton hidePassword2;
    private String name, email, password, confirmPassword, code;
    private BancoDados dataBase;
    private EnviarCodigo sendCode;
    private GerarCodigoValidacao generateCode;
    TextView textViewBackup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        editTextName = findViewById(R.id.editTextNome);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextNovaSenha);
        editTextConfirmedPassword = findViewById(R.id.editTextConfirmNovaSenha);
        hidePassword = findViewById(R.id.senhaoculta);
        hidePassword2 = findViewById(R.id.imgButtonSenhaOFF);
        ImageButton back = findViewById(R.id.imgBtnVoltar);
        Button btnRegistration = findViewById(R.id.buttonSalvarUsuario);
        textViewBackup = findViewById(R.id.textViewBackupLink);

        //cria uma instancia de outras classes
        dataBase = new BancoDados(this);
        sendCode = new EnviarCodigo();
        generateCode = new GerarCodigoValidacao();

        btnRegistration.setOnClickListener(v ->{
            btnRegistration.setEnabled(false);
            try {
                if (!VerificaConexaoInternet.verificaConexao(this)) {
                    Toast.makeText(UserRegistrationActivity.this, "Sem conexão de internet!", Toast.LENGTH_SHORT).show();
                    return;
                }
                name = editTextName.getText().toString().trim();
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();
                confirmPassword = editTextConfirmedPassword.getText().toString().trim();
                if (name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
                    Toast.makeText(this, "Por favor, preencher todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(UserRegistrationActivity.this, "O e-mail informado é inválido!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(UserRegistrationActivity.this, "Senhas divergentes!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Integer checkEmailExistsBD = dataBase.verificaExisteEmail(email); //verifica se existe este usuario no banco
                if (checkEmailExistsBD == null) {
                    code = generateCode.gerarVerificador();
                    if (sendCode.enviaCodigo(email, code)) {
                        startValidationCodeActivity();
                    } else {
                        Toast.makeText(UserRegistrationActivity.this, "Email não Enviado!", Toast.LENGTH_SHORT).show();
                    }
                } else if (checkEmailExistsBD.equals(-1)) {
                    Toast.makeText(this, "Falha na comunicação, tente novamente!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserRegistrationActivity.this, "Já existe um usuário associado a esse e-mail, cadastrado!", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Log.e("kariti", e.toString());
            }finally {
                btnRegistration.setEnabled(true);
            }
        });

        editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        hidePassword.setOnClickListener(v -> {
                // Verifica se a senha está visivel ou oculta.
            if(editTextPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
                //Se a senha está visivel ou oculta.
                editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                hidePassword.setImageResource(R.mipmap.senhaoff);
            } else {
                editTextPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                hidePassword.setImageResource(R.mipmap.senhaon);
            }
            editTextPassword.setSelection(editTextPassword.getText().length());
        });
        editTextConfirmedPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        hidePassword2.setOnClickListener(v -> {
//                Verifica se a senha está visivel ou oculta.
            if(editTextConfirmedPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
//                  Se a senha está visivel ou oculta.
                editTextConfirmedPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                hidePassword2.setImageResource(R.mipmap.senhaoff);
            } else {
                editTextConfirmedPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                hidePassword2.setImageResource(R.mipmap.senhaon);
            }
            editTextConfirmedPassword.setSelection(editTextConfirmedPassword.getText().length());
        });

        back.setOnClickListener(v -> {
            finish();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
        textViewBackup.setOnClickListener(v -> selectFileBackupDialog());
    }

    /**
     * Este método inicializa a tela de verificação de codigo, para autenticação de cadastro do usuário.
     */
    private void startValidationCodeActivity(){
        Intent proxima = new Intent(this, CodeValidationActivity.class);
        proxima.putExtra("identificador", 0);
        proxima.putExtra("nome", name);
        proxima.putExtra("email", email);
        proxima.putExtra("senha", password);
        proxima.putExtra("codigo", code);
        startActivity(proxima);
        finish();
    }
    private void displayFiles(){
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                    "application/zip"
            });
            intent.addCategory(Intent.CATEGORY_OPENABLE); // Garante que apenas arquivos abertos sejam exibidos
            startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT);
        }catch (Exception e){
            Log.e("ERRO", "ERRO AQUI11!!: "+e.toString());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_DOCUMENT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null){
                restoreDataBase(uri);
            }
        }
    }

    /**
     * Dado um arquivo .zip selecionado pelo usuario, reerente ao backup do Kariti
     * este método gerencia todo o processo de restauração do banco de dados no dispositivo atual
     * @param dbUri parâmetro contendo o endereço do arquivo selecionado
     */
    public void restoreDataBase(Uri dbUri) {
        try {
            File fileBackup = extractDataBase(dbUri);
            if (fileBackup == null) {
                Toast.makeText(this, "Erro na restauração dos seus dados no Kariti!", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject jsonObject = extrairJson(dbUri);
            if (jsonObject == null) {
                Toast.makeText(this, "Erro restauração dos seus dados no Kariti!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pega a versão do arquivo de backup
            String version = jsonObject.getString("version_db");
            int backupVersion = Integer.parseInt(version);

            //Log.e("version", String.format("%s",backupVersion));

            // Obtém o diretório do banco de dados do app
            File fileDbCurrent = getDatabasePath("base_dados.db");

            // Pega a versão do banco do app atualmente
            int currentVersion = dataBase.getDatabaseVersion();

            if (backupVersion > currentVersion) {
                Toast.makeText(this, "Este backup requer uma versão mais recente do app!", Toast.LENGTH_LONG).show();
                return;
            }
            if (backupVersion < currentVersion) {
                Toast.makeText(this, "Inválido, este backup é uma versão mais antiga do app!", Toast.LENGTH_LONG).show();
                return;
            }

            // Fecha qualquer execução do banco em aberto
            SQLiteDatabase db = SQLiteDatabase.openDatabase(fileDbCurrent.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
            db.close();

            InputStream inputStream = new FileInputStream(fileBackup);

            // Copia os dados do arquivo recebido para o banco de dados do app
            OutputStream outputStream = new FileOutputStream(fileDbCurrent);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Fecha os fluxos
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            sucessRestoration();

        } catch (Exception e) {
            Log.e("kariti", e.toString());
            Toast.makeText(this, "Erro na restauração dos seus dados no Kariti!", Toast.LENGTH_LONG).show();
        }
    }
    private void selectFileBackupDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("KARITI")
                .setMessage("Selecione o arquivo enviado para seu email!")
                .setPositiveButton("Selecionar", (dialog, which) -> {
                    displayFiles();
                    dialog.dismiss();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void sucessRestoration(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("KARITI")
                .setMessage("Seus dados foram restaurados com sucesso neste dispositivo.\n" +
                        "Agora basta realizar login no Kariti e continuar usando.")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Cria um dretório temporário para armazenar o banco
     * @return retorna o diretório criado
     */
    public File createDirectoreDBtemp() {
        try {
            File fileDB = new File(getCacheDir(), "backup_kariti.db");
            if (!fileDB.exists()) {
                try {
                    // Tenta criar o arquivo
                    if (fileDB.createNewFile()) {
                        Log.e("kariti","Diretorio criado");
                    } else {
                        Log.i("kariti", "Arquivo já existe.");
                    }
                } catch (IOException e) {
                    Log.e("kariti", "Erro ao criar diretorio!");
                }
            }
            return fileDB;
        }catch (Exception e){
            Log.e("circles", e.toString());
            return null;
        }
    }

    /**
     * Dado o uri do arquivo .zip selecionado pelo usuário
     * este método extrai o arquivo .db contendo banco de dados e salva no armazenamento interno do dispositivo
     * @param zipUri parâmetro contendo o endereço do arquivo
     * @return retorna o diretório onde será salvo o arquivo .db
     */

    public File extractDataBase(Uri zipUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(zipUri);
            if (inputStream == null) {
                Log.e("ZIP", "Erro ao abrir ZIP.");
                return null;
            }

            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
            ZipEntry zipEntry;

            File tempDB = createDirectoreDBtemp();
            if (tempDB == null) {
                Log.e("ZIP", "Erro ao criar diretório temporário.");
                zipInputStream.close();
                return null;
            }

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                Log.d("ZIP", "Arquivo encontrado: " + zipEntry.getName());

                if (zipEntry.getName().equals("base_dados.db")) {
                    try (OutputStream outputStream = new FileOutputStream(tempDB)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, length);
                        }
                        Log.e("ZIP", "Banco extraído com sucesso: " + tempDB.getAbsolutePath());
                    }
                }

                zipInputStream.closeEntry();
            }

            zipInputStream.close();
            return tempDB;

        } catch (Exception e) {
            Log.e("ZIP", "Erro ao extrair banco do ZIP: " + e.getMessage());
            return null;
        }
    }

    /**
     * Dado o uri do arquivo .zip selecionado pelo usuário
     * este método extrai o arquivo .json contendo a informação da versão do banco de dados
     * @param zipUri parâmetro contendo o endereço do arquivo
     * @return retorna um JSONObject com a informação
     */
    public JSONObject extrairJson(Uri zipUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(zipUri);
            if (inputStream == null) {
                Log.e("ZIP", "Erro ao abrir o arquivo ZIP.");
                return null;
            }

            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
            ZipEntry zipEntry;

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().equals("version_db.json")) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                    StringBuilder jsonString = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        jsonString.append(line);
                    }

                    // Fechar a entrada do ZIP e retornar o conteúdo JSON como JSONObject
                    zipInputStream.closeEntry();
                    zipInputStream.close();
                    inputStream.close();

                    // Retorna o conteúdo como JSONObject
                    return new JSONObject(jsonString.toString());
                }
                zipInputStream.closeEntry();
            }
            zipInputStream.close();

        } catch (IOException | JSONException e) {
            Log.e("ZIP", "Erro ao extrair o ZIP: " + e.getMessage());
            return null;
        }
        return null;
    }
}