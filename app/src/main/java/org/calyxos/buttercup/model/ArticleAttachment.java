package org.calyxos.buttercup.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class ArticleAttachment implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("filename")
    private String filename;

    @SerializedName("size")
    private int size;

    @SerializedName("preferences")
    private Map<String, Object> preferences;

    public ArticleAttachment() {
    }

    public ArticleAttachment(int id, String filename, int size, Map<String, Object> preferences) {
        this.id = id;
        this.filename = filename;
        this.size = size;
        this.preferences = preferences;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Map<String, Object> getPreferences() {
        return preferences;
    }

    public void setPreferences(Map<String, Object> preferences) {
        this.preferences = preferences;
    }

    public boolean hasContentType() {
        return preferences.containsKey("Content-Type");
    }

    public String getContentType() {
        return (String)preferences.get("Content-Type");
    }

    public boolean hasMimeType() {
        return preferences.containsKey("Mime-Type");
    }

    public String getMimeType() {
        return (String)preferences.get("Mime-Type");
    }

    public boolean hasCharset() {
        return preferences.containsKey("Charset");
    }

    public String getCharset() {
        return (String)preferences.get("Charset");
    }

    @Override
    public String toString() {
        return "ArticleAttachment{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", size=" + size +
                ", preferences=" + preferences +
                '}';
    }
}
