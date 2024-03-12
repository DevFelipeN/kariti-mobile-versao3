package com.example.kariti;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        testar();
    }

    public static void copiar(String origem, String destino) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try{
            is = new FileInputStream(origem);
            os = new FileOutputStream(destino);
            byte[] buffer = new byte[1024];
            int length;
            while((length = is.read())>0){
                os.write(buffer, 0, length);
            }
        }catch(Exception e){
            Log.e("KARITI", e.toString());

        }
        finally{
            is.close();
            os.close();
        }
    }
    public static boolean testar(){
        //exemplo de como compactar duas imagens em um arquivo zip
        List<String> arquivos = new ArrayList<>();
        arquivos.add("/storage/emulated/0/Download/apagar.png");
        arquivos.add("/storage/emulated/0/Download/senha.png");
        return compactar("/data/user/0/com.example.kariti/files/saida.zip", arquivos); //retorna true se funcionou
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
            Log.e("KARITI", "TTTTTTTTTTTTTTTTEEEEEEEEEEEEEEEESSSSSSSSSSSSSSSSTTTTTTTTTTTEEEEEEE");
            Log.e("KARITI", e.toString());
            return false;
        }
    }
}
