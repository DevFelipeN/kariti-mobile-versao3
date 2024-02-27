package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GabaritoActivity extends AppCompatActivity {
    TextView notaProva;
    Button cadProva;
    ImageButton voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gabarito);

        voltar = findViewById(R.id.imgBtnVoltar);
        cadProva = findViewById(R.id.btnCadProva);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cadProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaConfim();
            }
        });

        notaProva = findViewById(R.id.txtViewNotaProva);

        int quantidadeQuestoes = 10;
        int quantidadeAlternativas = 5;
        notaProva.setText("Nota total da prova " + quantidadeQuestoes + " pontos.");
        // Layout das questões
        LinearLayout layoutQuestoes = findViewById(R.id.layoutQuestoes);

        LinearLayout layoutAlternativas = new LinearLayout(this);
        layoutAlternativas.setOrientation(LinearLayout.HORIZONTAL);

        // Loop para criar as alternativas na primeira linha
        for (char letra = 'A'; letra < 'A' + quantidadeAlternativas; letra++) {
            TextView textViewAlternativa = new TextView(this);
            textViewAlternativa.setText(String.valueOf(letra));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(43, 0, 40, 0); // Adiciona margem esquerda e direita de 10 pixels
            textViewAlternativa.setLayoutParams(params); // Aplica os parâmetros de layout ao TextView
            textViewAlternativa.setGravity(Gravity.CENTER); // Centraliza o texto
            layoutAlternativas.addView(textViewAlternativa); // Adiciona a alternativa ao layout das alternativas

        }
        layoutQuestoes.addView(layoutAlternativas);

        //Questões e CheckBoxes
        for (int i = 0; i < quantidadeQuestoes; i++) {
            LinearLayout layoutQuestao = new LinearLayout(this);
            layoutQuestao.setOrientation(LinearLayout.HORIZONTAL);

            TextView textViewNumeroQuestao = new TextView(this);
            textViewNumeroQuestao.setText((i + 1) + " ");
            layoutQuestao.addView(textViewNumeroQuestao);

            // Loop para criar os CheckBoxes para as respostas
            for (int j = 0; j < quantidadeAlternativas; j++) {
                CheckBox checkBoxAlternativa = new CheckBox(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 0, 40, 0);
                checkBoxAlternativa.setLayoutParams(params);
                layoutQuestao.addView(checkBoxAlternativa); // Adicionar o CheckBox ao layout da questão
            }

            EditText editTextPontos = new EditText(this);
            editTextPontos.setText("1");
            layoutQuestao.addView(editTextPontos);

            // Adicionar layout da questão ao layout principal
            layoutQuestoes.addView(layoutQuestao);
        }




    }

    public void telaConfim() {
        Intent intent = new Intent(this, ProvaCadConfirActivity.class);
        startActivity(intent);
        finish();
    }
}