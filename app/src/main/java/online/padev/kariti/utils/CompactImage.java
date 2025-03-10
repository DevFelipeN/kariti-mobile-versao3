package online.padev.kariti.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompactImage {
    public static List<String> controllerImageOrig = new ArrayList<>();
    public static List<String> controllerImageWarp = new ArrayList<>();
    public static List<String> controllerImageWarpPaint = new ArrayList<>();
    private static List<String> imgs = new ArrayList<>();

    public static boolean compact(File diretorioImg, String caminhoZip){
        imgs.addAll(controllerImageOrig);
        imgs.addAll(controllerImageWarp);
        imgs.addAll(controllerImageWarpPaint);

        List<String> arquivos = new ArrayList<>();
        for(String image : imgs){
            arquivos.add(diretorioImg+"/"+image); //Carregando as imagens
        }
        return compactar(caminhoZip, arquivos); //retorna true se funcionou
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
            Log.e("kariti", "Arquivos compactados com sucesso");
            return true;
        }catch(Exception e){
            Log.e("KARITI", "Erro de compactação: "+e.getMessage());
            return false;
        }
    }
}
