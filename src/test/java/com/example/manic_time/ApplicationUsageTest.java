package com.example.manic_time;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApplicationUsageTest {

    @Test
    void testConstructorAndGetters() {
        // Tester le constructeur et les getters
        String appName = "Chrome";
        String timeSpent = "2h 30m";

        // Créer une instance de ApplicationUsage
        ApplicationUsage appUsage = new ApplicationUsage(appName, timeSpent);

        // Vérifier que les valeurs sont correctement initialisées
        assertEquals(appName, appUsage.getApplication(), "Le nom de l'application est incorrect.");
        assertEquals(timeSpent, appUsage.getTime(), "Le temps passé sur l'application est incorrect.");
    }

    @Test
    void testApplicationNameGetter() {
        // Tester le getter du nom de l'application
        String appName = "Firefox";
        String timeSpent = "1h 45m";
        ApplicationUsage appUsage = new ApplicationUsage(appName, timeSpent);

        // Vérifier que le nom de l'application est correctement retourné
        assertEquals(appName, appUsage.getApplication(), "Le nom de l'application retourné est incorrect.");
    }

    @Test
    void testTimeGetter() {
        // Tester le getter du temps passé
        String appName = "Discord";
        String timeSpent = "30m";
        ApplicationUsage appUsage = new ApplicationUsage(appName, timeSpent);

        // Vérifier que le temps passé est correctement retourné
        assertEquals(timeSpent, appUsage.getTime(), "Le temps passé retourné est incorrect.");
    }
}
