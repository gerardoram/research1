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
package com.hybris.mobile.app.b2b.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.hybris.mobile.app.b2b.B2BApplication;
import com.hybris.mobile.app.b2b.B2BConstants;
import com.hybris.mobile.app.b2b.R;
import com.hybris.mobile.lib.b2b.data.Category;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Adapter categories on the sync settings page
 */
public class CategoryListPreferenceAdapter extends ArrayAdapter<Category> {

    private OnSelectCategoryListener mOnSelectCategoryListener;

    public interface OnSelectCategoryListener {
        /**
         * Method for callback
         */
        public void onSelectCategory(boolean enableDownload);
    }

    public CategoryListPreferenceAdapter(Context context, CategoryListPreferenceAdapter.OnSelectCategoryListener onSelectCategoryListener, List<Category> values) {
        super(context, R.layout.item_category_offline_pref, values);
        mOnSelectCategoryListener = onSelectCategoryListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView;
        Category category = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.item_category_offline_pref, parent, false);
            rowView.setTag(new ViewHolder(rowView));
        } else {
            rowView = convertView;
        }

        final ViewHolder viewHolder = (ViewHolder) rowView.getTag();

        viewHolder.categoryName.setText(category.getName());
        viewHolder.category = category;
        viewHolder.position = position;
        viewHolder.checkbox.setChecked(getCheckedStatus(category.getId()));

        // We put a padding for the subcategories
        viewHolder.categoryName.setPadding(getNbParents(category) * 20, 0, 0, 0);

        return rowView;
    }

    /**
     * Return the checked status of the category
     *
     * @param categoryId
     * @return
     */
    private boolean getCheckedStatus(String categoryId) {
        Set<String> setCategory = B2BApplication.getStringSetFromSharedPreferences(B2BConstants.SETTINGS_SYNC_CATEGORY_ID_LIST,
                new HashSet<String>());

        return setCategory != null && setCategory.contains(categoryId);
    }

    /**
     * Return the number of parent of a category
     *
     * @param category
     * @return
     */
    private int getNbParents(Category category) {
        int nbParents = 0;
        if (category.getParent() != null) {
            nbParents = getNbParents(category.getParent()) + 1;
        }

        return nbParents;
    }

    class ViewHolder {

        private TextView categoryName;
        private CheckBox checkbox;
        private Category category;
        private int position;

        public ViewHolder(View view) {
            categoryName = (TextView) view.findViewById(R.id.item_preference_listview_checkbox_text);
            checkbox = (CheckBox) view.findViewById(R.id.item_preference_listview_checkbox_checkbox);

            checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Set<String> setCategory = B2BApplication.getStringSetFromSharedPreferences(
                            B2BConstants.SETTINGS_SYNC_CATEGORY_ID_LIST, new HashSet<String>());

                    // Saving the category
                    if (isChecked) {
                        setCategory.add(category.getId());
                    }
                    // Removing the category
                    else {
                        setCategory.remove(category.getId());
                    }

                    // Child categories
                    selectChildCategories(isChecked);

                    // Enable/disable download
                    if (mOnSelectCategoryListener != null) {
                        mOnSelectCategoryListener.onSelectCategory(B2BApplication.isOnline() && !setCategory.isEmpty());
                    }

                    B2BApplication.setStringSetToSharedPreferences(B2BConstants.SETTINGS_SYNC_CATEGORY_ID_LIST, setCategory);

                }

                /**
                 * Check/Uncheck the categories child
                 *
                 * @param isChecked
                 */
                private void selectChildCategories(boolean isChecked) {
                    Set<String> setCategory = B2BApplication.getStringSetFromSharedPreferences(
                            B2BConstants.SETTINGS_SYNC_CATEGORY_ID_LIST, new HashSet<String>());

                    // Parent view
                    ViewGroup parentView = (ViewGroup) checkbox.getParent().getParent();

                    if (parentView != null) {
                        for (int i = position + 1; i < parentView.getChildCount(); i++) {
                            View rowCategory = parentView.getChildAt(i);
                            ViewHolder viewHolder = (ViewHolder) rowCategory.getTag();

                            if (isChild(category, viewHolder.category.getId())) {
                                CheckBox checkBox = (CheckBox) rowCategory.findViewById(R.id.item_preference_listview_checkbox_checkbox);
                                checkBox.setChecked(isChecked);

                                // Saving the category
                                if (isChecked) {
                                    setCategory.add(category.getId());
                                }
                                // Removing the category
                                else {
                                    setCategory.remove(category.getId());
                                }
                            }
                        }

                        B2BApplication.setStringSetToSharedPreferences(B2BConstants.SETTINGS_SYNC_CATEGORY_ID_LIST, setCategory);
                    }

                }

                /**
                 * Return true if the categoryId is found under the category child
                 *
                 * @param category
                 * @param categoryId
                 * @return
                 */
                private boolean isChild(Category category, String categoryId) {

                    if (StringUtils.equals(category.getId(), categoryId)) {
                        return true;
                    } else if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
                        boolean childFound = false;
                        int i = 0;

                        while (!childFound && i < category.getSubcategories().size()) {
                            childFound = isChild(category.getSubcategories().get(i), categoryId);
                            i++;
                        }

                        return childFound;
                    }

                    return false;
                }
            });

        }
    }


}
