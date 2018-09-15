package ru.landmark.tnp;

import android.*;
import android.Manifest;
import android.animation.Animator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

import io.codetail.animation.ViewAnimationUtils;
import ru.landmark.tnp.Dialogs.ActiveAppDialog;
import ru.landmark.tnp.Functional.CustomToast;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Structurs.UsersPersonal;

public class LoginActivity extends AppCompatActivity
{
    private static String[] Per_Stor = new String[]{
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.INTERNET};

    Activity activity;
    Resources resources;
    Context context;

    SharedPreferences sharedPref;

    boolean appIsActive = false;

    LinearLayout layoutReg;
    LinearLayout layoutLogin;

    Button loginButton;
    Button regButton;
    Button regButtonTwo;

    TextInputLayout login;
    TextInputLayout password;

    TextInputLayout nameAndSurname;
    TextInputLayout loginReg;
    TextInputLayout passwordReg;
    EditText key;

    SwitchCompat switchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*
        String b = "142555650166";

        int o = Integer.valueOf(String.valueOf(b.charAt(1)));
        int t = Integer.valueOf(String.valueOf(b.charAt(3)));
        int f = Integer.valueOf(String.valueOf(b.charAt(5)));
        int fo = Integer.valueOf(String.valueOf(b.charAt(7)));
        int fi = Integer.valueOf(String.valueOf(b.charAt(9)));
        int six = Integer.valueOf(String.valueOf(b.charAt(11)));

        Log.d("myLog", o + " + " + t +" + " + f +" + " + fo +" + " + " + " + fi + " + " + six +" = " + (o+t+f+fo+fi+six));
        */

        verify(this);

        activity = this;
        resources = getResources();
        context = getApplicationContext();

        sharedPref = getSharedPreferences(resources.getString(R.string.appSettingsName), Context.MODE_PRIVATE);

        appIsActive = sharedPref.getBoolean(resources.getString(R.string.appIsActive), false);

        if (!appIsActive)
        {
            new ActiveAppDialog(getApplicationContext(), activity);
        }

        loginButton = (Button) findViewById(R.id.buttonLogin);
        regButton = (Button) findViewById(R.id.buttonRegister);
        regButtonTwo = (Button) findViewById(R.id.buttonRegisterTwo);

        layoutLogin = (LinearLayout) findViewById(R.id.layoutLogin);
        layoutReg = (LinearLayout) findViewById(R.id.layoutRegister);

        login = (TextInputLayout) findViewById(R.id.textInputLogin);
        password = (TextInputLayout) findViewById(R.id.textInputPassword);

        nameAndSurname = (TextInputLayout) findViewById(R.id.textInputNameAndSurname);
        loginReg = (TextInputLayout) findViewById(R.id.textInputLoginReg);
        passwordReg = (TextInputLayout) findViewById(R.id.textInputPasswordReg);

        key = (EditText) findViewById(R.id.editTextKeyDeveloper);

        switchCompat = (SwitchCompat) findViewById(R.id.switchCompat);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickLogin();
            }
        });

        regButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ClickReg();
            }
        });

        regButtonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickRegButtonTwo();
            }
        });

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if (b)
                {
                    key.setVisibility(View.VISIBLE);
                }
                else
                {
                    key.setVisibility(View.GONE);
                }
            }
        });
    }

    public void closeAnim()
    {
        int cx = (regButton.getLeft() + regButton.getRight()) / 2;
        int cy = (regButton.getTop() + regButton.getBottom()) / 2;

        // get the final radius for the clipping circle
        int dx = Math.max(cx, regButton.getWidth() - cx);
        int dy = Math.max(cy, regButton.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        // Android native animator
        Animator animator = ViewAnimationUtils.createCircularReveal(layoutReg, cx, cy, finalRadius, 0);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(200);

        layoutLogin.setVisibility(View.VISIBLE);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator)
            {
                layoutReg.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        regButton.setVisibility(View.VISIBLE);
        regButtonTwo.setVisibility(View.GONE);

        animator.start();
    }

    public void ClickRegButtonTwo()
    {
        DBHelper dbHelper = new DBHelper(context, DBHelper.USERS_PERSONALS);

        if (nameAndSurname.getEditText().getText().toString().equals(""))
        {
            nameAndSurname.setErrorEnabled(true);
            nameAndSurname.setError(resources.getString(R.string.TextInputNameAndSurname));
        }
        else
        {
            nameAndSurname.setErrorEnabled(false);
        }
        if (loginReg.getEditText().getText().toString().equals(""))
        {
            loginReg.setErrorEnabled(true);
            loginReg.setError(resources.getString(R.string.TextInputLogin));
        }
        else
        {
            loginReg.setErrorEnabled(false);
        }

        if (passwordReg.getEditText().getText().toString().equals(""))
        {
            passwordReg.setErrorEnabled(true);
            passwordReg.setError(resources.getString(R.string.TextInputPassword));
        }
        else
        {
            passwordReg.setErrorEnabled(false);
        }
        if (switchCompat.isChecked())
        {
            if (key.getText().toString().equals(""))
            {
                key.setError(resources.getString(R.string.TextNeedEditKey));
            }
            else
            {
                key.setError(null);
            }
        }
        else
        {
            key.setError(null);
        }

        if (needFiledsReg(nameAndSurname.getEditText(), loginReg.getEditText(), passwordReg.getEditText(), key))
        {
            final ContentValues cv = new ContentValues();

            if (switchCompat.isChecked())
            {
                String savedText = sharedPref.getString(resources.getString(R.string.appKey), "");
                Log.d("myLog", "Key " + savedText);

                if (key.getText().toString().equals(savedText))
                {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    cv.put("nameAndSurname", nameAndSurname.getEditText().getText().toString());
                    cv.put("login", loginReg.getEditText().getText().toString());
                    cv.put("password", passwordReg.getEditText().getText().toString());
                    cv.put("isAdmin", Boolean.toString(switchCompat.isChecked()));

                    long rowID = db.insert(dbHelper.DATABASE_NAME, null, cv);
                    Log.d("myLog", "row inserted, ID = " + rowID);

                    db.close();

                    new CustomToast(activity,resources.getString(R.string.TextRegisterTrue), false);

                    closeAnim();

                    nameAndSurname.getEditText().setText("");
                    loginReg.getEditText().setText("");
                    passwordReg.getEditText().setText("");
                    key.setText("");
                }
                else
                {
                    key.setError(resources.getString(R.string.TextKeyFail));
                }
            }
            else
            {
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                cv.put("nameAndSurname", nameAndSurname.getEditText().getText().toString());
                cv.put("login", loginReg.getEditText().getText().toString());
                cv.put("password", passwordReg.getEditText().getText().toString());
                cv.put("isAdmin", Boolean.toString(switchCompat.isChecked()));

                long rowID = db.insert(DBHelper.DATABASE_NAME, null, cv);
                Log.d("myLog", "row inserted, ID = " + rowID);

                db.close();

                new CustomToast(activity,resources.getString(R.string.TextRegisterTrue), false);

                closeAnim();

                nameAndSurname.getEditText().setText("");
                loginReg.getEditText().setText("");
                passwordReg.getEditText().setText("");
            }
        }

        dbHelper.close();
    }

    public void ClickReg()
    {
        int cx = (regButton.getLeft() + regButton.getRight()) / 2;
        int cy = (regButton.getTop() + regButton.getBottom()) / 2;

        // get the final radius for the clipping circle
        int dx = Math.max(cx, regButton.getWidth() - cx);
        int dy = Math.max(cy, regButton.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        // Android native animator
        Animator animator = ViewAnimationUtils.createCircularReveal(layoutReg, cx, cy, 0, finalRadius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(200);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator)
            {
                layoutLogin.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        layoutReg.setVisibility(View.VISIBLE);

        animator.start();

        regButton.setVisibility(View.GONE);
        regButtonTwo.setVisibility(View.VISIBLE);
    }

    public void ClickLogin()
    {
        DBHelper dbHelper;
        SQLiteDatabase Database;

        dbHelper = new DBHelper(context, DBHelper.USERS_PERSONALS);
        Database = dbHelper.getWritableDatabase();

        if (login.getEditText().getText().toString().equals(""))
        {
            login.setError(resources.getString(R.string.TextInputLogin));
        }
        else
        {
            login.setError(null);
        }

        if (password.getEditText().getText().toString().equals(""))
        {
            password.setError(resources.getString(R.string.TextInputPassword));
        }
        else
        {
            password.setError(null);
        }

        if (needFiledsLogin(login.getEditText(), password.getEditText()))
        {
            Cursor cursor = Database.rawQuery(
                    "SELECT * FROM `"+dbHelper.DATABASE_NAME+"` WHERE login = ? AND password = ?",
                    new String[]{login.getEditText().getText().toString(), password.getEditText().getText().toString()});

            if (cursor != null && cursor.moveToFirst())
            {
                Log.d("myLog", cursor.getString(cursor.getColumnIndex("login")));

                UsersPersonal user = new UsersPersonal(cursor.getString(cursor.getColumnIndex("nameAndSurname")),
                        cursor.getString(cursor.getColumnIndex("login")),
                        cursor.getString(cursor.getColumnIndex("isAdmin")));

                SharedPreferences.Editor ed = sharedPref.edit();
                ed.putString(resources.getString(R.string.appLoginUser), user.Login);
                ed.putString(resources.getString(R.string.appDisplayName), user.NameAndSurname);
                ed.putBoolean(resources.getString(R.string.appIsAdmin), user.IsAdmin);
                ed.putString(resources.getString(R.string.appPass), password.getEditText().getText().toString());
                ed.putString(resources.getString(R.string.appLogin), login.getEditText().getText().toString());
                ed.commit();

                Log.d("myLog", user.Login + " " + user.IsAdmin + " " + user.NameAndSurname);

                cursor.close();
                Database.close();

                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);

                activity.finish();

                Date now = new Date();
                String timeStart = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(now);

                String key = sharedPref.getString(resources.getString(R.string.appKey), "");

                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

                DatabaseReference DatabaseRef = mDatabase.getReference();
                DatabaseReference AppsRef = DatabaseRef.child("Apps");

                DatabaseReference AppRef = AppsRef.child(key);
                DatabaseReference HistoryLoginRef = AppRef.child("History Login");

                DatabaseReference LoginName = HistoryLoginRef.child(user.Login);

                LoginName.push().setValue(timeStart);
            }
            else
            {
                new CustomToast(activity, "Проверьте логин и пароль", true);
            }
        }

        Database.close();
        dbHelper.close();
    }

    boolean needFiledsLogin(EditText LoginEdit, EditText PasswordEdit)
    {
        if (!LoginEdit.getText().toString().equals(""))
        {
            if (!PasswordEdit.getText().toString().equals(""))
            {
                return  true;
            }
        }

        return false;
    }

    boolean needFiledsReg(EditText NameAndSurnameEdit, EditText LoginEdit, EditText PasswordEdit, EditText KeyAppEdit)
    {
        if (!NameAndSurnameEdit.getText().toString().equals(""))
        {
            if (!LoginEdit.getText().toString().equals(""))
            {
                if (!PasswordEdit.getText().toString().equals("")) {
                    return !KeyAppEdit.isActivated() || !KeyAppEdit.getText().toString().equals("");
                }
            }
        }

        return false;
    }

    public static void verify(Activity activity)
    {
        if (ContextCompat.checkSelfPermission(activity, Per_Stor[0]) != PackageManager.PERMISSION_GRANTED)
        {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Per_Stor[0])) {
                ActivityCompat.requestPermissions(activity, Per_Stor, 100);
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if (layoutReg.getVisibility() == View.VISIBLE)
        {
            closeAnim();
        }
    }
}
