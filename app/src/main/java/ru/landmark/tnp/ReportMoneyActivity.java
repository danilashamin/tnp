package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Structurs.Money;

public class ReportMoneyActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;
    TableLayout tableLayout;

    ArrayList<Money> moneys = new ArrayList<Money>();

    ArrayList<View> listView = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_money);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.TextReportMoney));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tableLayout = (TableLayout) findViewById(R.id.tabla_layout);

        fillArray();

        updateTable();
    }

    private void fillArray()
    {
        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.MONEY);
        SQLiteDatabase Database = dbHelper.getWritableDatabase();

        Cursor cursor = Database.rawQuery("SELECT * FROM Money", null);

        if (cursor.moveToLast())
        {
            do
            {
                int money = cursor.getInt(cursor.getColumnIndex("money"));
                String comment = cursor.getString(cursor.getColumnIndex("comment"));

                moneys.add(new Money(money, comment));
            }
            while (cursor.moveToPrevious());
        }

        cursor.close();
        Database.close();
        dbHelper.close();
    }

    private void updateTable()
    {
        for (int i=0; i<moneys.size(); i++)
        {
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            tr.setBackgroundColor(resources.getColor(R.color.backTr));

            TextView money = createTextView(moneys.get(i).money + "");
            TextView comment = createTextView(moneys.get(i).comment);

            tr.addView(money);
            tr.addView(comment);

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
}
