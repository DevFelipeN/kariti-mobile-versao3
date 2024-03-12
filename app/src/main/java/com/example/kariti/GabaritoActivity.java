package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Format;
import java.util.ArrayList;

public class GabaritoActivity extends AppCompatActivity {
    TextView notaProva, nProva,nturma, ndata;
    Button cadProva;

    ImageButton voltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gabarito);

        voltar = findViewById(R.id.imgBtnVoltar);
        cadProva = findViewById(R.id.btnCadProva);
        nProva = findViewById(R.id.textViewProva);
        nturma = findViewById(R.id.textViewTurma);
        ndata = findViewById(R.id.textViewData);

        String prova = getIntent().getExtras().getString("nomeProva");
        String data = getIntent().getExtras().getString("data");
        Integer quest = getIntent().getExtras().getInt("quest");
        Integer alter = getIntent().getExtras().getInt("alter");
        nProva.setText(String.format("Prova: %s", prova));
        nturma.setText("Turma: "+"Turma teste 123");
        ndata.setText("Data: "+data);


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

       //sayury
       notaProva = findViewById(R.id.txtViewNotaProva);
        int quantidadeQuestoes = quest;
        int quantidadeAlternativas = alter;
        LinearLayout layoutQuestoesGabarito = findViewById(R.id.layoutQuestoes); // Layout das questões
        LinearLayout layoutAlternativas = findViewById(R.id.layoutDasAlternativas); // Layout das alternativas

        // Loop para criar as alternativas na primeira linha
        for (char letra = 'A'; letra <  'A' + quantidadeAlternativas; letra++) {
            TextView textViewAlternativa = new TextView(this);
            textViewAlternativa.setText(String.valueOf(letra));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(42, 0, 42, 0);

            textViewAlternativa.setLayoutParams(params); // Aplica os parâmetros de layout ao TextView
            textViewAlternativa.setGravity(Gravity.CENTER); // Centraliza o texto
            layoutAlternativas.addView(textViewAlternativa); // Adiciona a alternativa ao layout das alternativas

        }
        TextView TextNota = new TextView(this);
        TextNota.setText("Nota");
        layoutAlternativas.addView(TextNota);

        //Questões e Radio
        for (int i = 0; i < quantidadeQuestoes; i++) {
            LinearLayout layoutQuestao = new LinearLayout(this);
            layoutQuestao.setOrientation(LinearLayout.HORIZONTAL);

            TextView textViewNumeroQuestao = new TextView(this);
            textViewNumeroQuestao.setText((i + 1) + " ");
            layoutQuestao.addView(textViewNumeroQuestao);

            //Agrupar os RadioButtons
            RadioGroup radioGroupAlternativas = new RadioGroup(this);
            radioGroupAlternativas.setOrientation(LinearLayout.HORIZONTAL);

            // Loop para criar Radio para as respostas
            for (int j = 0; j < quantidadeAlternativas; j++) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 25, 40, 0);

                RadioButton radioAlternativa = new RadioButton(this);
                radioAlternativa.setLayoutParams(params);
                radioGroupAlternativas.addView(radioAlternativa);
//                layoutQuestao.addView(radioAlternativa);
            }

            layoutQuestao.addView(radioGroupAlternativas);

            EditText editTextPontos = new EditText(this);
            editTextPontos.setText("1");
            layoutQuestao.addView(editTextPontos);

            editTextPontos.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int notas = 0;
                    //modificado


                    for (int j = 0; j < layoutQuestoesGabarito.getChildCount(); j++) {
                        LinearLayout questaoLayout = (LinearLayout) layoutQuestoesGabarito.getChildAt(j);
                        EditText pontosEditText = (EditText) questaoLayout.getChildAt(2);
                        String nt = pontosEditText.getText().toString();
                        if (!nt.isEmpty()) {
                            Integer n = Integer.valueOf(nt);
                            notas += n;
                        }
                    }
                    notaProva.setText("Nota total da prova " + notas + " pontos.");
                }
            });
            layoutQuestoesGabarito.addView(layoutQuestao);
        }

        // Calcular a nota inicial
        int notas = 0;
        for (int i = 0; i < layoutQuestoesGabarito.getChildCount(); i++) {
            LinearLayout questaoLayout = (LinearLayout) layoutQuestoesGabarito.getChildAt(i);
            EditText pontosEditText = (EditText) questaoLayout.getChildAt(2);
            String nt = pontosEditText.getText().toString();
            if (!nt.isEmpty()) {
                Integer n = Integer.valueOf(nt);
                notas += n;
            }
        }
        notaProva.setText("Nota total da prova " + notas + " pontos.");
    }

    public void telaConfim() {
        Intent intent = new Intent(this, ProvaCadConfirActivity.class);
        startActivity(intent);
    }
}
//            String nt = editTextPontos.getText().toString();
//            Integer n = Integer.valueOf(nt);
//            notas = notas + n;
//
//
//            //ArrayList<String> notaQuest = new ArrayList<String>();
//
//
//
//            Toast.makeText(this, "Valor: "+n, Toast.LENGTH_SHORT).show();
//
//            // Adicionar layout da questão ao layout principal
//            layoutQuestoes.addView(layoutQuestao);
//            //notaQuest.add();
//        }
//
//        notaProva.setText("Nota total da prova " + notas + " pontos.");
//
//
//
//
//    }

//    public void telaConfim() {
//        Intent intent = new Intent(this, ProvaCadConfirActivity.class);
//        startActivity(intent);
//        //finish();
//    }
//}