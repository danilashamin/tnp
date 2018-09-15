package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import ru.landmark.tnp.Functional.CustomToast;
import ru.landmark.tnp.Functional.NavigationDrawer;

public class TextRulesActivity extends AppCompatActivity
{

    TextInputLayout textInputLayout;
    Button primaryRules;

    SharedPreferences sharedPref;

    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_rules);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.TextControlRules));

        new NavigationDrawer(context, activity, toolbar);

        textInputLayout = (TextInputLayout) findViewById(R.id.textInputRules);
        primaryRules = (Button) findViewById(R.id.buttonRules);

        sharedPref = getSharedPreferences(resources.getString(R.string.appSettingsName), Context.MODE_PRIVATE);

        String rules = sharedPref.getString(resources.getString(R.string.appTextRules), "");

        textInputLayout.getEditText().setText(rules);

        primaryRules.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                textInputLayout.setErrorEnabled(false);

                if (!textInputLayout.getEditText().getText().toString().equals(""))
                {
                    SharedPreferences.Editor ed = sharedPref.edit();
                    ed.putString(resources.getString(R.string.appTextRules), textInputLayout.getEditText().getText().toString());
                    ed.commit();

                    new CustomToast(activity, "Правила изменены", false);
                }
                else
                {
                    textInputLayout.setErrorEnabled(true);
                    textInputLayout.setError("Заполните поле");
                }
            }
        });
    }
}
