package online.padev.kariti.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import online.padev.kariti.R;
import online.padev.kariti.entity.Student;

public class StudentOnDeleteAdapter extends ArrayAdapter<Student> {

    private List<Student> students;
    private Context context;

    public StudentOnDeleteAdapter(Context context, List<Student> students) {
        super(context, R.layout.list_alunos_delete, students);
        this.context = context;
        this.students = students;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_alunos_delete, null);
        }

        TextView textViewName = view.findViewById(R.id.textViewStudentOnDelete);
        Student student = students.get(position);
        textViewName.setText(student.getNameStudent());

        return view;
    }
}
