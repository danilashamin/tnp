package ru.landmark.tnp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.R;
import ru.landmark.tnp.Structurs.Report;
import ru.landmark.tnp.Structurs.Tariff;
import ru.landmark.tnp.Structurs.UserConsumers;

public class RVReport extends RecyclerView.Adapter<RVReport.ViewHolder>
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<Report> reports;
    ArrayList<Report> mCleanCopyDataset;

    public RVReport(Activity activity, Resources resources, ArrayList<Report> reports)
    {
        this.activity = activity;
        this.resources = resources;
        this.context = activity.getApplicationContext();

        this.reports = reports;
        mCleanCopyDataset = this.reports;
    }

    @Override
    public RVReport.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);

        RVReport.ViewHolder vh = new RVReport.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RVReport.ViewHolder holder, int position)
    {
        holder.fio.setText(reports.get(position).fio);
        holder.barcode.setText(reports.get(position).barcode);
        holder.startTime.setText(reports.get(position).startTime);
        holder.endTime.setText(reports.get(position).endTime);
        holder.holdingHire.setText(reports.get(position).timeHire);
        holder.deposit.setText(reports.get(position).deposit);

        if (reports.get(position).deposit.equals(""))
        {
            holder.layoutDeposit.setVisibility(View.GONE);
        }

        if (reports.get(position).summ.equals(""))
        {
            holder.summ.setText("-");
        }
        else
        {
            holder.summ.setText(reports.get(position).summ + " ");
        }

        holder.v.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
    }

    @Override
    public int getItemCount()
    {
        return reports.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView fio;
        TextView barcode;
        TextView startTime;
        TextView endTime;
        TextView holdingHire;
        TextView summ;

        LinearLayout layoutDeposit;
        TextView deposit;

        View v;

        public ViewHolder(View v)
        {
            super(v);

            fio = (TextView) v.findViewById(R.id.textFIO);
            barcode = (TextView) v.findViewById(R.id.textBarcode);
            startTime = (TextView) v.findViewById(R.id.textStartTime);
            endTime = (TextView) v.findViewById(R.id.textEndTime);
            holdingHire = (TextView) v.findViewById(R.id.textHoldingHire);
            summ = (TextView) v.findViewById(R.id.textSumm);

            layoutDeposit = (LinearLayout) v.findViewById(R.id.layoutDeposit);
            deposit = (TextView) v.findViewById(R.id.textDeposit);

            this.v = v;
        }
    }

    public void filter(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        reports = new ArrayList<Report>();
        if (charText.length() == 0)
        {
            reports.addAll(mCleanCopyDataset);
        }
        else
        {
            for (Report item : mCleanCopyDataset)
            {
                String nameAndSurname = item.fio;
                if (nameAndSurname.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    reports.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterBarcode(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        reports = new ArrayList<Report>();
        if (charText.length() == 0)
        {
            reports.addAll(mCleanCopyDataset);
        }
        else
        {
            for (Report item : mCleanCopyDataset)
            {
                if (item.barcode.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    reports.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
