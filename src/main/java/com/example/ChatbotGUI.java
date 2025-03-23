package com.example;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ChatbotGUI extends Application {
    private Chatbot chatbot;
    private Scene scene;
    private boolean isDarkMode = true; // Default to dark mode

    @Override
    public void start(Stage primaryStage) {
        // Load API Key
        String apiKey = System.getenv("HUGGINGFACE_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            showError("API Key Missing", "Please set the HUGGINGFACE_API_KEY environment variable.");
            return;
        }
        chatbot = new Chatbot(apiKey);

        // UI Components
        TextArea chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setId("chat-area");

        TextField userInputField = new TextField();
        userInputField.setPromptText("Type your message...");
        userInputField.setId("input-field");

        Button sendButton = new Button("Send");
        sendButton.setId("send-button");

        // Smooth Fade-in Animation for Send Button
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), sendButton);
        fadeIn.setFromValue(0.5);
        fadeIn.setToValue(1.0);
        sendButton.setOnMouseEntered(e -> fadeIn.playFromStart());

        sendButton.setOnAction(event -> {
            String userInput = userInputField.getText().trim();
            if (!userInput.isEmpty()) {
                chatArea.appendText("You: " + userInput + "\n");
                String response = chatbot.getResponse(userInput);
                chatArea.appendText("Bot: " + response + "\n\n");
                userInputField.clear();
            }
        });

        // Centering Send Button
        HBox sendButtonContainer = new HBox(sendButton);
        sendButtonContainer.setAlignment(Pos.CENTER);

        // Theme Toggle Button (Styled with CSS)
        Button themeToggleButton = new Button("🌙 Tap To Switch Theme");
        themeToggleButton.setId("theme-toggle");
        themeToggleButton.setOnAction(e -> toggleTheme(themeToggleButton));

        // Centering Toggle Button
        HBox toggleContainer = new HBox(themeToggleButton);
        toggleContainer.setAlignment(Pos.CENTER);

        // Layout Setup
        VBox root = new VBox(10, chatArea, userInputField, sendButtonContainer, toggleContainer);
        root.setPadding(new Insets(10));
        root.setId("root");

        // Load CSS
        scene = new Scene(root, 400, 500);
        String cssPath = getClass().getClassLoader().getResource("styles.css").toExternalForm();
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        } else {
            showError("CSS Error", "Failed to load styles.css");
        }

        // Apply default dark mode style
        root.getStyleClass().add("dark-mode");

        // Stage Setup
        primaryStage.setTitle("AI Chatbot");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void toggleTheme(Button toggleButton) {
        VBox root = (VBox) scene.getRoot(); // Ensure correct root reference
        if (isDarkMode) {
            root.getStyleClass().remove("dark-mode");
            root.getStyleClass().add("light-mode");
            toggleButton.setText("🌞 Light Mode");
        } else {
            root.getStyleClass().remove("light-mode");
            root.getStyleClass().add("dark-mode");
            toggleButton.setText("🌙 Dark Mode");
        }
        isDarkMode = !isDarkMode;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
