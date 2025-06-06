package online.padev.kariti.entity;

import java.util.ArrayList;
import java.util.List;

public class Result {
    private Integer resul_id, exam_id, student_id;
    private int question, answer_given;
        
    public Result(Integer student_id, Integer exam_id) {
        this.student_id = student_id;
        this.exam_id = exam_id;
    }

    public Integer getResul_id() {
        return resul_id;
    }

    public void setResul_id(Integer resul_id) {
        this.resul_id = resul_id;
    }

    public Integer getExam_id() {
        return exam_id;
    }

    public void setExam_id(Integer exam_id) {
        this.exam_id = exam_id;
    }

    public Integer getStudent_id() {
        return student_id;
    }

    public void setStudent_id(Integer student_id) {
        this.student_id = student_id;
    }

    public int getQuestion() {
        return question;
    }

    public void setQuestion(int question) {
        this.question = question;
    }

    public int getAnswer_given() {
        return answer_given;
    }

    public void setAnswer_given(int answer_given) {
        this.answer_given = answer_given;
    }
}
