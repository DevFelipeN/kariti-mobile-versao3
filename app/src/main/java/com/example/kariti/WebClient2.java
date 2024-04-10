package com.example.kariti;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WebClient2 {

    public String post(String json) throws IOException {

        URL url = new URL("http://www.umsitequalquer.com.br/fazPost");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");

        connection.setRequestProperty("Content-type", "application/json"); connection.setRequestProperty("Accept", "application/json");

        connection.setDoOutput(true);

        PrintStream printStream = new PrintStream(connection.getOutputStream()); printStream.println(json);

        connection.connect();

        String jsonDeResposta = new Scanner(connection.getInputStream()).next();

        return jsonDeResposta; }

    public String get() throws IOException {

        URL url = new URL("http://www.umsitequalquer.com.br/fazGet");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        connection.setRequestProperty("Accept", "application/json");

        connection.connect();

        String jsonDeResposta = new Scanner(connection.getInputStream()).next();

        return jsonDeResposta; }

}