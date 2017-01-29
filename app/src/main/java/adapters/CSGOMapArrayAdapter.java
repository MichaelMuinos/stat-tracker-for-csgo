package adapters;

import android.content.Context;
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

import net.AppConstants;
import net.pojo.CSGOMap;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class CSGOMapArrayAdapter extends ArrayAdapter<CSGOMap> implements Filterable {

    private List<CSGOMap> csgoMapOriginalList;
    private List<CSGOMap> csgoMapFilteredList;

    private CSGOMapFilter csgoMapFilter;

    public CSGOMapArrayAdapter(Context context, List<CSGOMap> mapList) {
        super(context, R.layout.maps_fragment_item_layout, mapList);
        this.csgoMapOriginalList = mapList;
        this.csgoMapFilteredList = mapList;
    }

    @Override
    public int getCount() {
        return csgoMapFilteredList.size();
    }

    /**
     * Get weapon list item id
     * @param position item index
     * @return current item id
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get specific item from weapon list
     * @param position item index
     * @return list item
     */
    @Override
    public CSGOMap getItem(int position) {
        return csgoMapFilteredList.get(position);
    }

    @Override
    public Filter getFilter() {
        if(csgoMapFilter == null)
            csgoMapFilter = new CSGOMapFilter();
        return csgoMapFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if(view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.maps_fragment_item_layout, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        // bind views
        ButterKnife.bind(this, view);

        // grab temp map object
        CSGOMap csgoMap = csgoMapFilteredList.get(position);

        // set background picture
        Picasso.with(getContext())
                .load(csgoMap.getMapDrawable())
                .fit()
                .transform(new RoundedCornersTransformation(7, 0))
                .into(holder.mapPicture);

        // set map name text view
        holder.mapName.setText(csgoMap.getMapName());

        // set map losses text view
        holder.mapLosses.setText(AppConstants.round(csgoMap.getMapLosses()));

        // set map wins text view
        holder.mapWins.setText(AppConstants.round(csgoMap.getMapWins()));

        // set map win percent text view
        float mapWinPercentageValue;
        if(csgoMap.getMapWins() + csgoMap.getMapLosses() != 0)
            mapWinPercentageValue = csgoMap.getMapWins() / (csgoMap.getMapWins() + csgoMap.getMapLosses());
        else
            mapWinPercentageValue = 0;
        holder.mapWinPercentage.setText(AppConstants.getPercentage(mapWinPercentageValue, 1));


        return view;
    }

    static class ViewHolder {
        @BindView(R.id.mapPicture) ImageView mapPicture;
        @BindView(R.id.mapName) TextView mapName;
        @BindView(R.id.mapWins) TextView mapWins;
        @BindView(R.id.mapLosses) TextView mapLosses;
        @BindView(R.id.mapWinPercentage) TextView mapWinPercentage;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class CSGOMapFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                filterResults.values = csgoMapOriginalList;
                filterResults.count = csgoMapOriginalList.size();
            } else {
                List<CSGOMap> tempMapList = new ArrayList<>();
                for(int i = 0; i < csgoMapOriginalList.size(); i++) {
                    // if the weapon name contains what the user enters,
                    // add to our filtered results
                    if(csgoMapOriginalList.get(i).getMapName().toLowerCase().startsWith(constraint.toString().toLowerCase()))
                        tempMapList.add(csgoMapOriginalList.get(i));
                }
                // must set our filter results variables because publish results
                // method can only take a FilteredResult Object
                filterResults.values = tempMapList;
                filterResults.count = tempMapList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results.count != 0) {
                csgoMapFilteredList = (ArrayList<CSGOMap>) results.values;
                notifyDataSetChanged();
            } else
                notifyDataSetInvalidated();
        }

    }

}
