package com.example.kariti;
import android.os.Build;

import java.io.File;
import java.net.URLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;
public class Json {
    public static void main(String[] args){
        testar();
    }
    public static void testar(){
        //Teste enviando dois arquivos (um jpg e outro zip) para serem corrigidos
        List<String> c = new ArrayList<>();
        c.add("/storage/emulated/0/Android/data/com.example.kariti/files/Cartoes/saida.zip");
        String resultadoJSON = enviarProvas(c);
        //return resultadoJSON;
    }
    public static String enviarProvas(List<String> caminhosArquivos){
        String result;
        try{
            String url = "http://kariti.online/src/pages/test/correct_test/core.php";
            String charset = "UTF-8";
            List<File> arquivos = new ArrayList<>();
            for (String caminho : caminhosArquivos){
                arquivos.add(new File(caminho));
            }
            String boundary = Long.toHexString(System.currentTimeMillis());
            String CRLF = "\r\n";

            URLConnection connection = new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (
                    OutputStream output = connection.getOutputStream(); PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
            ) {

                for (File arq : arquivos){
                    writer.append("--" + boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"userfile[]\"; filename=\"" + arq.getName() + "\"").append(CRLF);
                    writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(arq.getName())).append(CRLF);
                    writer.append("Content-Transfer-Encoding: binary").append(CRLF);
                    writer.append(CRLF).flush();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Files.copy(arq.toPath(), output);
                    }
                    output.flush(); // Important before continuing with writer!
                    writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.
                }
                writer.append("--" + boundary + "--").append(CRLF).flush();
            }

            HttpURLConnection cc = ((HttpURLConnection) connection);
            int code = cc.getResponseCode();
            if (code == 200){
                BufferedReader in = new BufferedReader(new InputStreamReader(cc.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                result = response.toString();
            }else{
                result = "{\"responseCode\": \"" + code + "\", \"responseMessage\": \"" + cc.getResponseMessage() + "\"}";
            }
        }catch(Exception exc){
            result = "{\"responseCode\": \"-1\", \"responseMessage\": \"" + exc.getMessage() + "\"}";
        }
        return result;
    }
}
