package ru.landmark.tnp.Structurs;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class DopService implements Parcelable
{
    public int id;
    public String name;
    public float price;

    public DopService(int id, String name, float price)
    {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    protected DopService(Parcel in) {
        id = in.readInt();
        name = in.readString();
        price = in.readFloat();
    }

    public static final Creator<DopService> CREATOR = new Creator<DopService>() {
        @Override
        public DopService createFromParcel(Parcel in) {
            return new DopService(in);
        }

        @Override
        public DopService[] newArray(int size) {
            return new DopService[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeFloat(price);
    }
}
