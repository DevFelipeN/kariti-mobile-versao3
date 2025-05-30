package online.padev.kariti.entity;

import java.util.ArrayList;
import java.util.List;

public class Answer_key {
    private Integer question;
    private Integer response;
    private float note;
    public static List<Answer_key> answerkeyDefault = new ArrayList<>(); // Guarda o gabarito de provas rapidas

    public Answer_key(Integer question, Integer response, float note) {
        this.question = question;
        this.response = response;
        this.note = note;
    }

    public Integer getQuestion() {
        return question;
    }

    public Integer getResponse() {
        return response;
    }

    public float getNote() {
        return note;
    }
}
