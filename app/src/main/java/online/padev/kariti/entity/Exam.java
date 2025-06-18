package online.padev.kariti.entity;

import androidx.annotation.NonNull;

import java.io.Serializable;

import online.padev.kariti.database.DataBaseKariti;

public class Exam implements Serializable {
    private Integer exam_id;
    private Integer class_id;
    private String nameExam, dateExam;
    private int numQuestions, numAlternatives;

    public static final int MAX_QUESTIONS = 20;
    public static final int MAX_ALTERNATIVES = 6;

    public static int numQuestsDefault; //Essa variável serve como controle da correção de prova rápida
    public static int numAlternativesDefault; //Essa variável serve como controle da correção de prova rápida

    public Exam() {
    }

    public Exam(Integer exam_id, DataBaseKariti dataBaseKariti) {
        this.exam_id = exam_id;
        String[] dados = dataBaseKariti.getExamData(exam_id);
        this.nameExam = dados[0];
        this.class_id = Integer.valueOf(dados[1]);
        this.dateExam = dados[2];
        this.numQuestions = Integer.parseInt(dados[3]);
        this.numAlternatives = Integer.parseInt(dados[4]);
    }

    public Integer getClass_id() {
        return class_id;
    }

    public void setClass_id(Integer class_id){
        this.class_id = class_id;
    }

    public Integer getExam_id() {
        return exam_id;
    }

    public void setExam_id(Integer exam_id) {
        this.exam_id = exam_id;
    }

    public String getNameExam() {
        return nameExam;
    }

    public void setNameExam(String nameExam) {
        this.nameExam = nameExam;
    }

    public String getDateExam() {
        return dateExam;
    }

    public void setDateExam(String dateExam) {
        this.dateExam = formatDateToCompare(dateExam);
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

    public boolean isDifferent(Exam outraExam) {
        return !this.nameExam.equals(outraExam.getNameExam()) ||
                !this.class_id.equals(outraExam.getClass_id()) ||
                !this.dateExam.equals(outraExam.getDateExam()) ||
                this.numQuestions != outraExam.getNumQuestions() ||
                this.numAlternatives != outraExam.getNumAlternatives();
    }
    private String formatDateToCompare(String data){
        String[] itens = data.split("/");
        return itens[2]+"-"+itens[1]+"-"+itens[0];
    }
    public String dateToDisplay(){
        String data = this.getDateExam();
        String[] itens = data.split("-");
        return itens[2]+"/"+itens[1]+"/"+itens[0];
    }

    @NonNull
    @Override
    public String toString() {
        return "prova{nome: "+this.nameExam +", id_turma: "+this.class_id +", data: "+this.dateExam +", questões: "+this.numQuestions +", Alternativas: "+this.numAlternatives +"}";
    }
}
