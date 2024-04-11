package com.example.kariti;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Json {
    private  static final String TAG= Json.class.getSimpleName();
    public Json(){

    }

    public String httpServiceCall(String requestUrl){
        String  result = null;
        try{
            URL url = new URL(requestUrl);
            HttpURLConnection conection = (HttpURLConnection) url.openConnection();
            conection.setRequestMethod("GET");

            InputStream inputStream = new BufferedInputStream(conection.getInputStream());
            result = convertResultToString(inputStream);
        }catch (MalformedURLException e){
            e.printStackTrace();
        } catch (ProtocolException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String convertResultToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        String li;

        while (true){
            try {
                if (!((li = bufferedReader.readLine())!=null)){
                    stringBuilder.append('\n');
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return  stringBuilder.toString();

        }

    }
}
