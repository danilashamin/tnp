package ru.landmark.tnp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sam4s.printer.Sam4sPrint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ru.landmark.tnp.Adapters.RVMain;
import ru.landmark.tnp.Adapters.RVUserConsumers;
import ru.landmark.tnp.Adapters.VPMain;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Functional.NavigationDrawer;
import ru.landmark.tnp.Structurs.Report;

import static android.R.attr.mode;

public class MainActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    ViewPager viewPager;
    VPMain vpMain;

    RVMain rvMain;
    RecyclerView recyclerView;

    TextView textViewDay;

    ArrayList<Report> reports = new ArrayList<Report>();

    SearchView searchView = null;
    MenuItem searchMenuItem = null;

    public static Sam4sPrint mPrinter = new Sam4sPrint();
    public static  boolean sp;

    SharedPreferences sharedPref;

    ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.TextMain));

        new NavigationDrawer(context, activity, toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        fragments.add(MainUsersFragment.getInstance());
        fragments.add(MainCartsFragment.getInstance());
        vpMain = new VPMain(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(vpMain);

        try
        {
            String temp = mPrinter.getPrinterStatus();

            if(temp == null)
            {
                Intent intent = new Intent(this, SearchPrintActivity.class);
                startActivityForResult(intent, 100);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();

            Intent intent = new Intent(this, SearchPrintActivity.class);
            startActivityForResult(intent, 100);
        }
    }

    static void showMsg(String msg, Context context){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int whichButton) {
                return ;
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        try
        {
            sp = false;
            mPrinter.closePrinter();
            finish();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
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
                MainActivity.showMsg("Error1 " + e.getMessage().toString() + "Barcode: " + barcode, MainActivity.this);
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
