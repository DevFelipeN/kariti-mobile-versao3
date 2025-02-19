package online.padev.kariti.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import online.padev.kariti.BancoDados;

public class Gabarito {
    private Integer id_gabarito;
    private Integer questao;
    private Integer resposta;
    private float nota;
    private List<Gabarito> gabarito;
    public static List<Gabarito> gabaritoDefault = new ArrayList<>(); // Guarda o gabarito de provas rapidas

    public Gabarito(BancoDados bancoDados, Integer id_provaBD){
        gabarito = bancoDados.listarDadosGabarito(id_provaBD);
    }

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

    public List<Gabarito> getGabarito() {
        return gabarito;
    }
}
