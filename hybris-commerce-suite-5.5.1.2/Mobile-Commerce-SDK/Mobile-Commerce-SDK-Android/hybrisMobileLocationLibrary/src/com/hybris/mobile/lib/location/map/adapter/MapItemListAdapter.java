/*******************************************************************************
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 ******************************************************************************/
package com.hybris.mobile.lib.location.map.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hybris.mobile.lib.location.R;
import com.hybris.mobile.lib.location.map.data.MapItem;

import java.util.List;


/**
 * Default adapter for the map item list
 */
public class MapItemListAdapter extends ArrayAdapter<MapItem> {

    public MapItemListAdapter(Activity context, List<MapItem> values) {
        super(context, R.layout.item_mapitem_listview, values);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView;
        MapItem mapItem = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.item_mapitem_listview, parent, false);
            rowView.setTag(new MapItemViewHolder(rowView, position));
        } else {
            rowView = convertView;
        }

        MapItemViewHolder viewHolder = (MapItemViewHolder) rowView.getTag();
        viewHolder.image.setImageDrawable(mapItem.getImage());
        viewHolder.name.setText(mapItem.getName());
        viewHolder.description.setText(mapItem.getDescription());

        return rowView;
    }

    /**
     * Holder for the UI elements
     */
    static class MapItemViewHolder {
        private final ImageView image;
        private final TextView name;
        private final TextView description;

        public MapItemViewHolder(View view, int position) {
            image = (ImageView) view.findViewById(R.id.hybris_map_item_image);
            name = (TextView) view.findViewById(R.id.hybris_map_item_name);
            description = (TextView) view.findViewById(R.id.hybris_map_item_description);
        }
    }

}
