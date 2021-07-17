package org.calyxos.buttercup.model.compat;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TicketArticleCompat implements Serializable {

    @SerializedName("subject")
    @NonNull
    private String subject = "";

    @SerializedName("body")
    @NonNull
    private String body = "";

    @SerializedName("type")
    @NonNull
    private String type = "";

    @SerializedName("internal")
    private boolean internal;

    @SerializedName("attachments")
    private List<ArticleAttachmentCompat> attachments;

    public TicketArticleCompat() {
    }

    public TicketArticleCompat(@NonNull String subject, @NonNull String body, @NonNull String type, boolean internal,
                               List<ArticleAttachmentCompat> attachments) {
        this.subject = subject;
        this.body = body;
        this.type = type;
        this.internal = internal;
        this.attachments = attachments;
    }

    @NonNull
    public String getSubject() {
        return subject;
    }

    public void setSubject(@NonNull String subject) {
        this.subject = subject;
    }

    @NonNull
    public String getBody() {
        return body;
    }

    public void setBody(@NonNull String body) {
        this.body = body;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public List<ArticleAttachmentCompat> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ArticleAttachmentCompat> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "TicketArticleCompat{" +
                "subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", type='" + type + '\'' +
                ", internal=" + internal +
                ", attachments=" + attachments +
                '}';
    }
}
