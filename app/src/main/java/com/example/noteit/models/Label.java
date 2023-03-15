package com.example.noteit.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Label implements Parcelable, Serializable {
    private String name;

    public Label(String name) {
        this.name = name;
;
    }

    protected Label(Parcel in) {
        name = in.readString();
    }

    public static final Creator<Label> CREATOR = new Creator<Label>() {
        @Override
        public Label createFromParcel(Parcel in) {
            return new Label(in);
        }

        @Override
        public Label[] newArray(int size) {
            return new Label[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Label() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}
