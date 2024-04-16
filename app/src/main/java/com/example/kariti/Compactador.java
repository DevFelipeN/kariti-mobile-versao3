package com.example.kariti;

import android.util.Log;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;
//compactar arquivos para enviar
public class Compactador{
    public static void main(String[] args){

        compactador();
    }
    public static boolean compactador(){
        //exemplo de como compactar duas imagens em um arquivo zip
        List<String> arquivos = new ArrayList<>();
        arquivos.add("/storage/emulated/0/Android/data/com.example.kariti/files/Cartoes/2_1_0_0.png");
        arquivos.add("/storage/emulated/0/Android/data/com.example.kariti/files/Cartoes/2_4_0_0.png");
        return compactar("/storage/emulated/0/Android/data/com.example.kariti/files/Cartoes/saida.zip", arquivos); //retorna true se funcionou
    }
    public static boolean compactar(String arquivoSaida, List<String> arquivosParaCompactar){
        try{
            FileOutputStream fos = new FileOutputStream(arquivoSaida);
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            for(String sourceFile : arquivosParaCompactar){
                File fileToZip = new File(sourceFile);
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
            }
            zipOut.close();
            fos.close();
            return true;
        }catch(Exception e){
            Log.e("KARITI", e.toString());
            return false;
        }
    }
}
