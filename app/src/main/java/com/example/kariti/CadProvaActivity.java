package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CadProvaActivity extends AppCompatActivity {
    private Button datePickerButton;
    private Calendar calendar;
    EditText nomeProva;
    TextView qtdQuest, qtdAlter;
    Button btnGerProva;
    ImageButton voltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_prova);

        datePickerButton = findViewById(R.id.datePickerButton);
        voltar = findViewById(R.id.imgBtnVoltar);
        btnGerProva = findViewById(R.id.btnGerarProva);
        nomeProva = findViewById(R.id.editTextNomeProva);
        qtdQuest = findViewById(R.id.textViewQuantity);
        qtdAlter = findViewById(R.id.textVieAlter);


        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnGerProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prova = nomeProva.getText().toString();
                String quest = qtdQuest.getText().toString();
                String alter = qtdAlter.getText().toString();
                if (!prova.equals("")){
                    Toast.makeText(CadProvaActivity.this, "Questão: "+quest, Toast.LENGTH_SHORT).show();
                    Toast.makeText(CadProvaActivity.this, "Alternativa: "+alter, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CadProvaActivity.this, "Informe o nome da prova!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Obtém a instância do calendário com a data atual
        calendar = Calendar.getInstance();

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cria um DatePickerDialog com a data atual
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CadProvaActivity.this,
                        (view, year, monthOfYear, dayOfMonth) -> {
                            // Atualiza a data no calendário quando o usuário seleciona uma nova data
                            calendar.set(year, monthOfYear, dayOfMonth);
                            // Atualiza o texto do botão com a data selecionada
                            datePickerButton.setText(formatDate(calendar));
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );

                // Exibe o DatePickerDialog
                datePickerDialog.show();
            }
        });
    }

    private String formatDate(Calendar calendar) {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return simpleDateFormat.format(calendar.getTime());
    }

    public void telaGabarito() {
        Intent intent = new Intent(this, GabaritoActivity.class);
        startActivity(intent);
        finish();
    }
}