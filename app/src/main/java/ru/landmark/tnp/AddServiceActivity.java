package ru.landmark.tnp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import ru.landmark.tnp.Functional.CustomToast;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Structurs.DopService;
import ru.landmark.tnp.Structurs.Tariff;

public class AddServiceActivity extends AppCompatActivity
{

    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    TextInputLayout nameTariff;
    TextInputLayout pricePerHour;

    DopService dopService;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        dopService = getIntent().getExtras().getParcelable("dopService");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nameTariff = (TextInputLayout) findViewById(R.id.textInputNameTariff);
        pricePerHour = (TextInputLayout) findViewById(R.id.textInputPricePerHour);

        if (dopService == null)
        {
            getSupportActionBar().setTitle("Добавление доп. услуги");
        }
        else
        {
            getSupportActionBar().setTitle("Редактирование доп. услуг");

            fillFields();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void fillFields ()
    {
        nameTariff.getEditText().setText(dopService.name);
        pricePerHour.getEditText().setText(String.valueOf(dopService.price));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (dopService == null)
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
            DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.DOP_SERVICE);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String nameTariff = this.nameTariff.getEditText().getText().toString();
            String pricePerHour = this.pricePerHour.getEditText().getText().toString();

            Log.d("myLog", nameTariff + " " + pricePerHour + " " + dopService.id);

            Cursor cursor = db
                    .rawQuery("UPDATE DopService SET name = ?, price = ? " +
                            "WHERE id = ?", new String[]{nameTariff,pricePerHour,String.valueOf(dopService.id)});

            if (cursor != null)
            {
                cursor.moveToFirst();
                cursor.close();

                db.close();
                dbHelper.close();

                new CustomToast(activity, "Услуга редактирована", false);

                setResult(RESULT_OK);
                finish();
            }
            else
            {
                new CustomToast(activity, "Не удалось редактировать услугу", false);
            }
        }
    }

    private void addClientOnClick()
    {
        if (checkInputLayout())
        {
            String nameTariff = this.nameTariff.getEditText().getText().toString();
            String pricePerHour = this.pricePerHour.getEditText().getText().toString();

            DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.DOP_SERVICE);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put("name", nameTariff);
            cv.put("price", pricePerHour);

            long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);

            db.close();
            dbHelper.close();

            Log.d("myLog", rowID + " ");

            new CustomToast(activity, "Услуга добавлена", false);

            setResult(RESULT_OK);
            finish();
        }
    }

    private boolean checkInputLayout()
    {
        boolean nameTariffBool = false;
        boolean pricePerHourBool = false;

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

        if (pricePerHour.getEditText().getText().toString().equals(""))
        {
            pricePerHour.setErrorEnabled(true);
            pricePerHour.setError(resources.getString(R.string.TextInputFullField));

            pricePerHourBool = false;
        }
        else
        {
            pricePerHour.setErrorEnabled(false);

            pricePerHourBool = true;
        }

        if (nameTariffBool && pricePerHourBool)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
