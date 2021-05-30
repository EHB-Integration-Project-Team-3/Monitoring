package com.brielage.monitor.Heartbeats;

import com.brielage.monitor.Elastic.ElasticRequest;
import com.brielage.monitor.Mail.MailSender;
import com.brielage.monitor.XML.Heartbeat;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class HeartbeatTimer
        extends Thread {
    private final String[] sources = {
            "canvas",
            "frontend",
            "planning"
    };

    private final HashMap<String, String> emailAdressen = new HashMap<>();

    {
        emailAdressen.put("canvas", "canvas@ad.min");
        emailAdressen.put("frontend", "frontend@ad.min");
        emailAdressen.put("planning", "planning@ad.min");
    }

    private final HashMap<String, Boolean> emailSent = new HashMap<>();

    {
        emailSent.put("canvas", false);
        emailSent.put("frontend", false);
        emailSent.put("planning", false);
    }

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

                    if (entry == null) {
                        log(source, makeHeartbeatOffline(source, now));

                        if (!emailSent.get(source)) {
                            sendMail(source);
                            emailSent.put(source, true);
                        }
                    } else {
                        latestDateTime = entry.getKey();

                        epochLatest = latestDateTime.atZone(zoneId).toEpochSecond();
                        epochNow = now.atZone(zoneId).toEpochSecond();

                        if (epochLatest < epochNow - 1) {
                            log(source, makeHeartbeatOffline(source, now));

                            if (epochLatest < epochNow - 10 && !emailSent.get(source)) {
                                sendMail(source);
                                emailSent.put(source, true);
                            }
                        } else if (emailSent.get(source)) emailSent.put(source, false);
                    }
                }
            } catch (InterruptedException | DatatypeConfigurationException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Map.Entry<LocalDateTime, Heartbeat> getLatestEntry(String source) {
        return HeartbeatCollector.getLatestHeartbeatEntry(source);
    }

    private Heartbeat makeHeartbeatOffline(String source,
                                           LocalDateTime dateTime)
            throws DatatypeConfigurationException {
        Heartbeat.Header hbh = new Heartbeat.Header();
        Heartbeat hb = new Heartbeat();

        hbh.setSource(source.toUpperCase());
        hbh.setStatus("OFFLINE");
        hb.setHeader(hbh);
        hb.setTimeStamp(dateTime.toString());

        return hb;
    }

    private void log(String source,
                     Heartbeat heartbeat)
            throws IOException {
        ElasticRequest.sendToElastic("heartbeat", heartbeat);
        HeartbeatLogger.logAppend(source, heartbeat.toString());
    }

    private void sendMail(String source) {
        String to = emailAdressen.get(source);
        String subject = "Service " + source + " down";
        String text = "Service " + source + " down at " + LocalDateTime.now();

        MailSender.sendMail(to, subject, text);
    }
}

