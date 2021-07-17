package org.calyxos.buttercup.model.compat;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ArticleAttachmentCompat implements Serializable {

    @SerializedName("filename")
    private String filename;

    @SerializedName("data")
    private String base64Data;

    @SerializedName("mime-type")
    private String mimeType;

    public ArticleAttachmentCompat() {
    }

    public ArticleAttachmentCompat(String filename, String base64Data, String mimeType) {
        this.filename = filename;
        this.base64Data = base64Data;
        this.mimeType = mimeType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getBase64Data() {
        return base64Data;
    }

    public void setBase64Data(String base64Data) {
        this.base64Data = base64Data;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return "ArticleAttachmentCompat{" +
                "filename='" + filename + '\'' +
                ", base64Data='" + base64Data + '\'' +
                ", mimeType='" + mimeType + '\'' +
                '}';
    }
}
