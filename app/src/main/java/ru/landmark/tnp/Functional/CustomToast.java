package ru.landmark.tnp.Functional;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.landmark.tnp.R;

public class CustomToast
{
    public CustomToast(Activity activity, String text, boolean isError)
    {
      //  View toastLayout = activity.getLayoutInflater().inflate(R.layout.toast, (ViewGroup)
       //         activity.findViewById(R.id.custom_toast_layout));
        LayoutInflater inflater = activity.getLayoutInflater();
        View toastLayout = inflater.inflate(R.layout.toast, null);

        LinearLayout root = (LinearLayout) toastLayout.findViewById(R.id.custom_toast_layout);

        TextView toastText = (TextView) toastLayout.findViewById(R.id.textView);
        toastText.setText(text);

        if (isError)
        {
            root.setBackground(activity.getResources().getDrawable(R.drawable.back_toast_red));
        }

        android.widget.Toast toast = new android.widget.Toast(activity.getApplicationContext());
        toast.setDuration(android.widget.Toast.LENGTH_LONG);
        toast.setView(toastLayout);
        toast.show();
    }
}
