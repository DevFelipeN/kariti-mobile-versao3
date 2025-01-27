package online.padev.kariti.utilities;

import java.io.Serializable;

import online.padev.kariti.BancoDados;

public class Prova implements Serializable {
    private Integer id_prova;
    private Integer id_turma;
    private String nomeProva, dataProva;
    private int numQuestoes, numAlternativas;

    public Prova() {
    }

    public Prova(Integer id_prova, BancoDados bancoDados) {
        this.id_prova = id_prova;
        String[] dados = bancoDados.pegarTodosDadosProva(id_prova);
        this.nomeProva = dados[0];
        this.id_turma = Integer.valueOf(dados[1]);
        this.dataProva = dados[2];
        this.numQuestoes = Integer.parseInt(dados[3]);
        this.numAlternativas = Integer.parseInt(dados[4]);
    }

    public Integer getId_turma() {
        return id_turma;
    }

    public void setId_turma(Integer id_turma){
        this.id_turma = id_turma;
    }

    public Integer getId_prova() {
        return id_prova;
    }

    public void setId_prova(Integer id_prova) {
        this.id_prova = id_prova;
    }

    public String getNomeProva() {
        return nomeProva;
    }

    public void setNomeProva(String nomeProva) {
        this.nomeProva = nomeProva;
    }

    public String getDataProva() {
        return dataProva;
    }

    public void setDataProva(String dataProva) {
        this.dataProva = formatDateToCompare(dataProva);
    }

    public int getNumQuestoes() {
        return numQuestoes;
    }

    public void setNumQuestoes(int numQuestoes) {
        this.numQuestoes = numQuestoes;
    }

    public int getNumAlternativas() {
        return numAlternativas;
    }

    public void setNumAlternativas(int numAlternativas) {
        this.numAlternativas = numAlternativas;
    }

    public boolean isDifferent(Prova outraProva) {
        return !this.nomeProva.equals(outraProva.getNomeProva()) ||
                !this.id_turma.equals(outraProva.getId_turma()) ||
                !this.dataProva.equals(outraProva.getDataProva()) ||
                this.numQuestoes != outraProva.getNumQuestoes() ||
                this.numAlternativas != outraProva.getNumAlternativas();
    }
    private String formatDateToCompare(String data){
        String[] itens = data.split("/");
        return itens[2]+"-"+itens[1]+"-"+itens[0];
    }
    public String dateToDisplay(){
        String data = this.getDataProva();
        String[] itens = data.split("-");
        String dataFor = itens[2]+"/"+itens[1]+"/"+itens[0];
        return dataFor;
    }
}
