package adapters;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.ActionBar;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.justplaingoatappsgmail.csgostatsviewer.R;
import com.squareup.picasso.Picasso;

import net.AppConstants;
import net.pojo.AllCSGOAchievementInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class AchievementRecyclerViewAdapter extends RecyclerView.Adapter<AchievementRecyclerViewAdapter.AchievementViewHolder> {

    private List<AllCSGOAchievementInfo> achievementList;
    private Context context;

    public AchievementRecyclerViewAdapter(Context context, List<AllCSGOAchievementInfo> achievementList) {
        this.context = context;
        this.achievementList = achievementList;
    }

    @Override
    public AchievementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievement_card_parent_view, parent, false);
        AchievementViewHolder achievementViewHolder = new AchievementViewHolder(context, view);
        return achievementViewHolder;
    }

    // similar to get view method in list view adapter
    @Override
    public void onBindViewHolder(final AchievementViewHolder holder, int position) {
        // Get our achievement object
        AllCSGOAchievementInfo achievementInfo = achievementList.get(position);

        // Set achievement name text view
        holder.achievementName.setText(achievementInfo.getAvailableAchievement().getDisplayName());
        // Set achievement description text view
        holder.achievementDesc.setText(achievementInfo.getAvailableAchievement().getDescription());
        // Set achievement image
        Picasso.with(context)
                .load(Uri.parse(achievementInfo.getAvailableAchievement().getIcon()))
                .resize(264, 264)
                .transform(new RoundedCornersTransformation(20, 0))
                .placeholder(R.drawable.questionmark)
                .into(holder.achievementPicture);
        // Set result text and background (if they have the achievement or not)
        if(achievementInfo.getAchievement().getAchieved() == 1) {
            holder.achievementResult.setBackgroundColor(ContextCompat.getColor(context, R.color.achievedColor));
            holder.achievementResult.setText("Achieved");
        } else {
            holder.achievementResult.setBackgroundColor(ContextCompat.getColor(context, R.color.notAchievedColor));
            holder.achievementResult.setText("Not Achieved");
        }

        // Below is for setting the back of the card view
        // Set achievement back name
        holder.achievementNameBack.setText(achievementInfo.getAvailableAchievement().getDisplayName());
        // Set achievement back desc
        holder.achievementDescBack.setText(achievementInfo.getAvailableAchievement().getDescription());
        // Set achievement picture back
        Picasso.with(context)
                .load(Uri.parse(achievementInfo.getAvailableAchievement().getIcon()))
                .resize(264, 264)
                .transform(new RoundedCornersTransformation(20, 0))
                .placeholder(R.drawable.questionmark)
                .into(holder.achievementPictureBack);

        float percentage = achievementInfo.getGlobalAchievement().getPercent();
        // Set percentage
        holder.globalAchievementPercentage.setText(AppConstants.round(percentage, 1) + "%");
        // Set fill text view depending on percentage
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.MATCH_PARENT,
                percentage);
        holder.fillTextView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return achievementList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class AchievementViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardView) CardView cardView;
        @BindView(R.id.achievementName) TextView achievementName;
        @BindView(R.id.cardBackAchName) TextView achievementNameBack;
        @BindView(R.id.achievementDesc) TextView achievementDesc;
        @BindView(R.id.cardBackDescription) TextView achievementDescBack;
        @BindView(R.id.achievementPicture) ImageView achievementPicture;
        @BindView(R.id.cardBackPicture) ImageView achievementPictureBack;
        @BindView(R.id.achievementResult) TextView achievementResult;
        @BindView(R.id.cardBackPercentage) TextView globalAchievementPercentage;
        @BindView(R.id.cardBackPercentFillTextView) TextView fillTextView;
        @BindView(R.id.card_front) FrameLayout cardFrontLayout;
        @BindView(R.id.card_back) FrameLayout cardBackLayout;

        private AnimatorSet mSetRightOut;
        private AnimatorSet mSetLeftIn;
        private boolean mIsBackVisible = false;

        private Context context;

        public AchievementViewHolder(Context context, View itemView) {
            super(itemView);
            // bind views
            ButterKnife.bind(this, itemView);

            this.context = context;
            mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.out_animation);
            mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.in_animator);
            changeCameraDistance();
        }

        @OnClick(R.id.cardView)
        public void cardViewFlip() {
            if(!mSetLeftIn.isRunning() && !mSetRightOut.isRunning()) {
                if (!mIsBackVisible) {
                    mSetRightOut.setTarget(cardFrontLayout);
                    mSetLeftIn.setTarget(cardBackLayout);
                    mSetRightOut.start();
                    mSetLeftIn.start();
                    mIsBackVisible = true;
                } else {
                    mSetRightOut.setTarget(cardBackLayout);
                    mSetLeftIn.setTarget(cardFrontLayout);
                    mSetRightOut.start();
                    mSetLeftIn.start();
                    mIsBackVisible = false;
                }
            }
        }

        private void changeCameraDistance() {
            int distance = 8000;
            float scale = context.getResources().getDisplayMetrics().density * distance;
            cardFrontLayout.setCameraDistance(scale);
            cardBackLayout.setCameraDistance(scale);
        }
    }

}
