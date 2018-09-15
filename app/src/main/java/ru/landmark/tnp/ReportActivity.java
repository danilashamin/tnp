package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Debug;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ru.landmark.tnp.Adapters.RVReport;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Functional.NavigationDrawer;
import ru.landmark.tnp.Structurs.Report;

public class ReportActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;
    TableLayout tableLayout;

    SearchView searchView;
    MenuItem searchMenuItem;

    ArrayList<Report> reports = new ArrayList<Report>();
    ArrayList<Report> mCleanCopyDataset;

    ArrayList<View> listView = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.TextReportUser));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tableLayout = (TableLayout) findViewById(R.id.tabla_layout);

       // new NavigationDrawer(context, activity, toolbar);

        fillArray();
        mCleanCopyDataset = this.reports;

        updateTable();

        Log.d("myLogS", reports.size() + " ");
    }

    private void updateTable()
    {
        for (int i=0; i<reports.size(); i++)
        {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            TextView fio = createTextView(reports.get(i).fio);
            TextView barcode = createTextView(reports.get(i).barcode);
            TextView numberOrder = createTextView(String.valueOf(reports.get(i).idNumber));
            TextView nameTariff = createTextView(reports.get(i).nameTariff);
            TextView countCart = createTextView(String.valueOf(reports.get(i).countCart));
            TextView start = createTextView(reports.get(i).startTime);
            TextView end = createTextView(reports.get(i).endTime);
            TextView timeHolding = createTextView(reports.get(i).timeHire);
            TextView summ = createTextView(reports.get(i).summ);
            TextView deposit = createTextView(reports.get(i).deposit);

            if (reports.get(i).endTime.equals("~"))
            {
                tr.setBackgroundColor(resources.getColor(R.color.backTr));
            }
            else
            {
                tr.setBackgroundColor(resources.getColor(R.color.backTrActive));
            }

            String damageStr = "~";

            tr.addView(fio);
            tr.addView(barcode);
            tr.addView(numberOrder);
            tr.addView(nameTariff);
            tr.addView(countCart);
            tr.addView(start);
            tr.addView(end);
            tr.addView(timeHolding);
            tr.addView(summ);
            tr.addView(deposit);

            listView.add(tr);

            tableLayout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private TextView createTextView(String text)
    {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView.setTextColor(resources.getColor(R.color.md_white_1000));
        textView.setPadding(5,5,5,5);
        textView.setGravity(Gravity.CENTER);


        if (Build.VERSION.SDK_INT < 23) {

            textView.setTextAppearance(context, android.R.style.TextAppearance_Medium);

        } else {

            textView.setTextAppearance(android.R.style.TextAppearance_Medium);
        }

        textView.setText(text);

        return textView;
    }

    private void fillArray()
    {
        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.HIRE);
        SQLiteDatabase Database = dbHelper.getWritableDatabase();

        Cursor cursor = Database.rawQuery("SELECT * FROM Hire", null);

        if (cursor.moveToLast())
        {
            do
            {
                String fio = "";
                String phoneNumber = "";
                String deposit = "~";
                String holdingTime = "~";
                String nameTariff = "~";

                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int userConsumersId = cursor.getInt(cursor.getColumnIndex("userConsumersId"));
                int userClientId = cursor.getInt(cursor.getColumnIndex("userClientId"));
                int tariffId = cursor.getInt(cursor.getColumnIndex("tariffId"));
                int countCart = cursor.getInt(cursor.getColumnIndex("countCart"));
                String startTime = cursor.getString(cursor.getColumnIndex("startDate"));
                String endTime = cursor.getString(cursor.getColumnIndex("endDate"));
                String summ = cursor.getString(cursor.getColumnIndex("summ"));
                String barcode = cursor.getString(cursor.getColumnIndex("barcode"));
                int damage = cursor.getInt(cursor.getColumnIndex("dop"));
                int idOrder = cursor.getInt(cursor.getColumnIndex("idOrder"));

                if (endTime.equals(""))
                {
                    endTime = "~";
                    summ = "~";
                }
                else
                {
                    DateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                    try
                    {
                        Date dateStart = df.parse(startTime);
                        Date currentDate = df.parse(endTime);

                        long diff = (currentDate.getTime() - dateStart.getTime()) / 1000;

                        long hours = diff / 3600;
                        long minutes = (diff - (3600 * hours)) / 60;
                        long seconds = (diff - (3600 * hours)) - minutes * 60;

                        String hourMinStr = minutes + "." + seconds;

                        holdingTime = hours + ":" + minutes + ":" + seconds;
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }

                if (userConsumersId != 0)
                {
                    DBHelper dbHelperConsumers = new DBHelper(getApplicationContext(), DBHelper.USERS_CONSUMERS);
                    SQLiteDatabase DatabaseConsumers = dbHelperConsumers.getWritableDatabase();

                    Cursor cursorConsumers = DatabaseConsumers.rawQuery(
                            "SELECT * FROM UserConsumers WHERE id = ? ",
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
                else if (userClientId != 0)
                {
                    DBHelper dbHelperClient = new DBHelper(getApplicationContext(), DBHelper.USERS_CLIENT);
                    SQLiteDatabase DatabaseClient = dbHelperClient.getWritableDatabase();

                    Cursor cursorClient = DatabaseClient.rawQuery(
                            "SELECT * FROM UserClient WHERE id = ? ",
                            new String[]{String.valueOf(userClientId)});

                    if (cursorClient.moveToFirst())
                    {
                        String name = cursorClient.getString(cursorClient.getColumnIndex("fio"));
                        String phone = cursorClient.getString(cursorClient.getColumnIndex("numberPhone"));

                        deposit = cursorClient.getString(cursorClient.getColumnIndex("deposit"));

                        fio = name;
                        phoneNumber = phone;
                    }

                    cursorClient.close();
                    DatabaseClient.close();
                    dbHelperClient.close();
                }

                DBHelper dbHelperTariff = new DBHelper(getApplicationContext(), DBHelper.TARIFF);
                SQLiteDatabase DatabaseTariff = dbHelperTariff.getWritableDatabase();

                Cursor cursorTariff = DatabaseTariff.rawQuery(
                        "SELECT * FROM Tariff WHERE id = ? ",
                        new String[]{String.valueOf(tariffId)});

                if (cursorTariff.moveToFirst())
                {
                    nameTariff = cursorTariff.getString(cursorTariff.getColumnIndex("nameTariff"));
                }

                cursorTariff.close();
                DatabaseTariff.close();
                dbHelperTariff.close();

                reports.add(new Report(id, phoneNumber, fio, barcode,idOrder,nameTariff,countCart, startTime, endTime, holdingTime, summ, deposit, damage));
            }
            while (cursor.moveToPrevious());
        }

        cursor.close();
        Database.close();
        dbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu_report, menu);
        searchMenuItem = menu.findItem(R.id.searchClient);

        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchView.clearFocus();

                String text = query.toLowerCase(Locale.getDefault());

                if (onBarcode(text))
                {
                    filterBarcode(text);
                }
                else
                {
                    filter(text);
                }

                int count = tableLayout.getChildCount();
                Log.d("mylog", count + " count");

                for (int i=0; i< listView.size(); i++)
                {
                    tableLayout.removeView(listView.get(i));
                }

                updateTable();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.camera:
                onClickCamera();
                return true;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return false;
        }
    }

    private void onClickCamera()
    {
        new IntentIntegrator(activity).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null)
        {
            if(result.getContents() == null)
            {
                Toast.makeText(this, resources.getString(R.string.TextFailScanned), Toast.LENGTH_LONG).show();
            }
            else
            {
                String barcode = result.getContents();

                MenuItemCompat.expandActionView(searchMenuItem);
                searchView.setQuery(barcode, true);
                searchView.setIconified(false);
                searchView.clearFocus();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
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

    public void filterBarcode(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        reports = new ArrayList<Report>();
        if (charText.length() == 0)
        {
            reports.addAll(mCleanCopyDataset);
        }
        else
        {
            for (Report item : mCleanCopyDataset)
            {
                if (item.barcode.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    reports.add(item);
                }
            }
        }
    }

    public void filter(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        reports = new ArrayList<Report>();
        if (charText.length() == 0)
        {
            reports.addAll(mCleanCopyDataset);
        }
        else
        {
            for (Report item : mCleanCopyDataset)
            {
                String nameAndSurname = item.fio;
                if (nameAndSurname.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    reports.add(item);
                }
            }
        }
    }
}
