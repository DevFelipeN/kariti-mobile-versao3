package online.padev.kariti;

/*
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebClient {

    public static String post(String json) throws IOException {

        OkHttpClient client = new OkHttpClient();

        String url = "http://www.umsitequalquer.com.br/fazPost";

        Request.Builder builder = new Request.Builder();

        builder.url(url);

        MediaType mediaType = MediaType.parse("text/csv; charset=utf-8"); RequestBody body = RequestBody.create(mediaType, json); builder.post(body);

        Request request = builder.build();

        Response response = client.newCall(request).execute();

        String jsonDeResposta = response.body().string(); return jsonDeResposta;

    }
    public static String get() throws IOException {

        String url = "http://www.umsitequalquer.com.br/fazGet";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();

        String jsonDeResposta = response.body().string();

        return jsonDeResposta; }

}
*/