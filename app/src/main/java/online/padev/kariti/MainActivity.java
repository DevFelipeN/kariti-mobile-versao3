package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText editTextnome, editTextemail, editTextsenha, editTextconfirmarSenha;
    private ImageButton ocultarSenha;
    private ImageButton ocultarSenha2;
    private String nome, email, senha, confirmacaoSenha, codigo;

    BancoDados bancoDados;
    EnviarEmail enviarEmail;
    GerarCodigoValidacao gerarCodigo;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //acessa os dados dos campos do xml
        editTextnome = findViewById(R.id.editTextNome);
        editTextemail = findViewById(R.id.editTextEmail);
        editTextsenha = findViewById(R.id.editTextNovaSenha);
        editTextconfirmarSenha = findViewById(R.id.editTextConfirmNovaSenha);
        ocultarSenha = findViewById(R.id.senhaoculta);
        ocultarSenha2 = findViewById(R.id.imgButtonSenhaOFF);
        ImageButton voltar = findViewById(R.id.imgBtnVoltaEscola);
        Button cadastro = findViewById(R.id.buttonSalvarEdit);

        //cria uma instancia de outras classes
        bancoDados = new BancoDados(this);
        enviarEmail = new EnviarEmail();
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
            if(!email.trim().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
                if (senha.equals(confirmacaoSenha)){
                    Boolean verificaSeExisteBD = bancoDados.checkNome(email); //verifica se existe este usuario no banco
                    if (verificaSeExisteBD.equals(false)) {
                        codigo = gerarCodigo.gerarVerificador();
                        if (enviarEmail.enviaCodigo(email, codigo)) {
                            carregarTelaCodigo();
                        } else {
                            Toast.makeText(MainActivity.this, "Email não Enviado", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Já existe um usuário associado a esse e-mail, cadastrado!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Senhas divergentes!", Toast.LENGTH_SHORT).show();
                }
            else {
                Toast.makeText(MainActivity.this, "E-mail Inválido!", Toast.LENGTH_SHORT).show();
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
}

    