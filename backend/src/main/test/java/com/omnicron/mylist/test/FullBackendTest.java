package com.omnicron.mylist.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class FullBackendTest {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static final String USER_CREDENTIALS = "admin:admin";

    public static void main(String[] args) {
        try {
            String authHeader = "Basic " + Base64.getEncoder().encodeToString(USER_CREDENTIALS.getBytes());

            // 1️⃣ Criar usuário
            String userJson = """
            {
                "name": "Allan",
                "email": "allan@example.com",
                "password": "123456",
                "salary": 5000
            }
            """;
            sendPostRequest(BASE_URL + "/users", userJson, authHeader);

            // 2️⃣ Listar usuários
            sendGetRequest(BASE_URL + "/users", authHeader);

            // 3️⃣ Criar despesa
            String expenseJson = """
            {
                "description": "Conta de luz",
                "amount": 150,
                "date": "2025-11-15",
                "type": "FIXO",
                "user": { "id": 1 }
            }
            """;
            sendPostRequest(BASE_URL + "/expenses", expenseJson, authHeader);

            // 4️⃣ Listar despesas
            sendGetRequest(BASE_URL + "/expenses", authHeader);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendGetRequest(String urlStr, String authHeader) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", authHeader);

        int status = con.getResponseCode();
        System.out.println("\nGET " + urlStr + " -> Response Code: " + status);

        BufferedReader in = new BufferedReader(new InputStreamReader(
                status >= 200 && status < 400 ? con.getInputStream() : con.getErrorStream()
        ));
        String line;
        StringBuilder content = new StringBuilder();
        while ((line = in.readLine()) != null) content.append(line);
        in.close();
        con.disconnect();

        System.out.println("Response: " + content.toString());
    }

    private static void sendPostRequest(String urlStr, String json, String authHeader) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", authHeader);

        try (OutputStream os = con.getOutputStream()) {
            os.write(json.getBytes("utf-8"));
        }

        int status = con.getResponseCode();
        System.out.println("\nPOST " + urlStr + " -> Response Code: " + status);

        BufferedReader in = new BufferedReader(new InputStreamReader(
                status >= 200 && status < 400 ? con.getInputStream() : con.getErrorStream()
        ));
        String line;
        StringBuilder content = new StringBuilder();
        while ((line = in.readLine()) != null) content.append(line);
        in.close();
        con.disconnect();

        System.out.println("Response: " + content.toString());
    }
}