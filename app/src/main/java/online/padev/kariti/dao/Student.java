package online.padev.kariti.dao;

public class Student {
    private Integer id_student;
    private String nameStudent;
    private String email;

    public Student(Integer id_student, String nameStudent, String email){
        this.id_student = id_student;
        this.nameStudent = nameStudent;
        this.email = email;
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

}
