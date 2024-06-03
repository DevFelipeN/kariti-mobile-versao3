package online.padev.kariti;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import online.padev.kariti.R;

import java.util.ArrayList;

public class VisualAlunoActivity extends AppCompatActivity {
    ImageButton btnVoltar;
    BancoDados bancoDados;
    EditText pesquisarAlunos;
    ArrayList<String> listAlunos;
    MyAdapter adapter;
    TextView tituloAppBarAlunos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_aluno);

        btnVoltar = findViewById(R.id.imgBtnVoltar);
        pesquisarAlunos = findViewById(R.id.editTextBuscar);
        ListView listView = findViewById(R.id.listSelecAluno);
        bancoDados = new BancoDados(this);



        tituloAppBarAlunos = findViewById(R.id.toolbar_title);
        tituloAppBarAlunos.setText("Alunos");

        listAlunos = (ArrayList<String>) bancoDados.listAlunos();
        if (listAlunos.size() == 0) {
            Intent intent = new Intent(this, ilustracionVoidSchoolctivity.class);
            startActivity(intent);
            finish();
            return;
        }

        /*

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this, listAlunos);
        recyclerView.setAdapter(adapter);

         */
        EscolaAdapter adapter = new EscolaAdapter(this, listAlunos, listAlunos);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Exibir a caixa de diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(VisualAlunoActivity.this);
                builder.setTitle("Atenção!")
                        .setMessage("Deseja excluir aluno?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Integer id_aluno = bancoDados.pegaIdAluno(adapter.getItem(position));
                                Boolean checkAlEmTurma = bancoDados.checkAlunoEmTurma(id_aluno);
                                if(!checkAlEmTurma){
                                    Boolean deletAluno = bancoDados.deletarAluno(id_aluno);
                                    if (deletAluno) {
                                        listAlunos.remove(position);
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(VisualAlunoActivity.this, "Aluno Excluido! ", Toast.LENGTH_SHORT).show();
                                    }else
                                        Toast.makeText(VisualAlunoActivity.this, "Erro: aluno não excluido!", Toast.LENGTH_SHORT).show();
                                }else avisoNotExluirAluno();
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //cancelou
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Integer id_aluno = bancoDados.pegaIdAluno(adapter.getItem(i));
                Intent intent = new Intent(getApplicationContext(), EditarAlunoActivity.class);
                intent.putExtra("id_aluno", id_aluno);
                startActivity(intent);
                finish();
            }
        });

        pesquisarAlunos.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){
                adapter.getFilter().filter(charSequence);
            }
            @Override
            public void afterTextChanged(Editable editable){
            }
        });
        btnVoltar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                onBackPressed();
                finish();
            }
        });
    }
    public void avisoNotExluirAluno(){
        AlertDialog.Builder builder = new AlertDialog.Builder(VisualAlunoActivity.this);
        builder.setTitle("Atenção!")
                .setMessage("Este aluno possui vínculo com uma ou mais turma(s) cadastrada(s), não sendo possível excluir!.");
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean haAlunoCadastrado(){
        SQLiteDatabase database = bancoDados.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM aluno WHERE id_aluno=" + BancoDados.USER_ID, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }
}