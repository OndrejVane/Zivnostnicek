package com.example.ondrejvane.zivnostnicek.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.ondrejvane.zivnostnicek.activities.note.NoteFragment;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderShowFragment;

public class TabPagerTraderAdapter extends FragmentStatePagerAdapter{

    private String[] tabArray= {"Informace", "Hodnocení"};

    private Integer numberOfTabs = 2;

    public TabPagerTraderAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabArray[position];
    }

    @Override
    public Fragment getItem(int position) {


        switch (position){
            case 0:
                return new TraderShowFragment();
            case 1:
                NoteFragment noteFragment = new NoteFragment();
                return noteFragment;
        }


        return null;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}

