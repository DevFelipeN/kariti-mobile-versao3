package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import online.padev.kariti.emails.EnviarCodigo;

public class MainActivity extends AppCompatActivity {

    private EditText editTextnome, editTextemail, editTextsenha, editTextconfirmarSenha;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 100;
    private ImageButton ocultarSenha;
    private ImageButton ocultarSenha2;
    private String nome, email, senha, confirmacaoSenha, codigo;
    private BancoDados bancoDados;
    private EnviarCodigo enviarCodigo;
    private GerarCodigoValidacao gerarCodigo;
    TextView textViewBackup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextnome = findViewById(R.id.editTextNome);
        editTextemail = findViewById(R.id.editTextEmail);
        editTextsenha = findViewById(R.id.editTextNovaSenha);
        editTextconfirmarSenha = findViewById(R.id.editTextConfirmNovaSenha);
        ocultarSenha = findViewById(R.id.senhaoculta);
        ocultarSenha2 = findViewById(R.id.imgButtonSenhaOFF);
        ImageButton voltar = findViewById(R.id.imgBtnVoltar);
        Button cadastro = findViewById(R.id.buttonSalvarUsuario);
        textViewBackup = findViewById(R.id.textViewBackupLink);

        //cria uma instancia de outras classes
        bancoDados = new BancoDados(this);
        enviarCodigo = new EnviarCodigo();
        gerarCodigo = new GerarCodigoValidacao();

        cadastro.setOnClickListener(v ->{
            if (!VerificaConexaoInternet.verificaConexao(MainActivity.this)) {
                Toast.makeText(MainActivity.this, "Sem conexão de rede!", Toast.LENGTH_SHORT).show();
                return;
            }
            nome = editTextnome.getText().toString();
            email = editTextemail.getText().toString();
            senha = editTextsenha.getText().toString();
            confirmacaoSenha = editTextconfirmarSenha.getText().toString();
            if (nome.trim().isEmpty() || senha.trim().isEmpty() || confirmacaoSenha.trim().isEmpty() || email.trim().isEmpty()) {
                Toast.makeText(MainActivity.this, "Por favor, preencher todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(MainActivity.this, "E-mail Inválido!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!senha.equals(confirmacaoSenha)) {
                Toast.makeText(MainActivity.this, "Senhas divergentes!", Toast.LENGTH_SHORT).show();
                return;
            }
            Integer verificaSeExisteEmailBD = bancoDados.verificaExisteEmail(email); //verifica se existe este usuario no banco
            if (verificaSeExisteEmailBD == null){
                codigo = gerarCodigo.gerarVerificador();
                if (enviarCodigo.enviaCodigo(email, codigo)){
                    carregarTelaCodigo();
                } else {
                    Toast.makeText(MainActivity.this, "Email não Enviado!", Toast.LENGTH_SHORT).show();
                }
            } else if(verificaSeExisteEmailBD.equals(-1)){
                Toast.makeText(this, "Falha na comunicação, tente novamente!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Já existe um usuário associado a esse e-mail, cadastrado!", Toast.LENGTH_SHORT).show();
            }
        });

        editTextsenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ocultarSenha.setOnClickListener(v -> {
                // Verifica se a senha está visivel ou oculta.
            if(editTextsenha.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
                //Se a senha está visivel ou oculta.
                editTextsenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ocultarSenha.setImageResource(R.mipmap.senhaoff);
            } else {
                editTextsenha.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ocultarSenha.setImageResource(R.mipmap.senhaon);
            }
            editTextsenha.setSelection(editTextsenha.getText().length());
        });
        editTextconfirmarSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ocultarSenha2.setOnClickListener(v -> {
//                Verifica se a senha está visivel ou oculta.
            if(editTextconfirmarSenha.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
//                  Se a senha está visivel ou oculta.
                editTextconfirmarSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ocultarSenha2.setImageResource(R.mipmap.senhaoff);
            } else {
                editTextconfirmarSenha.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ocultarSenha2.setImageResource(R.mipmap.senhaon);
            }
            editTextconfirmarSenha.setSelection(editTextconfirmarSenha.getText().length());
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
        textViewBackup.setOnClickListener(v -> selectBdFile());
    }

    /**
     * Este método inicializa a tela de verificação de codigo, para autenticação de cadastro do usuário.
     */
    private void carregarTelaCodigo(){
        Intent proxima = new Intent(this, CodSenhaActivity.class);
        proxima.putExtra("identificador", 0);
        proxima.putExtra("nome", nome);
        proxima.putExtra("email", email);
        proxima.putExtra("senha", senha);
        proxima.putExtra("codigo", codigo);
        startActivity(proxima);
        finish();
    }
    private void displayFiles(){
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
                    "application/x-sqlite3",
                    "application/octet-stream"
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
    public void restoreDataBase(Uri dbUri) {
        try {
            // Pega o nome do arquivo selecionado
            String fileName = getFileName(dbUri);

            // Pega a versão do arquivi de backup
            int backupVersion = getVersionBackup(fileName);
            if (backupVersion == -1){
                Toast.makeText(this, "Arquivo selecionado, inválido!", Toast.LENGTH_LONG).show();
                return;
            }

            // Obtém o diretório do banco de dados do app
            File fileDbCurrent = getDatabasePath("base_dados.db");

            // Pega a versão do banco do app atualmente
            int currentVersion = bancoDados.getDatabaseVersion();

            if (backupVersion > currentVersion) {
                Toast.makeText(this, "Este backup requer uma versão mais recente do app!", Toast.LENGTH_LONG).show();
                return;
            }
            if (backupVersion < currentVersion) {
                Toast.makeText(this, "Este backup é uma versão mais antiga do app!", Toast.LENGTH_LONG).show();
                return;
            }

            // fecha qualquer execução do banco em aberto
            SQLiteDatabase db = SQLiteDatabase.openDatabase(fileDbCurrent.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
            db.close();

            // Abre um InputStream do arquivo recebido por e-mail
            InputStream inputStream = getContentResolver().openInputStream(dbUri);

            if (inputStream == null) {
                Toast.makeText(this, "Erro ao abrir o arquivo!", Toast.LENGTH_SHORT).show();
                return;
            }

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
            Toast.makeText(this, "Erro restauração dos seus dados no Kariti!", Toast.LENGTH_LONG).show();
        }
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};

        try (Cursor cursor = getContentResolver().query(uri, projection, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                fileName = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e("kariti", e.toString());
        }

        return fileName;
    }

    private int getVersionBackup(String fileName){
        if(fileName != null && fileName.endsWith(".db")) {
            try {
                String[] parte1 = fileName.split("_");
                String[] parte2 = parte1[2].split("\\.");
                return Integer.parseInt(parte2[0]);
            }catch (Exception e){
                Log.e("kariti", e.toString());
                return -1;
            }
        }else{
            return -1;
        }
    }
    private void selectBdFile(){
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
                .setMessage("Seus dados foram restaurados com sucesso nesse dispositivo.\n" +
                        "Agora basta realizar login no Kariti e continuar usando.")
                .setPositiveButton("Sim", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

    