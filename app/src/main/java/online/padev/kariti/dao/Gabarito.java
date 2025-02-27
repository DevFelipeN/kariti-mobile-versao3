package online.padev.kariti.dao;

import java.util.ArrayList;
import java.util.List;

public class Gabarito {
    private Integer question;
    private Integer response;
    private float note;
    public static List<Gabarito> gabaritoDefault = new ArrayList<>(); // Guarda o gabarito de provas rapidas

    public Gabarito(Integer question, Integer response, float note) {
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
