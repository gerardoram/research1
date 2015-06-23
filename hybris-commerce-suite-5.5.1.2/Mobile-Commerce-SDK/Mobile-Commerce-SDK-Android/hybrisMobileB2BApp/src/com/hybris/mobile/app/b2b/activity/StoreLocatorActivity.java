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

package com.hybris.mobile.app.b2b.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.hybris.mobile.app.b2b.B2BApplication;
import com.hybris.mobile.app.b2b.IntentConstants;
import com.hybris.mobile.app.b2b.R;
import com.hybris.mobile.app.b2b.fragment.StoreListFragment;
import com.hybris.mobile.app.b2b.utils.UIUtils;
import com.hybris.mobile.lib.b2b.data.DataError;
import com.hybris.mobile.lib.b2b.data.store.Store;
import com.hybris.mobile.lib.b2b.data.store.StoreList;
import com.hybris.mobile.lib.b2b.query.QueryStores;
import com.hybris.mobile.lib.b2b.response.ResponseReceiver;
import com.hybris.mobile.lib.http.listener.OnRequestListener;
import com.hybris.mobile.lib.http.response.Response;
import com.hybris.mobile.lib.http.utils.RequestUtils;
import com.hybris.mobile.lib.location.map.MapConfiguration;
import com.hybris.mobile.lib.location.map.data.MapItem;
import com.hybris.mobile.lib.location.map.fragment.MapFragment;
import com.hybris.mobile.lib.location.map.listener.MapDataLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for the store locator
 */
public class StoreLocatorActivity extends MainActivity implements MapDataLoader, ResponseReceiver<StoreList>, StoreListFragment.MapActions {
    private String mStoreLocatorRequestId = RequestUtils.generateUniqueRequestId();
    private MapFragment mMapFragment;
    private StoreListFragment mListFragment;
    private MapConfiguration mMapConfiguration;
    private TextView mNbResults;
    private boolean mFirstCall = false;

    @Override
    public void centerMap(LatLng latLng) {
        if (mMapFragment != null) {
            mMapFragment.centerMap(latLng);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_store_locator);
        super.onCreate(savedInstanceState);

        mListFragment = (StoreListFragment) getFragmentManager().findFragmentById(R.id.store_locator_list);
        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.store_locator_map);
        mNbResults = (TextView) findViewById(R.id.store_locator_list_nb_results);

        mMapConfiguration = MapConfiguration.build().setDeviceLocation(true).setPadding(100).setAnimationSpeed(250).setCameraPosition(MapConfiguration.CameraPosition.FITS_DEVICE_LOCATION_NEAREST_ITEM).setZoomLevel(5);

        // 0 results by default
        mNbResults.setText(getString(R.string.stores_nb_results, 0));
    }

    @Override
    public void onResponse(Response<StoreList> response) {

        if (response.getData() != null && response.getData().getStores() != null) {
            List<MapItem> items = new ArrayList<MapItem>();

            for (Store store : response.getData().getStores()) {
                MapItem mapItem = new MapItem();

                mapItem.setDescription(store.getAddress().getFormattedAddress());
                mapItem.setName(store.getName());
                mapItem.setId(store.getName());
                mapItem.setLocation(new LatLng(store.getGeoPoint().getLatitude(), store.getGeoPoint().getLongitude()));

                items.add(mapItem);
            }

            // Displaying the stores on the map and list
            mMapFragment.updateMap(items);
            mListFragment.updateStoreList(response.getData().getStores());

            mNbResults.setText(getString(R.string.stores_nb_results, response.getData().getStores().size()));
        }

    }

    @Override
    public void onError(Response<DataError> response) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public MapConfiguration getMapConfiguration() {
        return mMapConfiguration;
    }

    @Override
    public void onMapItemClick(MapItem item) {
        Intent intent = new Intent(this, StoreDetailsActivity.class);
        intent.putExtra(IntentConstants.STORE_NAME, item.getId());

        startActivity(intent);
    }

    @Override
    public View getMapItemViewContent(MapItem item) {
        return null;
    }

    @Override
    public View getMapItemViewWindow(MapItem item) {
        return null;
    }

    @Override
    public void onMapLoaded() {
    }

    @Override
    public void onConnected(Location deviceLocation) {

        // Getting the stores
        QueryStores queryStores = new QueryStores();

        // Based on the device location
        if (deviceLocation != null) {
            mListFragment.setDeviceLocation(deviceLocation);

            QueryStores.Location location = new QueryStores.Location();
            location.setLatitude(deviceLocation.getLatitude());
            location.setLongitude(deviceLocation.getLongitude());
            location.setRadius(getResources().getInteger(R.integer.default_store_locator_radius));

            queryStores.setLocation(location);

            mMapConfiguration.setCameraPosition(MapConfiguration.CameraPosition.FITS_DEVICE_LOCATION_NEAREST_ITEM);
        } else {
            mMapConfiguration.setCameraPosition(MapConfiguration.CameraPosition.CENTER_FIRST_ITEM);
        }

        // onConnected is called by the location library multiple times according to the user location
        // If we already called this method and we still don' have the location, we don't need to call it twice
        if (!mFirstCall || deviceLocation != null) {
            B2BApplication.getContentServiceHelper()
                    .getStores(this, mStoreLocatorRequestId, queryStores, false, null, new OnRequestListener() {
                        @Override
                        public void beforeRequest() {
                            UIUtils.showLoadingActionBar(StoreLocatorActivity.this, true);
                        }

                        @Override
                        public void afterRequest(boolean isDataSynced) {
                            UIUtils.showLoadingActionBar(StoreLocatorActivity.this, false);
                        }
                    });
        }


        mFirstCall = true;

    }

    @Override
    protected void onStop() {
        super.onStop();
        B2BApplication.getContentServiceHelper().cancel(mStoreLocatorRequestId);
    }

}
