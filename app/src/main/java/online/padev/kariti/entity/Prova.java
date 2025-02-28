package online.padev.kariti.entity;

import androidx.annotation.NonNull;

import java.io.Serializable;

import online.padev.kariti.database.DataBaseKariti;

public class Prova implements Serializable {
    private Integer id_prova;
    private Integer id_class;
    private String nameProva, dateProva;
    private int numQuestions, numAlternatives;

    public static int numQuestsDefault; //Essa variável serve como controle da correção de prova rápida
    public static int numAlternativesDefault; //Essa variável serve como controle da correção de prova rápida

    public Prova() {
    }

    public Prova(Integer id_prova, DataBaseKariti dataBaseKariti) {
        this.id_prova = id_prova;
        String[] dados = dataBaseKariti.pegarTodosDadosProva(id_prova);
        this.nameProva = dados[0];
        this.id_class = Integer.valueOf(dados[1]);
        this.dateProva = dados[2];
        this.numQuestions = Integer.parseInt(dados[3]);
        this.numAlternatives = Integer.parseInt(dados[4]);
    }

    public Integer getId_class() {
        return id_class;
    }

    public void setId_class(Integer id_class){
        this.id_class = id_class;
    }

    public Integer getId_prova() {
        return id_prova;
    }

    public void setId_prova(Integer id_prova) {
        this.id_prova = id_prova;
    }

    public String getNameProva() {
        return nameProva;
    }

    public void setNameProva(String nameProva) {
        this.nameProva = nameProva;
    }

    public String getDateProva() {
        return dateProva;
    }

    public void setDateProva(String dateProva) {
        this.dateProva = formatDateToCompare(dateProva);
    }

    public int getNumQuestions() {
        return numQuestions;
    }

    public void setNumQuestions(int numQuestions) {
        this.numQuestions = numQuestions;
    }

    public int getNumAlternatives() {
        return numAlternatives;
    }

    public void setNumAlternatives(int numAlternatives) {
        this.numAlternatives = numAlternatives;
    }

    public boolean isDifferent(Prova outraProva) {
        return !this.nameProva.equals(outraProva.getNameProva()) ||
                !this.id_class.equals(outraProva.getId_class()) ||
                !this.dateProva.equals(outraProva.getDateProva()) ||
                this.numQuestions != outraProva.getNumQuestions() ||
                this.numAlternatives != outraProva.getNumAlternatives();
    }
    private String formatDateToCompare(String data){
        String[] itens = data.split("/");
        return itens[2]+"-"+itens[1]+"-"+itens[0];
    }
    public String dateToDisplay(){
        String data = this.getDateProva();
        String[] itens = data.split("-");
        return itens[2]+"/"+itens[1]+"/"+itens[0];
    }

    @NonNull
    @Override
    public String toString() {
        return "prova{nome: "+this.nameProva +", id_turma: "+this.id_class +", data: "+this.dateProva +", questões: "+this.numQuestions +", Alternativas: "+this.numAlternatives +"}";
    }
}
