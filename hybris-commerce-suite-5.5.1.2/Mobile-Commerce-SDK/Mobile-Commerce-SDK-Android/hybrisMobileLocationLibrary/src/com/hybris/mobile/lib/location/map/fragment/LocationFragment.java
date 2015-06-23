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

package com.hybris.mobile.lib.location.map.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationServices;
import com.hybris.mobile.lib.location.LocationUtils;
import com.hybris.mobile.lib.location.R;


/**
 * Base Fragment for fragments that needs user location and google play services
 */
public abstract class LocationFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LocationFragment.class.getCanonicalName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationClient mLocationClient;
    private GoogleApiClient mGoogleApiClient;
    private Location mDeviceLocation;
    private boolean mIsDeviceLocation = false;
    private DialogFragment mErrorDialog = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:

                switch (resultCode) {
                    case Activity.RESULT_OK:
                        onResume();
                        break;
                }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLocationClient = new LocationClient(getActivity(), this, this);

        // For the fused location provider in case we want the last known location
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLocationConfiguration();
    }

    /**
     * Check the location configuration: presence of Google Play Services on the device and device location services
     */
    private void checkLocationConfiguration() {
        // Check if Google Play services is available
        if (!LocationUtils.isGooglePlayServicesConnected(getActivity())) {
            // Get the error dialog from Google Play services
            final Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), getActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            if (errorDialog != null) {
                DialogFragment errorFragment = new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return errorDialog;
                    }
                };

                // Show the error dialog
                errorFragment.show(getFragmentManager(), getString(R.string.hybris_map_error_google_play_services));
            }
        } else {
            // Check if the user location services is activated
            if (mIsDeviceLocation && !LocationUtils.isLocationActivated(getActivity())) {
                mErrorDialog = LocationUtils.showDialogEnableLocationServices(getActivity());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mLocationClient != null) {
            mLocationClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Save the device location
        mDeviceLocation = mLocationClient.getLastLocation();

        // Try to get the last known location with the fused location provider
        if (mDeviceLocation == null) {
            mDeviceLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }

        // We call the abstract method
        onConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Trying to find a resolution if google play services fails to connect
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        } else {
            Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_LONG).show();
        }
    }

    Location getDeviceLocation() {
        return mDeviceLocation;
    }

    void registerForDeviceLocation(boolean isDeviceLocation) {
        mIsDeviceLocation = isDeviceLocation;
    }

    /**
     * Called when the services are connected
     */
    protected abstract void onConnected();
}
