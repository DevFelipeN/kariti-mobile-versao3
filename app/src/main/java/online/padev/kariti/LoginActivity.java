package online.padev.kariti;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import online.padev.kariti.R;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    TextView criarConta;
    Button entrar, esqueciSenha;
    ImageButton ocultarSenha;
    BancoDados bancoDados;
    CodSenhaActivity codSenhaActivity;
    EnviarEmail enviarEmail;
    GerarCodigoValidacao gerarCodigo;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        entrar = findViewById(R.id.buttonEntrarL);
        esqueciSenha = findViewById(R.id.buttonEsqueciSenhaL);
        criarConta = findViewById(R.id.textViewCriarConta);
        email = findViewById(R.id.editTextLogin);
        password = findViewById(R.id.editTextSenha);
        bancoDados = new BancoDados(this);

        enviarEmail = new EnviarEmail();
        gerarCodigo = new GerarCodigoValidacao();
        codSenhaActivity = new CodSenhaActivity();

        //email.setText("karitimobile@gmail.com");
        //password.setText("user1");

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailConf = email.getText().toString();
                String pass = password.getText().toString();
                if(emailConf.equals("")||pass.equals(""))
                    Toast.makeText(LoginActivity.this, "Por favor, preencher todos os campos ", Toast.LENGTH_SHORT).show();
                else{
                    if(!emailConf.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailConf).matches()) {
                        Integer checkemailpass = bancoDados.checkemailpass(emailConf, pass);
                        if (checkemailpass!=null) {
                            BancoDados.USER_ID = checkemailpass;
                            Toast.makeText(LoginActivity.this, "Bem Vindo Ao Kariti", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                            startActivity(intent);
                            finish();
                        } else {Toast.makeText(LoginActivity.this, "Usuário e/ou senha inválidos! ", Toast.LENGTH_SHORT).show();}
                    }else{Toast.makeText(LoginActivity.this, "E-mail Inválido", Toast.LENGTH_SHORT).show();}
                }
            }
        });
        criarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        esqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline()) {
                    String confEmail = email.getText().toString();
                    if (confEmail.equals("")) {
                       alerteEsqueciSenha();
                    } else {
                        Integer id = bancoDados.checkemail(confEmail);
                        if (id != null) {
                            String cod = gerarCodigo.gerarVerificador();
                            Boolean mandaEmail = enviarEmail.enviaCodigo(confEmail, cod);
                            if (mandaEmail == true) {
                                Intent proxima = new Intent(getApplicationContext(), CodSenhaActivity.class);
                                proxima.putExtra("identificador", 1);
                                proxima.putExtra("id", id);
                                proxima.putExtra("email", confEmail);
                                proxima.putExtra("cod", cod);
                                startActivity(proxima);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "E-mail não cadastrado!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "Sem conexão de rede!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ocultarSenha = findViewById(R.id.senhaoculta);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ocultarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Verifica se a senha está visivel ou oculta.
                if(password.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD){
//                  Se a senha está visivel ou oculta.
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ocultarSenha.setImageResource(R.mipmap.senhaoff);
                } else {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ocultarSenha.setImageResource(R.mipmap.senhaon);
                }
                password.setSelection(password.getText().length());
            }
        });
    }
    private boolean isOnline() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo() != null && manager.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    public void alerteEsqueciSenha(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Esqueceu Sua Senha?")
                .setMessage("Por favor informe seu e-mail cadastrado no campo sugerido, em seguida pressione 'Esqueci Minha Senha'")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(LoginActivity.this, "Informe o E-mail! ", Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}