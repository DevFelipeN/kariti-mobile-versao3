package online.padev.kariti.entity;

import java.io.Serializable;
import java.util.Objects;

public class Student implements Serializable {
    private Integer id_student;
    private String nameStudent;
    private String email;

    protected boolean selected;

    public Student(Integer id_student, String nameStudent, String email){
        this.id_student = id_student;
        this.nameStudent = nameStudent;
        this.email = email;
        this.selected = false;
    }

    public Integer getId_student() {
        return id_student;
    }

    public String getNameStudent() {
        return nameStudent;
    }

    public String getEmail() {
        return email;
    }

    public boolean isSelected() { return selected; }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return nameStudent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Student)) return false;
        Student other = (Student) obj;
        return id_student.equals(other.id_student);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_student);
    }

}
