package online.padev.kariti.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import online.padev.kariti.R;
import online.padev.kariti.adapters.StudentSelectedAdapter;
import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.entity.Student;
import online.padev.kariti.settings.ActivityLocale;

public class StudentSelectedActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ActivityLocale.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_selected);

        TextView titleActivity = findViewById(R.id.toolbar_title);
        titleActivity.setText(getString(R.string.textViewStudents));


        ImageButton back = findViewById(R.id.imgBtnVoltar);
        EditText editTextSearch = findViewById(R.id.editTextBuscarToSelected);
        RecyclerView recyclerView = findViewById(R.id.listStudentsToClass);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button bFinish = findViewById(R.id.buttonFinishSelect);

        DataBaseKariti dbk = new DataBaseKariti(this);

        List<Student> studentsBd = dbk.listStudentsData(1);
        List<Student> studentsClass = (List<Student>) getIntent().getSerializableExtra("students");

        List<Student> studentsChecked = studentsStatus(studentsBd, studentsClass);

        StudentSelectedAdapter sta = new StudentSelectedAdapter(studentsChecked);
        recyclerView.setAdapter(sta);

        bFinish.setOnClickListener(v -> {
            List<Student> studentsSelect = sta.getStudentsSelected();
            restartClassRegistration(studentsSelect);
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sta.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        back.setOnClickListener(v -> restartClassRegistration(sta.getStudentsSelected()));
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                restartClassRegistration(sta.getStudentsSelected());
            }
        });
    }

    private List<Student> studentsStatus(List<Student> l, List<Student> lClass) {
        if(!lClass.isEmpty()) {
            Set<Student> atualSet = new HashSet<>(lClass); // usa equals() para comparar
            for (Student s : l) {
                if (atualSet.contains(s)) {
                    s.setSelected(true);
                }
            }
        }
        return l;
    }

    public void restartClassRegistration(List<Student> l){
        Intent intent = new Intent();
        intent.putExtra("students", (Serializable) l);
        setResult(RESULT_OK, intent);
        finish();
    }
}