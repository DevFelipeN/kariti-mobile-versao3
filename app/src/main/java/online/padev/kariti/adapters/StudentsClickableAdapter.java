package online.padev.kariti.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import online.padev.kariti.R;
import online.padev.kariti.entity.Student;

public class StudentsClickableAdapter extends RecyclerView.Adapter<StudentsClickableAdapter.ViewHolder> implements Filterable {

    private List<Student> students;
    private List<Student> dataListFull; // Lista completa para pesquisa
    private LayoutInflater inflater;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public StudentsClickableAdapter(Context context, List<Student> students, final OnItemClickListener clickListener, final OnItemLongClickListener longClickListener) {
        this.students = students;
        this.dataListFull = new ArrayList<>(students); // Cria uma cópia da lista original
        this.inflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public void updateData(List<Student> newList) {
        students.clear();
        students.addAll(newList);
        dataListFull.clear();
        dataListFull.addAll(newList);
        notifyDataSetChanged();
    }

    public void deleteStudent(Student s) {
        dataListFull.remove(s);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_escola, parent, false);
        return new ViewHolder(view, clickListener, longClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student item = students.get(position);
        holder.textView.setText(item.getNameStudent());
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Student> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(dataListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Student item : dataListFull) {
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
            students.addAll((List<Student>) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView, final OnItemClickListener clickListener, final OnItemLongClickListener longClickListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewNomeScol);
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onItemClick(position);
                    }
                }
            });
            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        longClickListener.onItemLongClick(position);
                        return true; // Indica que o evento foi consumido
                    }
                }
                return false;
            });
        }
    }
}

