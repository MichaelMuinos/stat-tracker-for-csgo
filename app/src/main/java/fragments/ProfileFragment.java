package fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.justplaingoatappsgmail.csgostatsviewer.R;
import com.squareup.picasso.Picasso;

import net.AppConstants;
import net.pojo.CSGOProfile;
import net.pojo.Player;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    @BindView(R.id.gamerTag) TextView gamerTag;
    @BindView(R.id.mvpTotal) TextView mvpTotal;
    @BindView(R.id.playTime) TextView playTime;
    @BindView(R.id.lastLogOff) TextView lastLogOff;
    @BindView(R.id.kdr) TextView kdr;
    @BindView(R.id.kills) TextView kills;
    @BindView(R.id.deaths) TextView deaths;
    @BindView(R.id.wlr) TextView wlr;
    @BindView(R.id.wins) TextView wins;
    @BindView(R.id.losses) TextView losses;
    @BindView(R.id.accuracyRatio) TextView accuracyRatio;
    @BindView(R.id.shotsFired) TextView shotsFired;
    @BindView(R.id.shotsHit) TextView shotsHit;
    @BindView(R.id.headshotRatio) TextView headshotRatio;
    @BindView(R.id.killsTwo) TextView killsTwo;
    @BindView(R.id.headshotKills) TextView headshotKills;
    @BindView(R.id.profilePicture) ImageView picture;

    private static final String PROFILE_FRAGMENT = "PROFILE_FRAGMENT";
    private static final String PROFILE_POJO = "PROFILE_POJO";
    private static final String PROFILE_MVP_SUFFIX = "Mvps";
    private static final String PROFILE_HOURS_SUFFIX = "Hrs";

    private Player CSGOProfile;
    private Map<String,Float> statMap;
    private static Gson gson;

    public static ProfileFragment newInstance(Player CSGOProfile, Map<String,Float> stats) {
        Bundle args = new Bundle();
        gson = new Gson();
        args.putSerializable(PROFILE_FRAGMENT, (Serializable) stats);
        args.putString(PROFILE_POJO, gson.toJson(CSGOProfile));

        // create CSGOProfile Fragment object
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String profileObjectString = getArguments().getString(PROFILE_POJO);
        CSGOProfile = gson.fromJson(profileObjectString, Player.class);
        statMap = (HashMap<String, Float>) getArguments().getSerializable(PROFILE_FRAGMENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment_layout, container, false);

        // Bind views
        ButterKnife.bind(this, view);

        // create foreground color span to set the appropriate color
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.limeColor));

        // set gamer tag text view
        gamerTag.setText(CSGOProfile.getPersonaname());

        // get mvp total
        String mvpString = AppConstants.round(statMap.get(AppConstants.TOTAL_MVPS)) + " " + PROFILE_MVP_SUFFIX;
        // spannable used to set the color of part of the text view
        Spannable mvpSpannable = new SpannableString(mvpString);
        // set our span location for the spannable object
        mvpSpannable.setSpan(foregroundColorSpan,
                mvpString.length() - PROFILE_MVP_SUFFIX.length(),
                mvpString.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        // set mvp text view
        mvpTotal.setText(mvpSpannable);

        // set last log off
        float epoch = Float.parseFloat(String.valueOf(CSGOProfile.getLastlogoff()));
        Date date = new Date((long) epoch * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // the format of the date
        String formattedDate = sdf.format(date);
        lastLogOff.setText(formattedDate);

        int dimensions = (int) getResources().getInteger(R.integer.profile_pic);
        // set picture with picasso
        Picasso.with(getContext())
                .load(Uri.parse(CSGOProfile.getAvatar()))
                .resize(dimensions, dimensions)
                .into(picture);

        // set total hours played text view
        int hours = Math.round(statMap.get(AppConstants.TOTAL_TIME_PLAYED) / 3600);
        String playTimeString = AppConstants.round(hours) + " " + PROFILE_HOURS_SUFFIX;
        Spannable playTimeSpannable = new SpannableString(playTimeString);
        playTimeSpannable.setSpan(foregroundColorSpan,
                playTimeString.length() - PROFILE_HOURS_SUFFIX.length(),
                playTimeString.length(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        playTime.setText(playTimeSpannable);

        // set kdr text view
        float kdrValue = statMap.get(AppConstants.TOTAL_KILLS) / (statMap.get(AppConstants.TOTAL_DEATHS));
        kdr.setText(AppConstants.round(kdrValue, 2));

        // set kills text view
        kills.setText(AppConstants.round(statMap.get(AppConstants.TOTAL_KILLS)));

        // set deaths text view
        deaths.setText(AppConstants.round(statMap.get(AppConstants.TOTAL_DEATHS)));

        // set wlr text view
        float wlrValue = statMap.get(AppConstants.TOTAL_WINS) / statMap.get(AppConstants.TOTAL_ROUNDS_PLAYED);
        wlr.setText(AppConstants.getPercentage(wlrValue, 1));

        // set total wins
        wins.setText(AppConstants.round(statMap.get(AppConstants.TOTAL_WINS)));

        // set total losses
        float lossesValue = statMap.get(AppConstants.TOTAL_ROUNDS_PLAYED) - statMap.get(AppConstants.TOTAL_WINS);
        losses.setText(AppConstants.round(lossesValue));

        // set accuracy ratio
        float accuracyValue = statMap.get(AppConstants.TOTAL_SHOTS_HIT) / statMap.get(AppConstants.TOTAL_SHOTS_FIRED);
        accuracyRatio.setText(AppConstants.getPercentage(accuracyValue, 1));

        // set shots hit text view
        shotsHit.setText(AppConstants.round(statMap.get(AppConstants.TOTAL_SHOTS_HIT)));

        // set shots fired text view
        shotsFired.setText(AppConstants.round(statMap.get(AppConstants.TOTAL_SHOTS_FIRED)));

        // set headshots ratio
        float headshotValue = statMap.get(AppConstants.TOTAL_KILLS_HEADSHOT) / statMap.get(AppConstants.TOTAL_KILLS);
        headshotRatio.setText(AppConstants.getPercentage(headshotValue, 1));

        // set killsTwo text view
        killsTwo.setText(AppConstants.round(statMap.get(AppConstants.TOTAL_KILLS)));

        // set headshot kills text view
        headshotKills.setText(AppConstants.round(statMap.get(AppConstants.TOTAL_KILLS_HEADSHOT)));

        return view;
    }
}
