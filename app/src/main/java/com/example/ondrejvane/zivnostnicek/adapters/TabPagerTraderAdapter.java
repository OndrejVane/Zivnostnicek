package com.example.ondrejvane.zivnostnicek.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.ondrejvane.zivnostnicek.activities.note.NoteFragment;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderShowFragment;

public class TabPagerTraderAdapter extends FragmentStatePagerAdapter {

    private String[] tabArray;

    private Integer numberOfTabs;

    /**
     * Konstruktor pro pomocnou třídu, která zajištuje přepnutí
     * mezi dvěma fragmenty.
     *
     * @param fm           fragment manager
     * @param titles       názvy fragmentů
     * @param numberOfTabs počet fragramntů
     */
    public TabPagerTraderAdapter(FragmentManager fm, String[] titles, int numberOfTabs) {
        super(fm);
        this.tabArray = titles;
        this.numberOfTabs = numberOfTabs;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabArray[position];
    }

    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0:
                return new TraderShowFragment();
            case 1:
                return new NoteFragment();
        }


        return null;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}

