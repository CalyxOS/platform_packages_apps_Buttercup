package org.calyxos.buttercup.model;

import org.calyxos.buttercup.model.compat.TicketCompat;

import java.io.Serializable;

public class Feedback implements Serializable {

    private String subject;
    private String body;
    private String screenshotUrl;
    private TicketCompat ticketCompat;

    public Feedback(String subject, String body, String screenshotUrl, TicketCompat ticketCompat) {
        this.subject = subject;
        this.body = body;
        this.screenshotUrl = screenshotUrl;
        this.ticketCompat = ticketCompat;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    public void setScreenshotUrl(String screenshotUrl) {
        this.screenshotUrl = screenshotUrl;
    }

    public TicketCompat getTicketCompat() {
        return ticketCompat;
    }

    public void setTicketCompat(TicketCompat ticketCompat) {
        this.ticketCompat = ticketCompat;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", screenshotUrl='" + screenshotUrl + '\'' +
                ", ticketCompat=" + ticketCompat +
                '}';
    }
}
