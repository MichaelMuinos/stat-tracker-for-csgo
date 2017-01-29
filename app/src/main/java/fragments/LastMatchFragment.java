package fragments;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.justplaingoatappsgmail.csgostatsviewer.R;
import com.squareup.picasso.Picasso;

import net.AppConstants;
import net.pojo.WeaponInfo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LastMatchFragment extends Fragment {

    @BindView(R.id.ctMatchWins) TextView ctRoundWins;
    @BindView(R.id.tMatchWins) TextView tRoundWins;
    @BindView(R.id.lastMatchStatus) TextView lastMatchStatus;
    @BindView(R.id.lastMatchWeaponPicture) ImageView lastMatchFavWeaponPicture;
    @BindView(R.id.lastMatchFavoriteWeaponName) TextView favWeaponName;
    @BindView(R.id.lastMatchWeaponKills) TextView favWeaponKills;
    @BindView(R.id.lastMatchWeaponHits) TextView favWeaponHits;
    @BindView(R.id.lastMatchWeaponShots) TextView favWeaponShots;
    @BindView(R.id.lastMatchWeaponAccuracy) TextView favWeaponAccuracy;
    @BindView(R.id.lastMatchKDRRatio) TextView lastMatchKDR;
    @BindView(R.id.lastMatchKills) TextView lastMatchKills;
    @BindView(R.id.lastMatchDeaths) TextView lastMatchDeaths;
    @BindView(R.id.lastMatchMVPCount) TextView lastMatchMVPCount;
    @BindView(R.id.lastMatchMoneySpent) TextView lastMatchMoneySpent;
    @BindView(R.id.lastMatchDamage) TextView lastMatchDamage;

    private static final String LAST_MATCH_FRAGMENT_STAT_MAP = "LAST_MATCH_FRAGMENT_STAT_MAP";
    private static final String LAST_MATCH_FRAGMENT_WEAPON_MAP = "LAST_MATCH_FRAGMENT_WEAPON_MAP";
    private static final String WEAPON_ID_NAME_PREFIX = "weapon_";

    private Map<String,Float> statMap;
    private Map<Integer,WeaponInfo> weaponInfoMap;

    private enum MatchOutcome {
        WIN,
        LOSS,
        DRAW
    }

    public static LastMatchFragment newInstance(Map<String,Float> statMap, Map<Integer,WeaponInfo> weaponInfoMap) {
        Bundle args = new Bundle();
        args.putSerializable(LAST_MATCH_FRAGMENT_STAT_MAP, (Serializable) statMap);
        args.putSerializable(LAST_MATCH_FRAGMENT_WEAPON_MAP, (Serializable) weaponInfoMap);

        // create last match fragment
        LastMatchFragment lastMatchFragment = new LastMatchFragment();
        lastMatchFragment.setArguments(args);
        return lastMatchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statMap = (HashMap<String,Float>) getArguments().getSerializable(LAST_MATCH_FRAGMENT_STAT_MAP);
        weaponInfoMap = (HashMap<Integer,WeaponInfo>) getArguments().getSerializable(LAST_MATCH_FRAGMENT_WEAPON_MAP);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.last_match_fragment_layout, container, false);

        // Bind views
        ButterKnife.bind(this, view);

        float lastMatchWinsValue = statMap.get(AppConstants.LAST_MATCH_WINS);
        float lastMatchTSideWinsValue = statMap.get(AppConstants.LAST_MATCH_T_WINS);
        float lastMatchCTSideWinsValue = statMap.get(AppConstants.LAST_MATCH_CT_WINS);

        // block of ifs below checks which side won and sets the appropriate colors
        MatchOutcome matchOutcome;
        if(lastMatchCTSideWinsValue < lastMatchTSideWinsValue) {
            ctRoundWins.setTextColor(Color.RED);
            tRoundWins.setTextColor(Color.GREEN);
            // determine whether the user has won or lost
            matchOutcome = lastMatchWinsValue == lastMatchCTSideWinsValue ? MatchOutcome.LOSS : MatchOutcome.WIN;
        } else if(lastMatchCTSideWinsValue > lastMatchTSideWinsValue) {
            ctRoundWins.setTextColor(Color.GREEN);
            tRoundWins.setTextColor(Color.RED);
            // determine whether the user has won or lost
            matchOutcome = lastMatchWinsValue == lastMatchCTSideWinsValue ? MatchOutcome.WIN : MatchOutcome.LOSS;
        } else {
            ctRoundWins.setTextColor(Color.GREEN);
            tRoundWins.setTextColor(Color.GREEN);
            matchOutcome = MatchOutcome.DRAW;
        }

        // set match outcome text view color
        if(matchOutcome == MatchOutcome.WIN || matchOutcome == MatchOutcome.DRAW)
            lastMatchStatus.setTextColor(Color.GREEN);
        else
            lastMatchStatus.setTextColor(Color.RED);

        // set match outcome text
        lastMatchStatus.setText(matchOutcome.toString());

        // set ct round wins text view
        ctRoundWins.setText(AppConstants.round(lastMatchCTSideWinsValue));

        // set t round wins text view
        tRoundWins.setText(AppConstants.round(lastMatchTSideWinsValue));

        int weaponId = statMap.get(AppConstants.LAST_MATCH_FAVWEAPON_ID).intValue();
        WeaponInfo weaponIDInfo = weaponInfoMap.get(weaponId);

        if(weaponIDInfo != null) {
            // set weapon name
            favWeaponName.setText(weaponIDInfo.getName().substring(WEAPON_ID_NAME_PREFIX.length()).toUpperCase());
            // set weapon picture
            Picasso.with(getContext())
                    .load(Uri.parse(weaponIDInfo.getImage_url()))
                    .fit()
                    .into(lastMatchFavWeaponPicture);
        } else {
            favWeaponName.setText("N/A");
            Picasso.with(getContext())
                    .load(R.drawable.questionmark)
                    .fit()
                    .into(lastMatchFavWeaponPicture);
        }

        // set fav wep kills
        favWeaponKills.setText(AppConstants.round(statMap.get(AppConstants.LAST_MATCH_FAVWEAPON_KILLS)));

        float favWeaponHitsValue = statMap.get(AppConstants.LAST_MATCH_FAVWEAPON_HITS);
        float favWeaponShotsValue = statMap.get(AppConstants.LAST_MATCH_FAVWEAPON_SHOTS);

        // set fav wep hits
        favWeaponHits.setText(AppConstants.round(favWeaponHitsValue));

        // set fav wep shots
        favWeaponShots.setText(AppConstants.round(favWeaponShotsValue));

        // set fav wep accuracy
        favWeaponAccuracy.setText(AppConstants.getPercentage(favWeaponHitsValue / favWeaponShotsValue, 1));

        float lastMatchKillsValue = statMap.get(AppConstants.LAST_MATCH_KILLS);
        float lastMatchDeathsValue = statMap.get(AppConstants.LAST_MATCH_DEATHS);

        // set kdr ratio
        lastMatchKDR.setText(AppConstants.round(lastMatchKillsValue / lastMatchDeathsValue, 2));

        // set kills text view
        lastMatchKills.setText(AppConstants.round(lastMatchKillsValue));

        // set deaths text view
        lastMatchDeaths.setText(AppConstants.round(lastMatchDeathsValue));

        // set mvp text view
        lastMatchMVPCount.setText(AppConstants.round(statMap.get(AppConstants.LAST_MATCH_MVPS)));

        // set money text view
        lastMatchMoneySpent.setText(AppConstants.round(statMap.get(AppConstants.LAST_MATCH_MONEY_SPENT)));

        // set damage text view
        lastMatchDamage.setText(AppConstants.round(statMap.get(AppConstants.LAST_MATCH_DAMAGE)));

        return view;
    }

}
