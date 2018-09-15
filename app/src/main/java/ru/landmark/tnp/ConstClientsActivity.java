package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Debug;
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
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Locale;

import ru.landmark.tnp.Adapters.RVUserConsumers;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Functional.NavigationDrawer;
import ru.landmark.tnp.Interfaces.ConstClientsInterface;
import ru.landmark.tnp.Structurs.UserConsumers;

public class ConstClientsActivity extends AppCompatActivity implements ConstClientsInterface
{
    Activity activity;
    Context context;
    Resources resources;

    int modeActivity;

    Toolbar toolbar;

    RecyclerView recyclerView;

    RVUserConsumers rvUserConsumers;

    ConstClientsInterface constClientsInterface;

    ArrayList<UserConsumers> userConsumerses = new ArrayList<UserConsumers>();

    static final private int ADD_CLIENT = 1;
    static final private int EDIT_CLIENT = 2;

    SearchView searchView = null;
    MenuItem searchMenuItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_const_clients);

        activity = this;
        context = getApplicationContext();
        resources = getResources();
        constClientsInterface = this;

        modeActivity = getIntent().getIntExtra("mode", 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.TextConstClients));
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (modeActivity == 1)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else if (modeActivity == 0)
        {
            new NavigationDrawer(context, activity, toolbar);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        fillArray();

        rvUserConsumers = new RVUserConsumers(activity, resources, userConsumerses, constClientsInterface);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rvUserConsumers);
    }

    private void fillArray()
    {
        DBHelper dbHelper = new DBHelper(activity.getApplicationContext(), DBHelper.USERS_CONSUMERS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        userConsumerses.clear();

        Cursor cursor = db.rawQuery("SELECT * FROM UserConsumers", null);

        if (cursor.moveToFirst())
        {
            do
            {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("fio"));
                String numberPassport = cursor.getString(cursor.getColumnIndex("numberPassport"));
                String homeAddress = cursor.getString(cursor.getColumnIndex("homeAddress"));
                String numberPhone = cursor.getString(cursor.getColumnIndex("numberPhone"));
                String barcode = cursor.getString(cursor.getColumnIndex("barcode"));
                String pathPhoto = cursor.getString(cursor.getColumnIndex("pathPhoto"));

                UserConsumers userConsumers = new UserConsumers(id, name,
                        numberPassport, homeAddress, numberPhone, barcode, pathPhoto);

                Log.d("myLog", " name " + name );

                userConsumerses.add(userConsumers);

            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        dbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //MainActivity.showMsg("onCreateMenu", ConstClientsActivity.this);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu_const_clients, menu);
        searchMenuItem = menu.findItem(R.id.searchClient);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                try
                {
                    String text = newText.toLowerCase(Locale.getDefault());
                    if (onBarcode(text))
                    {
                        rvUserConsumers.filterBarcode(text);
                    }
                    else
                    {

                        rvUserConsumers.filter(text);
                    }

                    return true;
                }
                catch (Exception e)
                {
                    MainActivity.showMsg("Error " + e.getMessage().toString(), ConstClientsActivity.this);

                    return false;
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private boolean onBarcode(String text)
    {
        ArrayList<Boolean> res = new ArrayList<Boolean>();
        String alph = "0123456789";

        for (int i=0; i<text.length(); i++)
        {
            for (int l=0; l<alph.length(); l++)
            {
                if (text.charAt(i) == alph.charAt(l))
                {
                    res.add(true);
                }
            }
        }

        Log.d("myLog", res.size() + " " + text.length());

        if (res.size() == text.length())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void addClientOnClick()
    {
        UserConsumers userConsumers = null;

        Intent intent = new Intent(this, AddConstClientActivity.class).putExtra("userConsumers", userConsumers);
        startActivityForResult(intent, ADD_CLIENT);
    }

    private void addCameraOnClick()
    {
        new IntentIntegrator(activity).initiateScan();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.addClient:
                addClientOnClick();
                return true;
            case R.id.camera:
                addCameraOnClick();
                return true;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_CLIENT)
        {
            fillArray();
            rvUserConsumers.notifyDataSetChanged();

            if (resultCode == RESULT_OK)
            {
                Log.d("myLog", "adds");
            }
        }
        else if (requestCode == EDIT_CLIENT)
        {
            fillArray();
            rvUserConsumers.notifyDataSetChanged();

            if (requestCode == RESULT_OK)
            {
                Log.d("myLog", "edits");
            }
            else {
                Log.d("myLogRes", requestCode + " ");
            }
        }
        else
        {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null)
            {
                String barcode = "";

                try
                {
                    if(result.getContents() == null)
                    {
                        Toast.makeText(this, resources.getString(R.string.TextFailScanned), Toast.LENGTH_LONG).show();

                        Log.d("myLog", "lll2");
                    }
                    else
                    {
                        barcode = result.getContents();

                        MenuItemCompat.expandActionView(searchMenuItem);
                        searchView.setQuery(barcode, false);
                        searchView.setIconified(false);
                        searchView.clearFocus();
                        rvUserConsumers.filterBarcode(barcode);

                        Log.d("myLog", "lll");
                    }
                }
                catch (Exception e)
                {
                    MainActivity.showMsg("Error1 " + e.getMessage().toString() + "Barcode: " + barcode, ConstClientsActivity.this);
                }
            }
            else
            {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onClickEdit(UserConsumers userConsumers)
    {
        Intent intent = new Intent(this, AddConstClientActivity.class).putExtra("mode", 0).putExtra("user", userConsumers);
        startActivityForResult(intent, EDIT_CLIENT);
    }

    @Override
    public void onClickUser(UserConsumers userConsumers)
    {
        if (modeActivity == 1)
        {
            Intent intent = new Intent(this, ChangeTariffActivity.class);
            intent.putExtra("user", userConsumers);

            startActivity(intent);
        }
    }
}
