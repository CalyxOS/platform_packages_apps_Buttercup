package org.calyxos.buttercup.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class Image implements Parcelable {

    private String fileName;
    private String mimeType;
    private int fileSize;
    private String base64Data;
    private byte[] dataBytes;
    private String fileURL;

    public Image() {
    }

    public Image(String fileName, String mimeType, int fileSize, String base64Data, byte[] dataBytes, String fileURL) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.base64Data = base64Data;
        this.dataBytes = dataBytes;
        this.fileURL = fileURL;
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

    public String getBase64Data() {
        return base64Data;
    }

    public void setBase64Data(String base64Data) {
        this.base64Data = base64Data;
    }

    public byte[] getDataBytes() {
        return dataBytes;
    }

    public void setDataBytes(byte[] dataBytes) {
        this.dataBytes = dataBytes;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    @Override
    public String toString() {
        return "Image{" +
                "fileName='" + fileName + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", fileSize=" + fileSize +
                ", data='" + base64Data + '\'' +
                ", dataBytes=" + Arrays.toString(dataBytes) +
                ", fileURL='" + fileURL + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileName);
        dest.writeString(this.mimeType);
        dest.writeInt(this.fileSize);
        dest.writeString(this.base64Data);
        dest.writeByteArray(this.dataBytes);
        dest.writeString(this.base64Data);
    }

    public void readFromParcel(Parcel source) {
        this.fileName = source.readString();
        this.mimeType = source.readString();
        this.fileSize = source.readInt();
        this.base64Data = source.readString();
        this.dataBytes = source.createByteArray();
        this.fileURL = source.readString();
    }

    protected Image(Parcel in) {
        this.fileName = in.readString();
        this.mimeType = in.readString();
        this.fileSize = in.readInt();
        this.base64Data = in.readString();
        this.dataBytes = in.createByteArray();
        this.fileURL = in.readString();
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
