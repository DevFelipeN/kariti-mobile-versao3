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
    EditText nome, email, sexo, cpf, senha, confirmSenha;
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
        confirmSenha = findViewById(R.id.editTextConfirmPassword);
        voltar = findViewById(R.id.buttonVoltar);
        cadastro = findViewById(R.id.buttonCadastrar);

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
        confirmSenha.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        ocultarSenha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Verifica se a senha está visivel ou oculta.
                if(confirmSenha.getInputType() == InputType.TYPE_NUMBER_VARIATION_PASSWORD){
//                  Se a senha está visivel ou oculta.
                    confirmSenha.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    ocultarSenha2.setImageResource(R.mipmap.senhaoff);
                } else {
                    confirmSenha.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    ocultarSenha2.setImageResource(R.mipmap.senhaon);
                }
                confirmSenha.setSelection(confirmSenha.getText().length());
            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voltarTelaWelcome();
            }
        });
        cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaSenha();
            }
        });
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