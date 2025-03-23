package com.example;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class Chatbot {
    private String apiKey;
    private static final String API_URL = "https://router.huggingface.co/hf-inference/models/facebook/blenderbot-400M-distill";

    public Chatbot(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getResponse(String userInput) {
        try {
            // Create HTTP connection
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // JSON payload
            String jsonInputString = "{\"inputs\": \"" + userInput + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                // Parse JSON response
                return parseResponse(response.toString());
            } else {
                return "Error: API request failed with code " + responseCode;
            }
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    private String parseResponse(String jsonResponse) {
        try {
            JSONArray responseArray = new JSONArray(jsonResponse);
            if (responseArray.length() > 0) {
                JSONObject responseObject = responseArray.getJSONObject(0);
                return responseObject.optString("generated_text", "I don't understand.");
            }
        } catch (Exception e) {
            return "Error parsing response.";
        }
        return "Unexpected response format.";
    }
}
