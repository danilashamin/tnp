package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Random;

import ru.landmark.tnp.Structurs.UserClient;

public class ClientSetParametrsActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    TextInputLayout fio;
    TextInputLayout phone;
    TextInputLayout deposit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_set_parametrs);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.TextAddClient));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fio = (TextInputLayout) findViewById(R.id.textInputFIO);
        phone = (TextInputLayout) findViewById(R.id.textInputPhone);
        deposit = (TextInputLayout) findViewById(R.id.textInputDeposit);
    }

    public void nextOnClick()
    {
        if (checkInputLayout())
        {
            String fioStr = fio.getEditText().getText().toString();
            String phoneStr = phone.getEditText().getText().toString();
            String depositStr = deposit.getEditText().getText().toString();
            String barcode = generateBarcode();

            UserClient userClient = new UserClient(fioStr, phoneStr, depositStr, barcode);

            Intent intent = new Intent(this, ChangeTariffActivity.class).putExtra("mode", 1).putExtra("user", userClient);
            startActivity(intent);
        }
    }

    private boolean checkInputLayout()
    {
        boolean fioBool = false;
        boolean phoneBool = false;
        boolean depositBool = false;

        if (fio.getEditText().getText().toString().equals(""))
        {
            fio.setErrorEnabled(true);
            fio.setError(resources.getString(R.string.TextInputFullField));

            fioBool = false;
        }
        else
        {
            fio.setErrorEnabled(false);

            fioBool = true;
        }

        if (phone.getEditText().getText().toString().equals(""))
        {
            phone.setErrorEnabled(true);
            phone.setError(resources.getString(R.string.TextInputFullField));

            phoneBool = false;
        }
        else
        {
            phone.setErrorEnabled(false);

            phoneBool = true;
        }

        if (deposit.getEditText().getText().toString().equals(""))
        {
            deposit.setErrorEnabled(true);
            deposit.setError(resources.getString(R.string.TextInputFullField));

            depositBool = false;
        }
        else
        {
            deposit.setErrorEnabled(false);

            depositBool = true;
        }

        if (depositBool && fioBool && phoneBool)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar_menu_client_set_parametrs, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.next:
                nextOnClick();
                return true;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return false;
        }
    }

    public String generateBarcode()
    {
        String barcode = "1";

        for (int l=0; l<11; l++)
        {
            barcode += String.valueOf(new Random().nextInt(9));
        }

        int chet = Integer.valueOf(String.valueOf(barcode.charAt(1)))
                + Integer.valueOf(Integer.valueOf(String.valueOf(barcode.charAt(3))))
                + Integer.valueOf(String.valueOf(barcode.charAt(5)))
                + Integer.valueOf(String.valueOf(barcode.charAt(7)))
                + Integer.valueOf(String.valueOf(barcode.charAt(9)))
                + Integer.valueOf(String.valueOf(barcode.charAt(11)));

        int chetRes = chet*3;
        int neChet = Integer.valueOf(String.valueOf(barcode.charAt(0)))
                + Integer.valueOf(Integer.valueOf(String.valueOf(barcode.charAt(2))))
                + Integer.valueOf(String.valueOf(barcode.charAt(4)))
                + Integer.valueOf(String.valueOf(barcode.charAt(6)))
                + Integer.valueOf(String.valueOf(barcode.charAt(8)))
                + Integer.valueOf(String.valueOf(barcode.charAt(10)));

        int barcodeRes = chetRes + neChet;

        boolean stop = false;

        int tmp, rrr = barcodeRes;
        if ((tmp = rrr % 10) != 0)
            rrr += rrr > -1 ? (10 - tmp) : -tmp;

      //  Log.d("myLog", tmp + " " + rrr);

        int controll = rrr - barcodeRes;
        barcode += String.valueOf(controll);
    //    Log.d("myLog", " Barcode "+ barcode + " c " + controll + " rr " + rrr + " barcodeRes " + barcodeRes + " chet " + chet + " chetRes " + chetRes + " nechet "+ neChet);

        return barcode;
    }
}
