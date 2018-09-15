package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sam4s.printer.Sam4sBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ru.landmark.tnp.Adapters.RVMain;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Functional.NavigationDrawer;
import ru.landmark.tnp.Interfaces.PrintCheckInterface;
import ru.landmark.tnp.Structurs.Report;

public class PrintCheck extends AppCompatActivity implements PrintCheckInterface
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    RVMain rvMain;
    RecyclerView recyclerView;

    ArrayList<Report> reports = new ArrayList<Report>();

    SearchView searchView = null;
    MenuItem searchMenuItem = null;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_check);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Печать чеков");

        sharedPref = getSharedPreferences(resources.getString(R.string.appSettingsName), Context.MODE_PRIVATE);

        new NavigationDrawer(context, activity, toolbar);

        fillArray();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        rvMain = new RVMain(activity, resources, reports, 1, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rvMain);
    }

    public void fillArray()
    {
        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.HIRE);
        SQLiteDatabase Database = dbHelper.getWritableDatabase();

        Cursor cursor = Database.rawQuery("SELECT * FROM Hire", null);

        if (cursor.moveToLast())
        {
            do
            {
                if (reports.size() != 10)
                {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    int userConsumersId = cursor.getInt(cursor.getColumnIndex("userConsumersId"));
                    int userClientId = cursor.getInt(cursor.getColumnIndex("userClientId"));
                    int tariffId = cursor.getInt(cursor.getColumnIndex("tariffId"));
                    int countCart = cursor.getInt(cursor.getColumnIndex("countCart"));
                    String startDate = cursor.getString(cursor.getColumnIndex("startDate"));
                    String endDate = cursor.getString(cursor.getColumnIndex("endDate"));
                    String summ = cursor.getString(cursor.getColumnIndex("summ"));
                    String barcode = cursor.getString(cursor.getColumnIndex("barcode"));
                    int dop = cursor.getInt(cursor.getColumnIndex("dop"));
                    int idOrder = cursor.getInt(cursor.getColumnIndex("idOrder"));

                    String phoneNumber = "";
                    String fio = "~";
                    String deposit = "";
                    String inHire = "~";

                    int summRes = 0;

                    if (!isNone(idOrder))
                    {
                        if (userConsumersId != 0)
                        {
                            DBHelper dbHelperConsumers = new DBHelper(getApplicationContext(), DBHelper.USERS_CONSUMERS);
                            SQLiteDatabase DatabaseConsumers = dbHelperConsumers.getWritableDatabase();

                            Cursor cursorConsumers = DatabaseConsumers.rawQuery("SELECT * FROM UserConsumers WHERE id = ?",
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
                        else
                        {
                            DBHelper dbHelperClient = new DBHelper(getApplicationContext(), DBHelper.USERS_CLIENT);
                            SQLiteDatabase DatabaseClient = dbHelperClient.getWritableDatabase();

                            Cursor cursorClient = DatabaseClient.rawQuery("SELECT * FROM UserClient WHERE id = ?",
                                    new String[]{String.valueOf(userClientId)});

                            if (cursorClient.moveToFirst())
                            {
                                String name = cursorClient.getString(cursorClient.getColumnIndex("fio"));
                                String phone = cursorClient.getString(cursorClient.getColumnIndex("numberPhone"));

                                fio = name;
                                phoneNumber = phone;

                                deposit = cursorClient.getString(cursorClient.getColumnIndex("deposit"));
                            }

                            cursorClient.close();
                            DatabaseClient.close();
                            dbHelperClient.close();
                        }

                        String endDateRes = "~";

                        if (!endDate.equals(""))
                        {
                            endDateRes = endDate;
                            summRes = Integer.parseInt(summ);

                            DBHelper dbHelperAllHire = new DBHelper(getApplicationContext(), DBHelper.HIRE);
                            SQLiteDatabase DatabaseAllHire = dbHelperAllHire.getWritableDatabase();

                            Cursor cursorAllHire = DatabaseAllHire.rawQuery("SELECT * FROM Hire WHERE idOrder = ?",
                                    new String[]{String.valueOf(idOrder)});

                            if (cursorAllHire.moveToFirst())
                            {
                                do
                                {
                                    int idH = cursorAllHire.getInt(cursorAllHire.getColumnIndex("id"));
                                    if (id != idH)
                                    {
                                        String summAllHire = cursorAllHire.getString(cursorAllHire.getColumnIndex("summ"));
                                        summRes += Integer.parseInt(summAllHire);
                                    }
                                }
                                while (cursor.moveToNext());
                            }

                            cursorAllHire.close();
                            DatabaseAllHire.close();
                            dbHelperAllHire.close();

                            DateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                            try
                            {
                                Date dateStart = df.parse(startDate);
                                Date dateEnd = df.parse(endDate);

                                long diff = (dateEnd.getTime() - dateStart.getTime()) / 1000;

                                long hours = diff / 3600;
                                long minutes = (diff - (3600 * hours)) / 60;
                                long seconds = (diff - (3600 * hours)) - minutes * 60;

                                String hourMinStr = minutes + "." + seconds;
                                float hourMinF = Float.parseFloat(hourMinStr);

                                inHire = hours + ":" + minutes + ":" + seconds;
                            }
                            catch (ParseException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        String summStrRes = "";

                        if (endDateRes.equals("~"))
                        {
                            summStrRes = "~";
                        }
                        else
                        {
                            summStrRes = String.valueOf(summRes);
                        }

                        reports.add(new Report(id, phoneNumber, fio, barcode, idOrder, "", 0, startDate, endDateRes, inHire, summStrRes, deposit, dop));
                    }
                }
                else
                {
                    break;
                }
            }
            while (cursor.moveToPrevious());
        }

        cursor.close();
        Database.close();
        dbHelper.close();
    }

    boolean isNone(int idOrder)
    {
        for (int i=0; i<reports.size(); i++)
        {
            if (reports.get(i).idNumber == idOrder)
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu_main_activity, menu);
        searchMenuItem = menu.findItem(R.id.searchClient);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        final SearchView searchViewAndroidActionBar = searchView;
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchViewAndroidActionBar.clearFocus();

                String text = query.toLowerCase(Locale.getDefault());

                rvMain.filterBarcode(text);

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

    private void onClickCamera()
    {
        new IntentIntegrator(activity).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

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
                    rvMain.filterBarcode(barcode);

                    Log.d("myLog", "lll");
                }
            }
            catch (Exception e)
            {
                MainActivity.showMsg("Error1 " + e.getMessage().toString() + "Barcode: " + barcode, PrintCheck.this);
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.camera:
                onClickCamera();
                return true;
            default:
                return false;
        }
    }

    @Override
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
                    MainActivity.showMsg(e.getMessage(), PrintCheck.this);
                }
            }
            catch(Exception e)
            {
                MainActivity.showMsg(e.getMessage(), PrintCheck.this);
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
                    MainActivity.showMsg(e.getMessage(), PrintCheck.this);
                }
            }
            catch(Exception e)
            {
                MainActivity.showMsg(e.getMessage(), PrintCheck.this);
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

    @Override
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
                    MainActivity.showMsg(e.getMessage(), PrintCheck.this);
                }
            }
            catch(Exception e)
            {
                MainActivity.showMsg(e.getMessage(), PrintCheck.this);
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
