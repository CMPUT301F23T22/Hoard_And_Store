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

public class ItemBreakDownAdapter extends BaseAdapter {

    private final Context context;
    private final List<Map.Entry<Tag, Integer>> itemTagChartInfo;

    public ItemBreakDownAdapter(Context context, List<Map.Entry<Tag, Integer>> itemTagChartInfo) {
        this.context = context;
        this.itemTagChartInfo = itemTagChartInfo;
    }

    /**
     * Returns the number of elements for chart info
     *
     * @return int
     */
    @Override
    public int getCount() {
        return itemTagChartInfo.size();
    }


    /**
     * Returns the item at position i
     *
     * @param i an integer for position in adapter
     * @return Object
     */
    @Override
    public Object getItem(int i) {
        return itemTagChartInfo.get(i);
    }

    /**
     * Returns the itemID at position i
     *
     * @param i an integer for position in adapter
     * @return long
     */
    @Override
    public long getItemId(int i) {
        return i;
    }


    /**
     * sets the custom view for out chart info
     *
     * @param position an integer for position in adapter
     * @param convertView view
     * @param parent parent views group
     * @return Object
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
