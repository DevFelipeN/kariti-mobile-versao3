package online.padev.kariti;

import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Objects;

public class DetalheCorrecao extends AppCompatActivity {

    ImageButton voltar;
    String nomeAluno, status;
    Integer id_aluno, id_prova, qtdQuestoes;
    BancoDados bancoDados;
    TextView alunoDetalhe, notaTotal;
    ArrayList<String> respostasDadas, gabarito, peso;
    TextView titulo;
    float nota = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_correcao);

        voltar = findViewById(R.id.imgBtnVoltar);
        alunoDetalhe  = findViewById(R.id.textViewDetalheAluno);
        notaTotal = findViewById(R.id.textViewNotaTotalDetalhe);
        titulo = findViewById(R.id.toolbar_title);
        bancoDados = new BancoDados(this);

        titulo.setText(String.format("%s","Detalhes"));

        id_aluno = Objects.requireNonNull(getIntent().getExtras()).getInt("id_aluno");
        nomeAluno = bancoDados.pegaNomeParaDetalhe(id_aluno.toString());
        id_prova = getIntent().getExtras().getInt("id_prova");
        qtdQuestoes = bancoDados.pegaqtdQuestoes(id_prova.toString());

        alunoDetalhe.setText(nomeAluno);
        //Carrega todas as respostas ordenadas por questao
        respostasDadas = (ArrayList<String>) bancoDados.respostasDadas(id_prova, id_aluno);
        Log.e("kariti", "respostasDadas - "+respostasDadas);
        gabarito = (ArrayList<String>) bancoDados.carregaGabarito(id_prova);
        peso = (ArrayList<String>) bancoDados.listNotaPorQuetao(id_prova);
        Log.e("kariti", "passei aqui");

        ShapeDrawable border = new ShapeDrawable(new RectShape());
        border.getPaint().setColor(0xFF000000); // Cor da borda
        border.getPaint().setStrokeWidth(1); // Largura da borda
        border.getPaint().setStyle(Paint.Style.STROKE);

        for(int x = 1; x <= qtdQuestoes; x++) {
            Integer respostaDada = bancoDados.pegaRespostaDada(id_prova, id_aluno, x);
            Log.e("kariti", "respostaDada = "+respostaDada);
            Integer respostaGabarito = bancoDados.pegaRespostaQuestao(id_prova, x);
            if(respostaGabarito.equals(respostaDada)){
                nota += bancoDados.pegaNotaQuestao(id_prova, x);
            }
            Log.e("kariti", "Questao = "+x);

            TableLayout tableLayout = findViewById(R.id.tableLayoutDetalheCorrecao);
            TableRow row = new TableRow(this);
            row.setBackground(border);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);

            // Cria uma célula para a nova linha para armazenar a questão
            TextView cell1 = new TextView(this);
            cell1.setText(String.valueOf(x));
            cell1.setGravity(Gravity.CENTER);
            cell1.setTextSize(16);
            row.addView(cell1);

            // Cria outra célula para a nova linha para armazenar a resposta marcada pelo aluno
            TextView cell2 = new TextView(this);
            if(respostaDada == null){
                respostasDadas.add(x-1, "-");
            }
            cell2.setText(respostasDadas.get(x-1));
            cell2.setGravity(Gravity.CENTER);
            cell2.setTextSize(16);
            row.addView(cell2);

            // Cria uma célula para a nova linha para armazenar a resposta do gabarito
            TextView cell3 = new TextView(this);
            cell3.setText(gabarito.get(x-1));
            cell3.setGravity(Gravity.CENTER);
            cell3.setTextSize(16);
            row.addView(cell3);

            // Cria outra célula para a nova linha para armazenar o status de acertos do aluno
            TextView cell4 = new TextView(this);
            if(respostasDadas.get(x-1).equals(gabarito.get(x-1))){
                status = "CERTA";
            }else{
                status = "ERRADA";
            }
            cell4.setText(status);
            cell4.setTextSize(14);
            cell4.setGravity(Gravity.CENTER);
            row.addView(cell4);

            // Cria outra célula para a nova linha para armazenar o peso da questão
            TextView cell5 = new TextView(this);
            cell5.setText(peso.get(x-1));
            cell5.setGravity(Gravity.CENTER);
            cell5.setTextSize(16);
            row.addView(cell5);

            // Adiciona a nova linha à tabela
            tableLayout.addView(row);
        }
        notaTotal.setText(String.format("Nota total obtida: %s pontos", nota));

        voltar.setOnClickListener(view -> {
            getOnBackPressedDispatcher();
            finish();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });


    }
}