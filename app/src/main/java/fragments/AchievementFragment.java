package fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.justplaingoatappsgmail.csgostatsviewer.R;

import net.AppConstants;
import net.pojo.AllCSGOAchievementInfo;

import java.io.Serializable;
import java.util.List;

import adapters.AchievementRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import sharedpreferences.SharedPref;

public class AchievementFragment extends Fragment {

    @BindView(R.id.achievementCoordinatorLayout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    private static final String ACHIEVEMENT_FRAGMENT_LIST = "ACHIEVEMENT_FRAGMENT_LIST";
    private static final String ACHIEVEMENT_SNACKBAR = "ACHIEVEMENT_SNACKBAR";

    private List<AllCSGOAchievementInfo> achievementList;

    public static AchievementFragment newInstance(List<AllCSGOAchievementInfo> achievementList) {
        Bundle args = new Bundle();
        args.putSerializable(ACHIEVEMENT_FRAGMENT_LIST, (Serializable) achievementList);

        AchievementFragment achievementFragment = new AchievementFragment();
        achievementFragment.setArguments(args);
        return achievementFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        achievementList = (List<AllCSGOAchievementInfo>) getArguments().getSerializable(ACHIEVEMENT_FRAGMENT_LIST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.achievement_recycler_view, container, false);
        // bind views
        ButterKnife.bind(this, view);

        // get our shared prefs instance
        final SharedPref sharedPref = SharedPref.getInstance(getContext());

        if(!sharedPref.getValue(ACHIEVEMENT_SNACKBAR)) {
            AppConstants.createSnackbar(getContext(), coordinatorLayout,
                    "Click an achievement for more info!", sharedPref, ACHIEVEMENT_SNACKBAR);
        }

        // create achievement adapter and set the adapter
        AchievementRecyclerViewAdapter adapter = new AchievementRecyclerViewAdapter(getContext(), achievementList);
        recyclerView.setAdapter(adapter);

        // set layout manager to recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

}
