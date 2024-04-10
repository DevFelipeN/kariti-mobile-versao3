package com.example.kariti;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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

    public static void download(String url) {
        /*
        Util.enableConsoleLog();
        DownloadTask task = new DownloadTask.Builder(url,
                new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download" + File.separator)) //设置下载地址和下载目录，这两个是必须的参数
                .setFilename("update.zip")                      // 设置下载文件名，没提供的话先看 response header ，再看 url path(即启用下面那项配置)
                .setFilenameFromResponse(false)                 // 是否使用 response header or url path 作为文件名，此时会忽略指定的文件名，默认false
                .setPassIfAlreadyCompleted(true)                // 如果文件已经下载完成，再次下载时，是否忽略下载，默认为true(忽略)，设为false会从头下载
                .setConnectionCount(1)                          // 需要用几个线程来下载文件，默认根据文件大小确定；如果文件已经 split block，则设置后无效
                .setPreAllocateLength(false)                    // 在获取资源长度后，设置是否需要为文件预分配长度，默认false
                .setMinIntervalMillisCallbackProcess(100)       // 通知调用者的频率，避免anr，默认3000
                .setWifiRequired(false)                         // 是否只允许wifi下载，默认为false
                .setAutoCallbackToUIThread(true)                // 是否在主线程通知调用者，默认为true
                //.setHeaderMapFields(new HashMap<String, List<String>>()) // 设置请求头
                //.addHeader(String key, String value) // 追加请求头
                .setPriority(0)                                             // 设置优先级，默认值是0，值越大下载优先级越高
                .setReadBufferSize(4096)                                    // 设置读取缓存区大小，默认4096
                .setFlushBufferSize(16384)                                  // 设置写入缓存区大小，默认16384
                .setSyncBufferSize(65536)                                   // 写入到文件的缓冲区大小，默认65536
                .setSyncBufferIntervalMillis(2000)                          // 写入文件的最小时间间隔，默认2000
                .build();

        task.enqueue(new DownloadListener() {
            @Override
            public void taskStart(@NonNull DownloadTask task) {
                Log.d(TAG, "taskStart");
            }

            @Override
            public void connectTrialStart(@NonNull DownloadTask task, @NonNull Map<String, List<String>> requestHeaderFields) {
                Log.d(TAG, "connectTrialStart");
            }

            @Override
            public void connectTrialEnd(@NonNull DownloadTask task, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
                Log.d(TAG, "connectTrialEnd");
            }

            @Override
            public void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @NonNull ResumeFailedCause cause) {
                Log.d(TAG, "downloadFromBeginning");
            }

            @Override
            public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {
                Log.d(TAG, "downloadFromBreakpoint");
            }

            @Override
            public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {
                Log.d(TAG, "connectStart");
            }

            @Override
            public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
                Log.d(TAG, "connectEnd");
            }

            @Override
            public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {
                Log.d(TAG, "fetchStart");
            }

            @Override
            public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {
                Log.d(TAG, "fetchProgress");
            }

            @Override
            public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {
                Log.d(TAG, "fetchEnd");
            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                Log.d(TAG, "taskEnd");
            }
        });
         */

    }
    public static void teste(InputStream arquivo, FileOutputStream fos) {
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
            /*
            entityBuilder.addTextBody(USER_ID, userId);
            entityBuilder.addTextBody(NAME, name);
            entityBuilder.addTextBody(TYPE, type);
            entityBuilder.addTextBody(COMMENT, comment);
            entityBuilder.addTextBody(LATITUDE, String.valueOf(User.Latitude));
            entityBuilder.addTextBody(LONGITUDE, String.valueOf(User.Longitude));
            */
                        entityBuilder.addBinaryBody("userfile[]", arquivo);
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
    public static void armazenar(InputStream inputStream, FileOutputStream fOut){
        try {

            //FileOutputStream fOut = openFileOutput(file,MODE_WORLD_READABLE);
            //InputStream inputStream = cc.getInputStream();

            Log.e("Kariti", "G");
            int bytesRead = -1;
            byte[] buffer = new byte[4096];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fOut.write(buffer, 0, bytesRead);
            }

            fOut.close();
            inputStream.close();
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}