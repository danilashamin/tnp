package ru.landmark.tnp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Debug;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sam4s.printer.Sam4sBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ru.landmark.tnp.ChangeTariffActivity;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.InfoHireActivity;
import ru.landmark.tnp.Interfaces.PrintCheckInterface;
import ru.landmark.tnp.MainActivity;
import ru.landmark.tnp.R;
import ru.landmark.tnp.Structurs.Report;
import ru.landmark.tnp.Structurs.UserConsumers;

public class RVMain extends RecyclerView.Adapter<RVMain.ViewHolder>
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<Report> reports;
    ArrayList<Report> mCleanCopyDataset;

    int mode = 0;

    SharedPreferences sharedPref;

    PrintCheckInterface printCheckInterface;

    public RVMain(Activity activity, Resources resources, ArrayList<Report> reports, int mode, PrintCheckInterface printCheckInterface)
    {
        this.activity = activity;
        this.resources = resources;
        this.context = activity.getApplicationContext();

        this.reports = reports;
        mCleanCopyDataset = this.reports;

        this.mode = mode;

        sharedPref = activity.getSharedPreferences(resources.getString(R.string.appSettingsName), Context.MODE_PRIVATE);

        this.printCheckInterface = printCheckInterface;
    }

    @Override
    public RVMain.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);

        RVMain.ViewHolder vh = new RVMain.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(RVMain.ViewHolder holder, final int position)
    {
        holder.textFio.setText("ФИО: " + reports.get(position).fio);
        holder.textBarcode.setText(reports.get(position).barcode);
        holder.dateStart.setText("Начало: " + reports.get(position).startTime);
        holder.dateEnd.setText("Окончание: " + reports.get(position).endTime);
        holder.dateHolding.setText("В прокате: " + reports.get(position).timeHire);
        holder.textSumm.setText("Сумма: " + reports.get(position).summ);

        if (reports.get(position).endTime.equals("~"))
        {
            holder.root.setCardBackgroundColor(resources.getColor(R.color.md_green_500));
        }
        else
        {
            try
            {
                Date datePrint = new SimpleDateFormat("dd/MM/yyyy").parse(reports.get(position).startTime);
                String datePrintS = new SimpleDateFormat("dd/MM/yyyy").format(datePrint);

                Date currDate = new Date();
                String currDateStr = new SimpleDateFormat("dd/MM/yyyy").format(currDate);

                if (datePrintS.equals(currDateStr))
                {
                    holder.root.setCardBackgroundColor(resources.getColor(R.color.md_red_500));
                }
                else
                {
                    holder.root.setCardBackgroundColor(resources.getColor(R.color.md_grey_500));
                }
            }
            catch (Exception e)
            {
            }
        }

        if (reports.get(position).deposit.equals(""))
        {
            holder.layoutDeposit.setVisibility(View.GONE);
        }
        else
        {
            holder.textDeposit.setText("Залог: " + reports.get(position).deposit);
        }

        if (mode == 0)
        {
            holder.buttonPrint.setVisibility(View.GONE);
        }
        else
        {
            holder.buttonPrint.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (reports.get(position).endTime.equals("~"))
                    {
                        try
                        {
                            Date datePrint = new SimpleDateFormat("dd/MM/yyyy").parse(reports.get(position).startTime);
                            String datePrintS = new SimpleDateFormat("dd/MM/yyyy").format(datePrint);

                            Date time = new SimpleDateFormat("HH:mm:ss").parse(reports.get(position).startTime);
                            String timePrintS = new SimpleDateFormat("HH:mm:ss").format(time);

                            printCheckInterface.printCheckInHire(reports.get(position).barcode,
                                    datePrintS, reports.get(position).fio, reports.get(position).deposit,
                                    timePrintS);
                        }
                        catch (Exception e)
                        {
                        }
                    }
                    else
                    {
                        try
                        {
                            Date datePrint = new SimpleDateFormat("dd/MM/yyyy").parse(reports.get(position).startTime);
                            String datePrintS = new SimpleDateFormat("dd/MM/yyyy").format(datePrint);

                            printCheckInterface.printCheckStop(reports.get(position).summ,
                                    dop(String.valueOf(reports.get(position).idNumber)) ,datePrintS,
                                    reports.get(position).fio, reports.get(position).timeHire);
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }
            });
        }

        holder.v.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(context, InfoHireActivity.class);
                intent.putExtra("report", reports.get(position));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView root;

        TextView textFio;
        TextView textBarcode;
        TextView dateStart;
        TextView dateEnd;
        TextView dateHolding;
        TextView textSumm;
        TextView textDeposit;

        LinearLayout layoutDeposit;

        Button buttonPrint;

        View v;

        public ViewHolder(View v)
        {
            super(v);

            root = (CardView) v.findViewById(R.id.card_view_root);

            textFio = (TextView) v.findViewById(R.id.textFIO);
            textBarcode = (TextView) v.findViewById(R.id.textBarcode);
            dateStart = (TextView) v.findViewById(R.id.textStartTime);
            dateEnd = (TextView) v.findViewById(R.id.textEndTime);
            dateHolding = (TextView) v.findViewById(R.id.textHoldingHire);
            textSumm = (TextView) v.findViewById(R.id.textSumm);
            textDeposit = (TextView) v.findViewById(R.id.textDeposit);

            layoutDeposit = (LinearLayout) v.findViewById(R.id.layoutDeposit);

            buttonPrint = (Button) v.findViewById(R.id.buttonPrint);

            this.v = v;
        }
    }

    private int dop(String idOrderRes)
    {
        DBHelper dbHelper = new DBHelper(activity.getApplicationContext(), DBHelper.SALE_DOP);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM SaleDop WHERE idOrder = ?", new String[]{String.valueOf(idOrderRes)});

        int summDop = 0;

        if (cursor.moveToFirst())
        {
            do
            {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int idDop = cursor.getInt(cursor.getColumnIndex("idDop"));
                int idOrder = cursor.getInt(cursor.getColumnIndex("idOrder"));

                DBHelper dbHelperDop = new DBHelper(activity.getApplicationContext(), DBHelper.DOP_SERVICE);
                SQLiteDatabase dbDop = dbHelperDop.getWritableDatabase();

                Cursor cursorDop = dbDop.rawQuery("SELECT * FROM DopService WHERE id = ?", new String[]{String.valueOf(idDop)});

                if (cursorDop.moveToFirst())
                {
                    int idD = cursorDop.getInt(cursorDop.getColumnIndex("id"));
                    String nameDop = cursorDop.getString(cursorDop.getColumnIndex("name"));
                    float namePrice = cursorDop.getFloat(cursorDop.getColumnIndex("price"));

                    summDop += namePrice;
                }

                cursorDop.close();
                dbDop.close();
                dbHelperDop.close();
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbHelper.close();

        return summDop;
    }

    @Override
    public int getItemCount()
    {
        return reports.size();
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
