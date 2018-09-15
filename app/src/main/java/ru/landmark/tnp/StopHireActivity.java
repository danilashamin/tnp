package ru.landmark.tnp;

import android.app.Activity;
import android.content.ContentValues;
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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sam4s.printer.Sam4sBuilder;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ru.landmark.tnp.Adapters.RVTariffOrder;
import ru.landmark.tnp.Functional.CustomToast;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Functional.NavigationDrawer;
import ru.landmark.tnp.Structurs.DopService;
import ru.landmark.tnp.Structurs.IntervalTime;
import ru.landmark.tnp.Structurs.Tariff;
import ru.landmark.tnp.Structurs.TariffOrder;

public class StopHireActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    LinearLayout linearLayout;
    LinearLayout layoutDeposit;
    LinearLayout notUser;

    TextView fio;
    TextView startTime;
    TextView endTime;
    TextView summ;
    TextView deposit;
    TextView barcode;
    TextView holdingHire;

    TextView textInfo;

    RecyclerView recyclerView;

    SearchView searchView;
    MenuItem searchMenuItem;

    RVTariffOrder rvTariffOrder;

    String holdingTimePrint = "";
    String datePrint = "";
    String fioPrint = "";
    String summPrint = "";
    String damageRes = "";

    int idOrderRes = 0;
    String barcodeRes = "";
    Date dateEndRes;

    int countCart = 0;
    int conuntCartInTariff = 0;

    boolean isPrint = false;

    SharedPreferences sharedPref;

    ArrayList<DopService> dopServices = new ArrayList<DopService>();

    float summDop;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_hire);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.TextStopHire));

        new NavigationDrawer(context, activity, toolbar);

        sharedPref = getSharedPreferences(resources.getString(R.string.appSettingsName), Context.MODE_PRIVATE);

        linearLayout = (LinearLayout) findViewById(R.id.layout);
        layoutDeposit = (LinearLayout) findViewById(R.id.layoutDeposit);
        notUser = (LinearLayout) findViewById(R.id.notUser);

        fio = (TextView) findViewById(R.id.textPerson);
        startTime = (TextView) findViewById(R.id.textStartTime);
        endTime = (TextView) findViewById(R.id.textEndTime);
        summ = (TextView) findViewById(R.id.textSumm);
        deposit = (TextView) findViewById(R.id.textDeposit);
        barcode = (TextView) findViewById(R.id.textBarcode);
        holdingHire = (TextView) findViewById(R.id.textHoldingHire);
        textInfo = (TextView) findViewById(R.id.textInfo);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        ArrayList<TariffOrder> tariffOrders = new ArrayList<TariffOrder>();

        rvTariffOrder = new RVTariffOrder(activity, resources, tariffOrders, 1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rvTariffOrder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu_stop_hire_activity, menu);
        searchMenuItem = menu.findItem(R.id.searchClient);

        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                searchView.clearFocus();

                String barcode = query.toLowerCase(Locale.getDefault());

                findInBarcode(barcode);
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
            case R.id.print:
                onClickPrint();
                return true;
            case R.id.dopService:
                if (idOrderRes != 0)
                {
                    Intent intent = new Intent(this, SaleDopActivity.class).putExtra("idOrder", idOrderRes);
                    startActivityForResult(intent, 500);
                }
                else
                {
                    new CustomToast(activity, "Выберите пользователя", true);
                }
                return true;
            default:
                return false;
        }
    }

    private void printCheck()
    {
        final int SEND_TIMEOUT = 10 * 1000;
        final int SIZEWIDTH_MAX = 8;
        final int SIZEHEIGHT_MAX = 8;

        Sam4sBuilder builder = null;

        String textPrint = "";

        float summPrintR = Float.valueOf(summPrint);
        float res = summPrintR + summDop;

        textPrint = sharedPref.getString(resources.getString(R.string.appTextRekv), "null")+"\n      \"Тачки на прокачку\"\n" +
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
                    MainActivity.showMsg(e.getMessage(), StopHireActivity.this);
                }
            }
            catch(Exception e)
            {
                MainActivity.showMsg(e.getMessage(), StopHireActivity.this);
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

    private void onClickPrint()
    {
        if (isPrint)
        {
            try
            {
                printCheck();
            }
            catch (Exception e)
            {
                MainActivity.showMsg(e.getMessage().toString(), StopHireActivity.this);

                return;
            }

            stopHire();

            float summPrintR = Float.valueOf(summPrint);
            float res = summPrintR + summDop;

            int userInHire = sharedPref.getInt(resources.getString(R.string.appUsersInHire), 0);
            userInHire -= 1;

            Date currentDate = new Date();
            String dateParse = new SimpleDateFormat("dd_MM_yyyy").format(currentDate);

            int userMoneyInDay = sharedPref.getInt(resources.getString(R.string.appUserMoneyInDay) + dateParse, 0);
            userMoneyInDay += res;

            int moneyCase = sharedPref.getInt(resources.getString(R.string.appMoneyCase), 0);
            moneyCase += res;

            String key = sharedPref.getString(resources.getString(R.string.appKey), "");

            SharedPreferences.Editor ed = sharedPref.edit();
            ed.putInt(resources.getString(R.string.appUsersInHire), userInHire);
            ed.putInt(resources.getString(R.string.appUserMoneyInDay)+dateParse, userMoneyInDay);
            ed.putInt(resources.getString(R.string.appMoneyCase), moneyCase);
            ed.commit();

            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

            DatabaseReference DatabaseRef = mDatabase.getReference();
            DatabaseReference AppsRef = DatabaseRef.child("Apps");

            DatabaseReference AppRef = AppsRef.child(key);

            DatabaseReference Report = AppRef.child("Report");

            Date now;

            try
            {
                now = new SimpleDateFormat("dd/MM/yyyy").parse(datePrint);
            }
            catch (Exception e)
            {
                MainActivity.showMsg(e.getMessage(), StopHireActivity.this);

                return;
            }

            String dateStrF = new SimpleDateFormat("dd_MM_yyyy").format(now);
            DatabaseReference DateStartRef = Report.child(dateStrF);
            DatabaseReference usersInHire = DateStartRef.child("UsersInHire");
            usersInHire.setValue(userInHire);

            DatabaseReference userMoneyInDateRef = DateStartRef.child("UsersMoneyInDay");
            userMoneyInDateRef.setValue(userMoneyInDay);

            DatabaseReference moneyInCaseRef = DateStartRef.child("CaseInMoney");
            moneyInCaseRef.setValue(moneyCase);

            new CustomToast(activity, "Прокат остановлен", false);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else
        {
            new CustomToast(activity, "Выберите пользователя", true);
        }
    }

    private void onClickCamera()
    {
        new IntentIntegrator(activity).initiateScan();
    }

    private void findInBarcode(String Barcode)
    {
        notUser.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);

        rvTariffOrder.clear();

        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.HIRE);
        SQLiteDatabase Database = dbHelper.getWritableDatabase();

        Cursor cursor = Database.rawQuery(
                "SELECT * FROM Hire WHERE barcode = ? AND endDate = ? ",
                new String[]{Barcode, ""});

        String startDateText = "";
        String endTimeText = "";
        String fioText = "";
        String depositText = "";
        String holdingTime = "";
        String barcodeText = Barcode;

        int Summ = 0;

        Date currentDate = new Date();
        endTimeText = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(currentDate);

        int idOrderResL = 0;

        if (cursor.moveToFirst())
        {
            isPrint = true;
            do
            {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int userConsumersId = cursor.getInt(cursor.getColumnIndex("userConsumersId"));
                int userClientId = cursor.getInt(cursor.getColumnIndex("userClientId"));
                int tariffId = cursor.getInt(cursor.getColumnIndex("tariffId"));
                countCart = cursor.getInt(cursor.getColumnIndex("countCart"));
                String startDate = cursor.getString(cursor.getColumnIndex("startDate"));
                String endDate = cursor.getString(cursor.getColumnIndex("endDate"));
                String summ = cursor.getString(cursor.getColumnIndex("summ"));
                String barcode = cursor.getString(cursor.getColumnIndex("barcode"));
                int damage = cursor.getInt(cursor.getColumnIndex("dop"));
                int idOrder = cursor.getInt(cursor.getColumnIndex("idOrder"));

                idOrderResL = idOrder;

                startDateText = startDate;

                Log.d("myLogRes", userConsumersId + " " + userClientId + " " + tariffId + " " + countCart + " " + startDate +
                        " "+ endDate + " " +summ + " " + barcode + " " + damage + " " + idOrder);

                String fio = "";


                if (userConsumersId != 0 && fioText.equals(""))
                {
                    layoutDeposit.setVisibility(View.GONE);

                    DBHelper dbHelperConsumers = new DBHelper(getApplicationContext(), DBHelper.USERS_CONSUMERS);
                    SQLiteDatabase DatabaseConsumers = dbHelperConsumers.getWritableDatabase();

                    Cursor cursorConsumers = DatabaseConsumers.rawQuery(
                            "SELECT * FROM UserConsumers WHERE id = ? ",
                            new String[]{String.valueOf(userConsumersId)});

                    if (cursorConsumers.moveToFirst())
                    {
                        String name = cursorConsumers.getString(cursorConsumers.getColumnIndex("fio"));

                        fio = name;

                        fioText = fio;
                    }

                    cursorConsumers.close();
                    DatabaseConsumers.close();
                    dbHelperConsumers.close();
                }
                else if (userClientId != 0 && fioText.equals(""))
                {
                    layoutDeposit.setVisibility(View.VISIBLE);

                    DBHelper dbHelperClient = new DBHelper(getApplicationContext(), DBHelper.USERS_CLIENT);
                    SQLiteDatabase DatabaseClient = dbHelperClient.getWritableDatabase();

                    Cursor cursorClient = DatabaseClient.rawQuery(
                            "SELECT * FROM UserClient WHERE id = ? ",
                            new String[]{String.valueOf(userClientId)});

                    if (cursorClient.moveToFirst())
                    {
                        String name = cursorClient.getString(cursorClient.getColumnIndex("fio"));

                        String deposit = cursorClient.getString(cursorClient.getColumnIndex("deposit"));

                        depositText = deposit;

                        fio = name;

                        fioText = fio;
                    }

                    cursorClient.close();
                    DatabaseClient.close();
                    dbHelperClient.close();
                }

                DateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                try
                {
                    Date dateStart = df.parse(startDate);
                    dateEndRes = currentDate;

                    long diff = (currentDate.getTime() - dateStart.getTime()) / 1000;

                    long hours = diff / 3600;
                    long minutes = (diff - (3600 * hours)) / 60;
                    long seconds = (diff - (3600 * hours)) - minutes * 60;

                    String hourMinStr = minutes + "." + seconds;
                    float hourMinF = Float.parseFloat(hourMinStr);

                    holdingTime = hours + "." + minutes + "." + seconds;

                    Cursor cursorTariff = Database.rawQuery(
                            "SELECT * FROM Hire WHERE barcode = ? AND idOrder = ? ",
                            new String[]{Barcode, String.valueOf(idOrder)});

                    if (cursorTariff.moveToFirst())
                    {
                        do
                        {
                            String countCartT = cursorTariff.getString(cursorTariff.getColumnIndex("countCart"));
                            String idTariff = cursorTariff.getString(cursorTariff.getColumnIndex("tariffId"));

                            DBHelper dbHelperTariffs = new DBHelper(getApplicationContext(), DBHelper.TARIFF);
                            SQLiteDatabase DatabaseTariffs = dbHelperTariffs.getWritableDatabase();

                            Cursor cursorGetTariff = DatabaseTariffs.rawQuery("SELECT * FROM Tariff WHERE id = ?",
                                    new String[]{idTariff});

                            if (cursorGetTariff.moveToFirst())
                            {
                                conuntCartInTariff = cursorGetTariff.getInt(cursorGetTariff.getColumnIndex("countCart"));
                                String pathTariff = cursorGetTariff.getString(cursorGetTariff.getColumnIndex("pathTariff"));
                                String nameTariff = cursorGetTariff.getString(cursorGetTariff.getColumnIndex("nameTariff"));

                                ArrayList<TariffOrder> list = rvTariffOrder.getArray();

                                boolean isT = false;

                                for (int i=0; i<list.size();i++)
                                {
                                    if (nameTariff.equals(list.get(i).nameTariff))
                                    {
                                        isT = true;
                                    }
                                }

                                if (!isT)
                                {
                                    ArrayList<IntervalTime> intervalTimes = fillArray(pathTariff);

                                    String dayRes = (String) android.text.format.DateFormat.format("EEEE", currentDate);
                                    String dayOfTheWeek = dayRes.toLowerCase();


                                    for (int i = 0; i < intervalTimes.size(); i++)
                                    {
                                        boolean days = false;

                                        Log.d("myLog", dayOfTheWeek + " days");

                                        if (dayOfTheWeek.equals("monday") || dayOfTheWeek.equals("понедельник"))
                                        {
                                            days = intervalTimes.get(i).pn;
                                        }
                                        else if (dayOfTheWeek.equals("tuesday") || dayOfTheWeek.equals("вторник"))
                                        {
                                            days = intervalTimes.get(i).vt;
                                        }
                                        else if (dayOfTheWeek.equals("wednesday") || dayOfTheWeek.equals("среда"))
                                        {
                                            days = intervalTimes.get(i).sr;
                                        }
                                        else if (dayOfTheWeek.equals("thursday") || dayOfTheWeek.equals("четверг"))
                                        {
                                            days = intervalTimes.get(i).ct;
                                        }
                                        else if (dayOfTheWeek.equals("friday") || dayOfTheWeek.equals("пятница"))
                                        {
                                            days = intervalTimes.get(i).pt;
                                        }
                                        else if (dayOfTheWeek.equals("saturday") || dayOfTheWeek.equals("суббота"))
                                        {
                                            days = intervalTimes.get(i).sub;
                                        }
                                        else if (dayOfTheWeek.equals("sunday") || dayOfTheWeek.equals("воскресенье"))
                                        {
                                            days = intervalTimes.get(i).vosk;
                                        }

                                        if (days)
                                        {
                                            Date startTime = new SimpleDateFormat("HH.mm.ss").parse(intervalTimes.get(i).startTime);
                                            Date endTime = new SimpleDateFormat("HH.mm.ss").parse(intervalTimes.get(i).endTime);

                                            Date currentTime = new SimpleDateFormat("HH.mm.ss").parse(holdingTime);

                                            if (startTime.getTime() <= currentTime.getTime())
                                            {
                                                Summ += intervalTimes.get(i).price * Integer.valueOf(countCartT);
                                            }
                                        }
                                    }

                                    rvTariffOrder.AddCartParametr(nameTariff, Integer.parseInt(countCartT));
                                }
                            }

                            DatabaseTariffs.close();
                            dbHelperTariffs.close();
                        }
                        while (cursorTariff.moveToNext());
                    }
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());

            cursor.close();
            Database.close();
            dbHelper.close();

            datePrint = new SimpleDateFormat("dd/MM/yyyy").format(currentDate);
            fioPrint = fioText;
            holdingTimePrint = holdingTime;
            summPrint = String.valueOf(Summ);

            barcodeRes = barcodeText;
            idOrderRes = idOrderResL;

            fio.setText(fioText);
            startTime.setText("Начало: " + startDateText);
            endTime.setText("Окончание: " + endTimeText);
            holdingHire.setText("Время а прокате: " + holdingTime);
            summ.setText("Сумма: " + Summ);
            deposit.setText("Залог: " + depositText);
            barcode.setText("Штрихкод: " + barcodeText);

            linearLayout.setVisibility(View.VISIBLE);

            dop();
        }
        else
        {
            idOrderRes = 0;
            isPrint = false;
            notUser.setVisibility(View.VISIBLE);
            textInfo.setText("По данному штрихкоду пользователей в прокате не найденно");
        }
    }

    private ArrayList<IntervalTime> fillArray(String pathTariff)
    {
        ArrayList<IntervalTime> intervalTimes = new ArrayList<IntervalTime>();

        Log.d("myLog", pathTariff + " pathInterval");

        FileInputStream fis = null;
        InputStreamReader isr = null;

        File file = new File(pathTariff);

        String data = "";

        try
        {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            data = new String(inputBuffer);
            isr.close();
            fis.close();

            InputStream is = new ByteArrayInputStream(data.getBytes("UTF-8"));

            DocumentBuilderFactory dbf;
            DocumentBuilder db;
            NodeList items = null;
            Document dom;

            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            dom = db.parse(is);
            // normalize the document
            dom.getDocumentElement().normalize();

            items = dom.getElementsByTagName("interval");

            for (int i=0;i<items.getLength();i++)
            {
                Node item = items.item(i);
                NodeList child = item.getChildNodes();

                Log.d("myLog", child.item(1).getNodeName() + " "+
                        child.item(2).getNodeName() + " " +
                        child.item(3).getNodeName());

                String startTime = child.item(1).getTextContent();
                String endTime = child.item(3).getTextContent();
                String price = child.item(5).getTextContent();

                boolean pn = Boolean.valueOf(child.item(7).getTextContent());
                boolean vt = Boolean.valueOf(child.item(9).getTextContent());
                boolean sr = Boolean.valueOf(child.item(11).getTextContent());
                boolean ct = Boolean.valueOf(child.item(13).getTextContent());
                boolean pt = Boolean.valueOf(child.item(15).getTextContent());
                boolean sub = Boolean.valueOf(child.item(17).getTextContent());
                boolean vosk = Boolean.valueOf(child.item(19).getTextContent());

                intervalTimes.add(new IntervalTime(startTime, endTime, Float.valueOf(price),
                        pn, vt, sr, ct, pt, sub, vosk, false));
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return intervalTimes;
    }

    private void dop()
    {
        DBHelper dbHelper = new DBHelper(activity.getApplicationContext(), DBHelper.SALE_DOP);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM SaleDop WHERE idOrder = ?", new String[]{String.valueOf(idOrderRes)});

        if (cursor.moveToFirst())
        {
            summDop = 0;
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
            float summPrintR = Float.valueOf(summPrint);
            float res = summPrintR + summDop;
            summ.setText("Сумма: " + res);
        }

        cursor.close();
        db.close();
        dbHelper.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 500)
        {
            dop();
        }
        else
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
    }

    private void stopHire()
    {
        DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.HIRE);
        SQLiteDatabase Database = dbHelper.getWritableDatabase();

        Cursor cursor = Database.rawQuery(
                "SELECT * FROM Hire WHERE barcode = ? AND idOrder = ? ",
                new String[]{barcodeRes, String.valueOf(idOrderRes)});

        if (cursor.moveToFirst())
        {
            DateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String dateEndStr = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dateEndRes);
            do
            {
                try
                {
                    int Summ = 0;

                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String startDate = cursor.getString(cursor.getColumnIndex("startDate"));
                    Date startDateRes = df.parse(startDate);

                    long diff = (dateEndRes.getTime() - startDateRes.getTime()) / 1000;

                    long hours = diff / 3600;
                    long minutes = (diff - (3600 * hours)) / 60;
                    long seconds = (diff - (3600 * hours)) - minutes * 60;

                    String hourMinStr = minutes + "." + seconds;
                    float hourMinF = Float.parseFloat(hourMinStr);

                    String holdingTime = hours + "." + minutes + "." + seconds;

                    String countCartT = cursor.getString(cursor.getColumnIndex("countCart"));
                    String idTariff = cursor.getString(cursor.getColumnIndex("tariffId"));

                    DBHelper dbHelperTariffs = new DBHelper(getApplicationContext(), DBHelper.TARIFF);
                    SQLiteDatabase DatabaseTariffs = dbHelperTariffs.getWritableDatabase();

                    Cursor cursorGetTariff = DatabaseTariffs.rawQuery("SELECT * FROM Tariff WHERE id = ?",
                            new String[]{idTariff});

                    if (cursorGetTariff.moveToFirst())
                    {
                        conuntCartInTariff = cursorGetTariff.getInt(cursorGetTariff.getColumnIndex("countCart"));
                        String pathTariff = cursorGetTariff.getString(cursorGetTariff.getColumnIndex("pathTariff"));
                        String nameTariff = cursorGetTariff.getString(cursorGetTariff.getColumnIndex("nameTariff"));

                        ArrayList<IntervalTime> intervalTimes = fillArray(pathTariff);

                        String dayRes = (String) android.text.format.DateFormat.format("EEEE", dateEndRes);
                        String dayOfTheWeek = dayRes.toLowerCase();


                        for (int i = 0; i < intervalTimes.size(); i++)
                        {
                            boolean days = false;

                            Log.d("myLog", dayOfTheWeek + " days");

                            if (dayOfTheWeek.equals("monday") || dayOfTheWeek.equals("понедельник"))
                            {
                                days = intervalTimes.get(i).pn;
                            }
                            else if (dayOfTheWeek.equals("tuesday") || dayOfTheWeek.equals("вторник"))
                            {
                                days = intervalTimes.get(i).vt;
                            }
                            else if (dayOfTheWeek.equals("wednesday") || dayOfTheWeek.equals("среда"))
                            {
                                days = intervalTimes.get(i).sr;
                            }
                            else if (dayOfTheWeek.equals("thursday") || dayOfTheWeek.equals("четверг"))
                            {
                                days = intervalTimes.get(i).ct;
                            }
                            else if (dayOfTheWeek.equals("friday") || dayOfTheWeek.equals("пятница"))
                            {
                                days = intervalTimes.get(i).pt;
                            }
                            else if (dayOfTheWeek.equals("saturday") || dayOfTheWeek.equals("суббота"))
                            {
                                days = intervalTimes.get(i).sub;
                            }
                            else if (dayOfTheWeek.equals("sunday") || dayOfTheWeek.equals("воскресенье"))
                            {
                                days = intervalTimes.get(i).vosk;
                            }

                            if (days)
                            {
                                Log.d("myLog", "loggg");
                                Date startTime = new SimpleDateFormat("HH.mm.ss").parse(intervalTimes.get(i).startTime);
                                Date endTime = new SimpleDateFormat("HH.mm.ss").parse(intervalTimes.get(i).endTime);

                                Date currentTime = new SimpleDateFormat("HH.mm.ss").parse(holdingTime);

                                if (startTime.getTime() <= currentTime.getTime())
                                {
                                    Summ += intervalTimes.get(i).price * Integer.valueOf(countCartT);
                                }
                            }
                        }

                        Cursor cursorUpdate = Database
                                .rawQuery("UPDATE Hire SET endDate = ?, summ = ?, dop = ? " +
                                        "WHERE id = ?", new String[]{dateEndStr,String.valueOf(Summ),damageRes,String.valueOf(id)});

                        if (cursorUpdate != null)
                        {
                            cursorUpdate.moveToFirst();
                            cursorUpdate.close();

                            new CustomToast(activity, "Прокат окончен", false);
                        }
                        else
                        {
                            new CustomToast(activity, "Не удалось окончить прок", false);
                        }

                    }

                    DatabaseTariffs.close();
                    dbHelperTariffs.close();
                    cursorGetTariff.close();
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        Database.close();
        dbHelper.close();
    }
}

