package com.example.kariti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DesativadaAdapter extends ArrayAdapter<String> {

    private ArrayList<String> escolas;
    private Context context;

    public DesativadaAdapter(Context context, ArrayList<String> escolas) {
        super(context, R.layout.custom_escola_desativada, escolas);
        this.context = context;
        this.escolas = escolas;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_escola_desativada, null);
        }

        TextView textViewNome = view.findViewById(R.id.textViewNome);
        ImageView imageViewIcon = view.findViewById(R.id.imageViewIcon);

        String nomeEscola = escolas.get(position);
        textViewNome.setText(nomeEscola);


        return view;
    }
}
