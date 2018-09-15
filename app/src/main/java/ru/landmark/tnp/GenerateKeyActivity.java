package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

import ru.landmark.tnp.Functional.NavigationDrawer;

public class GenerateKeyActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    Button generateButton;
    TextInputLayout textDiscripth;

    TextView textGenerate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_key);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.TextMain));

        new NavigationDrawer(context, activity, toolbar);

        textDiscripth = (TextInputLayout) findViewById(R.id.textDisripth);
        generateButton = (Button) findViewById(R.id.buttonGenerate);
        textGenerate = (TextView) findViewById(R.id.textGenerate);

        generateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickGenerateButton();
            }
        });
    }

    private void onClickGenerateButton()
    {
        textDiscripth.setErrorEnabled(false);

        if (!textDiscripth.getEditText().getText().toString().equals(""))
        {
            String name = textDiscripth.getEditText().getText().toString();
            String key = getSaltString();

            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

            DatabaseReference DatabaseRef = mDatabase.getReference();
            DatabaseReference KeysRef = DatabaseRef.child("Keys");

            DatabaseReference KeyRef = KeysRef.child(key);

            DatabaseReference nameKey = KeyRef.child("name");
            nameKey.setValue(name);

            DatabaseReference valueKey = KeyRef.child("Value");
            valueKey.setValue(false);

            textGenerate.setText("Ключ: "+key);
        }
        else
        {
            textDiscripth.setErrorEnabled(true);
            textDiscripth.setError("Введите название");
        }
    }

    protected String getSaltString()
    {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
}
