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

import net.AppConstants;
import net.pojo.Weapon;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeaponArrayAdapter extends ArrayAdapter<Weapon> implements Filterable {

    private List<Weapon> weaponOriginalList;
    private List<Weapon> weaponFilteredList;

    private WeaponFilter weaponFilter;

    public WeaponArrayAdapter(Context context, List<Weapon> weaponList) {
        super(context, R.layout.weapons_fragment_item_layout, weaponList);
        this.weaponOriginalList = weaponList;
        this.weaponFilteredList = weaponList;

        getFilter();
    }

    @Override
    public int getCount() {
        return weaponFilteredList.size();
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
    public Weapon getItem(int position) {
        return weaponFilteredList.get(position);
    }

    @Override
    public Filter getFilter() {
        if(weaponFilter == null)
            weaponFilter = new WeaponFilter();
        return weaponFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if(view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.weapons_fragment_item_layout, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        // bind views
        ButterKnife.bind(this, view);

        // grab temp weapon object
        Weapon weapon = weaponFilteredList.get(position);

        // set background picture
        Picasso.with(getContext())
                .load(Uri.parse(weapon.getWeaponDrawable()))
                .fit()
                .into(holder.weaponPicture);

        // set weapon name text view
        holder.weaponName.setText(weapon.getWeaponName());

        // set weapon kill text view
        holder.weaponKills.setText(AppConstants.round(weapon.getWeaponKills()));

        // set weapon hits text view
        holder.weaponHits.setText(AppConstants.round(weapon.getWeaponHits()));

        // set weapon shots text view
        holder.weaponShots.setText(AppConstants.round(weapon.getWeaponShots()));

        // set weapon accuracy text view
        float weaponAccuracyValue = weapon.getWeaponHits() / weapon.getWeaponShots();
        holder.weaponAccuracy.setText(AppConstants.getPercentage(weaponAccuracyValue, 1));

        return view;
    }

    static class ViewHolder {
        @BindView(R.id.weaponPicture) ImageView weaponPicture;
        @BindView(R.id.weaponName) TextView weaponName;
        @BindView(R.id.weaponKills) TextView weaponKills;
        @BindView(R.id.weaponHits) TextView weaponHits;
        @BindView(R.id.weaponShots) TextView weaponShots;
        @BindView(R.id.weaponAccuracy) TextView weaponAccuracy;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private class WeaponFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                filterResults.values = weaponOriginalList;
                filterResults.count = weaponOriginalList.size();
            } else {
                List<Weapon> tempWeaponList = new ArrayList<>();
                for(int i = 0; i < weaponOriginalList.size(); i++) {
                    // if the weapon name contains what the user enters,
                    // add to our filtered results
                    if(weaponOriginalList.get(i).getWeaponName().toLowerCase().startsWith(constraint.toString().toLowerCase()))
                        tempWeaponList.add(weaponOriginalList.get(i));
                }
                // must set our filter results variables because publish results
                // method can only take a FilteredResult Object
                filterResults.values = tempWeaponList;
                filterResults.count = tempWeaponList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results.count != 0) {
                weaponFilteredList = (ArrayList<Weapon>) results.values;
                notifyDataSetChanged();
            } else
                notifyDataSetInvalidated();
        }

    }

}
