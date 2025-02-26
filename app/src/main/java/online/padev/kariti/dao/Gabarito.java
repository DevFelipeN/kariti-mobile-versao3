package online.padev.kariti.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import online.padev.kariti.BancoDados;

public class Gabarito {
    private Integer questao;
    private Integer resposta;
    private float nota;
    public static List<Gabarito> gabaritoDefault = new ArrayList<>(); // Guarda o gabarito de provas rapidas

    public Gabarito(Integer questao, Integer resposta, float nota) {
        this.questao = questao;
        this.resposta = resposta;
        this.nota = nota;
    }

    public Integer getQuestao() {
        return questao;
    }

    public Integer getResposta() {
        return resposta;
    }

    public float getNota() {
        return nota;
    }
}
