package ru.landmark.tnp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sam4s.printer.Sam4sBuilder;
import com.sam4s.printer.Sam4sPrint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import ru.landmark.tnp.Adapters.RVTariffOrder;
import ru.landmark.tnp.Adapters.RVUserConsumers;
import ru.landmark.tnp.Functional.CustomToast;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Structurs.Tariff;
import ru.landmark.tnp.Structurs.TariffOrder;
import ru.landmark.tnp.Structurs.UserClient;
import ru.landmark.tnp.Structurs.UserConsumers;

public class ChangeTariffActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;
    RecyclerView recyclerView;

    int mode = 0;

    UserClient userClient;
    UserConsumers userConsumers;

    RVTariffOrder rvTariffOrder;

    ArrayList<TariffOrder> tariffOrders = new ArrayList<TariffOrder>();

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_tariff);

        mode = getIntent().getIntExtra("mode", 0);

        if (mode == 1)
        {
            userClient = getIntent().getParcelableExtra("user");
        }
        else
        {
            userConsumers = getIntent().getParcelableExtra("user");
        }

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        sharedPref = getSharedPreferences(resources.getString(R.string.appSettingsName), Context.MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.TextChangeTariff));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tariffOrders.add(new TariffOrder("", 0, true));

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        rvTariffOrder = new RVTariffOrder(activity, resources, tariffOrders, 0);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rvTariffOrder);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu_change_tariff, menu);

        return true;
    }

    private void addTariffOnClick()
    {
        rvTariffOrder.addTariff();
    }

    private void playOnCLick()
    {
        tariffOrders = rvTariffOrder.getArray();

        boolean r = false;

        for (int i=0; i<tariffOrders.size(); i++)
        {
            if (!tariffOrders.get(i).isCreate)
            {
                r = true;
                //return;
            }
        }
       // new CustomToast(activity, "sss", true);
        if (r)
        {
          //  new CustomToast(activity, "Eeee2s", true);
            int idOrder = generateId();
            String barcode = "";
            long rowID = 0;
            long rowIDConsumers = 0;
            Date now = new Date();
            String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(now);

            Log.d("myLog", date + " Date");

            String fio = "";
            String phoneNumber = "";
            String deposit = "";
            String timeStart = new SimpleDateFormat("HH:mm:ss").format(now);
            String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(now);

            if (mode == 1)
            {
                deposit = userClient.deposit;
                fio =userClient.name;
                phoneNumber = userClient.numberPhone;

                barcode = userClient.barcode;
            }
            else
            {
                fio = userConsumers.name;
                /*
                if (userClient.numberPhone != null)
                {
                    phoneNumber = userClient.numberPhone;
                }*/
                barcode = userConsumers.barcode;
            }

            boolean isCol = false;

            for (int i=0; i<tariffOrders.size(); i++)
            {
                int tariffId = getIdTariff(tariffOrders.get(i).nameTariff);
                int count = getMaxColTariff(tariffOrders.get(i).nameTariff);

                DBHelper dbHelperHireCount = new DBHelper(getApplicationContext(), DBHelper.HIRE);
                SQLiteDatabase dbHireCount = dbHelperHireCount.getWritableDatabase();

                Cursor cursor = dbHireCount.rawQuery("SELECT * FROM Hire WHERE tariffId = ? AND endDate = ?", new String[]{String.valueOf(tariffId), ""});

                int countC = 0;

                if (cursor.moveToFirst())
                {
                    do
                    {
                        int countCart = cursor.getInt(cursor.getColumnIndex("countCart"));
                        countC += countCart;
                    } while (cursor.moveToNext());
                }

                cursor.close();
                dbHireCount.close();
                dbHelperHireCount.close();

                countC += tariffOrders.get(i).countCart;

                if (countC > count)
                {
                    new CustomToast(activity, "Тележки по тарифу "+tariffOrders.get(i).nameTariff + " заняты", true);
                    isCol = true;
                    break;
                }
            }

            if (!isCol)
            {
             //   new CustomToast(activity, "Eeee2", true);
                try
                {
                    printCheck(barcode, dateStr, fio, deposit, timeStart, phoneNumber);
                }
                catch (Exception e)
                {
                    MainActivity.showMsg(e.getMessage().toString(), ChangeTariffActivity.this);
                    return;
                }

                if (mode == 1)
                {
                    DBHelper dbHelperClient = new DBHelper(getApplicationContext(), DBHelper.USERS_CLIENT);
                    SQLiteDatabase dbClient = dbHelperClient.getWritableDatabase();

                    ContentValues cvClient = new ContentValues();

                    cvClient.put("fio", userClient.name);
                    cvClient.put("numberPhone", userClient.numberPhone);
                    cvClient.put("deposit", userClient.deposit);
                    cvClient.put("barcode", userClient.barcode);

                    rowID = dbClient.insert(dbHelperClient.getDatabaseName(), null, cvClient);

                    dbClient.close();
                    dbHelperClient.close();
                }
                else
                {
                    rowIDConsumers = userConsumers.id;
                }

                DBHelper dbHelperHire = new DBHelper(getApplicationContext(), DBHelper.HIRE);
                SQLiteDatabase dbHire = dbHelperHire.getWritableDatabase();

                for (int i=0; i<tariffOrders.size(); i++)
                {
                    if (!tariffOrders.get(i).isCreate)
                    {
                        ContentValues cvHire = new ContentValues();

                        int tariffId = getIdTariff(tariffOrders.get(i).nameTariff);

                        cvHire.put("userConsumersId", rowIDConsumers);
                        cvHire.put("userClientId", rowID);
                        cvHire.put("tariffId", tariffId);
                        cvHire.put("countCart", tariffOrders.get(i).countCart);
                        cvHire.put("startDate", date);
                        cvHire.put("endDate", "");
                        cvHire.put("summ", "");
                        cvHire.put("barcode", barcode);
                        cvHire.put("dop", 0);
                        cvHire.put("idOrder", idOrder);

                        long rowIDHire = dbHire.insert(dbHelperHire.getDatabaseName(), null, cvHire);

                        Log.d("myLogCreate", "rowId: " + rowIDHire + " userConsumersId: " + rowIDConsumers + " userClientId: " +
                                rowID + " tariffId: " + tariffId + " countCart: " + tariffOrders.get(i).countCart +
                                " startTime: " + date + " endTime: " + "-" + " summ: " + "-" + " barcode: " + barcode + " idOrder: " + idOrder);
                    }

                    int userInHire = sharedPref.getInt(resources.getString(R.string.appUsersInHire), 0);
                    userInHire += 1;

                    Date currentDate = new Date();
                    String dateParse = new SimpleDateFormat("dd_MM_yyyy").format(currentDate);
                    int userHire = sharedPref.getInt(resources.getString(R.string.appUserHire) + dateParse, 0);
                    userHire += 1;

                    SharedPreferences.Editor ed = sharedPref.edit();
                    ed.putInt(resources.getString(R.string.appUsersInHire), userInHire);
                    ed.putInt(resources.getString(R.string.appUserHire)  + dateParse, userHire);
                    ed.commit();

                    String key = sharedPref.getString(resources.getString(R.string.appKey), "");

                    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

                    DatabaseReference DatabaseRef = mDatabase.getReference();
                    DatabaseReference AppsRef = DatabaseRef.child("Apps");

                    DatabaseReference AppRef = AppsRef.child(key);

                    DatabaseReference Report = AppRef.child("Report");
                    String dateStrF = new SimpleDateFormat("dd_MM_yyyy").format(now);
                    DatabaseReference DateStartRef = Report.child(dateStrF);
                    DatabaseReference usersInHire = DateStartRef.child("UsersInHire");
                    usersInHire.setValue(userInHire);

                    DatabaseReference userHireRef = DateStartRef.child("UserHire");
                    userHireRef.setValue(userHire);

                    new CustomToast(activity, resources.getString(R.string.TextHireStart), false);
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }

                dbHire.close();
                dbHelperHire.close();
            }
            else
            {
              //  new CustomToast(activity, "Eeee", true);
            }
        }
        else
        {
            new CustomToast(activity, "Добавьте тариф", false);
        }
    }

    private void printCheck(String barcode, String Date, String FIO, String deposit, String timeStart, String phoneNumber)
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
            textPrint += "      \"Тачки на прокачку\"\nДата: "+Date+"\nВремя начала проката: "+timeStart+"\nФио: "+FIO+"\nЗалог: "+deposit+"\n      " + textRules + "\nНомер телефона: " + phoneNumber + "\n     ";
        }
        else
        {
            textPrint += "      \"Тачки на прокачку\"\nДата: "+Date+"\nВремя начала проката: "+timeStart+"\nФио: "+FIO+"\n         " + textRules + "\nНомер телефона: " + phoneNumber + "\n     ";
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
                    MainActivity.showMsg(e.getMessage(), ChangeTariffActivity.this);
                }
            }
            catch(Exception e)
            {
                MainActivity.showMsg(e.getMessage(), ChangeTariffActivity.this);
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
                    MainActivity.showMsg(e.getMessage(), ChangeTariffActivity.this);
                }
            }
            catch(Exception e)
            {
                MainActivity.showMsg(e.getMessage(), ChangeTariffActivity.this);
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

    private int getMaxColTariff(String nameTariff)
    {
        DBHelper dbHelper = new DBHelper(context, DBHelper.TARIFF);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Tariff WHERE nameTariff = ?", new String[]{nameTariff});

        int count = 0;

        if (cursor.moveToFirst())
        {
            count = cursor.getInt(cursor.getColumnIndex("countCart"));
            cursor.close();
            db.close();
            dbHelper.close();
        }

        return count;
    }

    private int getIdTariff(String nameTariff)
    {
        DBHelper dbHelper = new DBHelper(context, DBHelper.TARIFF);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Tariff WHERE nameTariff = ?", new String[]{nameTariff});

        int id = 0;

        if (cursor.moveToFirst())
        {
            id = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
            db.close();
            dbHelper.close();
        }

        return  id;
    }

    public int generateId()
    {
        String id = "";

        for (int l=0; l<5; l++)
        {
            id += new Random().nextInt(9);
        }

        return Integer.parseInt(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.addTariff:
                addTariffOnClick();
                return true;
            case R.id.play:
                playOnCLick();
                return true;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return false;
        }
    }
}
