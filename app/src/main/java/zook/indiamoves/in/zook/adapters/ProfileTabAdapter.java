package zook.indiamoves.in.zook.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import zook.indiamoves.in.zook.fragments.UserAboutFragment;
import zook.indiamoves.in.zook.fragments.UserAddressFragment;

/**
 * Created by Admin on 7/6/2016.
 */
public class ProfileTabAdapter extends FragmentStatePagerAdapter {


    public ProfileTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return new UserAboutFragment();
            case 1:
                return new UserAddressFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "ABOUT";
            case 1:
                return "ADDRESS";
        }
        return null;
    }
}