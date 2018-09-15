package ru.landmark.tnp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.landmark.tnp.Functional.CustomToast;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Functional.NavigationDrawer;
import ru.landmark.tnp.Structurs.UserConsumers;

public class AddConstClientActivity extends AppCompatActivity
{
    static final int REQUEST_TAKE_PHOTO = 1;

    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    ImageView header;
    ImageView camera;

    TextInputLayout fio;
    TextInputLayout phone;
    TextInputLayout passport;
    TextInputLayout home;
    TextInputLayout barocde;

    String mCurrentPhotoPath = "";

    UserConsumers userConsumers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        userConsumers = getIntent().getExtras().getParcelable("user");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_const_client);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // new NavigationDrawer(context, activity, toolbar);

        header = (ImageView) findViewById(R.id.header);
        camera = (ImageView) findViewById(R.id.icon_camera);

        fio = (TextInputLayout) findViewById(R.id.textInputFIO);
        phone = (TextInputLayout) findViewById(R.id.textInputPhone);
        passport = (TextInputLayout) findViewById(R.id.textInputPassport);
        home = (TextInputLayout) findViewById(R.id.textInputHomeAddress);
        barocde = (TextInputLayout) findViewById(R.id.textInputBarcode);

        header.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                headerOnClick();
            }
        });

        camera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cameraOnClick();
            }
        });

        if (userConsumers == null)
        {
            getSupportActionBar().setTitle(resources.getString(R.string.TextAddConstClient));
        }
        else
        {
            getSupportActionBar().setTitle(resources.getString(R.string.TextEditConstClient));

            fillFields();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void fillFields ()
    {
        mCurrentPhotoPath = userConsumers.pathPhoto;

        if (!mCurrentPhotoPath.equals(""))
        {
            File f = new File(mCurrentPhotoPath);
            Picasso.with(activity).load(f).into(header);
        }

        fio.getEditText().setText(userConsumers.name);
        phone.getEditText().setText(userConsumers.numberPhone);
        passport.getEditText().setText(userConsumers.numberPassport);
        home.getEditText().setText(userConsumers.homeAddress);
        barocde.getEditText().setText(userConsumers.barcode);
    }

    private void editClientOnClick()
    {
        if (checkInputLayout())
        {
            DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.USERS_CONSUMERS);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String fioStr = fio.getEditText().getText().toString();
            String phoneStr = phone.getEditText().getText().toString();
            String passportStr = passport.getEditText().getText().toString();
            String homeStr = home.getEditText().getText().toString();
            String barcodeStr = barocde.getEditText().getText().toString();
            String pathPhoto = mCurrentPhotoPath;

            Cursor cursor = db
                    .rawQuery("UPDATE UserConsumers SET fio = ?, numberPassport = ?, " +
                            "homeAddress = ?, numberPhone = ?, barcode = ?, pathPhoto = ? " +
                            "WHERE id = ?", new String[]{fioStr,
                            passportStr, homeStr, phoneStr, barcodeStr, pathPhoto, String.valueOf(userConsumers.id)});

            if (cursor != null)
            {
                cursor.moveToFirst();
                cursor.close();

                db.close();
                dbHelper.close();

                new CustomToast(activity, resources.getString(R.string.TextTrueEditClient), false);

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
            String fioStr = fio.getEditText().getText().toString();
            String phoneStr = phone.getEditText().getText().toString();
            String passportStr = passport.getEditText().getText().toString();
            String homeStr = home.getEditText().getText().toString();
            String barcodeStr = barocde.getEditText().getText().toString();
            String pathPhoto = mCurrentPhotoPath;

            DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.USERS_CONSUMERS);

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put("fio", fioStr);
            cv.put("numberPassport", passportStr);
            cv.put("homeAddress", homeStr);
            cv.put("numberPhone", phoneStr);
            cv.put("barcode", barcodeStr);
            cv.put("pathPhoto", pathPhoto);

            long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);

            db.close();
            dbHelper.close();

            new CustomToast(activity, resources.getString(R.string.TextTrueAddClient), false);

            setResult(RESULT_OK);
            finish();
        }
    }

    private void cameraOnClick()
    {
        new IntentIntegrator(activity).initiateScan();
    }

    private void headerOnClick()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            }
            catch (IOException ex)
            {
                Log.e("ERROR", ex.getMessage().toString());
            }

            if (photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(context,
                        "ru.landmark.tnp",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private boolean checkInputLayout()
    {
        boolean fioBool = false;
        boolean phoneBool = false;
        boolean passportBool = false;
        boolean homeBool = false;
        boolean barcodeBool = false;

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

        if (passport.getEditText().getText().toString().equals(""))
        {
            passport.setErrorEnabled(true);
            passport.setError(resources.getString(R.string.TextInputFullField));

            passportBool = false;
        }
        else
        {
            passport.setErrorEnabled(false);

            passportBool = true;
        }

        if (home.getEditText().getText().toString().equals(""))
        {
            home.setErrorEnabled(true);
            home.setError(resources.getString(R.string.TextInputFullField));

            homeBool = false;
        }
        else
        {
            home.setErrorEnabled(false);

            homeBool = true;
        }

        if (barocde.getEditText().getText().toString().equals(""))
        {
            barocde.setErrorEnabled(true);
            barocde.setError(resources.getString(R.string.TextInputFullField));

            barcodeBool = false;
        }
        else
        {
            barocde.setErrorEnabled(false);

            barcodeBool = true;
        }

        if (fioBool && phoneBool && passportBool && homeBool && barcodeBool)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK)
        {
            File f = new File(mCurrentPhotoPath);

            Picasso.with(activity).load(f).into(header);
        }
        else if (requestCode != REQUEST_TAKE_PHOTO)
        {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null)
            {
                try
                {
                    if(result.getContents() == null)
                    {
                        Toast.makeText(this, resources.getString(R.string.TextFailScanned), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        barocde.getEditText().setText(result.getContents());
                    }
                }
                catch (Exception e)
                {
                    MainActivity.showMsg("Error " + e.getMessage().toString(), AddConstClientActivity.this);
                }
            }
            else
            {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (userConsumers == null)
        {
            getMenuInflater().inflate(R.menu.toolbar_menu_add_const_client, menu);
        }
        else
        {
            getMenuInflater().inflate(R.menu.toolbar_menu_edit_const_client, menu);
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
}
