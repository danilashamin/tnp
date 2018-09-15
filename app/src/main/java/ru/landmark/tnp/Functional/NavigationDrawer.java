package ru.landmark.tnp.Functional;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import ru.landmark.tnp.AddConstClientActivity;
import ru.landmark.tnp.ConstClientsActivity;
import ru.landmark.tnp.DopServiceActivity;
import ru.landmark.tnp.GenerateKeyActivity;
import ru.landmark.tnp.MainActivity;
import ru.landmark.tnp.PrintCheck;
import ru.landmark.tnp.R;
import ru.landmark.tnp.RemoteControlActivity;
import ru.landmark.tnp.ReportActivity;
import ru.landmark.tnp.ReportMainActivity;
import ru.landmark.tnp.SettingsActivity;
import ru.landmark.tnp.StartHireActivity;
import ru.landmark.tnp.StopHireActivity;
import ru.landmark.tnp.Structurs.UsersPersonal;
import ru.landmark.tnp.SumCaseActivity;
import ru.landmark.tnp.TariffsActivity;
import ru.landmark.tnp.TextRulesActivity;

public class NavigationDrawer
{
    Context context;
    Activity activity;
    Resources resources;
    Toolbar toolbar;
    UsersPersonal user;
    AccountHeader headerResult;

    SharedPreferences sharedPref;

    static
    {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public NavigationDrawer(final Context context, final Activity activity, final Toolbar toolbar)
    {
        this.context = context;
        this.activity = activity;
        this.resources = activity.getResources();
        this.toolbar = toolbar;
        sharedPref = activity.getSharedPreferences(resources.getString(R.string.appSettingsName), Context.MODE_PRIVATE);


        this.user = new UsersPersonal(sharedPref.getString(resources.getString(R.string.appDisplayName), "null"),
                sharedPref.getString(resources.getString(R.string.appLoginUser), "null"),
                sharedPref.getBoolean(resources.getString(R.string.appIsAdmin), false));

        String login = sharedPref.getString(resources.getString(R.string.appLogin), "");
        String pass = sharedPref.getString(resources.getString(R.string.appPass), "");

        int loginHash = login.hashCode();
        int passHash = pass.hashCode();

        Log.d("mmm " , sharedPref.getString(resources.getString(R.string.appDisplayName), "") + "   ddd");

        final IProfile profile = new ProfileDrawerItem()
                .withName(user.NameAndSurname)
                .withIcon(R.drawable.icon_person)
                .withIdentifier(1);

        if (user.IsAdmin)
        {
            profile.withEmail(resources.getString(R.string.TextAdmin));
        }
        else
        {
            profile.withEmail(resources.getString(R.string.TextManager));
        }

        headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(resources.getDrawable(R.drawable.navigation_back))
                .addProfiles(
                        profile
                )
                .build();

        PrimaryDrawerItem Main = new PrimaryDrawerItem().withIdentifier(2)
                .withName(resources.getString(R.string.TextMain))
                .withIcon(R.drawable.icon_main)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
                {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {
                          activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), MainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                          activity.finish();

                        return true;
                    }
                });

        PrimaryDrawerItem StartHire = new PrimaryDrawerItem().withIdentifier(2)
                .withName(resources.getString(R.string.TextStartHire))
                .withIcon(R.drawable.icon_start_hire)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
                {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), StartHireActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        return true;
                    }
                });

        PrimaryDrawerItem StopHire = new PrimaryDrawerItem().withIdentifier(2)
                .withName(resources.getString(R.string.TextStopHire))
                .withIcon(R.drawable.icon_stop_hire)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
                {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), StopHireActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        return true;
                    }
                });

        PrimaryDrawerItem ConstClient = new PrimaryDrawerItem().withIdentifier(3)
                .withName(resources.getString(R.string.TextConstClients))
                .withIcon(R.drawable.icon_const_clients)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
                {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), ConstClientsActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        return true;
                    }
                });

        PrimaryDrawerItem Tariff = new PrimaryDrawerItem().withIdentifier(4)
                .withName(resources.getString(R.string.TextTariffs))
                .withIcon(R.drawable.icon_balance)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
                {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), TariffsActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        return true;
                    }
                });

        PrimaryDrawerItem MoneyRemove= new PrimaryDrawerItem().withIdentifier(5)
                .withName("Касса")
                .withIcon(R.drawable.icon_money_light)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
                {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), SumCaseActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        return true;
                    }
                });

        PrimaryDrawerItem Report = new PrimaryDrawerItem().withIdentifier(6)
                .withName(resources.getString(R.string.TextReport))
                .withIcon(R.drawable.icon_report)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
                {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), ReportMainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        return true;
                    }
                });

        PrimaryDrawerItem Key = new PrimaryDrawerItem().withIdentifier(7)
                .withName(resources.getString(R.string.TextGenerateKey))
                .withIcon(R.drawable.icon_key)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
                {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), GenerateKeyActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        return true;
                    }
                });

        PrimaryDrawerItem Settings = new PrimaryDrawerItem().withIdentifier(8)
                .withName("Настройки")
                .withIcon(R.drawable.icon_settings)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
                {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), SettingsActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        return true;
                    }
                });

        PrimaryDrawerItem RemoteControl = new PrimaryDrawerItem().withIdentifier(9)
                .withName(resources.getString(R.string.TextRemoteControl))
                .withIcon(R.drawable.icon_remote_controll)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
                {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), RemoteControlActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        return true;
                    }
                });

        PrimaryDrawerItem DopService = new PrimaryDrawerItem().withIdentifier(10)
                .withName("Разное")
                .withIcon(R.drawable.icon_dop_service)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
                {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), DopServiceActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        return true;
                    }
                });

        PrimaryDrawerItem printCheck = new PrimaryDrawerItem().withIdentifier(11)
                .withName("Печать чеков")
                .withIcon(R.drawable.icon_print_naw)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener()
                {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem)
                    {
                        activity.getApplicationContext().startActivity(new Intent(activity.getApplicationContext(), PrintCheck.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        return true;
                    }
                });

        String key = sharedPref.getString(resources.getString(R.string.appKey), "");

        if (!user.IsAdmin)
        {
            final Drawer result = new DrawerBuilder()
                    .withSelectedItem(-1)
                    .withActivity(activity)
                    .withToolbar(toolbar)
                    .withAccountHeader(headerResult)
                    .addDrawerItems(
                            Main,
                            ConstClient,
                            StartHire,
                            StopHire,
                            //printCheck,
                            new DividerDrawerItem(),
                            Report,
                            new SecondaryDrawerItem().withName("ID приложения " + key).withSelectable(false),
                            new SecondaryDrawerItem().withName("Версия приложения " + resources.getString(R.string.app_ver)).withSelectable(false)
                    )
                    .build();
        }
        else if (user.IsAdmin && loginHash == -1268162441 && passHash == 1983676182)
        {
            final Drawer result = new DrawerBuilder()
                    .withSelectedItem(-1)
                    .withActivity(activity)
                    .withToolbar(toolbar)
                    .withAccountHeader(headerResult)
                    .addDrawerItems(
                            Main,
                            ConstClient,
                            StartHire,
                            StopHire,
                            //printCheck,
                            new DividerDrawerItem(),
                            Tariff,
                            Settings,
                            MoneyRemove,
                            Report,
                            DopService,
                            new DividerDrawerItem(),
                            Key,
                            RemoteControl,
                            new SecondaryDrawerItem().withName("ID приложения " + key).withSelectable(false),
                            new SecondaryDrawerItem().withName("Версия приложения " + resources.getString(R.string.app_ver)).withSelectable(false)
                    )
                    .build();
        }
        else
        {
            final Drawer result = new DrawerBuilder()
                    .withSelectedItem(-1)
                    .withActivity(activity)
                    .withToolbar(toolbar)
                    .withAccountHeader(headerResult)
                    .addDrawerItems(
                            Main,
                            ConstClient,
                            StartHire,
                            StopHire,
                            //printCheck,
                            new DividerDrawerItem(),
                            Tariff,
                            Settings,
                            MoneyRemove,
                            Report,
                            DopService,
                            new SecondaryDrawerItem().withName("ID приложения " + key).withSelectable(false),
                            new SecondaryDrawerItem().withName("Версия приложения " + resources.getString(R.string.app_ver)).withSelectable(false)
                    )
                    .build();
        }
    }
}
