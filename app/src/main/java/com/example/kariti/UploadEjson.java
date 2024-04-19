package com.example.kariti;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class UploadEjson {
    public static void enviarArquivosP(File arquivo, FileOutputStream fos) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    try {
                        String URL = "http://kariti.online/src/pages/test/correct_test/core.php";
                        HttpClient client = new DefaultHttpClient();
                        HttpPost post = new HttpPost(URL);

                        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                        //entityBuilder.addBinaryBody("userfile[]", arquivo);
                        FileBody x = new FileBody(arquivo);

                        entityBuilder.addPart("userfile[]", x);
                        HttpEntity entity = entityBuilder.build();
                        post.setEntity(entity);
                        HttpResponse response = client.execute(post);
                        HttpEntity httpEntity = response.getEntity();
                        InputStream is = httpEntity.getContent();
                        String filePath = "sample.txt";
                        //FileOutputStream fos = new FileOutputStream(new File(filePath));
                        int inByte;
                        while((inByte = is.read()) != -1)
                            fos.write(inByte);
                        is.close();
                        fos.close();
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
