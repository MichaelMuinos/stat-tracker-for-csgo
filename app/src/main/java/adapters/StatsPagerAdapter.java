package adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import net.pojo.AllCSGOAchievementInfo;
import net.pojo.CSGOProfile;
import net.pojo.Player;
import net.pojo.WeaponInfo;

import java.util.List;
import java.util.Map;

import fragments.AchievementFragment;
import fragments.LastMatchFragment;
import fragments.MapsFragment;
import fragments.ProfileFragment;
import fragments.WeaponsFragment;

public class StatsPagerAdapter extends FragmentPagerAdapter {

    private String[] tabTitles = {"Profile", "Weapon Stats", "Map Stats", "Last Match Stats", "Achievements"};

    private Player csgoProfile;
    private Map<String,Float> statMap;
    private Map<String,String> weaponInfoPictureMap;
    private Map<Integer,WeaponInfo> weaponInfoMap;
    private List<AllCSGOAchievementInfo> achievementList;

    public StatsPagerAdapter(FragmentManager fm, Player csgoProfile,
                             Map<String,Float> statMap,
                             Map<String,String> weaponInfoPictureMap,
                             Map<Integer,WeaponInfo> weaponInfoMap,
                             List<AllCSGOAchievementInfo> achievementList) {
        super(fm);
        this.statMap = statMap;
        this.csgoProfile = csgoProfile;
        this.weaponInfoPictureMap = weaponInfoPictureMap;
        this.weaponInfoMap = weaponInfoMap;
        this.achievementList = achievementList;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ProfileFragment.newInstance(csgoProfile, statMap);
            case 1:
                return WeaponsFragment.newInstance(statMap, weaponInfoPictureMap);
            case 2:
                return MapsFragment.newInstance(statMap);
            case 3:
                return LastMatchFragment.newInstance(statMap, weaponInfoMap);
            case 4:
                return AchievementFragment.newInstance(achievementList);
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
