package ru.landmark.tnp.Adapters;

import android.app.Activity;
import android.content.ContentValues;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.R;
import ru.landmark.tnp.Structurs.DopService;
import ru.landmark.tnp.Structurs.DopServiceOrder;

public class RVDopOrder extends RecyclerView.Adapter<RVDopOrder.ViewHolder>
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<DopServiceOrder> tariffs;
    ArrayList<String> tariffsName = new ArrayList<String>();
    ArrayList<Integer> tariffsId = new ArrayList<Integer>();
    ArrayList<Float> priceT = new ArrayList<Float>();

    int idOrder = 0;

    public RVDopOrder(Activity activity, Resources resources, ArrayList<DopServiceOrder> tariffs, int idorder)
    {
        this.activity = activity;
        this.resources = resources;
        this.context = activity.getApplicationContext();

        this.tariffs = tariffs;
        this.idOrder = idorder;

        for (int i=0; i< tariffs.size(); i++)
        {
            Log.d("myLog", tariffs.get(i).nameTariff);
        }

        DBHelper dbHelper = new DBHelper(context, DBHelper.DOP_SERVICE);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM DopService", null);

        if (cursor.moveToFirst())
        {
            do
            {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String nameTariff = cursor.getString(cursor.getColumnIndex("name"));
                float price = cursor.getFloat(cursor.getColumnIndex("price"));
                priceT.add(price);
                tariffsName.add(nameTariff);
                tariffsId.add(id);

                Log.d("myLog", nameTariff + " name " + id);
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();
    }

    @Override
    public RVDopOrder.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_dop, parent, false);

        RVDopOrder.ViewHolder vh = new RVDopOrder.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RVDopOrder.ViewHolder holder, final int position)
    {
        if (tariffs.get(position).isCreate)
        {
            Log.d("myLog", "cr");
            holder.layout.setVisibility(View.GONE);
            holder.addLayout.setVisibility(View.VISIBLE);

            SpinnerCustom spinnerCustom = new SpinnerCustom(activity, R.layout.item_spinner_text, tariffsName);
            holder.spinner.setAdapter(spinnerCustom);

            if (!tariffs.get(position).nameTariff.equals(""))
            {
                int spinnerPosition = spinnerCustom.getPosition(tariffs.get(position).nameTariff);
                holder.spinner.setSelection(spinnerPosition);

                holder.countCart.getEditText().setText(String.valueOf(tariffs.get(position).price));
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
            Log.d("myLog", "fl");
            holder.addLayout.setVisibility(View.GONE);
            holder.layout.setVisibility(View.VISIBLE);

            holder.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {removeTariff(position);}
            });

            holder.textNameTariff.setText(tariffs.get(position).nameTariff);
            holder.textCartCount.setText(String.valueOf(tariffs.get(position).price));
        }
    }

    private void buttonAdd(RVDopOrder.ViewHolder holder, int pos)
    {
        if (checkInputLayout(holder))
        {
            tariffs.get(pos).isCreate = false;
            tariffs.get(pos).nameTariff = tariffsName.get(holder.spinner.getSelectedItemPosition());
            tariffs.get(pos).price = priceT.get(holder.spinner.getSelectedItemPosition());

            notifyItemChanged(pos);

            //holder.spinner.setSelection(0);
            //holder.countCart.getEditText().setText("");

            DBHelper dbHelper = new DBHelper(context, DBHelper.SALE_DOP);

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues cv = new ContentValues();

            Log.d("myLog", holder.spinner.getSelectedItemPosition() + " tt");

            cv.put("idDop", tariffsId.get(holder.spinner.getSelectedItemPosition()));
            cv.put("idOrder", idOrder);

            long rowID = db.insert(dbHelper.getDatabaseName(), null, cv);
            tariffs.get(pos).id = (int) rowID;

            holder.spinner.setSelection(0);
            db.close();
            dbHelper.close();
        }
    }

    private boolean checkInputLayout(RVDopOrder.ViewHolder holder)
    {
        return true;
    }

    public void clear()
    {
        tariffs.clear();
        notifyDataSetChanged();
    }

    public void addTariff()
    {
        tariffs.add(new DopServiceOrder(0,"",0, true));
        notifyDataSetChanged();
    }

    public ArrayList<DopService> getArray()
    {
        ArrayList<DopService> dopServices = new ArrayList<DopService>();

        for (int i=0; i< tariffs.size(); i++)
        {
            if (!tariffs.get(i).isCreate)
            {
                dopServices.add(new DopService(dopServices.get(i).id,dopServices.get(i).name, dopServices.get(i).price));
            }
        }

        return dopServices;
    }

    public void AddCartParametr(String nameTariff, int countCart)
    {
        tariffs.add(new DopServiceOrder(0, nameTariff, countCart, false));
        notifyDataSetChanged();
    }

    private void removeTariff(int pos)
    {
        DBHelper dbHelper = new DBHelper(context, DBHelper.SALE_DOP);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int delCount = db.delete("SaleDop", "id = " + tariffs.get(pos).id, null);

        Log.d("myLog", delCount + " " + tariffs.get(pos).id + " ");

        tariffs.remove(pos);
        notifyDataSetChanged();

        db.close();
        dbHelper.close();
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

            remove = (ImageView) v.findViewById(R.id.buttonRemove);
        }
    }
}
