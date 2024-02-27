package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CadProvaActivity extends AppCompatActivity {
    private Button datePickerButton;
    private Calendar calendar;
    Button btnGerProva;
    ImageButton voltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_prova);

        datePickerButton = findViewById(R.id.datePickerButton);
        voltar = findViewById(R.id.imgBtnVoltar);
        btnGerProva = findViewById(R.id.btnGerarProva);

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnGerProva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                telaGabarito();
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