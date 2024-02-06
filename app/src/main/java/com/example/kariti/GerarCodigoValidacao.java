package com.example.kariti;

import java.util.Random;

public class GerarCodigoValidacao {
    public String gerarVerificador(){
        Random r = new Random();
        String saida = "";
        for(int i = 0; i < 4; i++) {
            saida += "" + r.nextInt(10);
        }
        return saida;
    }

}
