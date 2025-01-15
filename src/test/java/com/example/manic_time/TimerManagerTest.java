package com.example.manic_time;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TimerManagerTest {

    private ScheduledExecutorService mockExecutorService;

    @BeforeEach
    public void setUp() {
        mockExecutorService = Mockito.mock(ScheduledExecutorService.class);
        TimerManager.executorService = mockExecutorService;
        Platform.setImplicitExit(false); // Prevent JavaFX platform from exiting in tests
    }

    @AfterEach
    public void tearDown() {
        TimerManager.executorService.shutdownNow();
    }

    @Test
    public void testStartAppTimer_AppRunning() {
        // Mock AppChecker behavior
        AppChecker appCheckerMock = mock(AppChecker.class);
        when(appCheckerMock.isAppRunning("TestApp")).thenReturn(true);

        // Invoke startAppTimer
        TimerManager.startAppTimer("TestApp", 300);

        // Verify the executor service is scheduled correctly
        verify(mockExecutorService, times(1))
                .scheduleAtFixedRate(any(Runnable.class), eq(0L), eq(5L), eq(TimeUnit.SECONDS));
    }

    @Test
    public void testStartAppTimer_AppNotRunning() {
        // Mock AppChecker behavior
        AppChecker appCheckerMock = mock(AppChecker.class);
        when(appCheckerMock.isAppRunning("TestApp")).thenReturn(false);

        // Invoke startAppTimer
        TimerManager.startAppTimer("TestApp", 300);

        // Verify the executor service is not scheduled for further tasks
        verify(mockExecutorService, never())
                .scheduleAtFixedRate(any(Runnable.class), eq(0L), eq(1L), eq(TimeUnit.SECONDS));
    }

    @Test
    public void testRunTimer_TimerCountsDown() throws InterruptedException {
        // Simulate remaining time
        TimerManager.remainingTime = 5;

        // Run the timer
        TimerManager.runTimer();

        // Wait to ensure the countdown occurs (adjust for your system speed)
        Thread.sleep(6000); // Wait for 6 seconds to simulate countdown

        // Assert that the timer reaches 0
        assertEquals(0, TimerManager.remainingTime);
    }

    @Test
    public void testShowAlert() {
        Platform.runLater(() -> {
            TimerManager.showAlert("Test Alert", "This is a test message.");
        });

        // Since alerts are UI-dependent, we check for execution rather than visible UI
        assertDoesNotThrow(() -> TimerManager.showAlert("Test Alert", "This is a test message."));
    }
}
