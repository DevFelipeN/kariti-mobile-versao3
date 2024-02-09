package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CodSenhaActivity extends AppCompatActivity {
    EditText n1, n2, n3, n4;
    Button buttonValidarSenha;
    BancoDados bancoDados;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cod_senha);

        n1 = (EditText) findViewById(R.id.editTextNumber);
        n2 = (EditText) findViewById(R.id.editTextNumber2);
        n3 = (EditText) findViewById(R.id.editTextNumber3);
        n4 = (EditText) findViewById(R.id.editTextNumber4);

        addTextWatcher(n1, n2);
        addTextWatcher(n2, n3);
        addTextWatcher(n3, n4);

        bancoDados = new BancoDados(this);

        buttonValidarSenha = findViewById(R.id.buttonValidarSenhaw);
        buttonValidarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String v1 = n1.getText().toString();
                String v2 = n2.getText().toString();
                String v3 = n3.getText().toString();
                String v4 = n4.getText().toString();
                String codigitado = v1+v2+v3+v4;
                String identificacao = getIntent().getExtras().getString("identificador");
                String usernome = getIntent().getExtras().getString("nome");
                String password = getIntent().getExtras().getString("senha");
                String emails = getIntent().getExtras().getString("email");
                String codorigin = getIntent().getExtras().getString("cod");
                if(codigitado.equals(codorigin)) {
                    if(identificacao=="0") {
                        Boolean insert = bancoDados.insertData(usernome, password, emails);
                        if (insert == true) {
                            Toast.makeText(CodSenhaActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(CodSenhaActivity.this, "Erro: Usuário não Registrado! ", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Integer id = getIntent().getExtras().getInt("id");
                        String ids = Integer.toString(id);
                        String nome = bancoDados.pegaNome(ids);
                        Intent proxima = new Intent(getApplicationContext(), AtualizarSenha.class);
                        proxima.putExtra("id", id);
                        proxima.putExtra("nome", nome);
                        proxima.putExtra("email", emails);
                        startActivity(proxima);
                    }
               }else{Toast.makeText(CodSenhaActivity.this, "Código Inválido!", Toast.LENGTH_SHORT).show();}
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
}