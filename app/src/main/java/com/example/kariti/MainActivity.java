package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText nome, email, senha, confirmarSenha;
    Button cadastro;
    ImageButton ocultarSenha, ocultarSenha2, voltar;

    BancoDados bancoDados;
    EnviarEmail enviarEmail;
    GerarCodigoValidacao gerarCodigo;
    CodSenhaActivity codSenha;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nome = findViewById(R.id.editTextNome);
        email = findViewById(R.id.editTextEmail);
        senha = findViewById(R.id.editTextNovaSenha);
        confirmarSenha = findViewById(R.id.editTextConfirmNovaSenha);
        voltar = findViewById(R.id.imgBtnVoltaEscola);
        cadastro = findViewById(R.id.buttonSalvarEdit);

        bancoDados = new BancoDados(this);
        enviarEmail = new EnviarEmail();
        gerarCodigo = new GerarCodigoValidacao();
        codSenha = new CodSenhaActivity();


        cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernome = nome.getText().toString();
                String emails = email.getText().toString();
                String password = senha.getText().toString();
                String repassword = confirmarSenha.getText().toString();

                if(usernome.equals("")||password.equals("")||repassword.equals("")||emails.equals(""))
                    Toast.makeText(MainActivity.this, "Por favor preencher todos os campos!", Toast.LENGTH_SHORT).show();
                else{
                    if(!emails.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emails).matches())
                        if (password.equals(repassword)) {
                            Boolean checkuserMail = bancoDados.checkNome(usernome, emails);
                            if (checkuserMail == false) {
                                String cod = gerarCodigo.gerarVerificador();
                                Boolean mandaEmail = enviarEmail.enviaCodigo(emails, cod);
                                if (mandaEmail == true) {
                                    Intent proxima = new Intent(getApplicationContext(), CodSenhaActivity.class);
                                    proxima.putExtra("identificador", 0);
                                    proxima.putExtra("nome", usernome);
                                    proxima.putExtra("email", emails);
                                    proxima.putExtra("senha", password);
                                    proxima.putExtra("cod", cod);
                                    startActivity(proxima);
                                    finish();
                                } else {Toast.makeText(MainActivity.this, "Email não Enviado", Toast.LENGTH_SHORT).show();}
                            } else {Toast.makeText(MainActivity.this, "Usuário já existe!", Toast.LENGTH_SHORT).show();}
                        } else {Toast.makeText(MainActivity.this, "Senhas Divergentes!", Toast.LENGTH_SHORT).show();}
                    else{Toast.makeText(MainActivity.this, "E-mail Inválido!", Toast.LENGTH_SHORT).show();}
                }
            }
        });

        ocultarSenha = findViewById(R.id.senhaoculta);
        senha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ocultarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Verifica se a senha está visivel ou oculta.
                if(senha.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
//                  Se a senha está visivel ou oculta.
                    senha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ocultarSenha.setImageResource(R.mipmap.senhaoff);
                } else {
                    senha.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ocultarSenha.setImageResource(R.mipmap.senhaon);
                }
                senha.setSelection(senha.getText().length());
            }
        });
        ocultarSenha2 = findViewById(R.id.imgButtonSenhaOFF);
        confirmarSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ocultarSenha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Verifica se a senha está visivel ou oculta.
                if(confirmarSenha.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
//                  Se a senha está visivel ou oculta.
                    confirmarSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ocultarSenha2.setImageResource(R.mipmap.senhaoff);
                } else {
                    confirmarSenha.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ocultarSenha2.setImageResource(R.mipmap.senhaon);
                }
                confirmarSenha.setSelection(confirmarSenha.getText().length());
            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}