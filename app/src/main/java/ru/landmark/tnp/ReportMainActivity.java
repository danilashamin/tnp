package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.landmark.tnp.Functional.NavigationDrawer;

public class ReportMainActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    Button buttonReportUsers;
    Button buttonReportMoney;
    Button buttonReportDop;

    TextView moneyInDay;
    TextView moneyInCase;
    TextView usersInHire;
    TextView usersInDay;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_main);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.TextReport));
        new NavigationDrawer(context, activity, toolbar);

        moneyInDay = (TextView) findViewById(R.id.moneyInDay);
        moneyInCase = (TextView) findViewById(R.id.moneyInCase);

        usersInHire = (TextView) findViewById(R.id.usersInHire);
        usersInDay = (TextView) findViewById(R.id.usersInDay);

        sharedPref = getSharedPreferences(resources.getString(R.string.appSettingsName), Context.MODE_PRIVATE);
        Date currentDate = new Date();
        String dateParse = new SimpleDateFormat("dd_MM_yyyy").format(currentDate);
        int userMoneyInDay = sharedPref.getInt(resources.getString(R.string.appUserMoneyInDay) + dateParse, 0);
        int userMoneyInCase = sharedPref.getInt(resources.getString(R.string.appMoneyCase), 0);
        int userInHire = sharedPref.getInt(resources.getString(R.string.appUsersInHire), 0);

        Date currentDateE = new Date();
        String dateParseE = new SimpleDateFormat("dd_MM_yyyy").format(currentDateE);
        int userInDay = sharedPref.getInt(resources.getString(R.string.appUserHire) + dateParseE, 0);

        Log.d("myLog", resources.getString(R.string.appUserHire) + dateParseE + " reesss");

        moneyInDay.setText("Выручка за день: " + userMoneyInDay);
        moneyInCase.setText("Сумма в кассе: " + userMoneyInCase);
        this.usersInHire.setText("В прокате: " + userInHire);
        this.usersInDay.setText("Обслужено за день: " + userInDay);

        buttonReportUsers = (Button) findViewById(R.id.buttonReportUser);
        buttonReportMoney = (Button) findViewById(R.id.buttonReportMoney);
        buttonReportDop = (Button) findViewById(R.id.buttonReportDop);

        buttonReportUsers.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(activity, ReportActivity.class);
                startActivity(intent);
            }
        });

        buttonReportMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ReportMoneyActivity.class);
                startActivity(intent);
            }
        });

        buttonReportDop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DopReportActivity.class);
                startActivity(intent);
            }
        });
    }
}
