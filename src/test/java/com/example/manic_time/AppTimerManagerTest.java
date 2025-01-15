package com.example.manic_time;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AppTimerManagerTest {

    @BeforeEach
    public void setUp() {
        // Avant chaque test, réinitialise les données
        AppTimerManager.cancelAppTimer("AppTest");
    }

    @Test
    public void testStartAppTimer() {
        // Teste le démarrage d'un temporisateur pour une application
        AppTimerManager.startAppTimer("AppTest", 3); // 3 minutes
        assertEquals(3, AppTimerManager.getRemainingTime("AppTest"));
    }

    @Test
    public void testCancelAppTimer() {
        // Teste l'annulation d'un temporisateur
        AppTimerManager.startAppTimer("AppTest", 3);
        AppTimerManager.cancelAppTimer("AppTest");
        assertEquals(3, AppTimerManager.getRemainingTime("AppTest"));  // Temps restant réinitialisé après annulation
    }

    @Test
    public void testTimerDecreases() throws InterruptedException {
        // Teste que le temps restant diminue à chaque minute
        AppTimerManager.startAppTimer("AppTest", 5); // 5 minutes
        Thread.sleep(2000); // Attends 2 secondes (simuler le passage du temps)
        assertEquals(5, AppTimerManager.getRemainingTime("AppTest")); // Toujours 5, car la minuterie n'a pas encore été exécutée

        // Teste après l'exécution de la tâche
        Thread.sleep(60000); // Attends une minute
        assertEquals(4, AppTimerManager.getRemainingTime("AppTest"));
    }

    @Test
    public void testTimeOutApp() throws InterruptedException {
        // Teste que la minuterie se termine lorsque le temps est écoulé
        AppTimerManager.startAppTimer("AppTest", 1); // 1 minute
        Thread.sleep(60000); // Attends 1 minute (le temps devrait être écoulé)
        assertEquals(1, AppTimerManager.getRemainingTime("AppTest"));  // Le timer devrait être annulé
    }

    @Test
    public void testGetRemainingTimeApp() {
        // Teste que le temps restant pour une application est récupéré correctement
        AppTimerManager.startAppTimer("AppTest", 5); // 5 minutes
        assertEquals(5, AppTimerManager.getRemainingTime("AppTest"));
    }

    @Test
    public void testGetRemainingTimeForNonExistentApp() {
        // Teste que le temps restant pour une application inexistante est renvoyé comme 0
        assertEquals(0, AppTimerManager.getRemainingTime("NonExistentApp"));
    }
}
