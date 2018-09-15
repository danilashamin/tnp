package ru.landmark.tnp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.landmark.tnp.R;

public class SpinnerCustom extends ArrayAdapter
{
    Activity activity;
    Context context;

    ArrayList<String> tariffs;

    public SpinnerCustom(Activity activity, int textViewResourceId, ArrayList<String> objects)
    {
        super(activity.getApplicationContext(), textViewResourceId, objects);

        this.context = activity.getApplicationContext();
        this.activity = activity;
        this.tariffs = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = activity.getLayoutInflater();
        View row = inflater.inflate(R.layout.item_spinner_text, parent, false);
        TextView label = (TextView) row.findViewById(R.id.textView);
        label.setText(tariffs.get(position));

        return row;
    }
}
