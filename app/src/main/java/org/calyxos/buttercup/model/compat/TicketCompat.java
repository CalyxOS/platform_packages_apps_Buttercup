package org.calyxos.buttercup.model.compat;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TicketCompat implements Serializable {

    @SerializedName("title")
    @NonNull
    private String title = "";

    @SerializedName("group")
    private String group;

    @SerializedName("customer")
    private String customer;

    @NonNull
    @SerializedName("article")
    private TicketArticleCompat article = new TicketArticleCompat();

    @SerializedName("note")
    private String note;

    public TicketCompat() {
    }

    public TicketCompat(@NonNull String title, String group, String customer, @NonNull TicketArticleCompat article, String note) {
        this.title = title;
        this.group = group;
        this.customer = customer;
        this.article = article;
        this.note = note;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    @NonNull
    public TicketArticleCompat getArticle() {
        return article;
    }

    public void setArticle(@NonNull TicketArticleCompat article) {
        this.article = article;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String toString() {
        return "TicketCompat{" +
                "title='" + title + '\'' +
                ", group='" + group + '\'' +
                ", customer='" + customer + '\'' +
                ", article=" + article +
                ", note='" + note + '\'' +
                '}';
    }
}
