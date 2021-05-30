package com.brielage.monitor.Heartbeats;

import com.brielage.monitor.Elastic.ElasticRequest;
import com.brielage.monitor.XML.Heartbeat;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

public class HeartbeatTimer
        extends Thread {
    private final String[] sources = {
            "canvas",
            "frontend",
            "planning"
    };

    @Override
    public void run() {
        ZoneId zoneId = ZoneId.systemDefault();
        Map.Entry<LocalDateTime, Heartbeat> entry;
        LocalDateTime now, latestDateTime;
        long epochLatest, epochNow;

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                //noinspection BusyWait
                sleep(1000);

                for (String source : sources) {
                    HeartbeatCollector.removeAllButLastHeartbeat(source);

                    entry = getLatestEntry(source);
                    now = LocalDateTime.now();

                    if (entry == null) //HeartbeatLogger.randomLog("null " + source);
                        log(source, makeHeartbeatOffline(source, now));
                    else {
                        latestDateTime = entry.getKey();

                        epochLatest = latestDateTime.atZone(zoneId).toEpochSecond();
                        epochNow = now.atZone(zoneId).toEpochSecond();

                        //HeartbeatLogger.randomLog(epochLatest, epochNow);

                        if (epochLatest < epochNow - 1) {
                            log(source, makeHeartbeatOffline(source, now));
                        }
                    }
                }
            } catch (InterruptedException | DatatypeConfigurationException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Map.Entry<LocalDateTime, Heartbeat> getLatestEntry(String source) {
        //HeartbeatLogger.randomLog(source);
        return HeartbeatCollector.getLatestHeartbeatEntry(source);
    }

    private Heartbeat makeHeartbeatOffline(String source, LocalDateTime dateTime)
            throws DatatypeConfigurationException {
        Heartbeat.Header hbh = new Heartbeat.Header();
        Heartbeat hb = new Heartbeat();

        hbh.setSource(source.toUpperCase());
        hbh.setStatus("OFFLINE");
        hb.setHeader(hbh);
        hb.setTimeStamp(dateTime.toString());

        return hb;
    }

    private void log(String source, Heartbeat heartbeat)
            throws IOException {
        ElasticRequest.sendToElastic("heartbeat", heartbeat);
        HeartbeatLogger.logAppend(source, heartbeat.toString());
    }
}
