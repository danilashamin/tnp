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

import ru.landmark.tnp.AddConstClientActivity;
import ru.landmark.tnp.AddTariffActivity;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Interfaces.ConstClientsInterface;
import ru.landmark.tnp.R;
import ru.landmark.tnp.Structurs.Tariff;

public class RVTariff extends RecyclerView.Adapter<RVTariff.ViewHolder>
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<Tariff> tariffs;
    ArrayList<Tariff> mCleanCopyDataset;

    static final private int EDIT_TARIFF = 2;

    public RVTariff (Activity activity, Resources resources, ArrayList<Tariff> tariffs)
    {
        this.activity = activity;
        this.resources = resources;
        this.context = activity.getApplicationContext();

        this.tariffs = tariffs;
        mCleanCopyDataset = this.tariffs;
    }

    @Override
    public RVTariff.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tariff, parent, false);

        RVTariff.ViewHolder vh = new RVTariff.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RVTariff.ViewHolder holder, final int position)
    {
        holder.edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                editOnClick(position);
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                removeOnClick(position);
            }
        });

        holder.nameTariff.setText(tariffs.get(position).nameTariff);

        if (tariffs.get(position).countCart >= 9)
        {
            holder.countCart.setText(String.valueOf(tariffs.get(position).countCart) + " Тележек");
        }
        else
        {
            holder.countCart.setText(String.valueOf(tariffs.get(position).countCart));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView edit;
        ImageView remove;

        TextView nameTariff;
        TextView countCart;

        public ViewHolder(View v)
        {
            super(v);

            edit = (ImageView) v.findViewById(R.id.buttonEdit);
            remove = (ImageView) v.findViewById(R.id.buttonRemove);

            nameTariff = (TextView) v.findViewById(R.id.textNameTariff);
            countCart = (TextView) v.findViewById(R.id.textCartCount);
        }
    }

    private void editOnClick(int position)
    {
        Intent intent = new Intent(activity, AddTariffActivity.class)
                .putExtra("tariff", tariffs.get(position));

        activity.startActivityForResult(intent, EDIT_TARIFF);
    }

    private void removeOnClick(int position)
    {
        DBHelper dbHelper = new DBHelper(context, DBHelper.TARIFF);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int delCount = db.delete(DBHelper.TARIFF, "id = ?",
                new String[]{String.valueOf(tariffs.get(position).id)});

        db.close();
        dbHelper.close();

        tariffs.remove(position);
        notifyItemRemoved(position);
    }

    public void filter(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        tariffs = new ArrayList<Tariff>();
        if (charText.length() == 0)
        {
            tariffs.addAll(mCleanCopyDataset);
        }
        else
        {
            for (Tariff item : mCleanCopyDataset)
            {
                String nameAndSurname = item.nameTariff;
                if (nameAndSurname.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    tariffs.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return tariffs.size();
    }
}
