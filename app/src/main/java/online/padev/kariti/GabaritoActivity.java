package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import online.padev.kariti.utilities.Gabarito;
import online.padev.kariti.utilities.Prova;

public class GabaritoActivity extends AppCompatActivity {
    private TextView txtViewNotaProva, txtViewProva, txtViewTurma, txtViewData;
    private Button btnCadastrarProva;
    private ImageButton voltar, iconAjuda;
    private LinearLayout layoutHorizontal;
    private TextView titulo;
    private List<Float> notas = new ArrayList<>();
    private List<RadioGroup> listRadioGroups;
    private Map<Integer, Integer> alternativasEscolhidas;
    private List<Gabarito> gabarito = new ArrayList<>();

    private BancoDados bancoDados;
    private Prova dadosProva;

    int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gabarito);


        voltar = findViewById(R.id.imgBtnVoltaDescola);
        iconAjuda = findViewById(R.id.iconHelp);
        titulo = findViewById(R.id.toolbar_title);
        btnCadastrarProva = findViewById(R.id.btnCadProva);
        txtViewProva = findViewById(R.id.textViewProva);
        txtViewTurma = findViewById(R.id.textViewTurma);
        txtViewData = findViewById(R.id.textViewData);
        txtViewNotaProva = findViewById(R.id.txtViewNotaProva);
        layoutHorizontal = findViewById(R.id.layoutHorizontalAlternat);

        bancoDados = new BancoDados(this);
        dadosProva = new Prova();
        listRadioGroups = new ArrayList<>();
        alternativasEscolhidas = new HashMap<>();

        titulo.setText(String.format("%s","Gabarito"));

        dadosProva = (Prova) getIntent().getSerializableExtra("prova");

        if(dadosProva.getId_prova() != null){
            status = getIntent().getExtras().getInt("status");
            btnCadastrarProva.setText(String.format("%s","Salvar"));
        }

        txtViewProva.setText(String.format("Prova: %s", dadosProva.getNomeProva()));
        txtViewTurma.setText(String.format("Turma: %s", bancoDados.pegarNomeTurma(dadosProva.getId_turma().toString())));
        txtViewData.setText(String.format("Data: %s", dadosProva.dateToDisplay()));

        btnCadastrarProva.setOnClickListener(v -> {
            btnCadastrarProva.setEnabled(false);
            boolean respostaSelecionada = true;
            boolean respostasNotasPreenchidas = true;

            //Verica aqui se todas as respostas fora marcadas
            for (RadioGroup radioGroup : listRadioGroups) {
                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(GabaritoActivity.this, "Por favor, selecione uma resposta para todas as questões.", Toast.LENGTH_SHORT).show();
                    respostaSelecionada = false;
                    break;
                }
            }
            // Verifica se todos os campos de notas foram preenchidos
            for (int j = 0; j < layoutHorizontal.getChildCount(); j++) {
                LinearLayout questaoLayout = (LinearLayout) layoutHorizontal.getChildAt(j);
                EditText pontosEditText = (EditText) questaoLayout.getChildAt(2);
                String nt = pontosEditText.getText().toString();
                if (nt.isEmpty()) {
                    Toast.makeText(GabaritoActivity.this, "Por favor, preencha todas as notas para as questões.", Toast.LENGTH_SHORT).show();
                    respostasNotasPreenchidas = false;
                    break;
                }
            }

            if (respostaSelecionada && respostasNotasPreenchidas) { //Caso todas as alternativas forem marcadas e as notas adicionadas

                if (!notaFinal()){
                    btnCadastrarProva.setEnabled(true);
                    return;
                }
                if(!notas.isEmpty()){
                    for(int i = 1; i <= dadosProva.getNumQuestoes(); i++){
                        Integer resp = alternativasEscolhidas.get(i-1);
                        float notaQuestaoI = notas.get(i-1);
                        Log.e("notas","n1: "+notaQuestaoI);
                        Gabarito g = new Gabarito(i, resp + 1, notaQuestaoI);
                        gabarito.add(g);
                    }

                    if (dadosProva.getId_prova() == null){
                        if (bancoDados.cadastrarProva(dadosProva, gabarito)){
                            dialogProvaSucess("cadastrada");
                        } else {
                            avisoErroDeCadastro("no cadastro");
                        }
                    }else{
                        if (bancoDados.alterarDadosProva(dadosProva, gabarito, status)){
                            dialogProvaSucess("alterada");
                        }else{
                            avisoErroDeCadastro("na alteração");
                        }
                    }
                }else{
                    Toast.makeText(this, "Falha no sistema, tente novamente", Toast.LENGTH_SHORT).show();
                    btnCadastrarProva.setEnabled(true);
                }
            }else{
                btnCadastrarProva.setEnabled(true);
            }
       });

        int quantidadeQuestoes = dadosProva.getNumQuestoes();
        int quantidadeAlternativas = dadosProva.getNumAlternativas();
        txtViewNotaProva.setText(String.format("%s","Nota total da prova " + quantidadeQuestoes + " pontos."));

        String[] letras = new String[quantidadeAlternativas];
        for (int i = 0; i < quantidadeAlternativas; i++) {
            char letra = (char)('A' + i);
            letras[i] = String.valueOf(letra);
        }

        //Questões e Radio
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < quantidadeQuestoes; i++) {
            LinearLayout layoutQuestao = new LinearLayout(this);
            layoutQuestao.setOrientation(LinearLayout.HORIZONTAL);

            TextView textViewNumeroQuestao = new TextView(this);
            textViewNumeroQuestao.setText((i + 1) + " ");
            layoutQuestao.addView(textViewNumeroQuestao);

            //Agrupar os RadioButtons
            RadioGroup radioGroupAlternativas = new RadioGroup(this);
            radioGroupAlternativas.setOrientation(LinearLayout.HORIZONTAL);
            listRadioGroups.add(radioGroupAlternativas);

            // Loop para criar Radio para as respostas
            for (int j = 0; j < quantidadeAlternativas; j++) {
                params.setMargins(0, 20, 20, 0);

                RadioButton radioAlternativa = new RadioButton(this);
                radioAlternativa.setLayoutParams(params);
                radioAlternativa.setText(letras[j]);
                radioGroupAlternativas.addView(radioAlternativa);
            }
            radioGroupAlternativas.setOnCheckedChangeListener((group, checkedId) -> {
                for (int a = 0; a < listRadioGroups.size(); a++) {
                    if (listRadioGroups.get(a) == group) {
                        int positionDaQuestao = a;
                        int selectedRadioButtonId = group.getCheckedRadioButtonId();
                        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                        int position = group.indexOfChild(selectedRadioButton);
                        alternativasEscolhidas.put(positionDaQuestao, position);
                        break;
                    }
                }
            });
            layoutQuestao.addView(radioGroupAlternativas);

            LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(
                    150,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            EditText editTextPontos = new EditText(this);
            editTextPontos.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editTextPontos.setText(String.valueOf(1));
            editTextPontos.setGravity(Gravity.CENTER);
            editTextPontos.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            editTextPontos.setBackground(ContextCompat.getDrawable(this, R.drawable.borda_fina));
            paramsText.setMargins(5, 15, 0, 0);

            editTextPontos.setLayoutParams(paramsText);

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
                    calcularNotaAtual();
                }
            });

            layoutHorizontal.addView(layoutQuestao);
            calcularNotaAtual();

        }
        iconAjuda.setOnClickListener(v -> dialogHelpDetalhes());
        voltar.setOnClickListener(view -> avisoVoltar());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                avisoVoltar();
            }
        });

    }
    public void dialogProvaSucess(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(GabaritoActivity.this);
        builder.setTitle("Prova "+text+" com sucesso!")
                .setMessage("Selecione uma das opções a seguir, para ter acesso aos Cartões Resposta.")
                .setPositiveButton("OK", (dialog, which) -> {
                    baixarCartoes();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void avisoErroDeCadastro(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(GabaritoActivity.this);
        builder.setTitle("AVISO!")
                .setMessage("Falha "+text+" da prova, por favor tente novamente!")
                .setPositiveButton("Sair", (dialog, which) -> finish());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void baixarCartoes() {
        Intent intent = new Intent(this, ProvaCartoesActivity.class);
        intent.putExtra("prova", dadosProva.getNomeProva());
        intent.putExtra("id_turma", dadosProva.getId_turma());
        intent.putExtra("endereco", 1);
        startActivity(intent);
        finish();
    }
    private void calcularNotaAtual() {
        float notas = 0;
        for (int j = 0; j < layoutHorizontal.getChildCount(); j++) {
            LinearLayout questaoLayout = (LinearLayout) layoutHorizontal.getChildAt(j);
            EditText pontosEditText = (EditText) questaoLayout.getChildAt(2);
            String nota = pontosEditText.getText().toString();
            if(nota.isEmpty() || nota.charAt(0) == '.'){
                nota = "0"+nota;
            }
            float n = Float.parseFloat(nota);
            notas += n;
        }
        txtViewNotaProva.setText(String.format("%s %.2f %s","Nota total da prova", notas, "pontos."));
    }
    private boolean notaFinal() {
        try {
            for (int j = 0; j < layoutHorizontal.getChildCount(); j++) {
                LinearLayout questaoLayout = (LinearLayout) layoutHorizontal.getChildAt(j);
                EditText pontosEditText = (EditText) questaoLayout.getChildAt(2);
                String nota = pontosEditText.getText().toString();
                if (nota.isEmpty() || nota.charAt(0) == '.') {
                    nota = "0" + nota;
                }
                float n = Float.parseFloat(nota);
                Log.e("notas","n: "+n);
                notas.add(n);
            }
            return true;
        }catch (Exception e){
            Toast.makeText(this, "Falha no sistema, tente novamente", Toast.LENGTH_SHORT).show();
            Log.e("kariti", e.toString());
            return false;
        }
    }
    public void dialogHelpDetalhes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("olá, agora é hora de preencher o gabarito da sua prova.\n" +
                "• Marque as respostas correspondentes as questões da prova\n" +
                "• Informe o peso de cada questão nos campos sugeridos \n\n" +
                "• Antes de finalizar o cadastro confira todos os dados! ");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    public void avisoVoltar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(GabaritoActivity.this);
        builder.setTitle("ATENÇÃO!")
                .setMessage("Ao confirmar essa ação, os dados dessa prova serão perdidos!\n\n" +
                        "Deseja realmente voltar?")
                .setPositiveButton("SIM", (dialog, which) -> finish())
                .setNegativeButton("NÃO", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
