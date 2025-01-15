package com.example.manic_time;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class AppCheckerTest {

    @Test
    void testIsAppRunning_AppFound() throws Exception {
        // Mock the Process to simulate the output of "tasklist"
        Process mockProcess = Mockito.mock(Process.class);
        String simulatedOutput = "SomeApp.exe\nAnotherApp.exe";
        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(simulatedOutput.getBytes()));

        // Mock Runtime to return our mocked Process
        Runtime runtime = mock(Runtime.class);
        when(runtime.exec("tasklist")).thenReturn(mockProcess);

        // Inject mocked Runtime
        try (MockedStatic<Runtime> mockedRuntime = Mockito.mockStatic(Runtime.class)) {
            mockedRuntime.when(Runtime::getRuntime).thenReturn(runtime);

            // Assert that the app is detected
            assertTrue(AppChecker.isAppRunning("SomeApp.exe"));
        }
    }

    @Test
    void testIsAppRunning_AppNotFound() throws Exception {
        // Mock the Process to simulate the output of "tasklist"
        Process mockProcess = Mockito.mock(Process.class);
        String simulatedOutput = "AnotherApp.exe";
        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(simulatedOutput.getBytes()));

        // Mock Runtime to return our mocked Process
        Runtime runtime = mock(Runtime.class);
        when(runtime.exec("tasklist")).thenReturn(mockProcess);

        // Inject mocked Runtime
        try (MockedStatic<Runtime> mockedRuntime = Mockito.mockStatic(Runtime.class)) {
            mockedRuntime.when(Runtime::getRuntime).thenReturn(runtime);

            // Assert that the app is not detected
            assertFalse(AppChecker.isAppRunning("SomeApp.exe"));
        }
    }

    @Test
    void testIsAppRunning_ExceptionHandled() throws Exception {
        // Mock Runtime to throw an exception
        Runtime runtime = mock(Runtime.class);
        when(runtime.exec("tasklist")).thenThrow(new RuntimeException("Simulated Exception"));

        // Inject mocked Runtime
        try (MockedStatic<Runtime> mockedRuntime = Mockito.mockStatic(Runtime.class)) {
            mockedRuntime.when(Runtime::getRuntime).thenReturn(runtime);

            // Assert that false is returned when an exception occurs
            assertFalse(AppChecker.isAppRunning("SomeApp.exe"));
        }
    }
}
