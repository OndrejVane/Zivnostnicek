package com.example.ondrejvane.zivnostnicek.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.ondrejvane.zivnostnicek.activities.home.HomeIncomeExpenseFragment;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeTradersFragment;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeVatFragment;
import com.example.ondrejvane.zivnostnicek.activities.home.HomeYearSummaryFragment;
import com.example.ondrejvane.zivnostnicek.activities.note.NoteFragment;
import com.example.ondrejvane.zivnostnicek.activities.trader.TraderShowFragment;

public class TabPagerHomeAdapter extends FragmentStatePagerAdapter{

    private String[] tabArray;

    private Integer numberOfTabs;

    public TabPagerHomeAdapter(FragmentManager fm, String[] titles, int numberOfTabs) {
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


        switch (position){
            case 0:
                return new HomeIncomeExpenseFragment();
            case 1:
                return new HomeYearSummaryFragment();
            case 2:
                return new HomeVatFragment();
            case 3:
                return new HomeTradersFragment();
        }


        return null;
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}

