package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sam4s.printer.Sam4sFinder;
import com.sam4s.printer.Sam4sPrint;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import ru.landmark.tnp.Adapters.RVPrinter;
import ru.landmark.tnp.Functional.NavigationDrawer;
import ru.landmark.tnp.Interfaces.SearchPrintInterface;

public class SearchPrintActivity extends AppCompatActivity implements Runnable, SearchPrintInterface
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    RadioButton radioButtonEt;
    RadioButton radioButtonBt;
    RecyclerView recyclerView;

    RVPrinter rvPrinter;

    Sam4sFinder ef = new Sam4sFinder();
    ScheduledFuture<?> future;
    ScheduledExecutorService scheduler;
    final static int DISCOVERY_INTERVAL = 500;

    Handler handler = new Handler();

    SearchPrintInterface searchPrintInterface;

    static String SelectedEllix = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_print);

        activity = this;
        context = getApplicationContext();
        resources = getResources();
        searchPrintInterface = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.TextSearchPrinter));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radioButtonEt = (RadioButton) findViewById(R.id.radioET);
        radioButtonBt = (RadioButton) findViewById(R.id.radioBT);

        radioButtonBt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    recyclerView.setAdapter(null);
                    Log.d("myLog", "BT");
                    findStart(1);
                }
            }
        });

        radioButtonEt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    recyclerView.setAdapter(null);
                    Log.d("myLog", "ET");
                    findStart(0);
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        scheduler = Executors.newSingleThreadScheduledExecutor();

        findStart(0);
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        //stop find
        if(future != null){
            future.cancel(false);
            while(!future.isDone()){
                try{
                    Thread.sleep(DISCOVERY_INTERVAL);
                }catch(Exception e){
                    break;
                }
            }
            future = null;
        }
        if(scheduler != null){
            scheduler.shutdown();
            scheduler = null;
        }
        //stop old finder
        while(true) {
            try{
                ef.stopSearch();
                break;
            }catch(Exception e){
                e.getStackTrace();
            }
        }
    }

    @Override
    public void run() {
        class UpdateListThread extends Thread{
            String[] list;
            public UpdateListThread(String[] listDevices) {
                list = listDevices;
            }

            @Override
            public void run() {
                try
                {
                    if(list != null){
                        ArrayList<String> mArrayList = new ArrayList<String>();

                        for(String mDeviceFromList : list)
                        {
                            if(mDeviceFromList != null)
                                mArrayList.add(mDeviceFromList);
                        }

                        rvPrinter = new RVPrinter(activity, resources, searchPrintInterface,mArrayList);

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(linearLayoutManager);

                        recyclerView.setAdapter(rvPrinter);
                        rvPrinter.notifyDataSetChanged();
                    }

                    Thread.sleep(100);
                }
                catch(Exception ex)
                {
                    MainActivity.showMsg(ex.getLocalizedMessage(), getBaseContext());
                }
            }
        }

        String[] deviceList = null;
        try{
            deviceList = ef.getResult();
            handler.post(new UpdateListThread(deviceList));
        }catch(Exception e){
            return;
        }
    }

    private void findStart(int pos)
    {
        try
        {
            if(scheduler == null){
                return;
            }

            //stop old finder
            while(true) {
                try{
                    ef.stopSearch();
                    break;
                }catch(Exception e){
                    e.getStackTrace();
                }
            }

            //stop find thread
            if(future != null){
                future.cancel(false);
                while(!future.isDone()){
                    try{
                        Thread.sleep(DISCOVERY_INTERVAL);
                    }catch(Exception e){
                        break;
                    }
                }
                future = null;
            }

            //get device type and find

            try
            {
                if (pos == 0)
                {
                    Log.d("myLog", "ET");
                    ef.startSearch(getBaseContext(), Sam4sFinder.DEVTYPE_ETHERNET);
                }
                else if (pos == 1)
                {
                    Log.d("myLog", "BT");
                    ef.startSearch(getBaseContext(), Sam4sFinder.DEVTYPE_BLUETOOTH);
                }
            }
            catch (Exception e)
            {
                MainActivity.showMsg(e.getLocalizedMessage(), SearchPrintActivity.this);
                return;
            }

            //start thread
            future = scheduler.scheduleWithFixedDelay(this, 0, DISCOVERY_INTERVAL, TimeUnit.MILLISECONDS);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            MainActivity.showMsg(ex.getLocalizedMessage(), SearchPrintActivity.this);
        }
    }

    @Override
    public void onClickPrint(int pos)
    {
        SelectedEllix = rvPrinter.getItemAtPosition(pos).toString();
        MainActivity.mPrinter.closePrinter();
        System.out.println(SelectedEllix);

        try
        {
            if (radioButtonEt.isChecked())
            {
                MainActivity.mPrinter.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, SelectedEllix);
            }
            else if (radioButtonBt.isChecked())
            {
                MainActivity.mPrinter.openPrinter(Sam4sPrint.DEVTYPE_BLUETOOTH, SelectedEllix);
            }

            ef.stopSearch();
            setResult(RESULT_OK);
            finish();
        }
        catch(Exception ex)
        {
            return;
        }
    }
}
