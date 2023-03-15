package com.example.noteit.models;


import android.os.Parcel;
import android.os.Parcelable;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Note  implements Parcelable{
    private String ID;
    private String owner;
    private String title;
    private String content;
    private Date time;
    private boolean isAlarmed;
    private List<Label> label;
    private Date lastModified;
    private boolean isPinned;
    private boolean isSecured;
    private String password;
    private int fontSize;
    private boolean isDelete;
    private int deletedDay;


    public Note() {
        this.time = new Date();
        this.isAlarmed = false;
        this.lastModified = new Date();
        this.isPinned = false;
        this.isSecured = false;
        this.password = "";
        this.label = new ArrayList<Label>();
        this.fontSize = 15;
        this.isDelete = false;
        this.deletedDay = 0;
    }

    public Note(String ID, String owner, String title, String content, Date time, boolean isAlarmed, List<Label> label, Date lastModified, boolean isPinned, boolean isSecured, String password, int fontSize, boolean isDelete, int deletedDay) {
        this.ID = ID;
        this.owner = owner;
        this.title = title;
        this.content = content;
        this.time = time;
        this.isAlarmed = isAlarmed;
        this.label = label;
        this.lastModified = lastModified;
        this.isPinned = isPinned;
        this.isSecured = isSecured;
        this.password = password;
        this.fontSize = fontSize;
        this.isDelete = isDelete;
        this.deletedDay = deletedDay;
    }


    protected Note(Parcel in) {

        ID = in.readString();
        owner = in.readString();
        title = in.readString();
        content = in.readString();
        isAlarmed = in.readByte() != 0;
        isPinned = in.readByte() != 0;
        isSecured = in.readByte() != 0;
        password = in.readString();
        fontSize = in.readInt();
        isDelete = in.readByte() != 0;
        deletedDay = in.readInt();


    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public int getDeletedDay() {
        return deletedDay;
    }

    public void setDeletedDay(int deletedDay) {
        this.deletedDay = deletedDay;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isAlarmed() {
        return isAlarmed;
    }

    public void setAlarmed(boolean alarmed) {
        isAlarmed = alarmed;
    }

    public List<Label> getLabel() {
        return label;
    }

    public void setLabel(List<Label> label) {
        this.label = label;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isSecured() {
        return isSecured;
    }

    public void setSecured(boolean secured) {
        isSecured = secured;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(owner);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeByte((byte) (isAlarmed ? 1 : 0));
        dest.writeByte((byte) (isPinned ? 1 : 0));
        dest.writeByte((byte) (isSecured ? 1 : 0));
        dest.writeString(password);
        dest.writeInt(fontSize);
        dest.writeByte((byte) (isDelete ? 1 : 0));

        dest.writeInt(deletedDay);
    }
}
