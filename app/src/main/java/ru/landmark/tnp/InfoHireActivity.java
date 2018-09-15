package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sam4s.printer.Sam4sBuilder;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.landmark.tnp.Adapters.RVTariffOrder;
import ru.landmark.tnp.Functional.CustomToast;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Structurs.Report;
import ru.landmark.tnp.Structurs.TariffOrder;

public class InfoHireActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    Report report;

    TextView textFIO;
    TextView textStartTime;
    TextView textEndTime;
    TextView textInHire;
    TextView textSumm;
    TextView textDeposit;
    TextView textBarcode;
    TextView textPhone;

    RecyclerView recyclerView;

    RVTariffOrder rvTariffOrder;

    Button buttonPrintFirst;
    Button buttonPrintSecond;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_hire);

        report = getIntent().getParcelableExtra("report");

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        sharedPref = getSharedPreferences(resources.getString(R.string.appSettingsName), Context.MODE_PRIVATE);

        toolbar = (Toolbar)findViewById(R.id.toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Информация о пользователе");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textFIO = (TextView)findViewById(R.id.textPerson);
        textStartTime = (TextView)findViewById(R.id.textStartTime);
        textEndTime = (TextView)findViewById(R.id.textEndTime);
        textInHire = (TextView)findViewById(R.id.textHoldingHire);
        textSumm = (TextView)findViewById(R.id.textSumm);
        textDeposit = (TextView)findViewById(R.id.textDeposit);
        textBarcode = (TextView)findViewById(R.id.textBarcode);
        textPhone = (TextView)findViewById(R.id.textPhone);

        buttonPrintFirst = (Button) findViewById(R.id.buttonPrintFirst);
        buttonPrintSecond = (Button) findViewById(R.id.buttonPrintSecond);

        buttonPrintFirst.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    Date datePrint = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(report.startTime);
                    String datePrintS = new SimpleDateFormat("dd/MM/yyyy").format(datePrint);

                    Date time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(report.startTime);
                    String timePrintS = new SimpleDateFormat("HH:mm:ss").format(time);

                    printCheckInHire(report.barcode, datePrintS, report.fio, report.deposit, timePrintS);
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
        });

        buttonPrintSecond.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!report.endTime.equals("~"))
                {
                    try
                    {
                        Date datePrint = new SimpleDateFormat("dd/MM/yyyy").parse(report.startTime);
                        String datePrintS = new SimpleDateFormat("dd/MM/yyyy").format(datePrint);

                        printCheckStop(report.summ,
                                dop(String.valueOf(report.idNumber)) ,datePrintS,
                                report.fio, report.timeHire);
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    new CustomToast(activity, "Прокат еще не закончен", false);
                }
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        ArrayList<TariffOrder> tariffOrders = new ArrayList<TariffOrder>();
        rvTariffOrder = new RVTariffOrder(activity, resources, tariffOrders, 1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rvTariffOrder);

        loadInfo();
    }

    private int dop(String idOrderRes)
    {
        DBHelper dbHelper = new DBHelper(activity.getApplicationContext(), DBHelper.SALE_DOP);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM SaleDop WHERE idOrder = ?", new String[]{String.valueOf(idOrderRes)});

        int summDop = 0;

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

                    summDop += namePrice;
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

        return summDop;
    }

    private void loadInfo()
    {
        textFIO.setText(report.fio);
        if (report.phone != null)
        {
            textPhone.setText(report.phone);
        }
        textStartTime.setText("Время начало: " + report.startTime);
        textEndTime.setText("Время окончания: " + report.endTime);
        textInHire.setText("Время в прокате: " + report.timeHire);
        textSumm.setText("Сумма: " + report.summ);
        if (report.deposit.equals(""))
        {
            report.deposit = "~";
        }
        textDeposit.setText("Залог: " + report.deposit);
        textBarcode.setText("Штрихкод: " + report.barcode);

        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.HIRE);
        SQLiteDatabase Database = dbHelper.getWritableDatabase();

        Cursor cursor = Database.rawQuery("SELECT * FROM Hire WHERE idOrder = ?", new String[]{String.valueOf(report.idNumber)});

        if (cursor.moveToFirst())
        {
            do
            {
                int tariffId = cursor.getInt(cursor.getColumnIndex("tariffId"));
                int countCart = cursor.getInt(cursor.getColumnIndex("countCart"));

                DBHelper dbHelperTariffs = new DBHelper(getApplicationContext(), DBHelper.TARIFF);
                SQLiteDatabase DatabaseTariffs = dbHelperTariffs.getWritableDatabase();

                Cursor cursorGetTariff = DatabaseTariffs.rawQuery("SELECT * FROM Tariff WHERE id = ?",
                        new String[]{String.valueOf(tariffId)});

                if (cursorGetTariff.moveToFirst())
                {
                    String nameTariff = cursorGetTariff.getString(cursorGetTariff.getColumnIndex("nameTariff"));

                    rvTariffOrder.AddCartParametr(nameTariff, countCart);
                }
            }
            while (cursor.moveToNext());
        }
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

    public void printCheckInHire(String barcode, String Date, String FIO, String deposit, String timeStart)
    {
        final int SEND_TIMEOUT = 10 * 1000;
        final int SIZEWIDTH_MAX = 8;
        final int SIZEHEIGHT_MAX = 8;

        Sam4sBuilder builder = null;

        String textRulesS = sharedPref.getString(resources.getString(R.string.appTextRules), "");

        String textRules = "      \"Правила проката\"\n" +
                textRulesS;

        String textPrint = "";

        if (!deposit.equals(""))
        {
            textPrint += "      \"Тачки на прокачку\"\nДата: "+Date+"\nВремя начала проката: "+timeStart+"\nФио: "+FIO+"\nЗалог: "+deposit+"\n      " + textRules + "\n     ";
        }
        else
        {
            textPrint += "      \"Тачки на прокачку\"\nДата: "+Date+"\nВремя начала проката: "+timeStart+"\nФио: "+FIO+"\n         " + textRules + "\n     ";
        }

        Log.d("myLogTextPrint", textPrint);

        try
        {
            try
            {
                builder = new Sam4sBuilder("ELLIX", Sam4sBuilder.LANG_EN);

                builder.addTextFont(Sam4sBuilder.FONT_A);
                builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
                builder.addTextPosition(0);
                builder.addTextLineSpace(30);
                builder.addCharacterCode(Sam4sBuilder.CHARACTER_CODE_WPC1251);
                builder.addTextSize(1, 1);
                builder.addTextStyle(false, false, false, Sam4sBuilder.COLOR_1);
                byte[] data= textPrint.getBytes("cp1251");
                builder.addCommand(data);
                builder.addFeedUnit(30);

                try
                {
                    MainActivity.mPrinter.sendData(builder);
                }
                catch(Exception e)
                {
                    MainActivity.showMsg(e.getMessage(), InfoHireActivity.this);
                }
            }
            catch(Exception e)
            {
                MainActivity.showMsg(e.getMessage(), InfoHireActivity.this);
            }
            if(builder != null)
            {
                try
                {
                    builder.clearCommandBuffer();
                    builder = null;
                }
                catch(Exception e)
                {
                    builder = null;
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        try
        {
            Sam4sBuilder builderBar = null;

            try
            {
                builderBar = new Sam4sBuilder("ELLIX", Sam4sBuilder.LANG_KO);

                builderBar.addBarcode(barcode, Sam4sBuilder.BARCODE_EAN13,
                        Sam4sBuilder.HRI_BELOW, Sam4sBuilder.FONT_A, 3, 162);
                builderBar.addCut(Sam4sBuilder.CUT_FEED);

                try
                {
                    MainActivity.mPrinter.sendData(builderBar);
                }
                catch(Exception e)
                {
                    MainActivity.showMsg(e.getMessage(), InfoHireActivity.this);
                }
            }
            catch(Exception e)
            {
                MainActivity.showMsg(e.getMessage(), InfoHireActivity.this);
            }
            if(builderBar != null)
            {
                try
                {
                    builderBar.clearCommandBuffer();
                    builderBar = null;
                }
                catch(Exception e)
                {
                    builderBar = null;
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void printCheckStop(String summPrint, int summDop, String datePrint, String fioPrint, String holdingTimePrint)
    {
        final int SEND_TIMEOUT = 10 * 1000;
        final int SIZEWIDTH_MAX = 8;
        final int SIZEHEIGHT_MAX = 8;

        Sam4sBuilder builder = null;

        String textPrint = "";

        float summPrintR = Float.valueOf(summPrint);
        float res = summPrintR + summDop;

        textPrint = sharedPref.getString(resources.getString(R.string.appTextRekv), "")+"\n      \"Тачки на прокачку\"\n" +
                "Дата: " + datePrint + "\nФИО: " + fioPrint + "\nВремя в прокате: "+
                holdingTimePrint + "\nСумма: " + String.valueOf(res)+ "\nКассир: "+ sharedPref.getString(resources.getString(R.string.appDisplayName), "null") +"\n      Будем рады Вам снова !";
        try
        {
            try
            {
                builder = new Sam4sBuilder("ELLIX", Sam4sBuilder.LANG_EN);

                builder.addTextFont(Sam4sBuilder.FONT_A);
                builder.addTextAlign(Sam4sBuilder.ALIGN_LEFT);
                builder.addTextPosition(0);
                builder.addTextLineSpace(30);
                builder.addCharacterCode(Sam4sBuilder.CHARACTER_CODE_WPC1251);
                builder.addTextSize(1, 1);
                builder.addTextStyle(false, false, false, Sam4sBuilder.COLOR_1);
                byte[] data= textPrint.getBytes("cp1251");
                builder.addCommand(data);
                builder.addCut(Sam4sBuilder.CUT_FEED);
                builder.addFeedUnit(30);

                try
                {
                    MainActivity.mPrinter.sendData(builder);
                }
                catch(Exception e)
                {
                    MainActivity.showMsg(e.getMessage(), InfoHireActivity.this);
                }
            }
            catch(Exception e)
            {
                MainActivity.showMsg(e.getMessage(), InfoHireActivity.this);
            }
            if(builder != null)
            {
                try
                {
                    builder.clearCommandBuffer();
                    builder = null;
                }
                catch(Exception e)
                {
                    builder = null;
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
