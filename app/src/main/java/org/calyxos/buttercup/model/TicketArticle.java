package org.calyxos.buttercup.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TicketArticle implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("ticket_id")
    private String ticketId;

    @SerializedName("sender_id")
    private int senderId;

    @SerializedName("from")
    private String from;

    @SerializedName("to")
    private String to;

    @SerializedName("cc")
    private String cc;

    @SerializedName("subject")
    private String subject;

    @SerializedName("reply_to")
    private String replyTo;

    @SerializedName("in_reply_to")
    private String inReplyTo;

    @SerializedName("message_id")
    private String messageId;

    @SerializedName("message_id_md5")
    private String messageIdMd5;

    @SerializedName("references")
    private String references;

    @SerializedName("body")
    private String body;

    @SerializedName("content_type")
    private String contentType;

    @SerializedName("type")
    private String type;

    @SerializedName("sender")
    private String sender;

    @SerializedName("internal")
    private boolean internal;

    @SerializedName("attachments")
    private List<ArticleAttachment> attachments;

    @SerializedName("created_by_id")
    private int createdById;

    @SerializedName("updated_by_id")
    private int updatedById;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("created_by")
    private String createdBy;

    @SerializedName("updated_by")
    private String updatedBy;

    public TicketArticle() {
    }

    public TicketArticle(int id, String ticketId, int senderId, String from, String to, String cc, String subject, String replyTo,
                         String inReplyTo, String messageId, String messageIdMd5, String references, String body, String contentType,
                         String type, String sender, boolean internal, List<ArticleAttachment> attachments, int createdById,
                         int updatedById, String createdAt, String updatedAt, String createdBy, String updatedBy) {
        this.id = id;
        this.ticketId = ticketId;
        this.senderId = senderId;
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.subject = subject;
        this.replyTo = replyTo;
        this.inReplyTo = inReplyTo;
        this.messageId = messageId;
        this.messageIdMd5 = messageIdMd5;
        this.references = references;
        this.body = body;
        this.contentType = contentType;
        this.type = type;
        this.sender = sender;
        this.internal = internal;
        this.attachments = attachments;
        this.createdById = createdById;
        this.updatedById = updatedById;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getInReplyTo() {
        return inReplyTo;
    }

    public void setInReplyTo(String inReplyTo) {
        this.inReplyTo = inReplyTo;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageIdMd5() {
        return messageIdMd5;
    }

    public void setMessageIdMd5(String messageIdMd5) {
        this.messageIdMd5 = messageIdMd5;
    }

    public String getReferences() {
        return references;
    }

    public void setReferences(String references) {
        this.references = references;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public List<ArticleAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ArticleAttachment> attachments) {
        this.attachments = attachments;
    }

    public int getCreatedById() {
        return createdById;
    }

    public void setCreatedById(int createdById) {
        this.createdById = createdById;
    }

    public int getUpdatedById() {
        return updatedById;
    }

    public void setUpdatedById(int updatedById) {
        this.updatedById = updatedById;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "TicketArticle{" +
                "id=" + id +
                ", ticketId='" + ticketId + '\'' +
                ", senderId=" + senderId +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", cc='" + cc + '\'' +
                ", subject='" + subject + '\'' +
                ", replyTo='" + replyTo + '\'' +
                ", inReplyTo='" + inReplyTo + '\'' +
                ", messageId='" + messageId + '\'' +
                ", messageIdMd5='" + messageIdMd5 + '\'' +
                ", references='" + references + '\'' +
                ", body='" + body + '\'' +
                ", contentType='" + contentType + '\'' +
                ", type='" + type + '\'' +
                ", sender='" + sender + '\'' +
                ", internal=" + internal +
                ", attachments=" + attachments +
                ", createdById=" + createdById +
                ", updatedById=" + updatedById +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }
}
