package net;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.justplaingoatappsgmail.csgostatsviewer.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import sharedpreferences.SharedPref;

public class AppConstants {
    public static final String STEAM_API_KEY = "PRIVATE API KEY";
    public static final int CS_GO_APP_ID = 730;
    public static final String BASE_ADDRESS = "http://api.steampowered.com/";
    public static final int COMMUNITY_VISIBILITY_PUBLIC = 3;
    public static final int STEAM_ID_FOUND_SUCCESS = 1;
    public static final String STEAM_ID = "STEAM_ID";
    public static final String PROFILE = "PROFILE";
    public static final String PAST_SEARCH = "PAST_SEARCH";
    public static final String POPUP_CHOICE = "POPUP_CHOICE";
    public static final int POPUP_DELETE_CHOICE = 0;
    public static final int POPUP_REFRESH_CHOICE = 1;

    public static final String TOTAL_KILLS = "total_kills";
    public static final String TOTAL_DEATHS = "total_deaths";
    public static final String TOTAL_TIME_PLAYED = "total_time_played";
    public static final String TOTAL_WINS = "total_wins";
    public static final String TOTAL_KILLS_HEADSHOT = "total_kills_headshot";
    public static final String TOTAL_SHOTS_HIT = "total_shots_hit";
    public static final String TOTAL_SHOTS_FIRED = "total_shots_fired";
    public static final String TOTAL_ROUNDS_PLAYED = "total_rounds_played";
    public static final String LAST_MATCH_T_WINS = "last_match_t_wins";
    public static final String LAST_MATCH_CT_WINS = "last_match_ct_wins";
    public static final String LAST_MATCH_WINS = "last_match_wins";
    public static final String LAST_MATCH_KILLS = "last_match_kills";
    public static final String LAST_MATCH_DEATHS = "last_match_deaths";
    public static final String LAST_MATCH_MVPS = "last_match_mvps";
    public static final String LAST_MATCH_FAVWEAPON_ID = "last_match_favweapon_id";
    public static final String LAST_MATCH_FAVWEAPON_SHOTS = "last_match_favweapon_shots";
    public static final String LAST_MATCH_FAVWEAPON_HITS = "last_match_favweapon_hits";
    public static final String LAST_MATCH_FAVWEAPON_KILLS = "last_match_favweapon_kills";
    public static final String LAST_MATCH_DAMAGE = "last_match_damage";
    public static final String LAST_MATCH_MONEY_SPENT = "last_match_money_spent";
    public static final String TOTAL_MVPS = "total_mvps";

    // remove virtual keyboard from view
    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // Round float number to a certain decimal place and convert to string
    public static String round(float d, int decimalPlace) {
        if(Float.isNaN(d)) return "0";
        return Float.toString(new BigDecimal(Float.toString(d)).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue());
    }

    // Round float number to contain no decimal and convert string to contain commas
    public static String round(float d) {
        DecimalFormat formatter = new DecimalFormat("#,###,###,###,###");
        return formatter.format(d);
    }

    public static String getPercentage(float d, int decimalPlace) {
        float proportion = Float.valueOf(round(d * 100, decimalPlace));
        return Float.toString(proportion) + " %";
    }

    // method to create snackbar anywhere in the app
    public static void createSnackbar(Context context, CoordinatorLayout cl,
                                      String message, final SharedPref sharedPref, final String constant) {
        Snackbar snackbar = Snackbar
                .make(cl, message, Snackbar.LENGTH_INDEFINITE)
                // set snackbar callback to determine if the view has been swiped away
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        sharedPref.addValue(constant, true);
                    }
                })
                .setAction("X", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sharedPref.addValue(constant, true);
                    }
                });
        // set color of our "X"
        snackbar.setActionTextColor(Color.RED);
        // set text color for snackbar message
        View snackbarView = snackbar.getView();
        TextView snackbarTextView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        snackbarTextView.setTextColor(ContextCompat.getColor(context, R.color.alphaColor));
        // show snackbar
        snackbar.show();
    }

}
