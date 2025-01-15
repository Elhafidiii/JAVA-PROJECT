package com.example.manic_time;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTimeTest {

    @Test
    void testConstructorWithIcon() {
        // Tester le constructeur avec icône
        String appName = "chrome";
        String timeLimit = "2h";
        String remainingTime = "1h 30m";
        ImageView appIcon = AppIconUtil.getApplicationIcon(appName);

        // Créer une instance de ApplicationTime
        ApplicationTime applicationTime = new ApplicationTime(appName, timeLimit, remainingTime, appIcon);

        // Vérifier les valeurs des propriétés
        assertEquals(appName, applicationTime.getAppName(), "Le nom de l'application est incorrect.");
        assertEquals(timeLimit, applicationTime.getTimeLimit(), "La limite de temps est incorrecte.");
        assertEquals(remainingTime, applicationTime.getRemainingTime(), "Le temps restant est incorrect.");
        assertNotNull(applicationTime.getAppIcon(), "L'icône de l'application ne doit pas être nulle.");
    }

    @Test
    void testConstructorWithoutIcon() {
        // Tester le constructeur sans icône
        String appName = "firefox";
        String timeLimit = "3h";
        String remainingTime = "2h";

        // Créer une instance de ApplicationTime
        ApplicationTime applicationTime = new ApplicationTime(appName, timeLimit, remainingTime);

        // Vérifier les valeurs des propriétés
        assertEquals(appName, applicationTime.getAppName(), "Le nom de l'application est incorrect.");
        assertEquals(timeLimit, applicationTime.getTimeLimit(), "La limite de temps est incorrecte.");
        assertEquals(remainingTime, applicationTime.getRemainingTime(), "Le temps restant est incorrect.");

        // Vérifier que l'icône est générée
        assertNotNull(applicationTime.getAppIcon(), "L'icône de l'application ne doit pas être nulle.");
    }

    @Test
    void testSettersAndGetters() {
        // Tester les setters et getters
        ApplicationTime applicationTime = new ApplicationTime("discord", "4h", "3h");

        // Vérifier les getters initiaux
        assertEquals("discord", applicationTime.getAppName());
        assertEquals("4h", applicationTime.getTimeLimit());
        assertEquals("3h", applicationTime.getRemainingTime());

        // Modifier les valeurs via les setters
        applicationTime.setAppName("zoom");
        applicationTime.setTimeLimit("5h");
        applicationTime.setRemainingTime("4h");

        // Vérifier que les setters ont bien mis à jour les valeurs
        assertEquals("zoom", applicationTime.getAppName());
        assertEquals("5h", applicationTime.getTimeLimit());
        assertEquals("4h", applicationTime.getRemainingTime());
    }

    @Test
    void testAppIconIsGeneratedCorrectly() {
        // Tester si l'icône est générée correctement pour une application spécifique
        String appName = "android studio";

        // Créer une instance de ApplicationTime
        ApplicationTime applicationTime = new ApplicationTime(appName, "2h", "1h");

        // Vérifier que l'icône est générée pour Android Studio
        assertNotNull(applicationTime.getAppIcon(), "L'icône pour Android Studio ne doit pas être nulle.");
        assertTrue(applicationTime.getAppIcon().getImage().getUrl().contains("3DDC84"), "L'icône pour Android Studio n'est pas correcte.");
    }

    @Test
    void testDefaultConstructor() {
        // Tester le constructeur par défaut
        String appName = "unknownApp";
        ApplicationTime applicationTime = new ApplicationTime(appName, "1h", "30m");

        // Vérifier la génération de l'icône pour une application inconnue
        assertNotNull(applicationTime.getAppIcon(), "L'icône de l'application inconnue ne doit pas être nulle.");
        assertEquals("📌", ((Label) applicationTime.getAppIcon().getParent().getChildrenUnmodifiable().get(1)).getText(), "Le texte de l'icône pour une application inconnue est incorrect.");
    }
}
