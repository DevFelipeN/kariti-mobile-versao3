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

public class BaixarResultadoCorrecao {
    public static void solicitarResultadoCorrecao(File arquivo, FileOutputStream fos, File fSaida, String filePdf, DownloadManager baixarPdf) {
        Thread thread = new Thread(() -> {
            try {
                String URL = "http://kariti.online/src/services/download_grades/download.php";
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(URL);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                FileBody x = new FileBody(arquivo);
                entityBuilder.addPart("userfile[]", x);
                HttpEntity entity = entityBuilder.build();
                post.setEntity(entity);
                HttpResponse response = client.execute(post);
                HttpEntity httpEntity = response.getEntity();
                InputStream is = httpEntity.getContent();
                int inByte;
                byte[]buffer = new byte[1024];
                while((inByte = is.read(buffer)) != -1)
                    fos.write(buffer, 0, inByte);
                is.close();
                fos.close();
                baixarPdf.addCompletedDownload(filePdf, "Cartao Resposta: " + filePdf, true, "application /pdf", fSaida.getAbsolutePath(), fSaida.length(), true);
                Log.e("Kariti", "Fim");
            } catch (Exception e) {
                Log.e("kariti", e.toString());
            }
        });
        thread.start();
    }
}
