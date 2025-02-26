package online.padev.kariti.correction;

import java.util.HashMap;
import java.util.Random;

import online.padev.kariti.BancoDados;

public class TesterSimulation {
    BancoDados bancoDados;
    public TesterSimulation(BancoDados bancoDados){
        this.bancoDados = bancoDados;
    }
    public void insertCorrectionFiction(Integer id_prova, Integer id_aluno){
        HashMap<Integer, Integer> result = new HashMap<>();

        for (int i = 0; i < 20; i++){
            int resp = new Random().nextInt(5);
            result.put(i+1, resp);
        }
        bancoDados.cadastrarCorrecao(result, id_prova, id_aluno);
    }
}
