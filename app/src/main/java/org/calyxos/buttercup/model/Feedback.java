package org.calyxos.buttercup.model;

import java.io.Serializable;

public class Feedback implements Serializable {

    private String message;
    private String screenshotUrl;

    public Feedback(String message, String screenshotUrl) {
        this.message = message;
        this.screenshotUrl = screenshotUrl;
    }

    public Feedback() {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getScreenshotUrl() {
        return screenshotUrl;
    }

    public void setScreenshotUrl(String screenshotUrl) {
        this.screenshotUrl = screenshotUrl;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "message='" + message + '\'' +
                ", screenshotUrl='" + screenshotUrl + '\'' +
                '}';
    }
}
