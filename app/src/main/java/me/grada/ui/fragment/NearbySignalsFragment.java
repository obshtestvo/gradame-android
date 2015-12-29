/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Obshtestvo
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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import me.grada.R;
import me.grada.di.Injector;
import me.grada.io.event.NearbySignalsInBackground;
import me.grada.io.event.NearbySignalsInForeground;
import me.grada.ui.view.MaterialProgressView;
import me.grada.utils.ViewUtils;

/**
 * Created by yavorivanov on 22/12/2015.
 */
public class NearbySignalsFragment extends BaseFragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST = 1;

    @Inject
    Bus bus;

    @Bind(R.id.progress_view)
    MaterialProgressView progressView;

    @Bind(R.id.map_view)
    MapView mapView;

    /**
     * Used as a prompt for granting location permission on >= Marshmallow
     */
    private Snackbar snackbar;

    public static NearbySignalsFragment newInstance() {
        return new NearbySignalsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.INSTANCE.getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nearby_signals, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Gets the MapView from the XML layout and creates it
        mapView.onCreate(savedInstanceState);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());
        mapView.getMapAsync(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newLatLngZoom(new LatLng(42.6603369, 23.283244), 12);
        googleMap.animateCamera(cameraUpdate);
        ViewUtils.animateOut(progressView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            // Received the permission for the location provider
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO: Get location fix
            } else {
                // Permission denied, we can't use the location provider
                // Determine if the user has ticked 'Never ask again'
                boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                if (showRationale) {
                    showLocationPermissionRationale();
                } else {
                    // TODO: Replace the MapView with an in-line error to indicate that
                    // the functionality doesn't work without the location service enabled
                }
            }
        }
    }

    @Subscribe
    public void onNearbySignalsSelected(NearbySignalsInForeground event) {
        getLocationPermission();
    }

    @Subscribe
    public void onNearbySignalsSelected(NearbySignalsInBackground event) {
        // Hide the snackbar, if present
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    private void getLocationPermission() {
        // Check if the target is Marshmallow, or newer, and proceed
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Get location fix
            } else {
                // Request permission to use location providers
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showLocationPermissionRationale();
                    // TODO: Explain why the permission is needed (occurs after previous rejection)
                } else {
                    promptLocationPermission();
                }
            }
        }
    }

    /**
     * Shows a {@link Snackbar} asking the user to enable location services.
     */
    private void showLocationPermissionRationale() {
        snackbar = Snackbar.make(getView(), getString(R.string.nearby_location_permission_rationale),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.enable_location), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        promptLocationPermission();
                    }
                });
        snackbar.show();
    }

    /**
     * Shows the standard Android dialog view prompting for location permission
     */
    private void promptLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST);
    }

}
