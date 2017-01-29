package fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.justplaingoatappsgmail.csgostatsviewer.R;

import net.AppConstants;
import net.pojo.Player;

import java.util.List;

import activities.CSGOStatsActivity;
import activities.PastSearchHoldActivity;
import adapters.PastSearchArrayAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import butterknife.OnTextChanged;
import database.PastSearchDatabase;
import database.models.PastSearch;
import sharedpreferences.SharedPref;

public class PastSearchFragment extends Fragment {

    @BindView(R.id.pastSearchCoordinatorLayout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.pastSearchListView) ListView pastSearchListView;
    @BindView(R.id.playerEditText) EditText playerEditText;

    private static final int POPUP_CHOICE_CHOSEN = 1;
    private static final String PAST_SEARCH_PREF = "PAST_SEARCH_PREF";

    private PastSearchArrayAdapter pastSearchArrayAdapter;
    private List<PastSearch> pastSearches;
    // used for when an item is long clicked
    private int pastSearchLongClickPosition;

    @OnTextChanged(value = R.id.playerEditText,
            callback = OnTextChanged.Callback.TEXT_CHANGED)
    public void playerEditTextChanged(CharSequence charSequence) {
        pastSearchArrayAdapter.getFilter().filter(charSequence.toString());
    }

    /* Listener for the item click. When an item is clicked, we pass the steam ID
    * and the player information through the intent */
    @OnItemClick(R.id.pastSearchListView)
    public void onPastSearchItemClick(int position) {
        // cast our item to PastSearch
        PastSearch pastSearch = (PastSearch) pastSearchListView.getItemAtPosition(position);
        // create our profile object
        Player player = new Player(pastSearch.getGamerTag(), pastSearch.getProfileUrl(), pastSearch.getLastLogOn());
        // create intent and pass steam id and player info
        Intent intent = new Intent(getActivity(), CSGOStatsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.STEAM_ID, pastSearch.getSteamId());
        bundle.putSerializable(AppConstants.PROFILE, player);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @OnItemLongClick(R.id.pastSearchListView)
    public boolean onPastSearchLongItemClick(int position) {
        // set our item position
        pastSearchLongClickPosition = position;
        // cast our item to PastSearch
        PastSearch pastSearch = (PastSearch) pastSearchListView.getItemAtPosition(position);
        Intent intent = new Intent(getActivity(), PastSearchHoldActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConstants.PAST_SEARCH, pastSearch);
        intent.putExtras(bundle);
        // request result for when popup is closed
        startActivityForResult(intent, POPUP_CHOICE_CHOSEN);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == POPUP_CHOICE_CHOSEN) {
            if(resultCode == Activity.RESULT_OK) {
                switch(data.getIntExtra(AppConstants.POPUP_CHOICE, 2)) {
                    case AppConstants.POPUP_DELETE_CHOICE:
                        // remove from our past searches
                        pastSearches.remove(pastSearchLongClickPosition);
                        pastSearchArrayAdapter.notifyDataSetChanged();
                        break;
                    case AppConstants.POPUP_REFRESH_CHOICE:
                        // Get updated results
                        PastSearchDatabase db = PastSearchDatabase.getInstance(getContext());
                        pastSearches.clear();
                        pastSearches.addAll(db.getAllPastSearches());
                        // Notify our list view that the data has changed
                        pastSearchArrayAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PastSearchDatabase db = PastSearchDatabase.getInstance(getContext());
        pastSearches = db.getAllPastSearches();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.past_search_fragment, container, false);
        // bind views
        ButterKnife.bind(this, view);
        // get shared pref instance
        final SharedPref sharedPref = SharedPref.getInstance(getContext());
        // if past searches is more than 0, display snackbar
        if(pastSearches.size() > 0) {
            if(!sharedPref.getValue(PAST_SEARCH_PREF)) {
                AppConstants.createSnackbar(getContext(), coordinatorLayout,
                        "Hold down a past search to refresh or delete!", sharedPref, PAST_SEARCH_PREF);
            }
        }
        // Create past search array adapter class
        pastSearchArrayAdapter = new PastSearchArrayAdapter(getContext(), pastSearches);
        // set list view adapter
        pastSearchListView.setAdapter(pastSearchArrayAdapter);
        pastSearchListView.setFastScrollEnabled(true);
        pastSearchListView.setCacheColorHint(Color.TRANSPARENT);
        pastSearchListView.setScrollingCacheEnabled(false);
        return view;
    }

}
