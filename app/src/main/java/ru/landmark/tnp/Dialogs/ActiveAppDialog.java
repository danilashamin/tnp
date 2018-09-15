package ru.landmark.tnp.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.victor.loading.rotate.RotateLoading;

import ru.landmark.tnp.Functional.CustomToast;
import ru.landmark.tnp.R;

public class ActiveAppDialog
{
    public Context context;
    Activity activity;
    Resources resources;

    FirebaseDatabase mDatabase;
    SharedPreferences sharedPref;

    public ActiveAppDialog(Context context, Activity activity)
    {
        this.context = context;
        this.activity = activity;
        this.resources = context.getResources();

        mDatabase = FirebaseDatabase.getInstance();
        sharedPref = activity.getSharedPreferences(resources.getString(R.string.appSettingsName), Context.MODE_PRIVATE);

        createDialog();
    }

    private void createDialog()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);

        LayoutInflater inflater = activity.getLayoutInflater();
        View AlertDialogForm = inflater.inflate(R.layout.dialog_active_app, null);

        Button Activation = (Button) AlertDialogForm.findViewById(R.id.buttonActivation);

        final TextInputLayout inputKeyText = (TextInputLayout) AlertDialogForm.findViewById(R.id.textInputKey);
        final EditText editTextKey = inputKeyText.getEditText();
        final RotateLoading rotateLoading = (RotateLoading) AlertDialogForm.findViewById(R.id.rotateloading);
        final TextView NameDialog = (TextView) AlertDialogForm.findViewById(R.id.textView);

        builder.setView(AlertDialogForm);
        final AlertDialog alert = builder.create();

        Activation.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!editTextKey.getText().toString().equals(""))
                {
                    inputKeyText.setErrorEnabled(false);

                    if (isOnline(context))
                    {
                        DatabaseReference DatabaseRef = mDatabase.getReference();
                        DatabaseReference KeysRef = DatabaseRef.child("Keys");
                        final DatabaseReference KeyRef = KeysRef.child(editTextKey.getText().toString());

                        final DatabaseReference valueKey = KeyRef.child("Value");

                        NameDialog.setVisibility(View.GONE);
                        rotateLoading.start();

                        valueKey.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.getValue() != null)
                                {
                                    boolean IsActive = Boolean.parseBoolean(dataSnapshot.getValue().toString());

                                    rotateLoading.stop();
                                    NameDialog.setVisibility(View.VISIBLE);

                                    if (!IsActive)
                                    {
                                        SharedPreferences.Editor ed = sharedPref.edit();
                                        ed.putBoolean(resources.getString(R.string.appIsActive), true);

                                        ed.putString(resources.getString(R.string.appKey), editTextKey.getText().toString());
                                        valueKey.setValue(true);
                                        ed.commit();

                                        new CustomToast(activity,resources.getString(R.string.TextActivateTrue), false);

                                        alert.dismiss();
                                    }
                                    else
                                    {
                                        inputKeyText.setErrorEnabled(true);
                                        inputKeyText.setError(resources.getString(R.string.TextKeyIsActive));
                                    }
                                }
                                else
                                {
                                    rotateLoading.stop();
                                    NameDialog.setVisibility(View.VISIBLE);

                                    inputKeyText.setErrorEnabled(true);
                                    inputKeyText.setError(resources.getString(R.string.TextNoKey));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {
                                Log.d("myLog", "Error " + databaseError.getMessage().toString() + " Ref " + KeyRef.getRef().toString());
                                rotateLoading.stop();
                                NameDialog.setVisibility(View.VISIBLE);

                                inputKeyText.setErrorEnabled(true);
                                inputKeyText.setError(resources.getString(R.string.TextErrorBD));
                            }
                        });
                    }
                    else
                    {
                        inputKeyText.setErrorEnabled(true);
                        inputKeyText.setError(resources.getString(R.string.TextNoInternet));
                    }
                }
                else
                {
                    inputKeyText.setErrorEnabled(true);
                    inputKeyText.setError(resources.getString(R.string.TextNeedEditKey));
                }
            }
        });

        alert.show();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }
}
