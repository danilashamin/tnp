package ru.landmark.tnp.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class VPMain extends FragmentPagerAdapter
{
    public ArrayList<Fragment> countF;

    public VPMain(FragmentManager fm, ArrayList<Fragment> countF)
    {
        super(fm);

        this.countF = countF;
    }

    @Override
    public Fragment getItem(int position)
    {
        return countF.get(position);
    }

    @Override
    public int getCount()
    {
        return countF.size();
    }
}
