package com.example.travelmantics.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Deal implements Parcelable {
    private String _id;
    private String name;
    private String price;
    private String description;
    private String imageUrl;

    public Deal(){}
    public Deal(String name, String price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;

    }

    protected Deal(Parcel in) {
        _id = in.readString();
        name = in.readString();
        price = in.readString();
        description = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<Deal> CREATOR = new Creator<Deal>() {
        @Override
        public Deal createFromParcel(Parcel in) {
            return new Deal(in);
        }

        @Override
        public Deal[] newArray(int size) {
            return new Deal[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name +"\n"+ this.description +"\n"+this.price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(description);
        dest.writeString(imageUrl);
    }
}
