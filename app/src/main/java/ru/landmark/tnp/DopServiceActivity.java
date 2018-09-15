package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Locale;

import ru.landmark.tnp.Adapters.RVService;
import ru.landmark.tnp.Adapters.RVTariff;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Functional.NavigationDrawer;
import ru.landmark.tnp.Structurs.DopService;
import ru.landmark.tnp.Structurs.Tariff;

public class DopServiceActivity extends AppCompatActivity {

    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    RecyclerView recyclerView;

    ArrayList<DopService> dopServices = new ArrayList<DopService>();

    static final private int ADD_SERVICE = 1;
    static final private int EDIT_SERVICE = 2;

    RVService rvService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dop_service);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Разное");

        new NavigationDrawer(context, activity, toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        fillArray();

        rvService = new RVService(activity, resources, dopServices);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rvService);
    }

    private void fillArray()
    {
        DBHelper dbHelper = new DBHelper(activity.getApplicationContext(), DBHelper.DOP_SERVICE);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        dopServices.clear();

        Cursor cursor = db.rawQuery("SELECT * FROM DopService", null);

        if (cursor.moveToFirst())
        {
            do
            {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                float price = cursor.getFloat(cursor.getColumnIndex("price"));

                DopService dopService = new DopService(id, name, price);

                dopServices.add(dopService);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu_dop, menu);
        MenuItem searchViewItem = menu.findItem(R.id.searchClient);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchViewAndroidActionBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                Log.d("myLog", newText + " newText ");

                String text = newText.toLowerCase(Locale.getDefault());
                rvService.filter(text);

                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void addClientOnClick()
    {
        DopService dopService = null;

        Intent intent = new Intent(this, AddServiceActivity.class).putExtra("dopService", dopService);
        startActivityForResult(intent, ADD_SERVICE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.addClient:
                addClientOnClick();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_SERVICE)
        {
            fillArray();
            rvService.notifyDataSetChanged();

            if (resultCode == RESULT_OK)
            {
                Log.d("myLog", "adds");
            }
        }
        else if (requestCode == EDIT_SERVICE)
        {
            fillArray();
            rvService.notifyDataSetChanged();

            if (requestCode == 2)
            {
                Log.d("myLog", "edits");
            }
            else {
                Log.d("myLogRes", requestCode + " ");
            }
        }
    }
}
