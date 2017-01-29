package fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.justplaingoatappsgmail.csgostatsviewer.R;

import net.pojo.CSGOMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adapters.CSGOMapArrayAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

public class MapsFragment extends Fragment {

    @BindView(R.id.mapsListView) ListView listView;
    @BindView(R.id.mapEditText) EditText mapEditText;

    private static final String MAPS_FRAGMENT = "MAPS_FRAGMENT";
    private static final String MAP_WIN_NAME_PREFIX = "total_wins_map_";
    private static final String MAP_ROUNDS_NAME_PREFIX = "total_rounds_map_";

    private CSGOMapArrayAdapter csgoMapArrayAdapter;

    private String[] mapNames = {"cs_assault", "cs_italy", "cs_office", "de_aztec", "de_cbble", "de_dust2",
            "de_dust", "de_inferno", "de_nuke", "de_train", "de_bank", "de_vertigo", "ar_monastery",
            "ar_shoots", "ar_baggage", "de_lake", "de_sugarcane", "de_stmarc", "de_shorttrain",
            "de_safehouse", "cs_militia"};

    private Map<String, Float> statMap;
    private List<CSGOMap> mapList;

    public static MapsFragment newInstance(Map<String, Float> statMap) {
        Bundle args = new Bundle();
        args.putSerializable(MAPS_FRAGMENT, (Serializable) statMap);

        // create maps fragment object
        MapsFragment mapsFragment = new MapsFragment();
        mapsFragment.setArguments(args);
        return mapsFragment;
    }

    @OnTextChanged(value = R.id.mapEditText,
            callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void weaponEditTextChanged(CharSequence charSequence) {
        csgoMapArrayAdapter.getFilter().filter(charSequence.toString());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statMap = (Map<String, Float>) getArguments().getSerializable(MAPS_FRAGMENT);
        mapList = new ArrayList<>();

        // Populate map list array
        for (int i = 0; i < mapNames.length; i++) {
            int mapDrawable = getResources().getIdentifier(mapNames[i], "drawable", getContext().getPackageName());
            String mapWins = MAP_WIN_NAME_PREFIX + mapNames[i];
            String mapRounds = MAP_ROUNDS_NAME_PREFIX + mapNames[i];

            float mapRoundsValue = statMap.get(mapRounds);
            float mapWinsValue = statMap.get(mapWins);

            // create map object
            CSGOMap csgoMap = new CSGOMap(mapNames[i].toUpperCase(), mapDrawable,
                    mapWinsValue, mapRoundsValue - mapWinsValue);
            // add to our map list
            mapList.add(csgoMap);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maps_fragment_layout, container, false);
        // bind views
        ButterKnife.bind(this, view);
        // Create map array adapter class
        csgoMapArrayAdapter = new CSGOMapArrayAdapter(getContext(), mapList);
        // set list view adapter
        listView.setAdapter(csgoMapArrayAdapter);
        // set on scroll listener
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_FLING || scrollState == SCROLL_STATE_TOUCH_SCROLL)
                    mapEditText.setVisibility(View.GONE);
                else
                    mapEditText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Do nothing
            }
        });
        listView.setFastScrollEnabled(true);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.setScrollingCacheEnabled(false);
        return view;
    }

}
