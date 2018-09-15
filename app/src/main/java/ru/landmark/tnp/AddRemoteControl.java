package ru.landmark.tnp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ru.landmark.tnp.Functional.CustomToast;
import ru.landmark.tnp.Functional.DBHelper;

public class AddRemoteControl extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    TextInputLayout textInputLayout;
    Button addRemoteControl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remote_control);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.TextAddAppRemoteControl));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textInputLayout = (TextInputLayout) findViewById(R.id.textInputId);
        addRemoteControl = (Button) findViewById(R.id.buttonAddControl);

        addRemoteControl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                textInputLayout.setErrorEnabled(false);

                if (!textInputLayout.getEditText().getText().equals(""))
                {
                    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference DatabaseRef = mDatabase.getReference();
                    DatabaseReference KeysRef = DatabaseRef.child("Keys");

                    final DatabaseReference KeyRef = KeysRef.child(textInputLayout.getEditText().getText().toString());

                    final DatabaseReference nameKey = KeyRef.child("name");

                    nameKey.addValueEventListener(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.getValue() != null)
                            {
                                String name = dataSnapshot.getValue().toString();

                                DBHelper dbHelper = new DBHelper(getApplicationContext(), DBHelper.REMOTE_APP);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                ContentValues cv = new ContentValues();

                                cv.put("nameApp", name);
                                cv.put("idApp", textInputLayout.getEditText().getText().toString());

                                long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);

                                db.close();
                                dbHelper.close();

                                new CustomToast(activity, "Приложение добавлено", false);

                                setResult(RESULT_OK);
                                finish();
                            }
                            else
                            {
                                new CustomToast(activity, "Неудалось найти приложение по данному ключу", true);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                            new CustomToast(activity, "Ошибка подключения", true);
                        }
                    });
                }
                else
                {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError("Заполните поле");
                }
            }
        });
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
}
