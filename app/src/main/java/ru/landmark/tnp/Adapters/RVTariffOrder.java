package ru.landmark.tnp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.R;
import ru.landmark.tnp.Structurs.TariffOrder;

public class RVTariffOrder extends RecyclerView.Adapter<RVTariffOrder.ViewHolder>
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<TariffOrder> tariffs;
    ArrayList<String> tariffsName = new ArrayList<String>();

    int mode = 0;

    public RVTariffOrder (Activity activity, Resources resources, ArrayList<TariffOrder> tariffs, int mode)
    {
        this.activity = activity;
        this.resources = resources;
        this.context = activity.getApplicationContext();

        this.tariffs = tariffs;
        this.mode = mode;

        DBHelper dbHelper = new DBHelper(context, DBHelper.TARIFF);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Tariff", null);

        if (cursor.moveToFirst())
        {
            do
            {
                String nameTariff = cursor.getString(cursor.getColumnIndex("nameTariff"));
                tariffsName.add(nameTariff);

                Log.d("myLog", nameTariff + " name");
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();
    }

    @Override
    public RVTariffOrder.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_tariff, parent, false);

        RVTariffOrder.ViewHolder vh = new RVTariffOrder.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RVTariffOrder.ViewHolder holder, final int position)
    {
        if (tariffs.get(position).isCreate)
        {
            holder.layout.setVisibility(View.GONE);
            holder.addLayout.setVisibility(View.VISIBLE);

            SpinnerCustom spinnerCustom = new SpinnerCustom(activity, R.layout.item_spinner_text, tariffsName);
            holder.spinner.setAdapter(spinnerCustom);

            if (!tariffs.get(position).nameTariff.equals(""))
            {
                int spinnerPosition = spinnerCustom.getPosition(tariffs.get(position).nameTariff);
                holder.spinner.setSelection(spinnerPosition);

                holder.countCart.getEditText().setText(String.valueOf(tariffs.get(position).countCart));
            }

            holder.add.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    buttonAdd(holder, position);
                }
            });
        }
        else
        {
            holder.addLayout.setVisibility(View.GONE);
            holder.layout.setVisibility(View.VISIBLE);

            if (mode == 0)
            {
                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        editTariff(position);
                    }
                });
                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {removeTariff(position);}
                });
            }
            else
            {
                holder.edit.setVisibility(View.GONE);
                holder.remove.setVisibility(View.GONE);
            }

            holder.textNameTariff.setText(tariffs.get(position).nameTariff);
            holder.textCartCount.setText(String.valueOf(tariffs.get(position).countCart));
        }
    }

    private void buttonAdd(RVTariffOrder.ViewHolder holder, int pos)
    {
        if (checkInputLayout(holder))
        {
            tariffs.get(pos).isCreate = false;
            tariffs.get(pos).nameTariff = tariffsName.get(holder.spinner.getSelectedItemPosition());
            tariffs.get(pos).countCart = Integer.parseInt(holder.countCart.getEditText().getText().toString());

            notifyItemChanged(pos);

            holder.spinner.setSelection(0);
            holder.countCart.getEditText().setText("");
        }
    }

    private boolean checkInputLayout(RVTariffOrder.ViewHolder holder)
    {
        boolean countCartBool = false;

        String countCart = holder.countCart.getEditText().getText().toString();

        /*
        if (nameTariffs.equals(""))
        {
            holder.nameTariff.setErrorEnabled(true);
            holder.nameTariff.setError(resources.getString(R.string.TextInputFullField));

            nameTariffsBool = false;
        }
        else
        {
            DBHelper dbHelper = new DBHelper(context, DBHelper.TARIFF);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.rawQuery("SELECT * FROM Tariff WHERE nameTariff = ?", new String[]{nameTariffs});

            if (cursor.moveToFirst())
            {
                holder.nameTariff.setErrorEnabled(false);
                nameTariffsBool = true;

                cursor.close();
            }
            else
            {
                holder.nameTariff.setErrorEnabled(true);
                holder.nameTariff.setError(resources.getString(R.string.TextFailTariff));

                nameTariffsBool = false;
            }
        }
        */

        if (countCart.equals("") || countCart.equals("0"))
        {
            holder.countCart.setErrorEnabled(true);
            holder.countCart.setError(resources.getString(R.string.TextInputFullField));

            countCartBool = false;
        }
        else
        {
            holder.countCart.setErrorEnabled(false);
            countCartBool = true;
        }

        if (countCartBool)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void clear()
    {
        tariffs.clear();
        notifyDataSetChanged();
    }

    public void addTariff()
    {
        tariffs.add(new TariffOrder("",0, true));
        notifyDataSetChanged();
    }

    public ArrayList<TariffOrder> getArray()
    {
        return tariffs;
    }

    public void AddCartParametr(String nameTariff, int countCart)
    {
        tariffs.add(new TariffOrder(nameTariff, countCart, false));
        notifyDataSetChanged();
    }

    private void removeTariff(int pos)
    {
        tariffs.remove(pos);
        notifyDataSetChanged();
    }

    private void editTariff(int pos)
    {
        tariffs.get(pos).isCreate = true;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return tariffs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        LinearLayout layout;
        LinearLayout addLayout;

        Spinner spinner;
        TextInputLayout countCart;

        Button add;

        TextView textNameTariff;
        TextView textCartCount;

        ImageView edit;
        ImageView remove;

        public ViewHolder(View v)
        {
            super(v);

            layout = (LinearLayout) v.findViewById(R.id.layout);
            addLayout = (LinearLayout) v.findViewById(R.id.addLayout);

            spinner = (Spinner) v.findViewById(R.id.spinner);
            countCart = (TextInputLayout) v.findViewById(R.id.textInputCountCart);

            add = (Button) v.findViewById(R.id.buttonAdd);

            textNameTariff = (TextView) v.findViewById(R.id.textNameTariff);
            textCartCount = (TextView) v.findViewById(R.id.textCartCount);

            edit = (ImageView) v.findViewById(R.id.buttonEdit);
            remove = (ImageView) v.findViewById(R.id.buttonRemove);
        }
    }
}
