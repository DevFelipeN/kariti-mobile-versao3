package com.example.kariti;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class VisualProvaCorrigidaActivity extends AppCompatActivity {
    ImageButton voltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_prova_corrigida);

        voltar = findViewById(R.id.imgBtnVoltarDcorrecao);
        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TableLayout tableLayout = findViewById(R.id.tableLayout);
        TableRow row = new TableRow(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(layoutParams);

        // Cria uma célula para a nova linha
        TextView cell1 = new TextView(this);
        cell1.setText("Romulo");
        cell1.setGravity(Gravity.CENTER);
        row.addView(cell1);

        // Cria outra célula para a nova linha
        TextView cell2 = new TextView(this);
        cell2.setText("6");
        cell2.setGravity(Gravity.CENTER);
        row.addView(cell2);

        TextView cell3 = new TextView(this);
        cell3.setText("6");
        cell3.setGravity(Gravity.CENTER);
        row.addView(cell3);

        // Adiciona a nova linha à tabela
        tableLayout.addView(row);
    }
    public void PopMenu(View v){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(VisualProvaCorrigidaActivity.this, "Preparado para implementação", Toast.LENGTH_SHORT).show();
            }
        });
    }
}