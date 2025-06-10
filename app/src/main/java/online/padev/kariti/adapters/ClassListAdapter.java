package online.padev.kariti.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import online.padev.kariti.R;
import online.padev.kariti.entity.ClassSchool;

public class ClassListAdapter extends ArrayAdapter<ClassSchool> {

    private List<ClassSchool> clssS;
    private Context context;

    public ClassListAdapter(Context context, List<ClassSchool> classS) {
        super(context, R.layout.list_escola, classS);
        this.context = context;
        this.clssS = classS;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_escola, null);
        }

        TextView textViewNome = view.findViewById(R.id.textViewNomeScol);
        //ImageView imageViewIcon = view.findViewById(R.id.imageViewIcon);

        ClassSchool classSchool = clssS.get(position);
        textViewNome.setText(classSchool.getName());

        return view;
    }
}
