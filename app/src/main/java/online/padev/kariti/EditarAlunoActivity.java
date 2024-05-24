package online.padev.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import online.padev.kariti.R;

public class EditarAlunoActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener{
    ImageButton voltar;
    EditText nomeAluno, emailAluno;
    Button salvar;
    BancoDados bancoDados;
    String id_aluno, aluno, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_aluno);

        nomeAluno = findViewById(R.id.editTextAlunoCadastrado);
        emailAluno = findViewById(R.id.editTextEmailCadastrado);
        voltar = findViewById(R.id.imgBtnVoltaEscola);
        salvar = findViewById(R.id.buttonSalvarEditAluno);
        bancoDados = new BancoDados(this);

        id_aluno = String.valueOf(getIntent().getExtras().getInt("id_aluno"));
        aluno = bancoDados.pegaNomeAluno(id_aluno);
        email = bancoDados.pegaEmailAluno(id_aluno);
        nomeAluno.setText(aluno);
        emailAluno.setText(email);
       voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {Intent intent = new Intent(getApplicationContext(), VisualAlunoActivity.class);
                startActivity(intent);
            finish();}
       });
       salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomAtual = nomeAluno.getText().toString();
                String emailAtual = emailAluno.getText().toString();
                if(!nomAtual.equals("")) {
                    if (!nomAtual.equals(aluno) || !emailAtual.equals(email)) {
                        if (!emailAtual.equals("")) {
                            if (Patterns.EMAIL_ADDRESS.matcher(emailAtual).matches()) {
                                Boolean alteraDadoAluno = bancoDados.upadateDadosAluno(nomAtual, emailAtual, Integer.valueOf(id_aluno));
                                if (alteraDadoAluno.equals(true)) {
                                    Toast.makeText(EditarAlunoActivity.this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), VisualAlunoActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else
                                    Toast.makeText(EditarAlunoActivity.this, "Dados não alterados", Toast.LENGTH_SHORT).show();
                            }else Toast.makeText(EditarAlunoActivity.this, "E-mail Inválido!", Toast.LENGTH_SHORT).show();
                        }else {
                            Boolean alteraDadoAluno = bancoDados.upadateDadosAluno(nomAtual, emailAtual, Integer.valueOf(id_aluno));
                            if (alteraDadoAluno.equals(true)) {
                                Toast.makeText(EditarAlunoActivity.this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), VisualAlunoActivity.class);
                                startActivity(intent);
                                finish();
                            }else
                                Toast.makeText(EditarAlunoActivity.this, "Dados não alterados", Toast.LENGTH_SHORT).show();
                        }
                    } else
                        Toast.makeText(EditarAlunoActivity.this, "Sem alterações encontradas para salvar!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(EditarAlunoActivity.this, "Por favor, preencher o campo aluno!", Toast.LENGTH_SHORT).show();
                }
            }
       });
    }

    public void popMenuAluno(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.activity_menualuno);
        popupMenu.show();
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuExcluirAluno) {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditarAlunoActivity.this);
            builder.setTitle("Atenção!")
                    .setMessage("Deseja realmente excluir o aluno?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Integer id_aluno = getIntent().getExtras().getInt("id_aluno");
                            Boolean deletAluno = bancoDados.deletarAluno(id_aluno);
                            if (deletAluno)
                                Toast.makeText(EditarAlunoActivity.this, "Aluno Excluido Com Sucesso", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent intent = new Intent(getApplicationContext(), VisualAlunoActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // cancelou
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return true;
        } else {
            return false;
        }
    }
}


