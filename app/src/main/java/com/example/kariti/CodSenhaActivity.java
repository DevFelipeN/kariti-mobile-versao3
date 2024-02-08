package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CodSenhaActivity extends AppCompatActivity {
    EditText codNum1, codNum2, codNum3, codNum4;
    Button buttonValidarSenha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cod_senha);

        buttonValidarSenha = findViewById(R.id.buttonValidarSenhaw);
        codNum1 = findViewById(R.id.editTextNum1);
        codNum2 = findViewById(R.id.editTextNum2);
        codNum3 = findViewById(R.id.editTextNum3);
        codNum4 = findViewById(R.id.editTextNum4);

        addTextWatcher(codNum1, codNum2);
        addTextWatcher(codNum2, codNum3);
        addTextWatcher(codNum3, codNum4);

        buttonValidarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mudarParaTelaLogin();
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

    public void mudarParaTelaLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}