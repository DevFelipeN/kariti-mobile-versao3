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
import android.widget.Toast;

public class AtualizarSenha extends AppCompatActivity {

    EditText editTextNome, editTextEmail, novaSenha, confNovaSenha;
    Button alterar;
    BancoDados bancoDados;
    ImageButton ocultarSenha, ocultarSenha2, voltar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar_senha);

        editTextNome = (EditText) findViewById(R.id.editTextNome);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        novaSenha = (EditText) findViewById(R.id.editTextNovaSenha);
        confNovaSenha = (EditText) findViewById(R.id.editTextConfirmNovaSenha);
        alterar = (Button) findViewById(R.id.buttonAlterar);
        voltar = (ImageButton) findViewById(R.id.btn_voltar_left);

        Integer id = getIntent().getExtras().getInt("id");
        String nome = getIntent().getExtras().getString("nome");
        String emails = getIntent().getExtras().getString("email");
        editTextNome.setText(nome);
        editTextEmail.setText(emails);

        bancoDados = new BancoDados(this);

        alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String senha = novaSenha.getText().toString();
                Boolean alterarSenha = bancoDados.upadateSenha(senha, id);
                if (alterarSenha==true) {
                    Toast.makeText(AtualizarSenha.this, "Senha Alterada Com Sucesso!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
            }
        });
        ocultarSenha = findViewById(R.id.senhaoculta);
        novaSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ocultarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Verifica se a senha está visivel ou oculta.
                if(novaSenha.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
//                  Se a senha está visivel ou oculta.
                    novaSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ocultarSenha.setImageResource(R.mipmap.senhaoff);
                } else {
                    novaSenha.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ocultarSenha.setImageResource(R.mipmap.senhaon);
                }
                novaSenha.setSelection(novaSenha.getText().length());
            }
        });
        ocultarSenha2 = findViewById(R.id.imgButtonSenhaOFF);
        confNovaSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        ocultarSenha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Verifica se a senha está visivel ou oculta.
                if(confNovaSenha.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
//                  Se a senha está visivel ou oculta.
                    confNovaSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ocultarSenha2.setImageResource(R.mipmap.senhaoff);
                } else {
                    confNovaSenha.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ocultarSenha2.setImageResource(R.mipmap.senhaon);
                }
                confNovaSenha.setSelection(confNovaSenha.getText().length());
            }
        });
    }
}