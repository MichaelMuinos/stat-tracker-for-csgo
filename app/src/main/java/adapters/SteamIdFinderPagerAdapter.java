package adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import fragments.PastSearchFragment;
import fragments.SteamIdFinderFragment;

public class SteamIdFinderPagerAdapter extends FragmentStatePagerAdapter {

    private String[] tabTitles = {"Search", "Past Searches"};

    public SteamIdFinderPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public int getItemPosition(Object object) {
        // POSITION_NONE makes it possible to reload the PagerAdapter
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SteamIdFinderFragment();
            case 1:
                return new PastSearchFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on tab title position
        return tabTitles[position];
    }

}
