package com.example.manic_time;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class AppFetcher {
    /**
     * Retrieves a list of installed applications from the Windows registry.
     *
     * @return List of application names.
     */
    public static List<String> getInstalledApplications() {
        List<String> apps = new ArrayList<>();
        try {
            // Run PowerShell command to query installed apps from the registry
            String command = "powershell.exe Get-ItemProperty -Path 'HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\*' | Select-Object -ExpandProperty DisplayName";
            Process process = Runtime.getRuntime().exec(command);
            Scanner scanner = new Scanner(process.getInputStream());

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.trim().isEmpty()) {
                    apps.add(line.trim());
                }
            }
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apps;
    }



    /**
     * Checks if a specific application is running on the system.
     *
     * @param appName The name of the application (e.g., "notepad.exe").
     * @return True if the application is running, false otherwise.
     */
    public static boolean isAppRunning(String appName) {
        try {
            // Run the 'tasklist' command to get a list of running processes
            ProcessBuilder processBuilder = new ProcessBuilder("tasklist");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Skip the header lines
            reader.readLine(); // Skip the first header line
            reader.readLine(); // Skip the separator line

            // Read the output and check if the process name appears in the tasklist
            while ((line = reader.readLine()) != null) {
                // Check if the process name is in the line (case-insensitive)
                if (line.toLowerCase().contains(appName.toLowerCase())) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    public static void main(String[] args) {
        // Test getInstalledApplications
        List<String> installedApps = getInstalledApplications();
        System.out.println("Installed Applications:");
        installedApps.forEach(System.out::println);

        // Test isAppRunning
        String appName = "SceneBuilder"; // Replace with the application you want to check
        if (isAppRunning(appName)) {
            System.out.println(appName + " is running.");
        } else {
            System.out.println(appName + " is not running.");
        }
    }
}
