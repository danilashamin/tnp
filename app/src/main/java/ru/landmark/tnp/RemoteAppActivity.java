package ru.landmark.tnp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import ru.landmark.tnp.Functional.CustomToast;

public class RemoteAppActivity extends AppCompatActivity
{
    Activity activity;
    Context context;
    Resources resources;

    Toolbar toolbar;

    TextView summInCase;
    TextView summInDay;
    TextView clientsInHire;
    TextView clientsInDay;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_app);

        String id = getIntent().getStringExtra("id");

        activity = this;
        context = getApplicationContext();
        resources = getResources();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Информация о приложение");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        summInCase = (TextView) findViewById(R.id.summInCase);
        summInDay = (TextView) findViewById(R.id.summInDay);
        clientsInHire = (TextView) findViewById(R.id.clientInhire);
        clientsInDay = (TextView) findViewById(R.id.clientInDay);


        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

        DatabaseReference DatabaseRef = mDatabase.getReference();
        DatabaseReference AppsRef = DatabaseRef.child("Apps");

        DatabaseReference AppRef = AppsRef.child(id);

        DatabaseReference Report = AppRef.child("Report");

        Date date = new Date();

        String dateStrF = new SimpleDateFormat("dd_MM_yyyy").format(date);
        DatabaseReference DateStartRef = Report.child(dateStrF);

        final DatabaseReference CaseInMoney = DateStartRef.child("CaseInMoney");
        CaseInMoney.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                {
                    String CaseInMoney = dataSnapshot.getValue().toString();

                    summInCase.setText("Сумма в кассе: " + CaseInMoney);
                }
                else
                {
                    CaseInMoney.setValue(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatabaseReference UsersMoneyInDay = DateStartRef.child("UsersMoneyInDay");
        UsersMoneyInDay.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                {
                    String UsersMoney = dataSnapshot.getValue().toString();

                    summInDay.setText("Сумма за день: " + UsersMoney);
                }
                else
                {
                    UsersMoneyInDay.setValue(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatabaseReference usersInHire = DateStartRef.child("UsersInHire");
        usersInHire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                {
                    String UsersInhire = dataSnapshot.getValue().toString();

                    clientsInHire.setText("Клиентов в прокате: " + UsersInhire);
                }
                else
                {
                    usersInHire.setValue(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final DatabaseReference UserHire = DateStartRef.child("UserHire");
        UserHire.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                {
                    String UserHire = dataSnapshot.getValue().toString();

                    clientsInDay.setText("Клиентов за день: " + UserHire);
                }
                else
                {
                    UserHire.setValue(0);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                new CustomToast(activity, "Ошибка подключения", true);

                summInCase.setText("Проверьте сетевое подключение");
                summInDay.setText("");
                clientsInDay.setText("");
                clientsInHire.setText("");
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
