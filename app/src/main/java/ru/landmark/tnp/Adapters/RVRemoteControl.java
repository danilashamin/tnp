package ru.landmark.tnp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Interfaces.ConstClientsInterface;
import ru.landmark.tnp.R;
import ru.landmark.tnp.RemoteAppActivity;
import ru.landmark.tnp.Structurs.RemoteApp;
import ru.landmark.tnp.Structurs.UserConsumers;

public class RVRemoteControl extends RecyclerView.Adapter<RVRemoteControl.ViewHolder>
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<RemoteApp> remoteApps;

    public RVRemoteControl(Activity activity, Resources resources, ArrayList<RemoteApp> remoteApps)
    {
        this.activity = activity;
        this.resources = resources;
        this.context = activity.getApplicationContext();
        this.remoteApps = remoteApps;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        TextView id;
        ImageView buttonRemove;

        View view;

        public ViewHolder(View v)
        {
            super(v);

            name = (TextView) v.findViewById(R.id.textViewName);
            id = (TextView) v.findViewById(R.id.textViewId);
            buttonRemove = (ImageView) v.findViewById(R.id.buttonRemove);

            view = v;
        }
    }

    @Override
    public RVRemoteControl.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_remote_app, parent, false);

        RVRemoteControl.ViewHolder vh = new RVRemoteControl.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RVRemoteControl.ViewHolder holder, final int position)
    {
        holder.name.setText("Имя приложения: " + remoteApps.get(position).Name);
        holder.id.setText("ID приложения: " + remoteApps.get(position).Id);

        holder.buttonRemove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                removeTariff(position);
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                viewOnClick(position);
            }
        });
    }

    private void viewOnClick(int pos)
    {
        activity.startActivity(new Intent(activity, RemoteAppActivity.class).putExtra("id", remoteApps.get(pos).Id));
    }

    @Override
    public int getItemCount()
    {
        return remoteApps.size();
    }

    private void removeTariff(int pos)
    {
        DBHelper dbHelper = new DBHelper(context, DBHelper.REMOTE_APP);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int delCount = db.delete("RemoteApp", "id = " + remoteApps.get(pos).idBD, null);

        db.close();
        dbHelper.close();
        remoteApps.remove(pos);
        notifyDataSetChanged();
    }
}
