package activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.justplaingoatappsgmail.csgostatsviewer.R;

import net.AppConstants;
import net.RestClient;
import net.pojo.CSGOProfile;
import net.pojo.Player;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.PastSearchDatabase;
import database.models.PastSearch;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PastSearchHoldActivity extends Activity {

    @BindView(R.id.popupName) TextView popupName;
    @BindView(R.id.popupProgressBar) BootstrapProgressBar progressBar;

    private PastSearch pastSearch;
    private DisplayMetrics displayMetrics;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.past_search_popup);

        ButterKnife.bind(this);

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Bundle bundle = getIntent().getExtras();
        pastSearch = (PastSearch) bundle.getSerializable(AppConstants.PAST_SEARCH);
        changeWindowSize(.8, .21);

        // set popup name
        popupName.setText(pastSearch.getGamerTag());
    }

    private void changeWindowSize(double widthFactor, double heightFactor) {
        int width = (int) (displayMetrics.widthPixels * widthFactor);
        int height = (int) (displayMetrics.heightPixels * heightFactor);
        getWindow().setLayout(width, height);
    }

    @OnClick(R.id.deleteButton)
    public void onDeleteButtonClick() {
        PastSearchDatabase pastSearchDatabase = PastSearchDatabase.getInstance(getApplicationContext());
        pastSearchDatabase.deletePastSearch(pastSearch.getSteamId());
        // create intent to send back
        Intent returnIntent = new Intent();
        returnIntent.putExtra(AppConstants.POPUP_CHOICE, AppConstants.POPUP_DELETE_CHOICE);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @OnClick(R.id.refreshButton)
    public void onRefreshButtonClick() {
        // change our display metrics
        changeWindowSize(.8, .25);
        // set progress bar to be visible
        progressBar.setVisibility(View.VISIBLE);
        // Grab updated results by calling get profile
        RestClient.getSteamApiInterface().getProfile(AppConstants.STEAM_API_KEY, pastSearch.getSteamId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<CSGOProfile>() {
                    @Override
                    public void call(CSGOProfile csgoProfile) {
                        // 0 + 25 = 25
                        progressBar.setProgress(progressBar.getProgress() + 25);
                    }
                })
                .delay(50, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .doOnNext(new Action1<CSGOProfile>() {
                    @Override
                    public void call(CSGOProfile csgoProfile) {
                        // 25 + 25 = 50
                        progressBar.setProgress(progressBar.getProgress() + 25);
                    }
                })
                .delay(50, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .doOnNext(new Action1<CSGOProfile>() {
                    @Override
                    public void call(CSGOProfile csgoProfile) {
                        // 25 + 50 = 75 (max)
                        progressBar.setProgress(progressBar.getProgress() + 25);
                    }
                })
                .delay(400, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CSGOProfile>() {
                    @Override
                    public void onCompleted() {
                        progressBar.setVisibility(View.GONE);
                        progressBar.setProgress(0);
                        // return result
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(AppConstants.POPUP_CHOICE, AppConstants.POPUP_REFRESH_CHOICE);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.GONE);
                        progressBar.setProgress(0);
                        finish();
                    }

                    @Override
                    public void onNext(CSGOProfile csgoProfile) {
                        PastSearchDatabase pastSearchDatabase = PastSearchDatabase.getInstance(getApplicationContext());
                        Player player = csgoProfile.getResponse().getPlayers().get(0);
                        PastSearch updatedPastSearch = new PastSearch(pastSearch.getSteamId(),
                                player.getAvatar(), player.getPersonaname(), player.getLastlogoff());
                        pastSearchDatabase.addPastSearch(updatedPastSearch);
                    }
                });
    }

}
