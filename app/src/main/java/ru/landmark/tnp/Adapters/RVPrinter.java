package ru.landmark.tnp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ru.landmark.tnp.Interfaces.SearchPrintInterface;
import ru.landmark.tnp.R;
import ru.landmark.tnp.Structurs.Report;

public class RVPrinter extends RecyclerView.Adapter<RVPrinter.ViewHolder>
{
    Activity activity;
    Resources resources;
    Context context;
    SearchPrintInterface searchPrintInterface;

    ArrayList<String> printers;

    public RVPrinter (Activity activity, Resources resources, SearchPrintInterface searchPrintInterface, ArrayList<String> printers)
    {
        this.activity = activity;
        this.resources = resources;
        this.context = activity.getApplicationContext();
        this.searchPrintInterface = searchPrintInterface;

        this.printers = printers;
    }

    @Override
    public RVPrinter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_print, parent, false);

        RVPrinter.ViewHolder vh = new RVPrinter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(RVPrinter.ViewHolder holder, final int position)
    {
        holder.printer.setText(printers.get(position));

        holder.v.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                searchPrintInterface.onClickPrint(position);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return printers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView printer;

        View v;

        public ViewHolder(View v)
        {
            super(v);

            this.printer = (TextView) v.findViewById(R.id.textViewPrintInfo);

            this.v = v;
        }
    }

    public String getItemAtPosition(int pos)
    {
        return printers.get(pos);
    }
}
