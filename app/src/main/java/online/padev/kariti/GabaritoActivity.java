package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import online.padev.kariti.correction.CoreKariti;
import online.padev.kariti.entity.Gabarito;
import online.padev.kariti.entity.Prova;
import online.padev.kariti.database.DataBaseKariti;

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

    private DataBaseKariti dataBaseKariti;
    private Prova dadosProva;
    private String direcion;

    private int statusEdition, typeMessage;

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

        dataBaseKariti = new DataBaseKariti(this);
        dadosProva = new Prova();
        listRadioGroups = new ArrayList<>();
        alternativasEscolhidas = new HashMap<>();

        titulo.setText(String.format("%s","Gabarito"));

        dadosProva = (Prova) getIntent().getSerializableExtra("prova");
        direcion = getIntent().getExtras().getString("direcao");


        if(dadosProva.getId_prova() != null && !dadosProva.getId_prova().equals(0)){
            statusEdition = getIntent().getExtras().getInt("status");
            btnCadastrarProva.setText(String.format("%s","Salvar"));
        }

        if (!direcion.equals("cardDefault")) {
            txtViewProva.setText(String.format("Prova: %s", dadosProva.getNameProva()));
            txtViewTurma.setText(String.format("Turma: %s", dataBaseKariti.pegarNomeTurma(dadosProva.getId_class().toString())));
            txtViewData.setText(String.format("Data: %s", dadosProva.dateToDisplay()));
        } else {
            txtViewProva.setVisibility(View.GONE);
            txtViewData.setVisibility(View.GONE);
            txtViewTurma.setVisibility(View.GONE);
            btnCadastrarProva.setText(String.format("%s","Salvar"));
            typeMessage = getIntent().getExtras().getInt("typeMessage");
            if (typeMessage == 4){ // indica que já existe um gabarito default cadastrado e que um novo deve ser cadastrado para prova rápida
                differentCardNotice();
            }
            if (typeMessage == 5){
                newGabaritoDefault();
            }
        }

        btnCadastrarProva.setOnClickListener(v -> {
            btnCadastrarProva.setEnabled(false);
            boolean respostaSelecionada = true;
            boolean respostasNotasPreenchidas = true;

            try {

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
                    if (nt.isEmpty() || nt.trim().equals(".")) {
                        Toast.makeText(GabaritoActivity.this, "Por favor, preencha todas as notas para as questões.", Toast.LENGTH_SHORT).show();
                        respostasNotasPreenchidas = false;
                        break;
                    }
                }

                if (respostaSelecionada && respostasNotasPreenchidas) { //Caso todas as alternativas forem marcadas e as notas adicionadas

                    if (!notaFinal()) {
                        btnCadastrarProva.setEnabled(true);
                        return;
                    }
                    if (!notas.isEmpty()) {
                        for (int i = 1; i <= dadosProva.getNumQuestions(); i++) {
                            Integer resp = alternativasEscolhidas.get(i - 1);
                            float notaQuestaoI = notas.get(i - 1);
                            Log.e("notas", "n1: " + notaQuestaoI);
                            Gabarito g = new Gabarito(i, resp + 1, notaQuestaoI);
                            gabarito.add(g);
                        }

                        if (dadosProva.getId_prova() == null) {
                            if (dataBaseKariti.cadastrarProva(dadosProva, gabarito)) {
                                dialogProvaSucess("cadastrada");
                            } else {
                                avisoErroDeCadastro("no cadastro");
                            }
                        } else if (!dadosProva.getId_prova().equals(0)) {
                            if (dataBaseKariti.alterarDadosProva(dadosProva, gabarito, statusEdition)) {
                                dialogProvaSucess("alterada");
                            } else {
                                avisoErroDeCadastro("na alteração");
                            }
                        } else { // Entra nessa estrutura quando o gabarito pertencer a uma prova rápida
                            Gabarito.gabaritoDefault = gabarito;
                            Prova.numQuestsDefault = dadosProva.getNumQuestions();
                            Prova.numAlternativesDefault = dadosProva.getNumAlternatives();
                            dialogHelpCorrectDefault();
                        }
                    } else {
                        Toast.makeText(this, "Falha no sistema, tente novamente", Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (Exception e){
                Log.e("kariti", e.toString());
                Toast.makeText(this, "Falha no sistema, tente novamente", Toast.LENGTH_SHORT).show();
            }finally {
                btnCadastrarProva.setEnabled(true);
            }
       });

        int quantidadeQuestoes = dadosProva.getNumQuestions();
        int quantidadeAlternativas = dadosProva.getNumAlternatives();
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
    private void dialogProvaSucess(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(GabaritoActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Prova "+text+" com sucesso!")
                .setMessage("Você pode realizar o download na tela a seguir ou em outro momento pelo menu inicial de provas na opção 'Gerar Cartões'.")
                .setPositiveButton("OK", (dialog, which) -> {
                    generatCards();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void avisoErroDeCadastro(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(GabaritoActivity.this);
        builder.setCancelable(false);
        builder.setTitle("AVISO!")
                .setMessage("Falha "+text+" da prova, por favor tente novamente!")
                .setPositiveButton("Sair", (dialog, which) -> finish());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void generatCards() {
        Intent intent = new Intent(this, ProvaGenerateCardRegisteredActivity.class);
        intent.putExtra("prova", dadosProva.getNameProva());
        intent.putExtra("id_turma", dadosProva.getId_class());
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
    private void dialogHelpDetalhes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajuda");
        builder.setMessage("olá, agora é hora de preencher o gabarito da sua prova.\n" +
                "• Marque as respostas correspondentes as questões da prova\n" +
                "• Informe o peso de cada questão nos campos sugeridos \n\n" +
                "• Antes de finalizar o cadastro confira todos os dados! ");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void avisoVoltar(){
        if (!direcion.equals("cardDefault")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(GabaritoActivity.this);
            builder.setTitle("ATENÇÃO!")
                    .setMessage("Ao confirmar essa ação, os dados dessa prova serão perdidos!\n\n" +
                            "Deseja realmente voltar?")
                    .setPositiveButton("SIM", (dialog, which) -> finish())
                    .setNegativeButton("NÃO", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            if (Gabarito.gabaritoDefault != null && !Gabarito.gabaritoDefault.isEmpty()){
                Toast.makeText(this, "Seu gabarito anterior ainda foi mantido!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                finish();
            }
        }
    }
    private void dialogHelpCorrectDefault() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Gabarito preenchido");
        builder.setMessage("Agora você pode realizar a correção de todas as provas que se aplicam a esse gabarito!");
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            correctFirstDefault();
        });
        builder.show();
    }
    private void startCamera(){
        Intent intent = new Intent(this, CameraxAndOpencv.class);
        startActivity(intent);
        finish();
    }
    private void differentCardNotice(){
        View overlayView = findViewById(R.id.overlayView);
        overlayView.setVisibility(View.VISIBLE);
        btnCadastrarProva.setVisibility(View.GONE);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("ATENÇÃO!")
                .setMessage("Este cartão é diferente do modelo de gabarito que você tem criado!\n\n" +
                        "Para corrigir este cartão, você deve criar outro gabarito referente a esse modelo de cartão.")
                .setPositiveButton("OK", (dialog, which) -> {
                    overlayView.setVisibility(View.GONE);
                    btnCadastrarProva.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                })

                .setNegativeButton("Cancelar", (dialog, which) -> startCamera());
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void newGabaritoDefault(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Caro(a) professor(a)")
                .setMessage("Antes de iniciar a correção, por favor, preencha o gabarito da(s) prova(s) que deseja corrigir!\n\n" +
                        "Você só precisa preencher o gabarito uma vez para corrigir todas as suas provas.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())

                .setNegativeButton("Cancelar", (dialog, which) -> finish());
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void correctFirstDefault(){
        try {
            String gabaritoDefault = "";
            for (Gabarito g : Gabarito.gabaritoDefault) {
                gabaritoDefault += g.getResponse();
            }
            String filePath = getIntent().getExtras().getString("filePath");
            if (filePath == null || gabaritoDefault.isEmpty()) {
                startCamera();
            }
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            Mat matWarp = new Mat();
            org.opencv.android.Utils.bitmapToMat(bitmap, matWarp);
            if (matWarp.channels() != 3) {
                Imgproc.cvtColor(matWarp, matWarp, Imgproc.COLOR_RGBA2RGB);
            }
            //Versão 3
            HashMap<Integer, Integer> correction;
            CoreKariti core = new CoreKariti(matWarp, dadosProva, gabaritoDefault);
            correction = core.correctCard(); // Versão 3: corrigindo com o Kariti Mobile
            if (correction != null && !correction.isEmpty()){
                deleteAllImages();
                Bitmap imgWarp = matToBitmap(matWarp);
                String nameCartao = "first_"+dadosProva.getNumQuestions()+"_"+dadosProva.getNumAlternatives();
                String filePathPaint = saveBitmapAndGetPath(imgWarp, nameCartao); //Salva a imagem cortada
                startViewImageDefault(correction, gabaritoDefault, filePathPaint);
            } else {
                startCamera();
            }
        } catch (Exception e) {
            Log.e("kariti", e.toString());
            startCamera();
        }
    }
    private void startViewImageDefault(HashMap<Integer, Integer> correction, String gabaritoDefault, String filePathPaint){
        try {
            Intent intent = new Intent(this, ViewCardCorrectedActivity.class);
            intent.putExtra("filePath", filePathPaint);
            intent.putExtra("gabarito", gabaritoDefault);
            intent.putExtra("resultGabarito", correction);
            intent.putExtra("status", 1);
            startActivity(intent);
            finish();
        } catch (Exception e){
            Log.e("kariti", e.toString());
            startCamera();
        }
    }
    private void deleteAllImages() {
        File externalDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CameraXopenCV");

        if (externalDir.exists() && externalDir.isDirectory()) {
            File[] files = externalDir.listFiles(); // Lista todos os arquivos no diretório

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.delete()) {
                        Log.e("kariti", "Diretório limpo: " + file.getName());
                    } else {
                        Log.e("kariti", "Erro ao tentar limpar diretório: " + file.getName());
                    }
                }
            }
        }
    }
    private Bitmap matToBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }
    public String saveBitmapAndGetPath(Bitmap bitmap, String name) {
        File externalDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CameraXopenCV");

        // Cria o diretório se não existir
        if (!externalDir.exists()) {
            externalDir.mkdirs();
        }

        File imageFile = new File(externalDir, name+".png");
        try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e("kariti", e.toString());
            return null;
        }

    }

}
