package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.landmark.tnp.Adapters.RVMain;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Functional.NavigationDrawer;
import ru.landmark.tnp.Structurs.Report;

public class MainUsersFragment extends Fragment
{
    Activity activity;
    Context context;
    Resources resources;

    RVMain rvMain;
    RecyclerView recyclerView;

    TextView textViewDay;

    ArrayList<Report> reports = new ArrayList<Report>();

    SearchView searchView = null;
    MenuItem searchMenuItem = null;

    SharedPreferences sharedPref;

    public static MainUsersFragment getInstance()
    {
        Log.d("myLog", "instance");
        Bundle args = new Bundle();
        MainUsersFragment fragment = new MainUsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MainUsersFragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        Log.d("myLog", "create");
        final View view = inflater.inflate(R.layout.fragment_main_users, container, false);

        activity = this.getActivity();
        context = activity.getApplicationContext();
        resources = activity.getResources();

        sharedPref = context.getSharedPreferences(resources.getString(R.string.appSettingsName), Context.MODE_PRIVATE);
        Date currentDate = new Date();
        String dateParse = new SimpleDateFormat("dd_MM_yyyy").format(currentDate);
        int userMoneyInDay = sharedPref.getInt(resources.getString(R.string.appUserMoneyInDay) + dateParse, 0);
        textViewDay = (TextView) view.findViewById(R.id.moneyInDay);
        textViewDay.setText("Выручка за день: " + userMoneyInDay);

        Log.d("myLog", "userMoneyInDay " + resources.getString(R.string.appUserMoneyInDay) + dateParse);

        fillArray();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        rvMain = new RVMain(activity, resources, reports, 0, null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rvMain);

        return view;
    }

    public void fillArray()
    {
        DBHelper dbHelper = new DBHelper(context, DBHelper.HIRE);
        SQLiteDatabase Database = dbHelper.getWritableDatabase();

        Cursor cursor = Database.rawQuery("SELECT * FROM Hire", null);

        if (cursor.moveToLast())
        {
            do
            {
                if (reports.size() != 100)
                {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    int userConsumersId = cursor.getInt(cursor.getColumnIndex("userConsumersId"));
                    int userClientId = cursor.getInt(cursor.getColumnIndex("userClientId"));
                    int tariffId = cursor.getInt(cursor.getColumnIndex("tariffId"));
                    int countCart = cursor.getInt(cursor.getColumnIndex("countCart"));
                    String startDate = cursor.getString(cursor.getColumnIndex("startDate"));
                    String endDate = cursor.getString(cursor.getColumnIndex("endDate"));
                    String summ = cursor.getString(cursor.getColumnIndex("summ"));
                    String barcode = cursor.getString(cursor.getColumnIndex("barcode"));
                    int dop = cursor.getInt(cursor.getColumnIndex("dop"));
                    int idOrder = cursor.getInt(cursor.getColumnIndex("idOrder"));

                    String fio = "~";
                    String phoneNumber = "";
                    String deposit = "";
                    String inHire = "~";

                    int summRes = 0;

                    if (!isNone(idOrder))
                    {
                        if (userConsumersId != 0)
                        {
                            DBHelper dbHelperConsumers = new DBHelper(context, DBHelper.USERS_CONSUMERS);
                            SQLiteDatabase DatabaseConsumers = dbHelperConsumers.getWritableDatabase();

                            Cursor cursorConsumers = DatabaseConsumers.rawQuery("SELECT * FROM UserConsumers WHERE id = ?",
                                    new String[]{String.valueOf(userConsumersId)});

                            if (cursorConsumers.moveToFirst())
                            {
                                String name = cursorConsumers.getString(cursorConsumers.getColumnIndex("fio"));
                                String phone = cursorConsumers.getString(cursorConsumers.getColumnIndex("numberPhone"));

                                fio = name;
                                phoneNumber = phone;
                            }

                            cursorConsumers.close();
                            DatabaseConsumers.close();
                            dbHelperConsumers.close();
                        }
                        else
                        {
                            DBHelper dbHelperClient = new DBHelper(context, DBHelper.USERS_CLIENT);
                            SQLiteDatabase DatabaseClient = dbHelperClient.getWritableDatabase();

                            Cursor cursorClient = DatabaseClient.rawQuery("SELECT * FROM UserClient WHERE id = ?",
                                    new String[]{String.valueOf(userClientId)});

                            if (cursorClient.moveToFirst())
                            {
                                String name = cursorClient.getString(cursorClient.getColumnIndex("fio"));
                                String phone = cursorClient.getString(cursorClient.getColumnIndex("numberPhone"));

                                fio = name;
                                phoneNumber = phone;

                                deposit = cursorClient.getString(cursorClient.getColumnIndex("deposit"));
                            }

                            cursorClient.close();
                            DatabaseClient.close();
                            dbHelperClient.close();
                        }

                        String endDateRes = "~";

                        if (!endDate.equals(""))
                        {
                            endDateRes = endDate;
                            summRes = Integer.parseInt(summ);

                            DBHelper dbHelperAllHire = new DBHelper(context, DBHelper.HIRE);
                            SQLiteDatabase DatabaseAllHire = dbHelperAllHire.getWritableDatabase();

                            Cursor cursorAllHire = DatabaseAllHire.rawQuery("SELECT * FROM Hire WHERE idOrder = ?",
                                    new String[]{String.valueOf(idOrder)});

                            if (cursorAllHire.moveToFirst())
                            {
                                do
                                {
                                    int idH = cursorAllHire.getInt(cursorAllHire.getColumnIndex("id"));
                                    if (id != idH)
                                    {
                                        String summAllHire = cursorAllHire.getString(cursorAllHire.getColumnIndex("summ"));
                                        summRes += Integer.parseInt(summAllHire);
                                    }
                                }
                                while (cursor.moveToNext());
                            }

                            cursorAllHire.close();
                            DatabaseAllHire.close();
                            dbHelperAllHire.close();

                            DateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                            try
                            {
                                Date dateStart = df.parse(startDate);
                                Date dateEnd = df.parse(endDate);

                                long diff = (dateEnd.getTime() - dateStart.getTime()) / 1000;

                                long hours = diff / 3600;
                                long minutes = (diff - (3600 * hours)) / 60;
                                long seconds = (diff - (3600 * hours)) - minutes * 60;

                                String hourMinStr = minutes + "." + seconds;
                                float hourMinF = Float.parseFloat(hourMinStr);

                                inHire = hours + ":" + minutes + ":" + seconds;
                            }
                            catch (ParseException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        String summStrRes = "";

                        if (endDateRes.equals("~"))
                        {
                            summStrRes = "~";
                        }
                        else
                        {
                            summStrRes = String.valueOf(summRes);
                        }

                        reports.add(new Report(id, phoneNumber, fio, barcode, idOrder, "", 0, startDate, endDateRes, inHire, summStrRes, deposit, dop));
                    }
                }
                else
                {
                    break;
                }
            }
            while (cursor.moveToPrevious());
        }

        cursor.close();
        Database.close();
        dbHelper.close();
    }

    boolean isNone(int idOrder)
    {
        for (int i=0; i<reports.size(); i++)
        {
            if (reports.get(i).idNumber == idOrder)
            {
                return true;
            }
        }

        return false;
    }
}
