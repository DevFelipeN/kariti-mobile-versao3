package online.padev.kariti;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import org.opencv.android.OpenCVLoader;

import online.padev.kariti.cards.CorrectionReportCard;
import online.padev.kariti.database.DataBaseKariti;

public class WelcomeActivity extends AppCompatActivity {
    DataBaseKariti dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        AppCompatButton btnRegistration = findViewById(R.id.buttonCadastroW);
        AppCompatButton btnLogin = findViewById(R.id.buttonLoginW);
        AppCompatButton btnDefaultPassword = findViewById(R.id.buttonDefaultPassword);

        if (OpenCVLoader.initDebug()) {
            Log.e("opencv","Sucesso na inicialização do openCV");
        }else{
            Log.e("opencv","Erro ao tentar executar openCV");
        }


        dataBase = new DataBaseKariti(this);

        //bancoDados.deletarCorrecao(61);
        //dataBase.deletarCorrecao(1);
        //bancoDados.deletarCorrecao(3);


        if(dataBase.verificaExisteEmail("karitimobile@gmail.com") == null) {
            dataBase.cadastrarUsuario("Master user", "user1", "karitimobile@gmail.com");
        }
        btnRegistration.setOnClickListener(v -> startRegistrationUser());
        btnLogin.setOnClickListener(v -> startLogin());
        btnDefaultPassword.setOnClickListener(v -> startProvaDefault());

        //TesterSimulation t = new TesterSimulation(bancoDados);
        //for(int n = 190; n < 195; n++) {
          //t.insertCorrectionFiction(62, n);
        //}

        //CorrectionReportCard createReport = new CorrectionReportCard(this, dataBase, 62);
        //createReport.generateCorrectionReport(0);
    }

    /**
     *Este método carrega a tela de cadastro de usuário
     */
    private void startRegistrationUser(){
        Intent intent = new Intent(this, UserRegistrationActivity.class);
        startActivity(intent);
    }

    /**
     * Este método carrega a tela de Login
     */
    private void startLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    /**
     * Este método carrega a opção de prova rápida
     */
    private void startProvaDefault(){
        Intent intent = new Intent(this, ProvaFastDefaultActivity.class);
        startActivity(intent);
    }
}
