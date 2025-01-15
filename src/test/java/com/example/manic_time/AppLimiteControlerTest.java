package com.example.manic_time;

import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class AppLimiteControlerTest {

    private AppLimiteControler controller;
    private ComboBox<String> appComboBox;
    private TableView<ApplicationTime> timeLimitsTable;

    @BeforeEach
    void setUp() {
        // Initialisation du contrôleur et des mocks
        controller = new AppLimiteControler();
        appComboBox = mock(ComboBox.class);
        timeLimitsTable = mock(TableView.class);

        // Injecter les mocks dans le contrôleur
        controller.appComboBox = appComboBox;
        controller.timeLimitsTable = timeLimitsTable;
    }

    @Test
    void testInitialize() {
        // Simuler le retour d'applications installées
        List<String> installedApps = Arrays.asList("Chrome", "Firefox", "Discord");

        // Mock de AppFetcher.getInstalledApplications()
        AppFetcher appFetcherMock = mock(AppFetcher.class);
        when(appFetcherMock.getInstalledApplications()).thenReturn(installedApps);

        // Appel de la méthode initialize
        controller.initialize();

        // Vérifier que les éléments de ComboBox ont été ajoutés
        verify(appComboBox).getItems().addAll(installedApps);
    }

    @Test
    void testHandleAddLimit() {
        // Mock des éléments d'interface utilisateur
        ComboBox<String> appComboBox = mock(ComboBox.class);
        TextField hoursField = mock(TextField.class);
        TextField minutesField = mock(TextField.class);
        TableView<ApplicationTime> timeLimitsTable = mock(TableView.class);

        // Simuler la sélection de l'application et des champs de texte
        when(appComboBox.getValue()).thenReturn("Chrome");
        when(hoursField.getText()).thenReturn("1");
        when(minutesField.getText()).thenReturn("30");

        // Appel de la méthode handleAddLimit
        controller.handleAddLimit();

        // Vérifier que le tableau a bien été mis à jour
        verify(timeLimitsTable).getItems();  // Vérifie si le tableau a été mis à jour
        verify(timeLimitsTable).refresh();
    }

    @Test
    void testStartTimerForApp() {
        // Simuler l'application et la limite de temps
        String appName = "Chrome";
        int hours = 1;
        int minutes = 30;

        // Mock des objets Timeline et AtomicInteger
        Timeline timelineMock = mock(Timeline.class);
        AtomicInteger remainingTimeMock = mock(AtomicInteger.class);

        // Appel de la méthode startTimerForApp
        controller.startTimerForApp(appName, hours, minutes);

        // Vérifier que le minuteur a été démarré
        verify(timelineMock).play();
    }

    @Test
    void testHandleCancelTimer() {
        // Simuler la sélection d'une application
        TableView<ApplicationTime> timeLimitsTable = mock(TableView.class);
        ApplicationTime selectedApp = mock(ApplicationTime.class);
        when(timeLimitsTable.getSelectionModel().getSelectedItem()).thenReturn(selectedApp);
        when(selectedApp.getAppName()).thenReturn("Chrome");

        // Simuler un minuteur actif
        //Map<String, TimerData> appTimers = new HashMap<>();
        //appTimers.put("Chrome", new TimerData(mock(Timeline.class), mock(AtomicInteger.class), mock(SimpleBooleanProperty.class), mock(Timeline.class)));

        // Appeler handleCancelTimer
        controller.handleCancelTimer();

        // Vérifier que le minuteur a été arrêté et l'application supprimée
        verify(timeLimitsTable).getItems();
    }


}