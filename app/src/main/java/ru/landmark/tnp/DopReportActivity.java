package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Structurs.DopService;
import ru.landmark.tnp.Structurs.DopServiceOrder;
import ru.landmark.tnp.Structurs.Report;

public class DopReportActivity extends AppCompatActivity
{

    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;
    TableLayout tableLayout;

    SearchView searchView;
    MenuItem searchMenuItem;

    ArrayList<DopServiceOrder> reports = new ArrayList<DopServiceOrder>();
    ArrayList<DopServiceOrder> mCleanCopyDataset;

    ArrayList<View> listView = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dop_report);

        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Отчеты о услугах");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tableLayout = (TableLayout) findViewById(R.id.tabla_layout);

        // new NavigationDrawer(context, activity, toolbar);

        fillArray();
        mCleanCopyDataset = this.reports;

        updateTable();
    }

    private void updateTable()
    {
        for (int i=0; i<reports.size(); i++)
        {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.setBackgroundColor(resources.getColor(R.color.backTr));

            TextView idOrder = createTextView(String.valueOf(reports.get(i).id));
            TextView name = createTextView(reports.get(i).nameTariff);
            TextView price = createTextView(String.valueOf(reports.get(i).price));


            String damageStr = "~";

            tr.addView(idOrder);
            tr.addView(name);
            tr.addView(price);

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
        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.SALE_DOP);
        SQLiteDatabase Database = dbHelper.getWritableDatabase();

        Cursor cursor = Database.rawQuery("SELECT * FROM SaleDop", null);

        if (cursor.moveToLast())
        {
            do
            {
                int idOrder = cursor.getInt(cursor.getColumnIndex("idOrder"));
                int idDop = cursor.getInt(cursor.getColumnIndex("idDop"));
                String nameOrder = "";
                float price = 0;

                DBHelper dbHelperS = new DBHelper(getApplicationContext(), DBHelper.DOP_SERVICE);
                SQLiteDatabase DatabaseS = dbHelperS.getWritableDatabase();

                Cursor cursorS = DatabaseS.rawQuery("SELECT * FROM DopService WHERE id = ?", new String[]{String.valueOf(idDop)});

                if (cursorS.moveToFirst())
                {
                    nameOrder = cursorS.getString(cursorS.getColumnIndex("name"));
                    price = cursorS.getFloat(cursorS.getColumnIndex("price"));
                }

                cursorS.close();
                DatabaseS.close();
                dbHelperS.close();

                reports.add(new DopServiceOrder(idOrder, nameOrder, price, false));
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
        inflater.inflate(R.menu.toolbar_search_report, menu);
        searchMenuItem = menu.findItem(R.id.searchClient);

        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchView.clearFocus();

                String text = query.toLowerCase(Locale.getDefault());

                filter(text);

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
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return false;
        }
    }

    public void filter(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        reports = new ArrayList<DopServiceOrder>();
        if (charText.length() == 0)
        {
            reports.addAll(mCleanCopyDataset);
        }
        else
        {
            for (DopServiceOrder item : mCleanCopyDataset)
            {
                String nameAndSurname = String.valueOf(item.id);
                if (nameAndSurname.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    reports.add(item);
                }
            }
        }
    }
}
