package fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.justplaingoatappsgmail.csgostatsviewer.R;

import net.AppConstants;
import net.pojo.Weapon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adapters.WeaponArrayAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

public class WeaponsFragment extends Fragment {

    @BindView(R.id.weaponsListView) ListView listView;
    @BindView(R.id.weaponEditText) EditText weaponEditText;

    private static final String WEAPONS_FRAGMENT_STAT_MAP = "WEAPONS_FRAGMENT_STAT_MAP";
    private static final String WEAPONS_FRAGMENT_PICTURE_MAP = "WEAPONS_FRAGMENT_PICTURE_MAP";
    private static final String WEAPON_KILL_NAME_PREFIX = "total_kills_";
    private static final String WEAPON_HIT_NAME_PREFIX = "total_hits_";
    private static final String WEAPON_SHOT_NAME_PREFIX = "total_shots_";

    private String[] weaponNames = {"deagle", "glock", "elite", "fiveseven", "awp", "ak47", "aug",
            "famas", "g3sg1", "p90", "mac10", "ump45", "xm1014", "m249", "hkp2000", "p250", "sg556",
            "scar20", "ssg08", "mp7", "mp9", "nova", "negev", "sawedoff", "bizon", "tec9", "mag7",
            "m4a1", "galilar"};
    private Map<String,Float> statMap;
    private Map<String,String> weaponInfoPictureMap;
    private List<Weapon> weaponList;
    private WeaponArrayAdapter weaponArrayAdapter;

    public static WeaponsFragment newInstance(Map<String,Float> statMap, Map<String,String> weaponInfoPictureMap) {
        Bundle args = new Bundle();
        args.putSerializable(WEAPONS_FRAGMENT_STAT_MAP, (Serializable) statMap);
        args.putSerializable(WEAPONS_FRAGMENT_PICTURE_MAP, (Serializable) weaponInfoPictureMap);

        // create Weapons Fragment object
        WeaponsFragment weaponsFragment = new WeaponsFragment();
        weaponsFragment.setArguments(args);
        return weaponsFragment;
    }

    @OnTextChanged(value = R.id.weaponEditText,
                callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void weaponEditTextChanged(CharSequence charSequence) {
        weaponArrayAdapter.getFilter().filter(charSequence.toString());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statMap = (Map<String, Float>) getArguments().getSerializable(WEAPONS_FRAGMENT_STAT_MAP);
        weaponInfoPictureMap = (Map<String,String>) getArguments().getSerializable(WEAPONS_FRAGMENT_PICTURE_MAP);
        weaponList = new ArrayList<>();

        // Populate weapon list array
        for(int i = 0; i < weaponNames.length; i++) {
            String weaponKillName = WEAPON_KILL_NAME_PREFIX + weaponNames[i];
            String weaponHitName = WEAPON_HIT_NAME_PREFIX + weaponNames[i];
            String weaponShotName = WEAPON_SHOT_NAME_PREFIX + weaponNames[i];

            float weaponKillValue = statMap.get(weaponKillName);
            float weaponHitValue = statMap.get(weaponHitName);
            float weaponShotValue = statMap.get(weaponShotName);

            // create weapon object
            Weapon weapon = new Weapon(weaponNames[i].toUpperCase(), weaponInfoPictureMap.get(weaponNames[i]), weaponKillValue,
                    weaponHitValue, weaponShotValue);
            // add to our weapon list
            weaponList.add(weapon);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weapons_fragment_layout, container, false);
        // bind views
        ButterKnife.bind(this, view);
        // set onKeyListener for the edit text
        weaponEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    AppConstants.hideSoftKeyboard(getContext(), weaponEditText);
                    return true;
                }
                return false;
            }
        });
        // Create Weapon array adapter class
        weaponArrayAdapter = new WeaponArrayAdapter(getContext(), weaponList);
        // set list view adapter
        listView.setAdapter(weaponArrayAdapter);
        // set on scroll listener
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_FLING || scrollState == SCROLL_STATE_TOUCH_SCROLL)
                    weaponEditText.setVisibility(View.GONE);
                else
                    weaponEditText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Do nothing
            }
        });
        // set text filter to be true
        listView.setTextFilterEnabled(true);
        listView.setFastScrollEnabled(true);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setScrollingCacheEnabled(false);
        return view;
    }

}
