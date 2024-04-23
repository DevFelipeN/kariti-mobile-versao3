package com.example.kariti;

import android.app.DownloadManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.io.OutputStreamWriter;
import java.io.InputStream;
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
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            Log.e("Kariti", "B");
            try {
                Log.e("Kariti", "B1");
                    //OutputStream output = connection.getOutputStream();
                DataOutputStream output = new DataOutputStream(connection.getOutputStream());
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
    public static void solicitarCartoesResposta(File arquivo, FileOutputStream fos, File fSaida, String filePdf, DownloadManager baixarPdf) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    try {
                        Log.e("kariti", "L1");
                        String URL = "http://kariti.online/src/services/download_template/download.php";
                        HttpClient client = new DefaultHttpClient();
                        Log.e("kariti", "L2");
                        HttpPost post = new HttpPost(URL);
                        Log.e("kariti", "L3");

                        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                        Log.e("kariti", "L4");
                        //entityBuilder.addBinaryBody("userfile[]", arquivo);
                        FileBody x = new FileBody(arquivo);

                        entityBuilder.addPart("userfile[]", x);
                        Log.e("kariti", "L5");
                        HttpEntity entity = entityBuilder.build();
                        post.setEntity(entity);
                        Log.e("kariti", "L6");
                        HttpResponse response = client.execute(post);
                        Log.e("kariti", "L7");
                        HttpEntity httpEntity = response.getEntity();
                        Log.e("kariti", "L8");
                        InputStream is = httpEntity.getContent();
                        Log.e("kariti", "L9");
                        String filePath = "sample.txt";
                        //FileOutputStream fos = new FileOutputStream(new File(filePath));
                        int inByte;
                        while((inByte = is.read()) != -1)
                            fos.write(inByte);
                        Log.e("kariti", "L10");
                        is.close();
                        Log.e("kariti", "L11");
                        fos.close();
                        baixarPdf.addCompletedDownload(filePdf, "Cartao Resposta: " + filePdf, true, "application /pdf", fSaida.getAbsolutePath(), fSaida.length(), true);
                        Log.e("Kariti", "Fim");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("kariti", e.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}