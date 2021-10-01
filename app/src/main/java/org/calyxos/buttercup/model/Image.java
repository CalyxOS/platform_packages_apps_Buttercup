package org.calyxos.buttercup.model;

import java.util.Arrays;

public class Image {

    private String fileName;
    private String mimeType;
    private int fileSize;
    private String data;
    private byte[] dataBytes;

    public Image() {
    }

    public Image(String fileName, String mimeType, int fileSize, String data, byte[] dataBytes) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.data = data;
        this.dataBytes = dataBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public byte[] getDataBytes() {
        return dataBytes;
    }

    public void setDataBytes(byte[] dataBytes) {
        this.dataBytes = dataBytes;
    }

    @Override
    public String toString() {
        return "Image{" +
                "fileName='" + fileName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", fileSize=" + fileSize +
                ", data='" + data + '\'' +
                ", dataBytes=" + Arrays.toString(dataBytes) +
                '}';
    }
}
