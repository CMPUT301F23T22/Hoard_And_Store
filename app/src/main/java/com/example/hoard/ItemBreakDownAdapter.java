package com.example.hoard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adapter for displaying item breakdown information in a ListView.
 * This adapter takes a list of Map entries where each entry consists of a Tag and its associated count.
 */
public class ItemBreakDownAdapter extends BaseAdapter {

    private final Context context;
    private final List<Map.Entry<Tag, Integer>> itemTagChartInfo;

    /**
     * Constructor for ItemBreakDownAdapter.
     *
     * @param context The current context.
     * @param itemTagChartInfo A list of Map entries representing tag data and their counts.
     */
    public ItemBreakDownAdapter(Context context, List<Map.Entry<Tag, Integer>> itemTagChartInfo) {
        this.context = context;
        this.itemTagChartInfo = itemTagChartInfo;
    }

    /**
     * Returns the count of items in the adapter.
     *
     * @return The number of items.
     */
    @Override
    public int getCount() {
        return itemTagChartInfo.size();
    }


    /**
     * Returns the item at the specified position.
     *
     * @param i The position of the item in the data set.
     * @return The item at the specified position.
     */
    @Override
    public Object getItem(int i) {
        return itemTagChartInfo.get(i);
    }

    /**
     * Returns the row ID associated with the specified position in the list.
     * In this case, the position itself is used as the row ID.
     *
     * @param i The position of the item within the adapter's data set.
     * @return The ID of the item at the specified position.
     */
    @Override
    public long getItemId(int i) {
        return i;
    }


    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.).
     * The method creates a new View for each item referenced by the adapter.
     *
     * @param position The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_breakdown_content, parent, false);
        }

        // Get the current language entry
        Map.Entry<Tag, Integer> chartEntry = itemTagChartInfo.get(position);

        // Bind data to the views in the list item layout
        TextView languageTagNameTextView = convertView.findViewById(R.id.tag_chart_info);
        TextView languageTagCountTextView = convertView.findViewById(R.id.tag_count_chart_info);

        languageTagNameTextView.setText(chartEntry.getKey().getTagName());
        languageTagCountTextView.setText(String.valueOf(chartEntry.getValue()));

        return convertView;
    }
}
