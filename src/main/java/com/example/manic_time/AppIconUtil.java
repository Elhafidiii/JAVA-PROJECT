package com.example.manic_time;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

public class AppIconUtil {
    
    public static ImageView getApplicationIcon(String appName) {
        // Créer un conteneur pour l'icône
        StackPane iconContainer = new StackPane();
        iconContainer.setPrefSize(24, 24);
        
        // Créer le fond de l'icône
        Rectangle background = new Rectangle(24, 24);
        background.setArcWidth(8);
        background.setArcHeight(8);
        background.setFill(getColorForApp(appName));
        background.setStroke(Color.TRANSPARENT);
        
        // Ajouter un effet d'ombre
        background.setEffect(new javafx.scene.effect.DropShadow(3, Color.gray(0, 0.2)));
        
        // Créer le texte de l'icône
        Label iconText = new Label(getIconTextForApp(appName));
        iconText.setFont(Font.font("FontAwesome", 13));
        iconText.setTextFill(Color.WHITE);
        iconText.setEffect(new javafx.scene.effect.DropShadow(1, Color.gray(0, 0.2)));
        
        // Assembler l'icône
        iconContainer.getChildren().addAll(background, iconText);
        
        // Créer un snapshot de l'icône
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage image = iconContainer.snapshot(params, null);
        
        // Créer et retourner l'ImageView
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(24);
        imageView.setFitWidth(24);
        return imageView;
    }
    
    private static Color getColorForApp(String appName) {
        switch (appName.toLowerCase()) {
            case "chrome":
                return Color.web("#4285F4");
            case "firefox":
                return Color.web("#FF7139");
            case "discord":
                return Color.web("#5865F2");
            case "Bluestacks":
                return Color.web("#0099FF");
            case "Android Studio":
                return Color.web("#3DDC84");
            default:
                return Color.web("#808080");
        }
    }
    
    private static String getIconTextForApp(String appName) {
        switch (appName.toLowerCase()) {
            case "chrome":
                return "🌐";
            case "firefox":
                return "🦊";
            case "discord":
                return "💬";
            case "Bluestacks":
                return "📱";
            case "Android Studio":
                return "⚙️";
            default:
                return "📌";
        }
    }
} 