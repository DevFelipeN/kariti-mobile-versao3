package online.padev.kariti.utils;

import java.util.HashMap;
import java.util.Random;

import online.padev.kariti.database.DataBaseKariti;

public class TesterSimulation {
    DataBaseKariti dataBaseKariti;
    public TesterSimulation(DataBaseKariti dataBaseKariti){
        this.dataBaseKariti = dataBaseKariti;
    }
    public void insertCorrectionFiction(Integer id_prova, Integer id_aluno){
        HashMap<Integer, Integer> result = new HashMap<>();

        for (int i = 0; i < 20; i++){
            int resp = new Random().nextInt(5);
            result.put(i+1, resp);
        }
        dataBaseKariti.cadastrarCorrecao(result, id_prova, id_aluno);
    }
}
