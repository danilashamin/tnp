package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import ru.landmark.tnp.Functional.NavigationDrawer;

public class StartHireActivity extends AppCompatActivity
{
    Activity activity;
    Resources resources;
    Context context;

    Toolbar toolbar;

    Button constClient;
    Button client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_hire);

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(resources.getString(R.string.TextStartHire));

        new NavigationDrawer(context, activity, toolbar);

        constClient = (Button) findViewById(R.id.buttonConstClient);
        client = (Button) findViewById(R.id.buttonClient);

        constClient.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                constClientOnClick();
            }
        });

        client.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clientOnClick();
            }
        });
    }

    private void constClientOnClick()
    {
        Intent intent = new Intent(this, ConstClientsActivity.class).putExtra("mode", 1);
        startActivity(intent);
    }

    private void clientOnClick()
    {
        Intent intent = new Intent(this, ClientSetParametrsActivity.class);
        startActivity(intent);
    }
}
