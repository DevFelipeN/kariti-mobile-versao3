package online.padev.kariti.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import online.padev.kariti.R;
import online.padev.kariti.entity.Student;

public class StudentSelectedAdapter extends RecyclerView.Adapter<StudentSelectedAdapter.ViewHolder> implements Filterable {
    private List<Student> students;

    // Variáveis usadas na filtragem
    private List<Student> studentsFull; // Lista completa para pesquisa

    public StudentSelectedAdapter(List<Student> students) {
        this.students = students;
        this.studentsFull = new ArrayList<>(students);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textNome;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.textName);
            checkBox = itemView.findViewById(R.id.checkSelected);
        }
    }

    @NonNull
    @Override
    public StudentSelectedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_selectable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudentSelectedAdapter.ViewHolder holder, int position) {
        Student student = students.get(position);
        holder.textNome.setText(student.getNameStudent());

        holder.checkBox.setOnCheckedChangeListener(null);

        holder.checkBox.setChecked(student.isSelected());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            student.setSelected(isChecked);
        });

    }

    public List<Student> getStudentsSelected() {
        List<Student> selecteds = new ArrayList<>();
        for (Student a : studentsFull) {
            if (a.isSelected()) selecteds.add(a);
        }
        return selecteds;
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    @Override
    public Filter getFilter() {
        return studentsFilter;
    }

    private Filter studentsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Student> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(studentsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Student item : studentsFull) {
                    if (item.getNameStudent().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            students.clear();
            students.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
