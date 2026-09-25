package com.parkinglot.app.infrastructure.persistence.local;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum OS {
    WINDOWS, MACOS, LINUX, UNKNOWN;

    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();

    public static OS current() {
        if (OS_NAME.contains("win")) return WINDOWS;
        else if (OS_NAME.contains("mac")) return MACOS;
        else if (OS_NAME.contains("linux") || OS_NAME.contains("nux")) return LINUX;
        else return UNKNOWN;
    }

    public Path getAppDataPath(String appName) {
        String userHome = System.getProperty("user.home");

        switch (this) {
            case WINDOWS:
                // Uses LocalAppData (preferred for apps) or falls back to AppData\Roaming
                String localAppData = System.getenv("LOCALAPPDATA");
                if (localAppData != null && !localAppData.isEmpty()) {
                    return Paths.get(localAppData, appName);
                }
                return Paths.get(System.getenv("APPDATA"), appName);

            case MACOS:
                return Paths.get(userHome, "Library", "Application Support", appName);

                case LINUX:
                    // Follows XDG Base Directory Specification
                    String xdgConfig = System.getenv("XDG_CONFIG_HOME");
                    if (xdgConfig != null && !xdgConfig.isEmpty()) {
                        return Paths.get(xdgConfig, appName);
                    }
                    return Paths.get(userHome, ".config",  appName);

            default:
                return Paths.get(userHome, "." + appName.toLowerCase());
        }
    }
}
