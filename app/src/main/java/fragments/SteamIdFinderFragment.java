package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.justplaingoatappsgmail.csgostatsviewer.R;

import net.AppConstants;
import net.RestClient;
import net.SteamApiService;
import net.pojo.CSGOOwnedGames;
import net.pojo.CSGOProfile;
import net.pojo.CSGOSteamID;
import net.pojo.OwnedGame;
import net.pojo.Player;

import java.math.BigInteger;
import java.util.List;
import java.util.regex.Pattern;

import activities.CSGOStatsActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.PastSearchDatabase;
import database.models.PastSearch;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SteamIdFinderFragment extends Fragment {

    @BindView(R.id.editTextView) EditText editText;
    @BindView(R.id.steamIdFinderProgressBar) BootstrapProgressBar progressBar;
    @BindView(R.id.errorMessage) TextView errorMessage;
    @BindView(R.id.customNameExample) TextView customNameExample;
    @BindView(R.id.steamIdExample) TextView steamIdExample;
    @BindView(R.id.steam64IdExample) TextView steam64IdExample;
    @BindView(R.id.steamId3Example) TextView steamId3Example;

    private static final BigInteger STEAM_ID_TO_64_NUM = new BigInteger("76561197960265728");
    private static final Pattern STEAM_ID_REGEX = Pattern.compile("STEAM_[0-5]:[0-1]:\\d+");
    private static final Pattern STEAM_ID3_REGEX = Pattern.compile("U:1:\\d+");
    private static final Pattern STEAM_64_ID_REGEX = Pattern.compile("[0-9]+");
    private static final Pattern STEAM_URL_NAME_REGEX = Pattern.compile(".*[a-z].*");
    private static final String CUSTOM_NAME = "Custom URL Name";
    private static final String CUSTOM_NAME_EX = "JustPlainGoat";
    private static final String STEAM_ID_NAME = "SteamID";
    private static final String STEAM_ID_EX = "STEAM_0:1:7271678";
    private static final String STEAM_64_ID_NAME = "Steam64ID";
    private static final String STEAM_64_ID_NAME_EX = "76561197974809085";
    private static final String STEAM_ID3_NAME = "SteamID3";
    private static final String STEAM_ID3_EX = "U:1:14543357";

    private SteamApiService steamApiService;
    private String steamId;
    private Player profile;
    private ForegroundColorSpan limeColorSpan;
    private ForegroundColorSpan alphaColorSpan;

    private enum InputType {
        STEAM_ID,
        STEAM_64_ID,
        STEAM_ID_3,
        URL_NAME,
        BAD_INPUT
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        steamApiService = RestClient.getSteamApiInterface();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(errorMessage.getVisibility() == View.VISIBLE)
            errorMessage.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.steam_id_finder_fragment, container, false);
        // Bind views
        ButterKnife.bind(this, view);
        // set onKeyListener for the edit text
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    AppConstants.hideSoftKeyboard(getContext(), editText);
                    return true;
                }
                return false;
            }
        });
        // create foreground colors span to set the appropriate color
        limeColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.limeColor));
        alphaColorSpan = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.alphaColor));
        // set colors for text views
        setTextViewExampleColor(customNameExample, CUSTOM_NAME, CUSTOM_NAME_EX);
        setTextViewExampleColor(steamIdExample, STEAM_ID_NAME, STEAM_ID_EX);
        setTextViewExampleColor(steamId3Example, STEAM_ID3_NAME, STEAM_ID3_EX);
        setTextViewExampleColor(steam64IdExample, STEAM_64_ID_NAME, STEAM_64_ID_NAME_EX);
        return view;
    }

    private void setTextViewExampleColor(TextView textView, String name, String example) {
        // create spannable for text view example
        Spannable spannable = new SpannableString(textView.getText().toString());
        // get index of name and set span to me lime color
        int nameIndex = spannable.toString().indexOf(name);
        spannable.setSpan(limeColorSpan, nameIndex, name.length() + 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        int nameExIndex = spannable.toString().indexOf(example);
        spannable.setSpan(alphaColorSpan, nameExIndex, spannable.toString().length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(spannable);
    }

    @OnClick(R.id.searchButton)
    public void onSearchButtonClick() {
        String text = editText.getText().toString();
        AppConstants.hideSoftKeyboard(getContext(), editText);
        // determine what the user entered
        InputType type = determineInputType(text);
        switch(type) {
            case STEAM_ID:
                convertSteamIDTo64(text);
                break;
            case STEAM_64_ID:
                getPlayerProfile(text);
                break;
            case STEAM_ID_3:
                convertSteamID3To64(text);
                break;
            case URL_NAME:
                convertUrlNameAndGetPlayerProfile(text);
                break;
            default:
                // bad input
                errorMessage.setVisibility(View.VISIBLE);
                break;
        }
    }

    private InputType determineInputType(String input) {
        InputType type;
        if(input.matches(STEAM_ID_REGEX.pattern()))
            type = InputType.STEAM_ID;
        else if(input.matches(STEAM_ID3_REGEX.pattern()))
            type = InputType.STEAM_ID_3;
        else if(input.matches(STEAM_64_ID_REGEX.pattern()))
            type = InputType.STEAM_64_ID;
        else if(input.matches(STEAM_URL_NAME_REGEX.pattern()))
            type = InputType.URL_NAME;
        else
            type = InputType.BAD_INPUT;
        return type;
    }

    private void convertSteamIDTo64(String id) {
        // Splits steam id based on the colon
        // 1st arg = STEAM_0 (ignore it)
        // 2nd arg = [0-5] (number we add)
        // 3rd arg = d+ (number we double)
        String[] splitSteamId = id.split(":");

        // Formula for conversion: STEAM_0:A:B
        // 64 ID = 76561197960265728 + (3rd arg * 2) + 2nd arg
        BigInteger multiplyId = new BigInteger(splitSteamId[2]).multiply(new BigInteger("2"));
        steamId = STEAM_ID_TO_64_NUM.add(multiplyId).add(new BigInteger(splitSteamId[1])).toString();
        getPlayerProfile(steamId);
    }

    private void convertSteamID3To64(String id) {
        // Grab d+ portion of our expression
        int startIndex = id.indexOf(":", id.indexOf(":") + 1) + 1;
        BigInteger steamIdNumber = new BigInteger(id.substring(startIndex, id.length()));

        // Formula for conversion: [U:1:d+]
        // 64 ID = 76561197960265728 + d+;
        steamId = STEAM_ID_TO_64_NUM.add(steamIdNumber).toString();
        getPlayerProfile(steamId);
    }

    private void updateProgressBar(final int num) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int currentProgress = progressBar.getProgress();
                // if progress count is the start, set bar to visible
                if(currentProgress == 0) progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(currentProgress + num);
            }
        });
    }

    private void resetProgressBar() {
        progressBar.setVisibility(View.GONE);
        progressBar.setProgress(0);
    }

    private void convertUrlNameAndGetPlayerProfile(String urlName) {
        steamApiService.getSteamId(AppConstants.STEAM_API_KEY, urlName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<CSGOSteamID>() {
                    @Override
                    public void call(CSGOSteamID csgoSteamID) {
                        // 0 + 15 = 15
                        updateProgressBar(15);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<CSGOSteamID, Observable<?>>() {
                    @Override
                    public Observable<?> call(CSGOSteamID csgoSteamID) {
                        if(csgoSteamID.getResponse().getSuccess() == AppConstants.STEAM_ID_FOUND_SUCCESS) {
                            steamId = csgoSteamID.getResponse().getSteamid();
                            // URL Name was valid, retrieve steam 64 id and get profile
                            return steamApiService.getProfile(AppConstants.STEAM_API_KEY, steamId)
                                    .subscribeOn(Schedulers.io());
                        } else return Observable.error(new Exception());
                    }
                })
                .doOnNext(new Action1<Object>() {
                    @Override
                    public void call(Object object) {
                        // 15 + 15 = 30
                        updateProgressBar(15);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Object, Observable<?>>() {
                    @Override
                    public Observable<?> call(Object object) {
                        CSGOProfile csgoProfile = (CSGOProfile) object;
                        profile = csgoProfile.getResponse().getPlayers().get(0);
                        if(profile.getCommunityvisibilitystate() == AppConstants.COMMUNITY_VISIBILITY_PUBLIC) {
                            // Now check to see if they own CSGO, if they do, go to new activity
                            return steamApiService.getOwnedGames(AppConstants.STEAM_API_KEY, steamId)
                                    .subscribeOn(Schedulers.io());
                        } else return Observable.error(new Exception());
                    }
                })
                .doOnNext(new Action1<Object>() {
                    @Override
                    public void call(Object object) {
                        // 30 + 20 = 50 (max)
                        updateProgressBar(20);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());
    }

    private void getPlayerProfile(final String id) {
        steamApiService.getProfile(AppConstants.STEAM_API_KEY, id)
                .subscribeOn(Schedulers.io())
                .doOnNext(new Action1<CSGOProfile>() {
                    @Override
                    public void call(CSGOProfile csgoProfile) {
                        // 0 + 25 = 25
                        updateProgressBar(25);
                    }
                })
                .flatMap(new Func1<CSGOProfile, Observable<?>>() {
                    @Override
                    public Observable<?> call(CSGOProfile csgoProfile) {
                        profile = csgoProfile.getResponse().getPlayers().get(0);
                        if(profile.getCommunityvisibilitystate() == AppConstants.COMMUNITY_VISIBILITY_PUBLIC) {
                            // Now check to see if they own CSGO, if they do, go to new activity
                            steamId = id;
                            return steamApiService.getOwnedGames(AppConstants.STEAM_API_KEY, id)
                                    .subscribeOn(Schedulers.io());
                        } else return Observable.error(new Exception());
                    }
                })
                .doOnNext(new Action1<Object>() {
                    @Override
                    public void call(Object object) {
                        // 25 + 25 = 50 (max)
                        updateProgressBar(25);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());
    }

    private Subscriber<Object> getSubscriber() {
        final boolean[] ownsCSGO = {false};
        return new Subscriber<Object>() {
           @Override
           public void onCompleted() {
               resetProgressBar();
               if(ownsCSGO[0]) {
                   // add past search to database
                   PastSearchDatabase db = PastSearchDatabase.getInstance(getContext());
                   PastSearch pastSearch = new PastSearch(steamId, profile.getAvatar(),
                           profile.getPersonaname(), profile.getLastlogoff());
                   db.addPastSearch(pastSearch);

                   // start other activity
                   Intent intent = new Intent(getContext(), CSGOStatsActivity.class);
                   Bundle bundle = new Bundle();
                   bundle.putSerializable(AppConstants.PROFILE, profile);
                   bundle.putString(AppConstants.STEAM_ID, steamId);
                   intent.putExtras(bundle);
                   startActivity(intent);
               } else {
                   if(errorMessage.getVisibility() == View.GONE)
                       errorMessage.setVisibility(View.VISIBLE);
               }
           }

           @Override
           public void onError(Throwable e) {
               resetProgressBar();
               // Set our error message to visible
               errorMessage.setVisibility(View.VISIBLE);
           }

           @Override
           public void onNext(Object object) {
               CSGOOwnedGames csgoOwnedGames = (CSGOOwnedGames) object;
               List<OwnedGame> ownedGames = csgoOwnedGames.getResponse().getGames();
               for(OwnedGame ownedGame : ownedGames) {
                   if(ownedGame.getAppid() == AppConstants.CS_GO_APP_ID)
                       ownsCSGO[0] = true;
               }
           }
       };
    }

}
