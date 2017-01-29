package activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.justplaingoatappsgmail.csgostatsviewer.R;

import adapters.SteamIdFinderPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnPageChange;

public class SteamIdFinderActivity extends AppCompatActivity {

    @BindView(R.id.steamIdViewPager) ViewPager steamIdViewPager;
    @BindView(R.id.steamIdSlidingTabs) TabLayout steamIdSlidingTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steam_id_view_pager_layout);

        setTitle(getResources().getString(R.string.app_name));
        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(this, R.color.parentColor));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        ButterKnife.bind(this);

        SteamIdFinderPagerAdapter adapter = new SteamIdFinderPagerAdapter(getSupportFragmentManager());
        steamIdViewPager.setAdapter(adapter);
        steamIdSlidingTabs.setTabGravity(TabLayout.GRAVITY_FILL);
        steamIdSlidingTabs.setTabMode(TabLayout.MODE_FIXED);
        // set tab layout text colors: 1st parameter = default color, 2nd parameter = chosen tab color
        steamIdSlidingTabs.setTabTextColors(ContextCompat.getColor(getApplicationContext(), R.color.whiteColor),
                ContextCompat.getColor(getApplicationContext(), R.color.alphaColor));
        // Give the TabLayout the ViewPager
        steamIdSlidingTabs.setupWithViewPager(steamIdViewPager);
    }

    @OnPageChange(value = R.id.steamIdViewPager,
                    callback = OnPageChange.Callback.PAGE_SELECTED)
    public void onPageSelected() {
        steamIdViewPager.getAdapter().notifyDataSetChanged();
    }

}
