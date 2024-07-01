package online.padev.kariti;

import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class UploadEjson {
    static Integer questao, respostaDada;
    public static void enviarArquivosP(File arquivo, FileOutputStream fos, File dir, BancoDados bancoDados) {
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
                        FileBody x = new FileBody(arquivo);

                        entityBuilder.addPart("userfile[]", x);
                        HttpEntity entity = entityBuilder.build();
                        post.setEntity(entity);
                        HttpResponse response = client.execute(post);
                        HttpEntity httpEntity = response.getEntity();
                        InputStream is = httpEntity.getContent();
                        int inByte;
                        while((inByte = is.read()) != -1)
                            fos.write(inByte);
                        is.close();
                        fos.close();
                        UploadEjson.fimUpload(dir, bancoDados);
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
    public static void fimUpload(File dir, BancoDados bancoDados){
        try {
            String situacao = Environment.getExternalStorageState();
            if (situacao.equals(Environment.MEDIA_MOUNTED)) {
                String result = GaleriaActivity.leitor(dir+"/json.json");
                JSONArray json = new JSONArray(result);
                for (int x = 0; x < json.length(); x++){
                    JSONObject objJson = json.getJSONObject(x);
                    Integer resultCorrect = objJson.getInt("resultado");
                    Integer id_prova = objJson.getInt("id_prova");
                    Integer id_aluno = objJson.getInt("id_aluno");
                    String mensagem = objJson.getString("mensagem");
                    if(resultCorrect.equals(0)){
                        mensagem = mensagem.replaceAll("\\),\\(", ");(");
                        mensagem = mensagem.replaceAll("\\)", "");
                        mensagem = mensagem.replaceAll("\\(", "");
                        mensagem = mensagem.replaceAll(" ", "");
                        String[] itens = mensagem.split(";");
                        Integer questAnterior = null;
                        Integer respostaAnterior = null;
                        for(String item : itens){
                            String[] sep = item.split(",");
                            questao = Integer.valueOf(sep[0]);
                            respostaDada = Integer.valueOf(sep[1]);
                            if(questao.equals(questAnterior)){ // Em caso de duas alternativas marcadas para uma questão
                                String respostaDupla = (respostaAnterior.toString()) + (respostaDada.toString()); // Concatenando as duas respostas
                                respostaDada = Integer.valueOf(respostaDupla);
                            }
                            if(bancoDados.checkResultadoCorrecao(id_prova, id_aluno, questao)){ //Caso prova já corregida anteriormente, realiza UPDATE
                                bancoDados.upadateResultadoCorrecao(id_prova, id_aluno, questao, respostaDada);
                            }else{
                                bancoDados.inserirResultCorrecao(id_prova, id_aluno, questao, respostaDada);
                            }
                            questAnterior = questao;
                            respostaAnterior = respostaDada;

                        }
                    }

                }
            }
        }catch (Exception e){
            Log.e("Kariti", e.toString());
        }
    }
}
