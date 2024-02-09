package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

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
    Button alterar, voltar;
    BancoDados bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar_senha);

        editTextNome = (EditText) findViewById(R.id.editTextNomeCad);
        editTextEmail = (EditText) findViewById(R.id.editTextEmailCad);
        novaSenha = (EditText) findViewById(R.id.editTextPasswordNova);
        confNovaSenha = (EditText) findViewById(R.id.editTextConfirmPasswordNova);
        alterar = (Button) findViewById(R.id.buttonAlterar);
        voltar = (Button) findViewById(R.id.buttonVoltar);

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
    }
}