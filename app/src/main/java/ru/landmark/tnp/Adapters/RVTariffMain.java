package ru.landmark.tnp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import ru.landmark.tnp.AddTariffActivity;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.R;
import ru.landmark.tnp.Structurs.Tariff;
import ru.landmark.tnp.Structurs.TariffMain;

public class RVTariffMain extends RecyclerView.Adapter<RVTariffMain.ViewHolder>
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<TariffMain> tariffs;

    public RVTariffMain(Activity activity, Resources resources, ArrayList<TariffMain> tariffs)
    {
        this.activity = activity;
        this.resources = resources;
        this.context = activity.getApplicationContext();

        this.tariffs = tariffs;
    }

    @Override
    public RVTariffMain.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tariff_active, parent, false);

        RVTariffMain.ViewHolder vh = new RVTariffMain.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RVTariffMain.ViewHolder holder, final int position)
    {
        holder.nameTariff.setText(tariffs.get(position).name);
        holder.freeCarts.setText("Свободно: " + tariffs.get(position).freeCarts);
        holder.busyCarts.setText("Занято: " + tariffs.get(position).busyCarts);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView nameTariff;
        TextView freeCarts;
        TextView busyCarts;

        public ViewHolder(View v)
        {
            super(v);

            nameTariff = (TextView) v.findViewById(R.id.textNameTariff);
            freeCarts = (TextView) v.findViewById(R.id.textCartCountFree);
            busyCarts = (TextView) v.findViewById(R.id.textCartCount);
        }
    }

    @Override
    public int getItemCount()
    {
        return tariffs.size();
    }
}
