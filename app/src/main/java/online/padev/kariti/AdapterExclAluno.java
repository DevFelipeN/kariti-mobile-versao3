package online.padev.kariti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class AdapterExclAluno extends ArrayAdapter<String> {

    ArrayList<String> alunos;
    Context context;

    public AdapterExclAluno(Context context, ArrayList<String> alunos) {
        super(context, R.layout.list_alunos_delete, alunos);
        this.context = context;
        this.alunos = alunos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_alunos_delete,null);
        }

        TextView textViewNome = view.findViewById(R.id.textViewNomeAluno);
        //ImageView imageViewIcon = view.findViewById(R.id.imageViewIcon);
        textViewNome.setText(alunos.get(position));


        return view;
    }
}
