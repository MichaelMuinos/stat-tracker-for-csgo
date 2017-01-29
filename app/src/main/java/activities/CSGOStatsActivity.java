package activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.justplaingoatappsgmail.csgostatsviewer.R;

import net.AppConstants;
import net.RestClient;
import net.SteamApiService;
import net.pojo.Achievement;
import net.pojo.AllCSGOAchievementInfo;
import net.pojo.AllCSGOPojo;
import net.pojo.AvailableAchievement;
import net.pojo.AvailableStat;
import net.pojo.CSGOAchievements;
import net.pojo.CSGOEconSchema;
import net.pojo.CSGOGlobalAchievements;
import net.pojo.CSGOProfile;
import net.pojo.CSGOStatSchema;
import net.pojo.CSGOStats;
import net.pojo.GlobalAchievement;
import net.pojo.Player;
import net.pojo.PlayerStats;
import net.pojo.Stat;
import net.pojo.WeaponInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import adapters.StatsPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import database.PastSearchDatabase;
import database.models.PastSearch;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func5;
import rx.functions.Func6;
import rx.schedulers.Schedulers;

public class CSGOStatsActivity extends AppCompatActivity {

    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.sliding_tabs) TabLayout tabLayout;
    @BindView(R.id.statsProgressBar) BootstrapProgressBar progressBar;
    @BindView(R.id.statsErrorMessage) TextView statsErrorMessage;

    private static final String WEAPON_SCHEMA_NAME_PREFIX = "weapon_";

    // used for storing all stat values
    private Map<String,Float> statMap;
    // used for storing all weapon info (id, name, picture)
    private Map<Integer,WeaponInfo> weaponInfoMap;
    // used for storing all weapon pictures
    private Map<String,String> weaponInfoPictureMap;
    // used to collect all achievement info
    private List<AllCSGOAchievementInfo> achievementList;

    private Player csgoProfile;
    private String steamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_view_pager_layout);

        // set back button for action bar
        setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.parentColor)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        // set steam id and profile info
        Bundle bundle = getIntent().getExtras();
        steamId = bundle.getString(AppConstants.STEAM_ID);
        csgoProfile = (Player) bundle.getSerializable(AppConstants.PROFILE);

        statMap = new HashMap<>();
        weaponInfoMap = new HashMap<>();
        weaponInfoPictureMap = new HashMap<>();
        achievementList = new ArrayList<>();

        grabAllCSGOInformation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void grabAllCSGOInformation() {
        // Create Api Service object to make appropriate REST calls
        SteamApiService steamApiService = RestClient.getSteamApiInterface();

        // Create stats observable
        Observable<CSGOStats> statsObservable = steamApiService
                .getStats(AppConstants.CS_GO_APP_ID, AppConstants.STEAM_API_KEY, steamId);

        // Create weapon info observable
        Observable<CSGOEconSchema> weaponInfoObservable = steamApiService
                .getWeaponIdInfo(AppConstants.STEAM_API_KEY);

        // Create all stats available observable
        Observable<CSGOStatSchema> csgoStatSchemaObservable = steamApiService
                .getAllAvailableCSGOInfo(AppConstants.STEAM_API_KEY, AppConstants.CS_GO_APP_ID);

        // Create user achievements observable
        Observable<CSGOAchievements> csgoAchievementsObservable = steamApiService
                .getAllAchievements(AppConstants.CS_GO_APP_ID, AppConstants.STEAM_API_KEY, steamId);

        // Create global user achievements observable
        Observable<CSGOGlobalAchievements> csgoGlobalAchievementsObservable = steamApiService
                .getGlobalAchievementPercentages(AppConstants.CS_GO_APP_ID);

        // Zip above observables into one observable contained
        // by the POJO class AllCSGOPojo
        final Observable<AllCSGOPojo> allCSGOPojoObservable = Observable
                .zip(statsObservable, weaponInfoObservable, csgoStatSchemaObservable,
                        csgoAchievementsObservable, csgoGlobalAchievementsObservable,
                        new Func5<CSGOStats, CSGOEconSchema, CSGOStatSchema,
                                                        CSGOAchievements, CSGOGlobalAchievements, AllCSGOPojo>() {
                    @Override
                    public AllCSGOPojo call(CSGOStats csgoStats,
                                            CSGOEconSchema econSchema, CSGOStatSchema statSchema,
                                            CSGOAchievements achievements, CSGOGlobalAchievements globalAchievements) {
                        return new AllCSGOPojo(csgoStats, econSchema,
                                achievements, statSchema, globalAchievements);
                    }
        });

        allCSGOPojoObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<AllCSGOPojo>() {
                    @Override
                    public void call(AllCSGOPojo allCSGOPojo) {
                        // 50 + 50 = 100 (max)
                        progressBar.setProgress(progressBar.getProgress() + 50);
                    }
                })
                // delay our emission of our observable so that the progress bar updates to the max.
                // put delay on main thread
                .delay(400, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AllCSGOPojo>() {
            @Override
            public void onCompleted() {
                progressBar.setVisibility(View.GONE);
                progressBar.setProgress(50);
                // Get the ViewPager and set it's PagerAdapter so that it can display items
                StatsPagerAdapter spa = new StatsPagerAdapter(getSupportFragmentManager(),
                        csgoProfile,
                        statMap,
                        weaponInfoPictureMap,
                        weaponInfoMap,
                        achievementList);
                viewPager.setAdapter(spa);
                // set tab layout to be scrollable so that the text does not cut off
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                // set tab layout text colors: 1st parameter = default color, 2nd parameter = chosen tab color
                tabLayout.setTabTextColors(ContextCompat.getColor(getApplicationContext(), R.color.whiteColor),
                        ContextCompat.getColor(getApplicationContext(), R.color.alphaColor));
                // Give the TabLayout the ViewPager
                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onError(Throwable e) {
                // set our error message to be visible
                statsErrorMessage.setVisibility(View.VISIBLE);
                // set progress bar to be gone
                progressBar.setVisibility(View.GONE);
                progressBar.setProgress(50);
            }

            @Override
            public void onNext(AllCSGOPojo allCSGOPojo) {
                // set weapon info maps to pass to stats pager adapter
                List<WeaponInfo> weaponInfoList = allCSGOPojo.getCsgoEconSchema().getResult().getItems();
                for(WeaponInfo weaponInfo : weaponInfoList) {
                    weaponInfoMap.put(weaponInfo.getDefindex(), weaponInfo);
                    if(weaponInfo.getName().startsWith(WEAPON_SCHEMA_NAME_PREFIX)) {
                        String weaponInfoName = weaponInfo.getName().substring(WEAPON_SCHEMA_NAME_PREFIX.length());
                        weaponInfoPictureMap.put(weaponInfoName, weaponInfo.getImage_url());
                    }
                }

                // save all available stats in stat map
                List<AvailableStat> availableStats = allCSGOPojo.getCsgoStatSchema().getGame().getAvailableGameStats().getStats();
                for(AvailableStat availableStat : availableStats)
                    statMap.put(availableStat.getName(), availableStat.getDefaultvalue());

                // save all user stats to statmap to override default values
                PlayerStats playerStats = allCSGOPojo.getCsgoStats().getPlayerStats();
                List<Stat> statList = playerStats.getStats();
                // Save all key/value pairs
                for(Stat stat : statList)
                    statMap.put(stat.getName(), stat.getValue());

                // create map to allow all achievement info to be grouped together
                Map<String,AllCSGOAchievementInfo> achievementInfoMap = new HashMap<>();

                // save all achievement user information in the achievement info hashmap
                // save available achievements info
                List<AvailableAchievement> availableAchievements = allCSGOPojo.getCsgoStatSchema().getGame().getAvailableGameStats().getAchievements();
                for(AvailableAchievement availableAchievement : availableAchievements) {
                    AllCSGOAchievementInfo achievementInfo = new AllCSGOAchievementInfo(availableAchievement, null, null);
                    achievementInfoMap.put(achievementInfo.getAvailableAchievement().getName(), achievementInfo);
                }

                // save user achievement info
                List<Achievement> achievements = allCSGOPojo.getCsgoAchievements().getPlayerstats().getAchievements();
                for(Achievement achievement : achievements) {
                    AllCSGOAchievementInfo achievementInfo = achievementInfoMap.get(achievement.getApiname());
                    achievementInfo.setAchievement(achievement);
                    achievementInfoMap.put(achievement.getApiname(), achievementInfo);
                }

                // save global achievement info
                List<GlobalAchievement> globalAchievements = allCSGOPojo.getCsgoGlobalAchievements().getAchievementpercentages().getAchievements();
                for(GlobalAchievement globalAchievement : globalAchievements) {
                    AllCSGOAchievementInfo achievementInfo = achievementInfoMap.get(globalAchievement.getName());
                    // must handle the edge case where api name is empty, thus causing
                    // achievement info to be null
                    if(achievementInfo != null) {
                        achievementInfo.setGlobalAchievement(globalAchievement);
                        achievementInfoMap.put(globalAchievement.getName(), achievementInfo);
                    }
                }

                achievementList.addAll(achievementInfoMap.values());
            }
        });
    }

}
