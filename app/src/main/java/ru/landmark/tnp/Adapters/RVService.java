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

import ru.landmark.tnp.AddServiceActivity;
import ru.landmark.tnp.AddTariffActivity;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.R;
import ru.landmark.tnp.Structurs.DopService;
import ru.landmark.tnp.Structurs.Tariff;

public class RVService extends RecyclerView.Adapter<RVService.ViewHolder>
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<DopService> dopServices;
    ArrayList<DopService> mCleanCopyDataset;

    static final private int EDIT_TARIFF = 2;

    public RVService(Activity activity, Resources resources, ArrayList<DopService> dopServices)
    {
        this.activity = activity;
        this.resources = resources;
        this.context = activity.getApplicationContext();

        this.dopServices = dopServices;
        mCleanCopyDataset = this.dopServices;
    }

    @Override
    public RVService.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dop_service, parent, false);

        RVService.ViewHolder vh = new RVService.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RVService.ViewHolder holder, final int position)
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

        holder.nameTariff.setText(dopServices.get(position).name);
        holder.pricePerHour.setText(String.valueOf(dopServices.get(position).price) + " \u20BD");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView edit;
        ImageView remove;

        TextView nameTariff;
        TextView pricePerHour;

        public ViewHolder(View v)
        {
            super(v);

            edit = (ImageView) v.findViewById(R.id.buttonEdit);
            remove = (ImageView) v.findViewById(R.id.buttonRemove);

            nameTariff = (TextView) v.findViewById(R.id.textNameTariff);
            pricePerHour = (TextView) v.findViewById(R.id.textPricePerHour);
        }
    }

    private void editOnClick(int position)
    {
        Intent intent = new Intent(activity, AddServiceActivity.class)
                .putExtra("dopService", dopServices.get(position));

        activity.startActivityForResult(intent, EDIT_TARIFF);
    }

    private void removeOnClick(int position)
    {
        DBHelper dbHelper = new DBHelper(context, DBHelper.DOP_SERVICE);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int delCount = db.delete(DBHelper.DOP_SERVICE, "id = ?",
                new String[]{String.valueOf(dopServices.get(position).id)});

        db.close();
        dbHelper.close();

        dopServices.remove(position);
        notifyItemRemoved(position);
    }

    public void filter(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        dopServices = new ArrayList<DopService>();
        if (charText.length() == 0)
        {
            dopServices.addAll(mCleanCopyDataset);
        }
        else
        {
            for (DopService item : mCleanCopyDataset)
            {
                String nameAndSurname = item.name;
                if (nameAndSurname.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    dopServices.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return dopServices.size();
    }
}
