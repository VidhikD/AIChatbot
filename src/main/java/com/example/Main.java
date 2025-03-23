package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private Chatbot chatbot; // Chatbot instance

    @Override
    public void start(Stage primaryStage) {
        // Get API key from environment variable
        String apiKey = System.getenv("HUGGINGFACE_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            showErrorDialog("API Key Missing", "Set the HUGGINGFACE_API_KEY environment variable.");
            return;
        }

        chatbot = new Chatbot(apiKey); // Initialize chatbot with API key

        primaryStage.setTitle("AI Chatbot");

        // Chat display area
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(400);

        // User input field
        TextField userInputField = new TextField();
        userInputField.setPromptText("Type your message...");

        // Send button
        Button sendButton = new Button("Send");
        sendButton.setOnAction(event -> {
            String userInput = userInputField.getText().trim();
            if (!userInput.isEmpty()) {
                chatArea.appendText("You: " + userInput + "\n");
                String response = chatbot.getResponse(userInput);
                chatArea.appendText("AI Chatbot: " + response + "\n\n");
                userInputField.clear();
            }
        });

        // Layout setup
        VBox layout = new VBox(10, chatArea, userInputField, sendButton);
        layout.setPadding(new Insets(10));

        // Scene and stage setup
        Scene scene = new Scene(layout, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Displays an error dialog if API key is missing.
     */
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        System.exit(0); // Exit app if API key is missing
    }

    public static void main(String[] args) {
        launch(args); // Launch JavaFX application
    }
}
