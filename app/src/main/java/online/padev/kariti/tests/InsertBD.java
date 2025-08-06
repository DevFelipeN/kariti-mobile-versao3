package online.padev.kariti.tests;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import online.padev.kariti.database.DataBaseKariti;
import online.padev.kariti.entity.Answer_key;
import online.padev.kariti.entity.Exam;
import online.padev.kariti.entity.School;
import online.padev.kariti.entity.Student;

public class InsertBD {
    DataBaseKariti db;
    Random random = new Random();
    public InsertBD(Context context){
        this.db = new DataBaseKariti(context);
    }

    public void insertDataRandom(){
        try {
            //Cadastra um usuario no Kariti

            db.insertUser("Master user", "001", "kariti2024@gmail.com");

            //Cadastra escolas ao usuario atual
            List<String> schoolsRandom = RandomDataGenerator.nameSchools;
            DataBaseKariti.USER_ID = db.getUserId("kariti2024@gmail.com");
            schoolsRandom.forEach(name -> db.insertSchool(name));

            List<School> schoolsDb = db.listSchools(1);
            List<Student> studentsRandom = RandomDataGenerator.students;

            //Itera sob cada escola cadastrada
            schoolsDb.forEach(school -> {
                DataBaseKariti.ID_ESCOLA = school.getSchool_id();

                //Cadastra alunos na escola atual
                studentsRandom.forEach(student -> db.insertStudent(student.getNameStudent(), student.getEmail(), 1));

                //lista com os alunos cadastrados no banco de dados
                List<Student> studentsDb = db.listStudentsData(1);

                //Seleciona alguns turmas
                List<String> classRandom = randomClassS();

                //Cadastra turmas na escola atual
                classRandom.forEach(name -> {
                    //Seleciona alguns alunos cadastrados no banco e cadastra na turma atual
                    List<Student> studentList = randomStudents(studentsDb);
                    Integer class_id = db.insertClass(name);
                    db.insertStudentsInClass_2(studentList, class_id);

                    //Lista os alunos cadastrados na turma atual
                    List<Student> studentsClassDb = db.listStudentsData(class_id);

                    //Cadastra algumas provas na turma atual
                    List<Exam> exams = randomExams();
                    exams.forEach(exam -> {
                        exam.setClass_id(class_id);
                        List<Answer_key> answerKey = answerKeyGenerate(exam);
                        db.insertExam(exam, answerKey);
                    });

                    exams.forEach(exam -> {
                        //Cadastra uma correção aleatória para cada prova
                        Integer exam_id = db.getExamId(exam.getNameExam(), class_id);
                        studentsClassDb.forEach(student -> {
                            Map<Integer, Integer> resp = answerKeyResponse(exam);
                            db.insertCorrected(resp, exam_id, student.getId_student());
                        });
                    });
                });
                DataBaseKariti.ID_ESCOLA = null;
            });
            DataBaseKariti.USER_ID = null;
        }catch (Exception e){
            Log.e("testando", "Erro no cadastro de dados! -> "+e.getMessage());
        }
    }

    public List<Student> randomStudents(List<Student> st){
        List<Student> studentsRandom = new ArrayList<>();
        try{
            List<Student> studentsAnonymous = new ArrayList<>();

            int fim = random.nextInt(st.size());
            int ini = random.nextInt(fim + 1);

            for (int i = ini; i <= fim; i++){
                studentsRandom.add(st.get(i));
            }

            int totAnonymous = random.nextInt(20) + 1;
            int t = String.valueOf(totAnonymous).length();
            for (int x = 1; x <= totAnonymous; x++) {
                String nameAnonymous = "Student"+String.format("%0"+t+"d",x);
                studentsAnonymous.add(new Student(0, nameAnonymous, null));
            }
            studentsRandom.addAll(studentsAnonymous);
        }catch (Exception e){
            Log.e("testando", "Erro na geração de alunos! -> "+e.getMessage());
        }
        return studentsRandom;
    }
    public List<Exam> randomExams(){
        List<Exam> examsRandom = RandomDataGenerator.listExams;
        List<Exam> exams = new ArrayList<>();
        try{
            int fim = random.nextInt(examsRandom.size());
            int ini = random.nextInt(fim + 1);

            for (int i = ini; i <= fim; i++){
                exams.add(examsRandom.get(i));
            }
        }catch (Exception e){
            Log.e("testando", "Erro em random de provas! -> "+e.getMessage());
        }
        return exams;
    }
    public List<String> randomClassS(){
        List<String> classRandom = RandomDataGenerator.namesClass;
        List<String> classS = new ArrayList<>();
        try{
            int fim = random.nextInt(classRandom.size());
            int ini = random.nextInt(fim + 1);

            for (int i = ini; i <= fim; i++){
                classS.add(classRandom.get(i));
            }
        }catch (Exception e){
            Log.e("testando", "Erro em random de provas! -> "+e.getMessage());
        }
        return classS;
    }
    private List<Answer_key> answerKeyGenerate(Exam exam){
        List<Answer_key> keys = new ArrayList<>();
        for (int i = 1; i <= exam.getNumQuestions(); i++){
            int resp = random.nextInt(exam.getNumAlternatives());
            keys.add(new Answer_key(i, resp + 1, 1));
        }
        return keys;
    }
    private Map<Integer, Integer> answerKeyResponse(Exam exam){
        Map<Integer, Integer> responses = new HashMap<>();
        for (int i = 1; i <= exam.getNumQuestions(); i++){
            int resp = random.nextInt(exam.getNumAlternatives());
            responses.put(i, resp + 1);
        }
        return responses;
    }
}
