package com.brielage.monitor.Heartbeats;

import com.brielage.monitor.XML.Heartbeat;

import java.time.LocalDateTime;
import java.util.*;


@SuppressWarnings({"rawtypes", "unchecked"})
public enum HeartbeatCollector {
    ;

    private final static HashMap<String, TreeMap<LocalDateTime, Heartbeat>> heartbeats;

    static {
        heartbeats = new HashMap();
        TreeMap<LocalDateTime, Heartbeat> canvas = new TreeMap();
        TreeMap<LocalDateTime, Heartbeat> frontend = new TreeMap();
        TreeMap<LocalDateTime, Heartbeat> planning = new TreeMap();
        heartbeats.put("canvas", canvas);
        heartbeats.put("frontend", frontend);
        heartbeats.put("planning", planning);
    }

    public static void addHeartbeat(Heartbeat heartbeat) {
        heartbeats.get(heartbeat.getHeader().getSource().toLowerCase()).put(LocalDateTime.now(), heartbeat);
    }

    public static void removeAllButLastHeartbeat(String source) {
        while (heartbeats.get(source).size() > 1) {
            heartbeats.get(source).pollFirstEntry();
        }
    }

    public static Map.Entry<LocalDateTime, Heartbeat> getLatestHeartbeatEntry(String source) {
        return heartbeats.get(source).lastEntry();
    }
}
