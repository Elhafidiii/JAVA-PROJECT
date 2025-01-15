package com.example.manic_time;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppFetcherTest {

    @Test
    void testGetInstalledApplications() {
        // Simuler la sortie du processus PowerShell
        String simulatedOutput = "App1\nApp2\nApp3\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedOutput.getBytes());

        try (MockedStatic<Runtime> mockedRuntime = mockStatic(Runtime.class)) {
            Runtime mockRuntime = mock(Runtime.class);
            mockedRuntime.when(Runtime::getRuntime).thenReturn(mockRuntime);

            Process mockProcess = mock(Process.class);
            when(mockRuntime.exec(anyString())).thenReturn(mockProcess);
            when(mockProcess.getInputStream()).thenReturn(inputStream);

            // Appeler la méthode
            List<String> installedApps = AppFetcher.getInstalledApplications();

            // Vérifications
            assertNotNull(installedApps);
            assertEquals(Arrays.asList("App1", "App2", "App3"), installedApps);
        } catch (Exception e) {
            fail("Une exception inattendue s'est produite : " + e.getMessage());
        }
    }

    @Test
    void testIsAppRunning_AppIsRunning() {
        // Simuler la sortie du processus tasklist
        String simulatedOutput = "notepad.exe    1234 Console    1   12,345 K\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedOutput.getBytes());

        try (MockedStatic<ProcessBuilder> mockedProcessBuilder = mockStatic(ProcessBuilder.class)) {
            ProcessBuilder mockBuilder = mock(ProcessBuilder.class);
            Process mockProcess = mock(Process.class);

            mockedProcessBuilder.when(() -> new ProcessBuilder("tasklist")).thenReturn(mockBuilder);
            when(mockBuilder.start()).thenReturn(mockProcess);
            when(mockProcess.getInputStream()).thenReturn(inputStream);

            // Appeler la méthode
            boolean isRunning = AppFetcher.isAppRunning("notepad.exe");

            // Vérifications
            assertTrue(isRunning);
        } catch (Exception e) {
            fail("Une exception inattendue s'est produite : " + e.getMessage());
        }
    }

    @Test
    void testIsAppRunning_AppIsNotRunning() {
        // Simuler une sortie où l'application n'est pas présente
        String simulatedOutput = "otherapp.exe    1234 Console    1   12,345 K\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedOutput.getBytes());

        try (MockedStatic<ProcessBuilder> mockedProcessBuilder = mockStatic(ProcessBuilder.class)) {
            ProcessBuilder mockBuilder = mock(ProcessBuilder.class);
            Process mockProcess = mock(Process.class);

            mockedProcessBuilder.when(() -> new ProcessBuilder("tasklist")).thenReturn(mockBuilder);
            when(mockBuilder.start()).thenReturn(mockProcess);
            when(mockProcess.getInputStream()).thenReturn(inputStream);

            // Appeler la méthode
            boolean isRunning = AppFetcher.isAppRunning("notepad.exe");

            // Vérifications
            assertFalse(isRunning);
        } catch (Exception e) {
            fail("Une exception inattendue s'est produite : " + e.getMessage());
        }
    }
}
