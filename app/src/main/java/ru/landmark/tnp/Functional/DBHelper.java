package ru.landmark.tnp.Functional;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 12;
    public static String DATABASE_NAME = "";

    Context context;

    public static String USERS_PERSONALS = "UsersPersonal";
    public static String USERS_CONSUMERS = "UserConsumers";
    public static String TARIFF = "Tariff";
    public static String USERS_CLIENT = "UserClient";
    public static String HIRE = "Hire";
    public static String MONEY = "Money";
    public static String REMOTE_APP = "RemoteApp";
    public static String DOP_SERVICE = "DopService";
    public static String SALE_DOP = "SaleDop";

    public DBHelper(Context context, String name)
    {
        super(context, name, null, DATABASE_VERSION);

        this.context = context;
        this.DATABASE_NAME = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        switch (DATABASE_NAME)
        {
            case "UsersPersonal":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "nameAndSurname text,"
                        + "login text,"
                        + "password text,"
                        + "isAdmin text" + ");");
                break;
            case "UserConsumers":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "fio text,"
                        + "numberPassport int,"
                        + "homeAddress text,"
                        + "numberPhone text,"
                        + "barcode text,"
                        + "pathPhoto text" + ");");
                break;
            case "UserClient":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "fio text,"
                        + "numberPhone text,"
                        + "deposit text,"
                        + "barcode text" + ");");
                break;
            case "Tariff":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "nameTariff text,"
                        + "pathTariff text,"
                        + "countCart integer" + ");");
                break;
            case "Hire":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "userConsumersId integer,"
                        + "userClientId integer,"
                        + "tariffId integer,"
                        + "countCart integer,"
                        + "startDate text,"
                        + "endDate text,"
                        + "summ text,"
                        + "barcode text,"
                        + "dop text,"
                        + "idOrder integer"+ ");");
                break;
            case "Money":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "money integer,"
                        + "comment text" + ");");
                break;
            case "RemoteApp":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "nameApp text,"
                        + "idApp text" + ");");
                break;
            case "DopService":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "name text,"
                        + "price real" + ");");
                break;
            case "SaleDop":
                db.execSQL("create table "+ DATABASE_NAME +" ("
                        + "id integer primary key autoincrement,"
                        + "idDop integer,"
                        + "idOrder integer" + ");");
                break;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }
}
