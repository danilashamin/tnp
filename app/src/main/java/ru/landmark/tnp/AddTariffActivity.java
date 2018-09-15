package ru.landmark.tnp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ru.landmark.tnp.Adapters.RVIntervalTime;
import ru.landmark.tnp.Functional.CustomToast;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Structurs.IntervalTime;
import ru.landmark.tnp.Structurs.Tariff;

import static android.R.attr.data;

public class AddTariffActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    TextInputLayout nameTariff;
    TextInputLayout countCart;

    RecyclerView recyclerView;

    Tariff tariff;
    ArrayList<IntervalTime> intervalTimes = new ArrayList<IntervalTime>();

    RVIntervalTime rvIntervalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        tariff = getIntent().getExtras().getParcelable("tariff");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tariff);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fillArray();

        nameTariff = (TextInputLayout) findViewById(R.id.textInputNameTariff);
        countCart = (TextInputLayout) findViewById(R.id.textInputCountCart);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        rvIntervalTime = new RVIntervalTime(activity, resources, intervalTimes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rvIntervalTime);

        if (tariff == null)
        {
            getSupportActionBar().setTitle(resources.getString(R.string.TextAddTariff));
        }
        else
        {
            getSupportActionBar().setTitle(resources.getString(R.string.TextEditTariff));

            fillFields();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void fillFields ()
    {
        nameTariff.getEditText().setText(tariff.nameTariff);
        countCart.getEditText().setText(String.valueOf(tariff.countCart));

        Log.d("myLog", intervalTimes.size() + " sizess");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (tariff == null)
        {
            getMenuInflater().inflate(R.menu.toolbar_menu_add_tariff, menu);
        }
        else
        {
            getMenuInflater().inflate(R.menu.toolbar_menu_edit_tariff, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.addClient:
                addClientOnClick();
                return true;
            case R.id.editClient:
                editClientOnClick();
                return true;
            case R.id.addTime:
                rvIntervalTime.addNew();
                return true;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return false;
        }
    }

    private void editClientOnClick()
    {
        if (checkInputLayout())
        {
            DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.TARIFF);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String nameTariff = this.nameTariff.getEditText().getText().toString();
            String pathInteravl = CreateArray();
            String countCart = this.countCart.getEditText().getText().toString();

            Cursor cursor = db
                    .rawQuery("UPDATE Tariff SET nameTariff = ?, pathTariff = ?, countCart = ? " +
                            "WHERE id = ?", new String[]{nameTariff,pathInteravl , countCart, String.valueOf(tariff.id)});

            if (cursor != null)
            {
                cursor.moveToFirst();
                cursor.close();

                db.close();
                dbHelper.close();

                new CustomToast(activity, resources.getString(R.string.TextTrueEditTariff), false);

                setResult(RESULT_OK);
                finish();
            }
            else
            {
                new CustomToast(activity, resources.getString(R.string.TextFailEditClient), false);
            }
        }
    }

    private void addClientOnClick()
    {
        if (checkInputLayout())
        {
            String nameTariff = this.nameTariff.getEditText().getText().toString();
            String pathInterval = CreateArray();
            String countCart = this.countCart.getEditText().getText().toString();

            DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.TARIFF);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put("nameTariff", nameTariff);
            cv.put("pathTariff", pathInterval);
            cv.put("countCart", countCart);

            long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);

            db.close();
            dbHelper.close();

            Log.d("myLog", rowID + " ");

            new CustomToast(activity, resources.getString(R.string.TextTrueAddTariff), false);

            setResult(RESULT_OK);
            finish();
        }
    }

    private void fillArray()
    {
        if (tariff != null)
        {
            Log.d("myLog", tariff.pathInterval + " pathInterval");

            FileInputStream fis = null;
            InputStreamReader isr = null;

            File file = new File(tariff.pathInterval);

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

        }
    }

    private String CreateArray()
    {
        ArrayList<IntervalTime> intervalTimes = rvIntervalTime.getArray();

        FileOutputStream fos = null;

        File file = null;

        String path = "";

        if (tariff != null)
        {
            file = new File(tariff.pathInterval);
        }
        else
        {
            file = new File(String.valueOf(generateId()));
        }

        try
        {
            File storageDir = getExternalFilesDir(Environment.getExternalStorageDirectory().getAbsolutePath());
            File xmlRes = File.createTempFile(
                    file.getName(),  /* prefix */
                    ".xml",         /* suffix */
                    storageDir      /* directory */
            );
            path = xmlRes.getPath();
            fos = new FileOutputStream (xmlRes, true);

            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument(null, Boolean.valueOf(true));
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.startTag(null, "root");

            for (int i=0; i<intervalTimes.size(); i++)
            {
                if (!intervalTimes.get(i).isCreate)
                {
                    serializer.startTag(null, "interval");

                    serializer.startTag(null, "startTime");
                    serializer.text(intervalTimes.get(i).startTime);
                    serializer.endTag(null, "startTime");

                    serializer.startTag(null, "endTime");
                    serializer.text(intervalTimes.get(i).endTime);
                    serializer.endTag(null, "endTime");

                    serializer.startTag(null, "price");
                    serializer.text(String.valueOf(intervalTimes.get(i).price));
                    serializer.endTag(null, "price");

                    serializer.startTag(null, "pn");
                    serializer.text(String.valueOf(intervalTimes.get(i).pn));
                    serializer.endTag(null, "pn");

                    serializer.startTag(null, "vt");
                    serializer.text(String.valueOf(intervalTimes.get(i).vt));
                    serializer.endTag(null, "vt");

                    serializer.startTag(null, "sr");
                    serializer.text(String.valueOf(intervalTimes.get(i).sr));
                    serializer.endTag(null, "sr");

                    serializer.startTag(null, "ct");
                    serializer.text(String.valueOf(intervalTimes.get(i).ct));
                    serializer.endTag(null, "ct");

                    serializer.startTag(null, "pt");
                    serializer.text(String.valueOf(intervalTimes.get(i).pt));
                    serializer.endTag(null, "pt");

                    serializer.startTag(null, "sub");
                    serializer.text(String.valueOf(intervalTimes.get(i).sub));
                    serializer.endTag(null, "sub");

                    serializer.startTag(null, "vosk");
                    serializer.text(String.valueOf(intervalTimes.get(i).vosk));
                    serializer.endTag(null, "vosk");

                    serializer.endTag(null, "interval");
                }
            }

            serializer.endTag(null, "root");

            serializer.endDocument();

            serializer.flush();

            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Log.d("myLog", "pathInterval " + path);

        return path;
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

    private boolean checkInputLayout()
    {
        boolean nameTariffBool = false;
        boolean countCartBool = false;

        if (nameTariff.getEditText().getText().toString().equals(""))
        {
            nameTariff.setErrorEnabled(true);
            nameTariff.setError(resources.getString(R.string.TextInputFullField));

            nameTariffBool = true;
        }
        else
        {
            nameTariff.setErrorEnabled(false);

            nameTariffBool = true;
        }

        if(countCart.getEditText().getText().toString().equals(""))
        {
            countCart.setErrorEnabled(true);
            countCart.setError(resources.getString(R.string.TextInputFullField));

            countCartBool = false;
        }
        else
        {
            countCart.setErrorEnabled(false);

            countCartBool = true;
        }

        if (nameTariffBool && countCartBool)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
