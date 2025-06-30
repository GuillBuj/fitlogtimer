package com.fitlogtimer.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ScriptExecutor {

    public static void runPowerShellScript(String scriptPath) {
        try {
            // Appel PowerShell
            ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-ExecutionPolicy", "Bypass", "-File", scriptPath);
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            log.info("Script terminé avec code : {}", exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
