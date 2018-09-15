package ru.landmark.tnp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.landmark.tnp.Adapters.RVDopOrder;
import ru.landmark.tnp.Functional.CustomToast;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Structurs.DopService;
import ru.landmark.tnp.Structurs.DopServiceOrder;

public class SaleDopActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    int idOrder = 0;
    RVDopOrder rvDopOrder;

    RecyclerView recyclerView;
    ArrayList<DopService> dopService = new ArrayList<DopService>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_dop);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Доп. услуги");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        idOrder = getIntent().getIntExtra("idOrder", 0);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        fillArray();

        ArrayList<DopServiceOrder> dopServiceOrders = new ArrayList<DopServiceOrder>();

        for (int i=0; i<dopService.size(); i++)
        {
            Log.d("myLog", dopService.get(i).id + " id");
            dopServiceOrders.add(new DopServiceOrder(dopService.get(i).id,dopService.get(i).name, dopService.get(i).price, false));
        }

        rvDopOrder = new RVDopOrder(activity, resources, dopServiceOrders, idOrder);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rvDopOrder);
    }

    private void fillArray()
    {
        DBHelper dbHelper = new DBHelper(activity.getApplicationContext(), DBHelper.SALE_DOP);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM SaleDop WHERE idOrder = ?", new String[]{String.valueOf(idOrder)});

        if (cursor.moveToFirst())
        {
            do
            {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int idDop = cursor.getInt(cursor.getColumnIndex("idDop"));
                int idOrder = cursor.getInt(cursor.getColumnIndex("idOrder"));

                DBHelper dbHelperDop = new DBHelper(activity.getApplicationContext(), DBHelper.DOP_SERVICE);
                SQLiteDatabase dbDop = dbHelperDop.getWritableDatabase();

                Cursor cursorDop = dbDop.rawQuery("SELECT * FROM DopService WHERE id = ?", new String[]{String.valueOf(idDop)});

                if (cursorDop.moveToFirst())
                {
                    int idD = cursorDop.getInt(cursorDop.getColumnIndex("id"));
                    String nameDop = cursorDop.getString(cursorDop.getColumnIndex("name"));
                    float namePrice = cursorDop.getFloat(cursorDop.getColumnIndex("price"));

                    dopService.add(new DopService(id, nameDop, namePrice));
                }

                cursorDop.close();
                dbDop.close();
                dbHelperDop.close();
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu_sale_dop, menu);

        return true;
    }

    private void addTariffOnClick()
    {
        rvDopOrder.addTariff();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.addClient:
                addTariffOnClick();
                return true;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return false;
        }
    }
}
