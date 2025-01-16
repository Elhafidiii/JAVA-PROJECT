package com.example.manic_time;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
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
            // Récupérer les applications depuis différents emplacements du registre Windows
            String[] commands = {
                "powershell.exe Get-ItemProperty -Path 'HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\*' | Select-Object -ExpandProperty DisplayName",
                "powershell.exe Get-ItemProperty -Path 'HKCU:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\*' | Select-Object -ExpandProperty DisplayName",
                "powershell.exe Get-Process | Select-Object -ExpandProperty ProcessName"
            };

            for (String command : commands) {
                Process process = Runtime.getRuntime().exec(command);
                Scanner scanner = new Scanner(process.getInputStream());

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (!line.isEmpty() && !apps.contains(line)) {
                        apps.add(line);
                    }
                }
                scanner.close();
            }

            // Trier la liste par ordre alphabétique
            Collections.sort(apps);
            
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



    public static void closeApp(String appName) {
        try {
            // Pour Windows
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                ProcessBuilder processBuilder = new ProcessBuilder("taskkill", "/F", "/IM", appName + ".exe");
                processBuilder.start();
            } 
            // Pour Linux
            else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                ProcessBuilder processBuilder = new ProcessBuilder("pkill", "-f", appName);
                processBuilder.start();
            }
            // Pour MacOS
            else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                ProcessBuilder processBuilder = new ProcessBuilder("pkill", "-f", appName);
                processBuilder.start();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la fermeture de l'application : " + e.getMessage());
            e.printStackTrace();
        }
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
