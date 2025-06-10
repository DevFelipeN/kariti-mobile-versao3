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
import online.padev.kariti.entity.Student;

public class ListStudentInClassAdapter extends ArrayAdapter<Student> {

    private List<Student> students;

    private Context context;

    public ListStudentInClassAdapter(Context context, List<Student> students) {
        super(context, R.layout.custom_escola_desativada, students);
        this.context = context;
        this.students = students;
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
        //ImageView imageViewIcon = view.findViewById(R.id.imageViewIcon);

        Student student = students.get(position);
        textViewNome.setText(student.getNameStudent());

        return view;
    }
}
