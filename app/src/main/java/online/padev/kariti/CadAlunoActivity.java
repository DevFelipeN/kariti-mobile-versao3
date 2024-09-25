package online.padev.kariti;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class CadAlunoActivity extends AppCompatActivity {
    ImageButton voltar;
    EditText nomeAluno, emailAluno;
    Button cadastrar;
    BancoDados bancoDados;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cad_aluno);

        nomeAluno = findViewById(R.id.editTextAlunoCad);
        emailAluno = findViewById(R.id.editTextEmailCad);
        voltar = findViewById(R.id.imgBtnVoltaEscola);
        cadastrar = findViewById(R.id.buttonSalvarEdit);
        bancoDados = new BancoDados(this);
        cadastrar.setOnClickListener(view -> {
            String nome = nomeAluno.getText().toString();
            String email = emailAluno.getText().toString();
            if (nome.trim().isEmpty()) {
                Toast.makeText(CadAlunoActivity.this, "Informe o nome do aluno", Toast.LENGTH_SHORT).show();
            } else {
                Boolean checkAluno = bancoDados.checkAluno(nome);
                if (!checkAluno) {
                    if (!email.trim().isEmpty()) {
                        if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && !bancoDados.verificaExisteEmailAluno(email)){
                            Integer insertAluno = bancoDados.cadastrarAluno(nome, email, 1);
                            if (insertAluno != -1){
                                Toast.makeText(CadAlunoActivity.this, "Aluno cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(CadAlunoActivity.this, "Falha no cadastro do aluno!", Toast.LENGTH_SHORT).show();
                            }
                        } else
                            Toast.makeText(CadAlunoActivity.this, "E-mail Inválido ou já existente!", Toast.LENGTH_SHORT).show();
                    } else {
                        Integer insertAlunoS = bancoDados.cadastrarAluno(nome, email, 1);
                        if (insertAlunoS != -1) {
                            Toast.makeText(CadAlunoActivity.this, "Aluno cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {Toast.makeText(CadAlunoActivity.this, "Aluno não cadastrado!", Toast.LENGTH_SHORT).show();}
                    }
                } else { Toast.makeText(CadAlunoActivity.this, "Aluno já cadastrado!", Toast.LENGTH_SHORT).show();}
            }
        });
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