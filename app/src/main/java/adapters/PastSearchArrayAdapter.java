package adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.justplaingoatappsgmail.csgostatsviewer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import database.models.PastSearch;

public class PastSearchArrayAdapter extends ArrayAdapter<PastSearch> implements Filterable {

    private List<PastSearch> pastSearchOriginalList;
    private List<PastSearch> pastSearchFilteredList;

    private PastSearchFilter pastSearchFilter;

    public PastSearchArrayAdapter(Context context, List<PastSearch> pastSearchList) {
        super(context, R.layout.past_search_item, pastSearchList);
        this.pastSearchOriginalList = pastSearchList;
        this.pastSearchFilteredList = pastSearchList;

        getFilter();
    }

    @Override
    public int getCount() {
        return pastSearchFilteredList.size();
    }

    /**
     * Get past search list item id
     * @param position item index
     * @return current item id
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get specific item from past search list
     * @param position item index
     * @return list item
     */
    @Override
    public PastSearch getItem(int position) {
        return pastSearchFilteredList.get(position);
    }

    @Override
    public Filter getFilter() {
        if(pastSearchFilter == null)
            pastSearchFilter = new PastSearchFilter();
        return pastSearchFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if(view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.past_search_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        // bind views
        ButterKnife.bind(this, view);

        // grab temp weapon object
        PastSearch pastSearch = pastSearchFilteredList.get(position);

        int dimensions = getContext().getResources().getInteger(R.integer.past_search_pic);
        // set past search picture
        Picasso.with(getContext())
                .load(Uri.parse(pastSearch.getProfileUrl()))
                .resize(dimensions, dimensions)
                .into(holder.pastSearchPicture);

        // set past search name
        holder.pastSearchName.setText(pastSearch.getGamerTag());

        // set past search steam id
        holder.pastSearchId.setText("ID: " + pastSearch.getSteamId());

        return view;
    }

    static class ViewHolder {
        @BindView(R.id.pastSearchId) TextView pastSearchId;
        @BindView(R.id.pastSearchName) TextView pastSearchName;
        @BindView(R.id.pastSearchPicture) ImageView pastSearchPicture;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class PastSearchFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                filterResults.values = pastSearchOriginalList;
                filterResults.count = pastSearchOriginalList.size();
            } else {
                List<PastSearch> tempPastSearchList = new ArrayList<>();
                for(int i = 0; i < pastSearchOriginalList.size(); i++) {
                    // if the weapon name contains what the user enters,
                    // add to our filtered results
                    if(pastSearchOriginalList.get(i).getGamerTag().toLowerCase().startsWith(constraint.toString().toLowerCase()))
                        tempPastSearchList.add(pastSearchOriginalList.get(i));
                }
                // must set our filter results variables because publish results
                // method can only take a FilteredResult Object
                filterResults.values = tempPastSearchList;
                filterResults.count = tempPastSearchList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results.count != 0) {
                pastSearchFilteredList = (ArrayList<PastSearch>) results.values;
                notifyDataSetChanged();
            } else
                notifyDataSetInvalidated();
        }

    }

}
