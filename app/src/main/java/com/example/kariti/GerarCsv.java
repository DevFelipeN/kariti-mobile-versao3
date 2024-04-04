package com.example.kariti;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
public class GerarCsv {
    public static void gerar(List<String[]> dados, String fileName)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        StringBuffer sb = new StringBuffer("");
        for(String[] linha : dados){
            sb.setLength(0);
            for(int i = 0; i < linha.length; i++){
                if (i > 0){
                    sb.append(";");
                }
                sb.append(linha[i]);
            }
            writer.append(sb.toString());
            writer.newLine();
        }
        writer.close();
    }
    public static void main(String[]args) throws Exception{
        List<String[]> dados = new ArrayList<>();

        dados.add(new String[]{"a", "b", "c"}); //linha 1
        dados.add(new String[]{"d", "e", "f"}); //linha 2
        dados.add(new String[]{"g", "h", "i"}); //linha 3

        gerar(dados, "entraday.csv");
        /*
        Este exemplo vai gerar um arquivo chamada entraday.csv com as seguintes linhas:
        -------
        |a,b,c|
        |d,e,f|
        |g,h,i|
        |     |
        -------
        */
    }
}
