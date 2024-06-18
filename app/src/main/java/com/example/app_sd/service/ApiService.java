package com.example.app_sd.service;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class ApiService{

    private String endpoint;
    private String method;
    private String jsonBody;

    private final int TIMEOUT = 3000;
    private String baseUrl = "http://192.168.0.118:8080/api";



    public void setEndpoint(String endpoint){
        this.endpoint = endpoint;
    }

    public String getEndpoint(){
        return this.endpoint;
    }

    public void setMethod(String method){
        this.method = method;
    }

    public String getMethod(){
        return this.method;
    }

    public void setJsonBody(String jsonBody){
        this.method = method;
    }

    public String getJsonBody(){
        return this.jsonBody;
    }

    public String request(String method,String urlString, String jsonInputString) throws Exception {
        try {
            URL url = new URL(this.baseUrl + urlString);
            Log.i("Rota","Method: "+method+" - "+ this.baseUrl + urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(TIMEOUT);
            conn.setReadTimeout(TIMEOUT);

            if (method.equals("POST") || method.equals("PUT")) {
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                if (jsonInputString != null) {
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }
                }
            }

            return getResponse(conn);
        } catch (Exception e) {

            return null;
        }
    }
    private String getResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        Log.e("Teste Resp", String.valueOf(responseCode));
        if (responseCode == 201 || responseCode == 200) {
            Log.e("Resp", "entrou no if");
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Log.e("Resp", response.toString());
                return response.toString();
            }


        } else {
            Log.e("HTTP error code", ""+responseCode);
            throw new IOException("HTTP error code: " + responseCode);
        }
    }


}