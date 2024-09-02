package online.padev.kariti;

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
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;



//faz download das folhas de respostas a serem preenchidas
public class BaixarModeloCartao {
    public static void solicitarCartoesResposta(File arquivo, FileOutputStream fos, File fSaida, String filePdf, DownloadManager baixarPdf) {
        Thread thread = new Thread(() -> {
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
                int inByte;
                byte[]buffer = new byte[1024];
                while((inByte = is.read(buffer)) != -1)
                    fos.write(buffer, 0, inByte);
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
        });
        thread.start();
    }
}