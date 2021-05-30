package com.brielage.monitor.Heartbeats;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public enum HeartbeatLogger {
    ;

    public static void logAppend(String source,
                                 String value) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter("/data/heartbeat-" + source + ".txt", true))) {
            writer.append(value);
            writer.newLine();
            writer.close();
            System.out.println(LocalDateTime.now() + " written");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
