package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import ru.landmark.tnp.Adapters.RVRemoteControl;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Functional.NavigationDrawer;
import ru.landmark.tnp.Structurs.RemoteApp;

public class RemoteControlActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    static final private int ADD_CONTROL = 1;

    RecyclerView recyclerView;

    ArrayList<RemoteApp> remoteApps = new ArrayList<RemoteApp>();

    RVRemoteControl rvRemoteControl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_control);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.TextRemoteControl));

        new NavigationDrawer(context, activity, toolbar);

        fillArray();

        rvRemoteControl = new RVRemoteControl(activity, resources, remoteApps);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rvRemoteControl);
    }

    protected void fillArray()
    {
        DBHelper dbHelper = new DBHelper(activity.getApplicationContext(), DBHelper.REMOTE_APP);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        remoteApps.clear();

        Cursor cursor = db.rawQuery("SELECT * FROM RemoteApp", null);
        if (cursor.moveToFirst())
        {
            do {
                String name = cursor.getString(cursor.getColumnIndex("nameApp"));
                String id = cursor.getString(cursor.getColumnIndex("idApp"));
                int idBD = cursor.getInt(cursor.getColumnIndex("id"));

                remoteApps.add(new RemoteApp(idBD, name, id));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        dbHelper.close();
    }

    private void addControlOnClick()
    {
        Intent intent = new Intent(this, AddRemoteControl.class);
        startActivityForResult(intent, ADD_CONTROL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu_remote_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.addControl:
                addControlOnClick();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == ADD_CONTROL)
        {
            fillArray();
            rvRemoteControl.notifyDataSetChanged();
        }
    }
}
