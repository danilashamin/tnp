package ru.landmark.tnp.Structurs;

import android.os.Parcel;
import android.os.Parcelable;

public class UserConsumers implements Parcelable
{
    public int id;
    public String name;
    public String numberPassport;
    public String homeAddress;
    public String numberPhone;
    public String barcode;
    public String pathPhoto;

    public UserConsumers(int id, String name,
                         String numberPassport, String homeAddress, String numberPhone,
                         String barcode, String pathPhoto)
    {
        this.id = id;
        this.name = name;
        this.numberPassport = numberPassport;
        this.homeAddress = homeAddress;
        this.numberPhone = numberPhone;
        this.barcode = barcode;
        this.pathPhoto = pathPhoto;
    }

    protected UserConsumers(Parcel in) {
        id = in.readInt();
        name = in.readString();
        numberPassport = in.readString();
        homeAddress = in.readString();
        numberPhone = in.readString();
        barcode = in.readString();
        pathPhoto = in.readString();
    }

    public static final Creator<UserConsumers> CREATOR = new Creator<UserConsumers>() {
        @Override
        public UserConsumers createFromParcel(Parcel in) {
            return new UserConsumers(in);
        }

        @Override
        public UserConsumers[] newArray(int size) {
            return new UserConsumers[size];
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
        dest.writeString(numberPassport);
        dest.writeString(homeAddress);
        dest.writeString(numberPhone);
        dest.writeString(barcode);
        dest.writeString(pathPhoto);
    }
}
