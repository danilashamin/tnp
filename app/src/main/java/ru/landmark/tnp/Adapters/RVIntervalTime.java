package ru.landmark.tnp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import ru.landmark.tnp.AddTariffActivity;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.R;
import ru.landmark.tnp.Structurs.IntervalTime;
import ru.landmark.tnp.Structurs.Tariff;

public class RVIntervalTime extends RecyclerView.Adapter<RVIntervalTime.ViewHolder>
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<IntervalTime> intervalTimes;

    public RVIntervalTime(Activity activity, Resources resources, ArrayList<IntervalTime> intervalTimes)
    {
        this.activity = activity;
        this.resources = resources;
        this.context = activity.getApplicationContext();

        this.intervalTimes = intervalTimes;
    }

    @Override
    public RVIntervalTime.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tariff_time, parent, false);

        RVIntervalTime.ViewHolder vh = new RVIntervalTime.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RVIntervalTime.ViewHolder holder, final int position)
    {
        if (intervalTimes.get(position).isCreate)
        {
            holder.layout.setVisibility(View.GONE);
            holder.addLayout.setVisibility(View.VISIBLE);
            if (!intervalTimes.get(position).startTime.equals(""))
            {
                holder.startTime.getEditText().setText(intervalTimes.get(position).startTime);
            }

            if (!intervalTimes.get(position).endTime.equals(""))
            {
                holder.endTime.getEditText().setText(intervalTimes.get(position).endTime);
            }

            if (intervalTimes.get(position).price != 0)
            {
                holder.price.getEditText().setText(intervalTimes.get(position).price + "");
            }

            holder.pn.setChecked(intervalTimes.get(position).pn);
            holder.vt.setChecked(intervalTimes.get(position).vt);
            holder.sr.setChecked(intervalTimes.get(position).sr);
            holder.ct.setChecked(intervalTimes.get(position).ct);
            holder.pt.setChecked(intervalTimes.get(position).pt);
            holder.sub.setChecked(intervalTimes.get(position).sub);
            holder.vosk.setChecked(intervalTimes.get(position).vosk);

            holder.buttonAdd.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    boolean startTimeBool = false;
                    boolean endTimeBool = false;
                    boolean priceBool = false;

                    holder.startTime.setErrorEnabled(false);
                    holder.endTime.setErrorEnabled(false);
                    holder.price.setErrorEnabled(false);

                    if (holder.startTime.getEditText().getText().toString().equals(""))
                    {
                        holder.startTime.setErrorEnabled(true);
                        holder.startTime.setError("Заполните поле");
                        startTimeBool = true;
                    }
                    else
                    {
                        String[] s = holder.startTime.getEditText().getText().toString().split("\\.");

                        boolean lengthArr = false;

                        for (int i=0; i<s.length; i++)
                        {
                            if (s[i].length() != 2)
                            {
                                lengthArr = true;
                            }
                        }

                        if (s.length != 3 || lengthArr)
                        {
                            holder.startTime.setErrorEnabled(true);
                            holder.startTime.setError("Формат \"10.20.30\"");
                            startTimeBool = true;
                        }
                    }

                    if (holder.endTime.getEditText().getText().toString().equals(""))
                    {
                        holder.endTime.setErrorEnabled(true);
                        holder.endTime.setError("Заполните поле");
                        endTimeBool = true;
                    }
                    else
                    {
                        String[] s = holder.endTime.getEditText().getText().toString().split("\\.");


                        boolean lengthArr = false;

                        for (int i=0; i<s.length; i++)
                        {
                            if (s[i].length() != 2)
                            {
                                lengthArr = true;
                            }
                        }

                        if (s.length != 3 || lengthArr)
                        {
                            holder.endTime.setErrorEnabled(true);
                            holder.endTime.setError("Формат \"10.20.30\"");
                            startTimeBool = true;
                        }
                    }

                    if (holder.price.getEditText().getText().toString().equals(""))
                    {
                        holder.price.setErrorEnabled(true);
                        holder.price.setError("Заполните поле");
                        priceBool = true;
                    }

                    if (!startTimeBool && !endTimeBool && !priceBool)
                    {
                        intervalTimes.get(position).startTime = holder.startTime.getEditText().getText().toString();
                        intervalTimes.get(position).endTime = holder.endTime.getEditText().getText().toString();
                        intervalTimes.get(position).price = Float.parseFloat(holder.price.getEditText().getText().toString());

                        intervalTimes.get(position).pn = holder.pn.isChecked();
                        intervalTimes.get(position).vt = holder.vt.isChecked();
                        intervalTimes.get(position).sr = holder.sr.isChecked();
                        intervalTimes.get(position).ct = holder.ct.isChecked();
                        intervalTimes.get(position).pt = holder.pt.isChecked();
                        intervalTimes.get(position).sub = holder.sub.isChecked();
                        intervalTimes.get(position).vosk = holder.vosk.isChecked();

                        intervalTimes.get(position).isCreate = false;

                        notifyDataSetChanged();
                    }

                    holder.startTime.getEditText().setText("");
                    holder.endTime.getEditText().setText("");
                    holder.price.getEditText().setText("");

                    holder.pn.setChecked(true);
                    holder.vt.setChecked(true);
                    holder.sr.setChecked(true);
                    holder.ct.setChecked(true);
                    holder.pt.setChecked(true);
                    holder.sub.setChecked(true);
                    holder.vosk.setChecked(true);
                }
            });
        }
        else
        {
            holder.addLayout.setVisibility(View.GONE);
            holder.layout.setVisibility(View.VISIBLE);

            holder.textStartTime.setText(intervalTimes.get(position).startTime);
            holder.textEndTime.setText(intervalTimes.get(position).endTime);
            holder.textCashMoney.setText(intervalTimes.get(position).price + "");

            holder.buttonEdit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    intervalTimes.get(position).isCreate = true;
                    notifyDataSetChanged();
                }
            });

            holder.buttonRemove.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    intervalTimes.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout addLayout;
        LinearLayout layout;

        TextView textStartTime;
        TextView textEndTime;
        TextView textCashMoney;

        ImageView buttonEdit;
        ImageView buttonRemove;

        TextInputLayout startTime;
        TextInputLayout endTime;
        TextInputLayout price;

        CheckBox pn;
        CheckBox vt;
        CheckBox sr;
        CheckBox ct;
        CheckBox pt;
        CheckBox sub;
        CheckBox vosk;

        Button buttonAdd;

        public ViewHolder(View v)
        {
            super(v);

            addLayout = (LinearLayout) v.findViewById(R.id.addLayout);
            layout = (LinearLayout) v.findViewById(R.id.layout);

            textStartTime = (TextView) v.findViewById(R.id.textStartTime);
            textEndTime = (TextView) v.findViewById(R.id.textEndTime);
            textCashMoney = (TextView) v.findViewById(R.id.textSumm);

            buttonEdit = (ImageView) v.findViewById(R.id.buttonEdit);
            buttonRemove = (ImageView) v.findViewById(R.id.buttonRemove);

            startTime = (TextInputLayout) v.findViewById(R.id.textInputStartTime);
            endTime = (TextInputLayout) v.findViewById(R.id.textInputEndTime);
            price = (TextInputLayout) v.findViewById(R.id.textInputSummCash);

            pn = (CheckBox) v.findViewById(R.id.pn);
            vt = (CheckBox) v.findViewById(R.id.vt);
            sr = (CheckBox) v.findViewById(R.id.sr);
            ct = (CheckBox) v.findViewById(R.id.ct);
            pt = (CheckBox) v.findViewById(R.id.pt);
            sub = (CheckBox) v.findViewById(R.id.sub);
            vosk = (CheckBox) v.findViewById(R.id.vosk);

            buttonAdd = (Button) v.findViewById(R.id.buttonAdd);
        }
    }

    public void addNew()
    {
        intervalTimes.add(new IntervalTime("", "", 0, true, true, true ,true, true, true, true, true));
        notifyDataSetChanged();
    }

    public ArrayList<IntervalTime> getArray()
    {
        return  this.intervalTimes;
    }

    @Override
    public int getItemCount()
    {
        return intervalTimes.size();
    }
}
