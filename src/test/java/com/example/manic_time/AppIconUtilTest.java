package com.example.manic_time;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppIconUtilTest {

    @Test
    void testGetApplicationIcon_Chrome() {
        ImageView icon = AppIconUtil.getApplicationIcon("chrome");

        // Vérifier la couleur de fond pour Chrome
        Rectangle background = (Rectangle) icon.getParent().getChildrenUnmodifiable().get(0);
        assertEquals(Color.web("#4285F4"), background.getFill(), "La couleur de fond pour Chrome est incorrecte.");

        // Vérifier le texte de l'icône pour Chrome
        String iconText = ((Label) icon.getParent().getChildrenUnmodifiable().get(1)).getText();
        assertEquals("🌐", iconText, "Le texte de l'icône pour Chrome est incorrect.");

        // Vérifier les dimensions de l'ImageView
        assertEquals(24, icon.getFitHeight(), "La hauteur de l'icône est incorrecte.");
        assertEquals(24, icon.getFitWidth(), "La largeur de l'icône est incorrecte.");
    }

    @Test
    void testGetApplicationIcon_Firefox() {
        ImageView icon = AppIconUtil.getApplicationIcon("firefox");

        // Vérifier la couleur de fond pour Firefox
        Rectangle background = (Rectangle) icon.getParent().getChildrenUnmodifiable().get(0);
        assertEquals(Color.web("#FF7139"), background.getFill(), "La couleur de fond pour Firefox est incorrecte.");

        // Vérifier le texte de l'icône pour Firefox
        String iconText = ((Label) icon.getParent().getChildrenUnmodifiable().get(1)).getText();
        assertEquals("🦊", iconText, "Le texte de l'icône pour Firefox est incorrect.");

        // Vérifier les dimensions de l'ImageView
        assertEquals(24, icon.getFitHeight(), "La hauteur de l'icône est incorrecte.");
        assertEquals(24, icon.getFitWidth(), "La largeur de l'icône est incorrecte.");
    }

    @Test
    void testGetApplicationIcon_UnknownApp() {
        ImageView icon = AppIconUtil.getApplicationIcon("unknownApp");

        // Vérifier la couleur de fond pour une application inconnue
        Rectangle background = (Rectangle) icon.getParent().getChildrenUnmodifiable().get(0);
        assertEquals(Color.web("#808080"), background.getFill(), "La couleur de fond pour une application inconnue est incorrecte.");

        // Vérifier le texte de l'icône pour une application inconnue
        String iconText = ((Label) icon.getParent().getChildrenUnmodifiable().get(1)).getText();
        assertEquals("📌", iconText, "Le texte de l'icône pour une application inconnue est incorrect.");

        // Vérifier les dimensions de l'ImageView
        assertEquals(24, icon.getFitHeight(), "La hauteur de l'icône est incorrecte.");
        assertEquals(24, icon.getFitWidth(), "La largeur de l'icône est incorrecte.");
    }
}
