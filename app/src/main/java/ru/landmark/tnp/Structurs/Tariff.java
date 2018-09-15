package ru.landmark.tnp.Structurs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Tariff implements Parcelable
{
    public int id;
    public String nameTariff;
    public int countCart;
    public String pathInterval;

    public Tariff(int id, String nameTariff,int countCart, String pathInterval)
    {
        this.id = id;
        this.nameTariff = nameTariff;
        this.countCart = countCart;
        this.pathInterval = pathInterval;
    }

    protected Tariff(Parcel in) {
        id = in.readInt();
        nameTariff = in.readString();
        countCart = in.readInt();
        pathInterval = in.readString();
    }

    public static final Creator<Tariff> CREATOR = new Creator<Tariff>() {
        @Override
        public Tariff createFromParcel(Parcel in) {
            return new Tariff(in);
        }

        @Override
        public Tariff[] newArray(int size) {
            return new Tariff[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nameTariff);
        dest.writeInt(countCart);
        dest.writeString(pathInterval);
    }
}
