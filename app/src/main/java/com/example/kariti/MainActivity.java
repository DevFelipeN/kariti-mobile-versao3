package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    EditText nome, email, senha, confirmarSenha;
    Button voltar, cadastro;
    ImageButton mostrarSenha, ocultarSenha, ocultarSenha2;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nome = findViewById(R.id.editTextNome);
        email = findViewById(R.id.editTextEmail);
        senha = findViewById(R.id.editTextPassword);
        confirmarSenha = findViewById(R.id.editTextConfirmPassword);
        voltar = findViewById(R.id.buttonVoltar);
        cadastro = findViewById(R.id.buttonCadastrar);

<<<<<<< HEAD
=======
        bancoDados = new BancoDados(this); //--Conectando ao banco de dados

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
                    if(password.equals(repassword)){
                        Boolean checkuser = bancoDados.checkuser(usernome);
                        Boolean checkemail = bancoDados.checkemail(emails);
                        if(checkuser==false && checkemail==false){
                            Boolean insert = bancoDados.insertData(usernome, password, emails);
                            if(insert==true){
                                Toast.makeText(MainActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(MainActivity.this, "Usuário não Registrado! ",Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "Usuário já cadastrado, favor realizar Login", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(MainActivity.this, "Senhas não são iguais nos dois campos! ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

>>>>>>> e9a3947cb742eadcbe75f010dac36271213b0e2d
        ocultarSenha = findViewById(R.id.senhaoculta);
        senha.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        ocultarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Verifica se a senha está visivel ou oculta.
                if(senha.getInputType() == InputType.TYPE_NUMBER_VARIATION_PASSWORD){
//                  Se a senha está visivel ou oculta.
                    senha.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    ocultarSenha.setImageResource(R.mipmap.senhaoff);
                } else {
                    senha.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    ocultarSenha.setImageResource(R.mipmap.senhaon);
                }
                senha.setSelection(senha.getText().length());
            }
        });
        ocultarSenha2 = findViewById(R.id.imgButtonSenhaOFF);
        confirmarSenha.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        ocultarSenha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Verifica se a senha está visivel ou oculta.
                if(confirmarSenha.getInputType() == InputType.TYPE_NUMBER_VARIATION_PASSWORD){
//                  Se a senha está visivel ou oculta.
                    confirmarSenha.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    ocultarSenha2.setImageResource(R.mipmap.senhaoff);
                } else {
                    confirmarSenha.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    ocultarSenha2.setImageResource(R.mipmap.senhaon);
                }
                confirmarSenha.setSelection(confirmarSenha.getText().length());
            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voltarTelaWelcome();
            }
        });
        /*cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaSenha();
            }
        });*/
    }
    public void voltarTelaWelcome(){
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }
    public void mudarParaTelaSenha(){
        Intent intent = new Intent(this, CodSenhaActivity.class);
        startActivity(intent);
    }
}