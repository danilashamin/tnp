package ru.landmark.tnp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Functional.NavigationDrawer;

public class SumCaseActivity extends AppCompatActivity
{
    TextInputLayout textRemoveInCase;
    TextInputLayout textComment;

    Activity activity;
    Context context;

    Toolbar toolbar;

    TextView summCase;
    Button buttonRemoveMoney;
    Button buttonAddMoney;

    SharedPreferences sharedPref;

    Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sum_case);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Добавление и вычитание из кассы");
        new NavigationDrawer(context, activity, toolbar);

        sharedPref = getSharedPreferences(resources.getString(R.string.appSettingsName), Context.MODE_PRIVATE);
        int summInCase = sharedPref.getInt(resources.getString(R.string.appMoneyCase), 0);

        textRemoveInCase = (TextInputLayout) findViewById(R.id.textRemoveMoney);
        textComment = (TextInputLayout) findViewById(R.id.textComment);
        summCase = (TextView) findViewById(R.id.textSumm);
        buttonRemoveMoney = (Button) findViewById(R.id.buttonRemoveMoney);
        buttonAddMoney = (Button) findViewById(R.id.buttonAddMoney);

        summCase.setText("Сумма в кассе: " + summInCase);

        buttonAddMoney.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                addMoney();
            }
        });

        buttonRemoveMoney.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                removeMoney();
            }
        });
    }

    private void addMoney()
    {
        textRemoveInCase.setErrorEnabled(false);
        textComment.setErrorEnabled(false);

        if (!textRemoveInCase.getEditText().getText().toString().equals("") &&
                !textComment.getEditText().getText().toString().equals(""))
        {
            SharedPreferences.Editor ed = sharedPref.edit();

            int summInCase = sharedPref.getInt(resources.getString(R.string.appMoneyCase), 0);

            int summ = summInCase + Integer.parseInt(textRemoveInCase.getEditText().getText().toString());

            ed.putInt(resources.getString(R.string.appMoneyCase), summ);
            ed.commit();

            DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.MONEY);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put("money", textRemoveInCase.getEditText().getText().toString());
            cv.put("comment", textComment.getEditText().getText().toString());

            long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);

            db.close();
            dbHelper.close();

            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

            Date date = new Date();

            String key = sharedPref.getString(resources.getString(R.string.appKey), "");

            DatabaseReference DatabaseRef = mDatabase.getReference();
            DatabaseReference AppsRef = DatabaseRef.child("Apps");

            DatabaseReference AppRef = AppsRef.child(key);

            DatabaseReference Report = AppRef.child("Report");
            String dateStrF = new SimpleDateFormat("dd_MM_yyyy").format(date);
            DatabaseReference DateStartRef = Report.child(dateStrF);
            DatabaseReference CaseInMoney = DateStartRef.child("CaseInMoney");
            CaseInMoney.setValue(summ);

            summCase.setText("Сумма в кассе: " + summ);
        }
        else
        {
            if (textRemoveInCase.getEditText().getText().toString().equals(""))
            {
                textRemoveInCase.setErrorEnabled(true);
                textRemoveInCase.setError("Заполните поле");
            }

            if (textComment.getEditText().getText().toString().equals(""))
            {
                textComment.setErrorEnabled(true);
                textComment.setError("Заполните поле");
            }
        }
    }

    private void removeMoney()
    {
        textRemoveInCase.setErrorEnabled(false);
        textComment.setErrorEnabled(false);

        if (!textRemoveInCase.getEditText().getText().toString().equals("") &&
                !textComment.getEditText().getText().toString().equals(""))
        {
            SharedPreferences.Editor ed = sharedPref.edit();

            int summInCase = sharedPref.getInt(resources.getString(R.string.appMoneyCase), 0);

            int summ = summInCase - Integer.parseInt(textRemoveInCase.getEditText().getText().toString());

            ed.putInt(resources.getString(R.string.appMoneyCase), summ);
            ed.commit();

            DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.MONEY);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put("money", textRemoveInCase.getEditText().getText().toString());
            cv.put("comment", textComment.getEditText().getText().toString());

            long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);

            db.close();
            dbHelper.close();

            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

            Date date = new Date();

            String key = sharedPref.getString(resources.getString(R.string.appKey), "");

            DatabaseReference DatabaseRef = mDatabase.getReference();
            DatabaseReference AppsRef = DatabaseRef.child("Apps");

            DatabaseReference AppRef = AppsRef.child(key);

            DatabaseReference Report = AppRef.child("Report");
            String dateStrF = new SimpleDateFormat("dd_MM_yyyy").format(date);
            DatabaseReference DateStartRef = Report.child(dateStrF);
            DatabaseReference CaseInMoney = DateStartRef.child("CaseInMoney");
            CaseInMoney.setValue(summ);

            summCase.setText("Сумма в кассе: " + summ);
        }
        else
        {
            if (textRemoveInCase.getEditText().getText().toString().equals(""))
            {
                textRemoveInCase.setErrorEnabled(true);
                textRemoveInCase.setError("Заполните поле");
            }

            if (textComment.getEditText().getText().toString().equals(""))
            {
                textComment.setErrorEnabled(true);
                textComment.setError("Заполните поле");
            }
        }
    }
}
