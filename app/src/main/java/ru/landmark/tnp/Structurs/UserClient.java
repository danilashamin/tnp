package ru.landmark.tnp.Structurs;

import android.os.Parcel;
import android.os.Parcelable;

public class UserClient implements Parcelable
{
    public String name;
    public String numberPhone;
    public String deposit;
    public String barcode;

    public UserClient(String name, String numberPhone, String deposit, String barcode)
    {
        this.name = name;
        this.numberPhone = numberPhone;
        this.deposit = deposit;
        this.barcode = barcode;
    }

    protected UserClient(Parcel in) {
        name = in.readString();
        numberPhone = in.readString();
        deposit = in.readString();
        barcode = in.readString();
    }

    public static final Creator<UserClient> CREATOR = new Creator<UserClient>() {
        @Override
        public UserClient createFromParcel(Parcel in) {
            return new UserClient(in);
        }

        @Override
        public UserClient[] newArray(int size) {
            return new UserClient[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(numberPhone);
        dest.writeString(deposit);
        dest.writeString(barcode);
    }
}
