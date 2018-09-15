package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.landmark.tnp.Adapters.RVMain;
import ru.landmark.tnp.Adapters.RVTariffMain;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Structurs.TariffMain;

public class MainCartsFragment extends Fragment
{
    Activity activity;
    Context context;
    Resources resources;

    ArrayList<TariffMain> tariffMains = new ArrayList<TariffMain>();
    RVTariffMain rvTariffMain;
    RecyclerView recyclerView;

    public static MainCartsFragment getInstance()
    {
        Log.d("myLog", "instance");
        Bundle args = new Bundle();
        MainCartsFragment fragment = new MainCartsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MainCartsFragment()
    {}

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_main_carts, container, false);

        activity = this.getActivity();
        context = activity.getApplicationContext();
        resources = activity.getResources();

        fillArray();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        rvTariffMain = new RVTariffMain(activity, resources, tariffMains);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rvTariffMain);

        return view;
    }

    public void fillArray()
    {
        DBHelper dbHelper = new DBHelper(context, DBHelper.TARIFF);
        SQLiteDatabase Database = dbHelper.getWritableDatabase();

        Cursor cursor = Database.rawQuery("SELECT * FROM Tariff", null);
        if (cursor.moveToFirst())
        {
            do
            {
                int freeCart = 0;
                int busyCart = 0;

                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int countCart = cursor.getInt(cursor.getColumnIndex("countCart"));

                String nameTariff = cursor.getString(cursor.getColumnIndex("nameTariff"));

                DBHelper dbHelperHire = new DBHelper(context, DBHelper.HIRE);
                SQLiteDatabase DatabaseHire = dbHelperHire.getWritableDatabase();

                Cursor cursorHire = DatabaseHire.rawQuery("SELECT * FROM Hire WHERE tariffId = ? AND endDate = ?",
                        new String[]{String.valueOf(id), ""});

                if (cursorHire.moveToFirst())
                {
                    do
                    {
                        int countCartCurr = cursorHire.getInt(cursorHire.getColumnIndex("countCart"));
                        busyCart += countCartCurr;
                        Log.d("main","count " + countCartCurr);
                    }
                    while (cursorHire.moveToNext());
                }

                cursorHire.close();
                DatabaseHire.close();
                dbHelperHire.close();

                freeCart = countCart - busyCart;

                tariffMains.add(new TariffMain(nameTariff, freeCart, busyCart));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        Database.close();
        dbHelper.close();
    }
}
