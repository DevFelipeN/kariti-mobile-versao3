package online.padev.kariti.activitys;

import static online.padev.kariti.utils.ImageDirectory.saveBitmapAndGetPath;
import static online.padev.kariti.utils.MatToBitmap.toBitmap;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import online.padev.kariti.R;
import online.padev.kariti.correction.CoreKariti;
import online.padev.kariti.entity.Answer_key;
import online.padev.kariti.entity.Exam;
import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.settings.ActivityLocale;

public class AnswerKeyActivity extends AppCompatActivity {
   TextView txtViewNotaExam, txtViewExam, txtViewClass, txtViewData;
    private Button btnRegisterExam;
    ImageButton back, iconHelp;
    private LinearLayout layoutHorizontal;
    TextView title;
    private List<Float> notas = new ArrayList<>();
    private List<RadioGroup> listRadioGroups;
    private Map<Integer, Integer> alternativesSelected;
    private List<Answer_key> answerkey = new ArrayList<>();

    private DataBaseKariti dataBaseKariti;
    private Exam dadosExam;
    private String direction;
    private int statusEdition, typeMessage;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ActivityLocale.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gabarito);

        back = findViewById(R.id.imgBtnVoltaDescola);
        iconHelp = findViewById(R.id.iconHelp);
        title = findViewById(R.id.toolbar_title);
        btnRegisterExam = findViewById(R.id.btnCadProva);
        txtViewExam = findViewById(R.id.textViewProva);
        txtViewClass = findViewById(R.id.textViewTurma);
        txtViewData = findViewById(R.id.textViewData);
        txtViewNotaExam = findViewById(R.id.txtViewNotaProva);
        layoutHorizontal = findViewById(R.id.layoutHorizontalAlternat);

        dataBaseKariti = new DataBaseKariti(this);
        dadosExam = new Exam();
        listRadioGroups = new ArrayList<>();
        alternativesSelected = new HashMap<>();

        title.setText(getString(R.string.titleAnswerKey));

        dadosExam = (Exam) getIntent().getSerializableExtra("prova");
        direction = getIntent().getExtras().getString("direcao");


        if(dadosExam.getExam_id() != null && !dadosExam.getExam_id().equals(0)){
            statusEdition = getIntent().getExtras().getInt("status");
            btnRegisterExam.setText(getString(R.string.btnSave));
        }

        if (!direction.equals("cardDefault")) {
            txtViewExam.setText(getString(R.string.textViewTitleExam, dadosExam.getNameExam()));
            txtViewClass.setText(getString(R.string.txtViewClass, dataBaseKariti.getClassName(dadosExam.getClass_id().toString())));
            txtViewData.setText(getString(R.string.txtViewDate, dadosExam.dateToDisplay()));
        } else {
            txtViewExam.setVisibility(View.GONE);
            txtViewData.setVisibility(View.GONE);
            txtViewClass.setVisibility(View.GONE);
            btnRegisterExam.setText(getString(R.string.btnSave));
            typeMessage = getIntent().getExtras().getInt("typeMessage");
            if (typeMessage == 4){ // indica que já existe um gabarito default cadastrado e que um novo deve ser cadastrado para prova rápida
                differentCardNotice();
            }
            if (typeMessage == 5){
                newAnswerKeyDefault();
            }
        }

        btnRegisterExam.setOnClickListener(v -> {
            btnRegisterExam.setEnabled(false);
            boolean respostaSelecionada = true;
            boolean respostasNotasPreenchidas = true;

            try {

                //Verica aqui se todas as respostas fora marcadas
                for (RadioGroup radioGroup : listRadioGroups) {
                    if (radioGroup.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(this, getString(R.string.toastSelectResponseQuestionsAll), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(this, getString(R.string.toastInfoScoresQuestionsAll), Toast.LENGTH_SHORT).show();
                        respostasNotasPreenchidas = false;
                        break;
                    }
                }

                if (respostaSelecionada && respostasNotasPreenchidas) { //Caso todas as alternativas forem marcadas e as notas adicionadas

                    if (!notaFinal()) {
                        btnRegisterExam.setEnabled(true);
                        return;
                    }
                    if (!notas.isEmpty()) {
                        for (int i = 1; i <= dadosExam.getNumQuestions(); i++) {
                            Integer resp = alternativesSelected.get(i - 1);
                            float notaQuestaoI = notas.get(i - 1);
                            Answer_key g = new Answer_key(i, resp + 1, notaQuestaoI);
                            answerkey.add(g);
                        }

                        if (dadosExam.getExam_id() == null) {
                            if (dataBaseKariti.insertExam(dadosExam, answerkey)) {
                                dialogExamSuccess(getString(R.string.descriptionRegister));
                            } else {
                                noticeRegisterFailed(getString(R.string.descriptionInTheRegister));
                            }
                        } else if (!dadosExam.getExam_id().equals(0)) {
                            if (dataBaseKariti.updateExamData(dadosExam, answerkey, statusEdition)) {
                                dialogExamSuccess(getString(R.string.descriptionChanged));
                            } else {
                                noticeRegisterFailed(getString(R.string.descriptionInTheChanged));
                            }
                        } else { // Entra nessa estrutura quando o gabarito pertencer a uma prova rápida
                            Answer_key.answerkeyDefault = answerkey;
                            Exam.numQuestsDefault = dadosExam.getNumQuestions();
                            Exam.numAlternativesDefault = dadosExam.getNumAlternatives();
                            dialogHelpCorrectDefault();
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (Exception e){
                Log.e("kariti", e.toString());
                Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            }finally {
                btnRegisterExam.setEnabled(true);
            }
       });

        int qtdQuestions = dadosExam.getNumQuestions();
        int qtdAlternatives = dadosExam.getNumAlternatives();
        txtViewNotaExam.setText(getString(R.string.txtViewNotaTotal,qtdQuestions));

        String[] letters = new String[qtdAlternatives];
        for (int i = 0; i < qtdAlternatives; i++) {
            char letter = (char)('A' + i);
            letters[i] = String.valueOf(letter);
        }

        //Questões e Radio
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < qtdQuestions; i++) {
            LinearLayout layoutQuestion = new LinearLayout(this);
            layoutQuestion.setOrientation(LinearLayout.HORIZONTAL);

            TextView textViewNumQuestion = new TextView(this);
            textViewNumQuestion.setWidth(dpToPx(30));
            textViewNumQuestion.setTextSize(18);
            textViewNumQuestion.setText(String.format("%d", i + 1));
            layoutQuestion.addView(textViewNumQuestion);

            //Agrupar os RadioButtons
            RadioGroup radioGroupAlternatives = new RadioGroup(this);
            radioGroupAlternatives.setOrientation(LinearLayout.HORIZONTAL);
            listRadioGroups.add(radioGroupAlternatives);

            // Loop para criar Radio para as respostas
            for (int j = 0; j < qtdAlternatives; j++) {
                params.setMargins(0, 20, 20, 0);

                RadioButton radioAlternative = new RadioButton(this);
                radioAlternative.setLayoutParams(params);
                radioAlternative.setText(letters[j]);
                radioGroupAlternatives.addView(radioAlternative);
            }
            radioGroupAlternatives.setOnCheckedChangeListener((group, checkedId) -> {
                for (int a = 0; a < listRadioGroups.size(); a++) {
                    if (listRadioGroups.get(a) == group) {
                        int selectedRadioButtonId = group.getCheckedRadioButtonId();
                        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                        int position = group.indexOfChild(selectedRadioButton);
                        alternativesSelected.put(a, position);
                        break;
                    }
                }
            });
            layoutQuestion.addView(radioGroupAlternatives);

            LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(
                    130,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            EditText editTextScore = new EditText(this);
            editTextScore.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            editTextScore.setFilters(new InputFilter[] { new InputFilter.LengthFilter(5) });
            editTextScore.setText(String.valueOf(1.0));
            editTextScore.setGravity(Gravity.CENTER);
            editTextScore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            editTextScore.setBackground(ContextCompat.getDrawable(this, R.drawable.borda_fina));
            paramsText.setMargins(5, 15, 0, 0);

            editTextScore.setLayoutParams(paramsText);

            layoutQuestion.addView(editTextScore);

            editTextScore.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }
                @Override
                public void afterTextChanged(Editable editable) {
                    calculateScoreCurrent();
                }
            });

            layoutHorizontal.addView(layoutQuestion);
            calculateScoreCurrent();

        }
        iconHelp.setOnClickListener(v -> dialogHelpDetails());
        back.setOnClickListener(view -> noticeBack());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                noticeBack();
            }
        });
        registrationGradeAll();
    }
    private void dialogExamSuccess(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(AnswerKeyActivity.this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.titleDialogSuccessExam, text))
                .setMessage(getString(R.string.longTextInfoToDownload))
                .setPositiveButton(getString(R.string.okDescription), (dialog, which) -> {
                    generateCards();
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void noticeRegisterFailed(String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(AnswerKeyActivity.this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.titleAttention))
                .setMessage(getString(R.string.longTextFailedExecExam, text))
                .setPositiveButton(getString(R.string.descriptionExit), (dialog, which) -> finish());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void generateCards() {
        Intent intent = new Intent(this, ExamGenerateCardRegisteredActivity.class);
        intent.putExtra("prova", dadosExam.getNameExam());
        intent.putExtra("id_turma", dadosExam.getClass_id());
        intent.putExtra("endereco", 1);
        startActivity(intent);
        finish();
    }
    private void calculateScoreCurrent() {
        float notas = 0;
        for (int j = 0; j < layoutHorizontal.getChildCount(); j++) {
            LinearLayout questionLayout = (LinearLayout) layoutHorizontal.getChildAt(j);
            EditText pontosEditText = (EditText) questionLayout.getChildAt(2);
            String nota = pontosEditText.getText().toString();
            if(nota.isEmpty() || nota.charAt(0) == '.'){
                nota = "0"+nota;
            }
            float n = Float.parseFloat(nota);
            notas += n;
        }
        txtViewNotaExam.setText(getString(R.string.txtViewNotaTotal, notas));
    }

    private void setGradeAll(float grade) {
        for (int j = 0; j < layoutHorizontal.getChildCount(); j++) {
            LinearLayout questionLayout = (LinearLayout) layoutHorizontal.getChildAt(j);
            EditText pontosEditText = (EditText) questionLayout.getChildAt(2);
            pontosEditText.setText(String.valueOf(grade));
        }
        txtViewNotaExam.setText(getString(R.string.txtViewNotaTotal, (float) dadosExam.getNumQuestions() * grade));
    }

    private boolean notaFinal() {
        try {
            for (int j = 0; j < layoutHorizontal.getChildCount(); j++) {
                LinearLayout questionLayout = (LinearLayout) layoutHorizontal.getChildAt(j);
                EditText pontosEditText = (EditText) questionLayout.getChildAt(2);
                String nota = pontosEditText.getText().toString();
                if (nota.isEmpty() || nota.charAt(0) == '.') {
                    nota = "0" + nota;
                }
                float n = Float.parseFloat(nota);
                //Log.e("notas","n: "+n);
                notas.add(n);
            }
            return true;
        }catch (Exception e){
            Toast.makeText(this, getString(R.string.toastApplicationError), Toast.LENGTH_SHORT).show();
            //Log.e("kariti", e.toString());
            return false;
        }
    }
    private void dialogHelpDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.titleHelp));
        builder.setMessage(getString(R.string.longTextHelpAnswerKey));
        builder.setPositiveButton(getString(R.string.okDescription), (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void noticeBack(){
        if (!direction.equals("cardDefault")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AnswerKeyActivity.this);
            builder.setTitle(getString(R.string.titleAttention))
                    .setMessage(getString(R.string.longTextNoticeBack))
                    .setPositiveButton(getString(R.string.yes_description), (dialog, which) -> finish())
                    .setNegativeButton(getString(R.string.not_description), (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            if (Answer_key.answerkeyDefault != null && !Answer_key.answerkeyDefault.isEmpty()){
                Toast.makeText(this, getString(R.string.toastPreviousAnswerKey), Toast.LENGTH_LONG).show();
                finish();
            } else {
                finish();
            }
        }
    }
    private void dialogHelpCorrectDefault() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.titleDialogSuccessAnswerKey));
        builder.setMessage(getString(R.string.longTextAnswerKeyCompleted));
        builder.setPositiveButton(getString(R.string.okDescription), (dialog, which) -> {
            dialog.dismiss();
            correctFirstDefault();
        });
        builder.show();
    }
    private void startCamera(){
        Intent intent = new Intent(this, CameraxAndOpencvActivity.class);
        startActivity(intent);
        finish();
    }
    private void differentCardNotice(){
        View overlayView = findViewById(R.id.overlayView);
        overlayView.setVisibility(View.VISIBLE);
        btnRegisterExam.setVisibility(View.GONE);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.titleAttention))
                .setMessage(getString(R.string.longTextCardDifferentAnswerKey))
                .setPositiveButton(getString(R.string.alterarDescription), (dialog, which) -> {
                    overlayView.setVisibility(View.GONE);
                    btnRegisterExam.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                })

                .setNegativeButton(getString(R.string.manterDescription), (dialog, which) -> startCamera());
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void newAnswerKeyDefault(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.titleDialogTeacher))
                .setMessage(getString(R.string.longTextInitCorrectionDefault))
                .setPositiveButton(getString(R.string.okDescription), (dialog, which) -> dialog.dismiss())
                .setNegativeButton(getString(R.string.cancel_description), (dialog, which) -> finish());
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void correctFirstDefault(){
        try {
            String gabaritoDefault = "";
            for (Answer_key g : Answer_key.answerkeyDefault) {
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
            CoreKariti core = new CoreKariti(matWarp, dadosExam, gabaritoDefault);
            correction = core.correctCard(); // Versão 3: corrigindo com o Kariti Mobile
            if (correction != null && !correction.isEmpty()){
                Bitmap imgWarp = toBitmap(matWarp);
                String nameCard = dadosExam.getNumQuestions()+"_"+ dadosExam.getNumAlternatives()+"_"+dataHoraAtual();
                String filePathPaint = saveBitmapAndGetPath(imgWarp, nameCard, this); //Salva a imagem cortada pintada
                startViewImageDefault(correction, gabaritoDefault, filePathPaint);
            } else {
                startCamera();
            }
        } catch (Exception e) {
            //Log.e("kariti", e.toString());
            startCamera();
        }
    }
    private void startViewImageDefault(HashMap<Integer, Integer> correction, String answerKeyDefault, String filePathPaint){
        try {
            Intent intent = new Intent(this, ViewCardCorrectedActivity.class);
            intent.putExtra("filePath", filePathPaint);
            intent.putExtra("gabarito", answerKeyDefault);
            intent.putExtra("resultGabarito", correction);
            intent.putExtra("status", 1);
            startActivity(intent);
            finish();
        } catch (Exception e){
            //Log.e("kariti", e.toString());
            startCamera();
        }
    }
    private String dataHoraAtual(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault());
        Date date = new Date();
        return sdf.format(date);
    }

    private void registrationGradeAll() {
        // Inflar o layout customizado
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.grade_all_dialog, null);

        float n = (float) dadosExam.getNumQuestions();

        // Inicializar os elementos do layout
        EditText editTextGradeAll = dialogView.findViewById(R.id.editTextGradeAll);
        Button btnRegistrationGrades = dialogView.findViewById(R.id.buttonYesGradeAll);
        Button btnNotGradeAll = dialogView.findViewById(R.id.buttonNotGradeAll);
        TextView textViewGradeTotal = dialogView.findViewById(R.id.txtViewGradeExam);

        textViewGradeTotal.setText(getString(R.string.txtViewNotaTotal, n));

        // Criar o AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(dialogView);
        // Mostrar o diálogo
        AlertDialog dialog = builder.create();
        dialog.show();

        editTextGradeAll.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String t = s.toString().trim();
                if (!t.isEmpty() && t.matches("^\\d+(\\.\\d+)?$")){
                    float notaAux = Float.parseFloat(t);
                    textViewGradeTotal.setText(getString(R.string.txtViewNotaTotal, n * notaAux));
                }
            }
        });

        btnNotGradeAll.setOnClickListener(v -> dialog.dismiss());

        btnRegistrationGrades.setOnClickListener(v -> {
            String t = editTextGradeAll.getText().toString().trim();
            if (!t.isEmpty() && t.matches("^\\d+(\\.\\d+)?$")){
                float notaAux = Float.parseFloat(t);
                setGradeAll(notaAux);
                dialog.dismiss();
            }else{
                Toast.makeText(this, getString(R.string.toastFormatScoreInvalid), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
