package com.example.kariti;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;

//faz download das folhas de respostas a serem preenchidas
public class BaixarModeloCartao {
    public static void main(String[] args){
        //Teste enviando dois arquivos (um jpg e outro zip) para serem corrigidos
        //boolean resultado = baixarProvas("D:\\Arquivos\\Downloads\\kariti-main\\kariti-main\\src\\test_core\\entrada.csv","D:\\teste.pdf");
        //System.out.println(resultado);
    }

    public static boolean baixarProvas(FileInputStream is, String nomeCSV, FileOutputStream outputStream){
        try{
            String url = "http://kariti.online/src/services/download_template/download.php";
            String charset = "UTF-8";
            List<FileInputStream> arquivos = new ArrayList<>();
            arquivos.add(is);
            Log.e("Kariti", "A");
            String boundary = Long.toHexString(System.currentTimeMillis());
            String CRLF = "\r\n";

            URLConnection connection = new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            Log.e("Kariti", "B");
            try {
                Log.e("Kariti", "B1");
                    OutputStream output = connection.getOutputStream();
                Log.e("Kariti", "B2");
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);

                Log.e("Kariti", "C");
                for (FileInputStream arq : arquivos){
                    writer.append("--" + boundary).append(CRLF);
                    writer.append("Content-Disposition: form-data; name=\"userfile[]\"; filename=\"" + nomeCSV + "\"").append(CRLF);
                    writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(nomeCSV)).append(CRLF);
                    writer.append("Content-Transfer-Encoding: binary").append(CRLF);
                    writer.append(CRLF).flush();
                    //Files.copy(arq.toPath(), output);
                    copy(arq, output);
                    output.flush(); // Important before continuing with writer!
                    writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.
                    Log.e("Kariti", "D");
                }
                writer.append("--" + boundary + "--").append(CRLF).flush();
                Log.e("Kariti", "E");
            }catch(Exception e){
                Log.e("Kariti", "ErroDesc");
            }

            HttpURLConnection cc = ((HttpURLConnection) connection);
            int code = cc.getResponseCode();
            Log.e("Kariti", "F");
            if (code == 200){
                InputStream inputStream = cc.getInputStream();

                Log.e("Kariti", "G");
                int bytesRead = -1;
                byte[] buffer = new byte[4096];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();
                return true;
            }else{
                return false;
            }
        }catch(Exception exc){
            Log.e("Kariti", exc.toString());
            return false;
        }
    }
    public static void copy(InputStream in, OutputStream out) throws IOException {
        try {
            //OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}