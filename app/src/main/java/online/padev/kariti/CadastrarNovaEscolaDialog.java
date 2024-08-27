package online.padev.kariti;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CadastrarNovaEscolaDialog {
    static FloatingActionButton cancelarFlut;
    static String notifique;
    public static String showCustomDialog(Context context, BancoDados bancoDados) {
        // Inflar o layout customizado
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.cadastrar_escola_dialog, null);

        // Inicializar os elementos do layout
        cancelarFlut = dialogView.findViewById(R.id.btnvoltarflutuante);
        EditText editTextEscola = dialogView.findViewById(R.id.editTextNomeEscolaDialog);
        Button btnCadastrar = dialogView.findViewById(R.id.buttonDialog);
        // Criar o AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(dialogView);
        // Mostrar o diálogo
        AlertDialog dialog = builder.create();
        dialog.show();

        btnCadastrar.setOnClickListener(v -> {
            String nomeEscola = editTextEscola.getText().toString();
            if (!nomeEscola.trim().isEmpty()) {
                if (!bancoDados.verificaEscola(nomeEscola)) {
                    if(bancoDados.inserirDadosEscola(nomeEscola, null, 1)){
                        Toast.makeText(context, "Escola cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        notifique = "novaEscola";
                    }
                }else{
                    Toast.makeText(context, "Atenção: Escola já cadastrada!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(context, "Informe o nome da escola!", Toast.LENGTH_SHORT).show();
            }
        });
        cancelarFlut.setOnClickListener(v -> dialog.dismiss()); //Fecha o diálogo
        return notifique;
    }
}
