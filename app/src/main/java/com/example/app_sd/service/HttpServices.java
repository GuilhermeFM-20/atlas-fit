package com.example.app_sd.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.AsyncTask;
import android.util.Log;

public class HttpServices {

    private final int TIMEOUT = 3000;
    private String baseUrl = "http://192.168.0.117:8000/api";



    public void sendGetRequest(String endpoint, ResponseCallback callback) {
        Log.i("GET Route",endpoint);
        new NetworkTask("GET", endpoint, null, callback).execute();
    }

    public void sendPostRequest(String endpoint, String jsonInputString, ResponseCallback callback) {
        new NetworkTask("POST", endpoint, jsonInputString, callback).execute();
    }

    public void sendPutRequest(String endpoint, String jsonInputString, ResponseCallback callback) {
        new NetworkTask("PUT", endpoint, jsonInputString, callback).execute();
    }

    public void sendDeleteRequest(String endpoint, ResponseCallback callback) {
        new NetworkTask("DELETE", endpoint, null, callback).execute();
    }

    private class NetworkTask extends AsyncTask<Void, Void, String> {

        private String method;
        private String endpoint;
        private String jsonInputString;
        private ResponseCallback callback;
        private Exception exception;

        NetworkTask(String method, String endpoint, String jsonInputString, ResponseCallback callback) {
            this.method = method;
            this.endpoint = endpoint;
            this.jsonInputString = jsonInputString;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(baseUrl + endpoint);
                Log.i("Rota","Method: "+method+" - "+ baseUrl + endpoint);
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
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (exception != null) {
                callback.onFailure(exception);
            } else {
                callback.onSuccess(result);
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
                throw new IOException("HTTP error code: " + responseCode);
            }
        }
    }

    public interface ResponseCallback {
        void onSuccess(String response);
        void onFailure(Exception exception);
    }
}