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

import ru.landmark.tnp.Adapters.RVTariff;
import ru.landmark.tnp.Adapters.RVUserConsumers;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Functional.NavigationDrawer;
import ru.landmark.tnp.Structurs.Tariff;
import ru.landmark.tnp.Structurs.UserConsumers;

public class TariffsActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    RecyclerView recyclerView;

    ArrayList<Tariff> tariffs = new ArrayList<Tariff>();

    static final private int ADD_TARIFF = 1;
    static final private int EDIT_TARIFF = 2;

    RVTariff rvTariff;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tariffs);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.TextTariffs));

        new NavigationDrawer(context, activity, toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        fillArray();

        rvTariff = new RVTariff(activity, resources, tariffs);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rvTariff);
    }

    private void fillArray()
    {
        DBHelper dbHelper = new DBHelper(activity.getApplicationContext(), DBHelper.TARIFF);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        tariffs.clear();

        Cursor cursor = db.rawQuery("SELECT * FROM Tariff", null);

        if (cursor.moveToFirst())
        {
            do
            {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String nameTariff = cursor.getString(cursor.getColumnIndex("nameTariff"));
                int countCart = cursor.getInt(cursor.getColumnIndex("countCart"));
                String pathFile = cursor.getString(cursor.getColumnIndex("pathTariff"));

                Tariff tariff = new Tariff(id, nameTariff, countCart, pathFile);

                tariffs.add(tariff);

            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        dbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu_tariffs, menu);
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
                rvTariff.filter(text);

                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void addClientOnClick()
    {
        Tariff tariff = null;

        Intent intent = new Intent(this, AddTariffActivity.class).putExtra("tariff", tariff);
        startActivityForResult(intent, ADD_TARIFF);
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

        if (requestCode == ADD_TARIFF)
        {
            fillArray();
            rvTariff.notifyDataSetChanged();

            if (resultCode == RESULT_OK)
            {
                Log.d("myLog", "adds");
            }
        }
        else if (requestCode == EDIT_TARIFF)
        {
            fillArray();
            rvTariff.notifyDataSetChanged();

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
