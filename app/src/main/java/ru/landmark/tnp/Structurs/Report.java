package ru.landmark.tnp.Structurs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Report implements Parcelable
{
    public int id;
    public String phone;
    public String fio;
    public String barcode;
    public int idNumber;
    public String nameTariff;
    public int countCart;
    public String startTime;
    public String endTime;
    public String timeHire;
    public String summ;
    public String deposit;
    public int damage;

    public Report(int id, String phone,String fio, String barcode, int idNumber,String nameTariff, int CountCart,String startTime, String endTime, String timeHire, String summ, String deposit, int damage)
    {
        this.id = id;
        this.phone = phone;
        this.fio = fio;
        this.barcode = barcode;
        this.idNumber = idNumber;
        this.nameTariff = nameTariff;
        this.countCart = CountCart;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeHire = timeHire;
        this.summ = summ;
        this.deposit = deposit;
        this.damage = damage;
    }

    protected Report(Parcel in) {
        id = in.readInt();
        phone = in.readString();
        fio = in.readString();
        barcode = in.readString();
        idNumber = in.readInt();
        nameTariff = in.readString();
        countCart = in.readInt();
        startTime = in.readString();
        endTime = in.readString();
        timeHire = in.readString();
        summ = in.readString();
        deposit = in.readString();
        damage = in.readInt();
    }

    public static final Creator<Report> CREATOR = new Creator<Report>() {
        @Override
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        @Override
        public Report[] newArray(int size) {
            return new Report[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(phone);
        dest.writeString(fio);
        dest.writeString(barcode);
        dest.writeInt(idNumber);
        dest.writeString(nameTariff);
        dest.writeInt(countCart);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(timeHire);
        dest.writeString(summ);
        dest.writeString(deposit);
        dest.writeInt(damage);
    }
}
