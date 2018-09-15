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

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import ru.landmark.tnp.AddConstClientActivity;
import ru.landmark.tnp.Functional.DBHelper;
import ru.landmark.tnp.Interfaces.ConstClientsInterface;
import ru.landmark.tnp.R;
import ru.landmark.tnp.Structurs.UserConsumers;

public class RVUserConsumers extends RecyclerView.Adapter<RVUserConsumers.ViewHolder>
{
    Activity activity;
    Resources resources;
    Context context;

    ArrayList<UserConsumers> userConsumerses;
    ArrayList<UserConsumers> mCleanCopyDataset;

    ConstClientsInterface constClientsInterface;

    static final private int EDIT_CLIENT = 2;

    public RVUserConsumers (Activity activity, Resources resources, ArrayList<UserConsumers> userConsumerses, ConstClientsInterface constClientsInterface)
    {
        this.activity = activity;
        this.resources = resources;
        this.context = activity.getApplicationContext();
        this.constClientsInterface = constClientsInterface;

        this.userConsumerses = userConsumerses;
        mCleanCopyDataset = this.userConsumerses;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView edit;
        ImageView remove;
        ImageView header;

        TextView FIO;
        TextView Phone;
        TextView Passport;
        TextView HomeAddress;
        TextView Barcode;

        View view;

        public ViewHolder(View v)
        {
            super(v);

            edit = (ImageView) v.findViewById(R.id.buttonEdit);
            remove = (ImageView) v.findViewById(R.id.buttonRemove);
            header = (ImageView) v.findViewById(R.id.header);

            FIO = (TextView) v.findViewById(R.id.textFIO);
            Phone = (TextView) v.findViewById(R.id.textPhone);
            Passport = (TextView) v.findViewById(R.id.textPassport);
            HomeAddress = (TextView) v.findViewById(R.id.textHome);
            Barcode = (TextView) v.findViewById(R.id.textBarcode);

            view = v;
        }
    }

    @Override
    public RVUserConsumers.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_consumers, parent, false);

        RVUserConsumers.ViewHolder vh = new RVUserConsumers.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RVUserConsumers.ViewHolder holder, final int position)
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

        if (!userConsumerses.get(position).pathPhoto.equals(""))
        {
            File f = new File(userConsumerses.get(position).pathPhoto);
            Picasso.with(activity).load(f).into(holder.header);
        }
        else
        {
            holder.header.setImageDrawable(resources.getDrawable(R.drawable.icon_person_gray));
        }

        holder.FIO.setText(userConsumerses.get(position).name);

        holder.Phone.setText(userConsumerses.get(position).numberPhone);
        holder.Passport.setText(userConsumerses.get(position).numberPassport);
        holder.HomeAddress.setText(userConsumerses.get(position).homeAddress);
        holder.Barcode.setText(userConsumerses.get(position).barcode);

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
        constClientsInterface.onClickUser(userConsumerses.get(pos));
    }

    @Override
    public int getItemCount()
    {
        return userConsumerses.size();
    }

    private void editOnClick(int position)
    {
        constClientsInterface.onClickEdit(userConsumerses.get(position));
    }

    private void removeOnClick(int position)
    {
        DBHelper dbHelper = new DBHelper(context, DBHelper.USERS_CONSUMERS);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int delCount = db.delete(DBHelper.USERS_CONSUMERS, "id = ?",
                new String[]{String.valueOf(userConsumerses.get(position).id)});

        db.close();
        dbHelper.close();

        userConsumerses.remove(position);
        notifyItemRemoved(position);
    }

    public void filter(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        userConsumerses = new ArrayList<UserConsumers>();
        if (charText.length() == 0)
        {
            userConsumerses.addAll(mCleanCopyDataset);
        }
        else
        {
            for (UserConsumers item : mCleanCopyDataset)
            {
                String nameAndSurname = item.name;
                if (nameAndSurname.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    userConsumerses.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterBarcode(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        userConsumerses = new ArrayList<UserConsumers>();
        if (charText.length() == 0)
        {
            userConsumerses.addAll(mCleanCopyDataset);
        }
        else
        {
            for (UserConsumers item : mCleanCopyDataset)
            {
                if (item.barcode.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    userConsumerses.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
