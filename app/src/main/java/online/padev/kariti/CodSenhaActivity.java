package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CodSenhaActivity extends AppCompatActivity {
    private  EditText n1, n2, n3, n4;
    private String v1, v2, v3, v4, nomeUsuario, senha, email, codigoCerto;
    BancoDados bancoDados;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cod_senha);

        TextView cancelar = findViewById(R.id.textViewCancelar);
        Button buttonValidarSenha = findViewById(R.id.buttonValidarSenhaw);
        TextView msgValidacao = findViewById(R.id.textViewMsgValidacao);

        bancoDados = new BancoDados(this);

        //pega os dados mandados por intent de outra activity
        int identificacao = getIntent().getExtras().getInt("identificador");
        nomeUsuario = getIntent().getExtras().getString("nome");
        senha = getIntent().getExtras().getString("senha");
        email = getIntent().getExtras().getString("email");
        codigoCerto = getIntent().getExtras().getString("codigo");

        String frase = "Código de validação enviado para " + email;
        msgValidacao.setText(frase);

        n1 = findViewById(R.id.editTextNumber);
        n2 = findViewById(R.id.editTextNumber2);
        n3 = findViewById(R.id.editTextNumber3);
        n4 = findViewById(R.id.editTextNumber4);

        addTextWatcher(n1, n2);
        addTextWatcher(n2, n3);
        addTextWatcher(n3, n4);

        cancelar.setOnClickListener(v -> {
            getOnBackPressedDispatcher();
            finish();
        });
        buttonValidarSenha.setOnClickListener(v -> {
            //ler o codigo digitado pelo usuário
            v1 = n1.getText().toString();
            v2 = n2.getText().toString();
            v3 = n3.getText().toString();
            v4 = n4.getText().toString();
            String codigoDigitado = v1+v2+v3+v4;
            if(codigoCerto.equals(codigoDigitado)) {
                if(identificacao == 0) {
                    Boolean cadastraUsuarioBD = bancoDados.cadastrarUsuario(nomeUsuario, senha, email);
                    if (cadastraUsuarioBD.equals(true)) {
                        carregarTelaLogin();
                    } else {
                        Toast.makeText(CodSenhaActivity.this, "Erro: Usuário não Registrado! ", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    carregarTelaNovaSenha();
                }
           }else{Toast.makeText(CodSenhaActivity.this, "Código Inválido!", Toast.LENGTH_SHORT).show();}
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
    private void addTextWatcher(final EditText currentEditText, final EditText nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    nextEditText.requestFocus();
                }
            }
        });
    }

    /**
     * Este método inicializa a tela de Login
     */
    private void carregarTelaLogin(){
        Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Este método inicializa a tela de Atualização de senha
     */
    private void carregarTelaNovaSenha(){
        Integer id_usuario = getIntent().getExtras().getInt("id_usuario");
        String nomeBD = bancoDados.pegaNome(id_usuario);
        Intent proxima = new Intent(this, AtualizarSenha.class);
        proxima.putExtra("id_usuario", id_usuario);
        proxima.putExtra("nome", nomeBD);
        proxima.putExtra("email", email);
        startActivity(proxima);
        finish();
    }
}