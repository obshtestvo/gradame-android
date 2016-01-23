/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Obshtestvo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.grada.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import me.grada.di.Injector;
import me.grada.io.event.LocationGrantedEvent;
import me.grada.io.event.LocationUpdateEvent;
import me.grada.io.event.ShowLocationRationaleEvent;

/**
 * Created by yavorivanov on 10/01/2016.
 */
public class LocationFragment extends BaseFragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    public static final String TAG = LocationFragment.class.getSimpleName();

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    private static final String LOCATION_PERMISSION_TYPE = Manifest.permission.ACCESS_FINE_LOCATION;

    @Inject
    Bus bus;

    private GoogleApiClient googleApiClient;

    public static LocationFragment newInstance() {
        return new LocationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.INSTANCE.getAppComponent().inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        disconnectLocationProvider();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            // Received the permission for the location provider
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bus.post(new LocationGrantedEvent());
                // Connect to Play Services Location API in order to get a location fix
                connectToLocationProvider();
            } else {
                // Permission denied, we can't use the location provider
                // Determine if the user has ticked 'Never ask again'
                boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                if (showRationale) {
                    bus.post(new ShowLocationRationaleEvent());
                } else {
                    // TODO: Replace the MapView with an in-line error to indicate that
                    // the functionality doesn't work without the location service enabled
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(getActivity(), LOCATION_PERMISSION_TYPE)
                != PackageManager.PERMISSION_GRANTED) {
            getPermission();
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        bus.post(new LocationUpdateEvent(location));
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO: Handle error
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // TODO: Handle error
    }

    public void getPermission() {
        // Check if the target is Marshmallow, or newer, and proceed
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ContextCompat.checkSelfPermission(getActivity(), LOCATION_PERMISSION_TYPE)
                    == PackageManager.PERMISSION_GRANTED) {
                // Connect to Play Services Location API in order to get a location fix
                connectToLocationProvider();
            } else {
                // Request permission to use location providers
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        LOCATION_PERMISSION_TYPE)) {
                    bus.post(new ShowLocationRationaleEvent());
                    // TODO: Explain why the permission is needed (occurs after previous rejection)
                } else {
                    promptLocationPermission();
                }
            }
        }
    }

    /**
     * Shows the standard Android dialog view prompting for location permission
     */
    public void promptLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{LOCATION_PERMISSION_TYPE},
                LOCATION_PERMISSION_REQUEST);
    }

    public void connectToLocationProvider() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        googleApiClient.connect();
    }

    public void disconnectLocationProvider() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

}
