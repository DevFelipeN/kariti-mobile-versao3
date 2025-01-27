package online.padev.kariti.utilities;

public class Gabarito {
    private Integer id_gabarito;
    private Integer questao;
    private Integer resposta;
    private float nota;

    public Gabarito() {
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
}
