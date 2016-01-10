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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import me.grada.R;
import me.grada.di.Injector;
import me.grada.io.event.LocationUpdateEvent;
import me.grada.io.event.MockNearbySignalsFailure;
import me.grada.io.event.MockNearbySignalsSuccess;
import me.grada.io.event.NearbySignalsInBackground;
import me.grada.io.event.NearbySignalsInForeground;
import me.grada.io.event.ShowLocationRationaleEvent;
import me.grada.io.model.Signal;
import me.grada.io.task.MockNearbySignalsTask;
import me.grada.ui.activity.SignalDetailActivity;
import me.grada.ui.view.MaterialProgressView;
import me.grada.utils.ViewUtils;

/**
 * Created by yavorivanov on 22/12/2015.
 */
public class NearbySignalsFragment extends BaseFragment implements OnMapReadyCallback {

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

    private GoogleMap googleMap;

    private Map<Marker, Signal> markerSignalMap = new HashMap<>();

    private LocationFragment locationFragment;

    public static NearbySignalsFragment newInstance() {
        return new NearbySignalsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.INSTANCE.getAppComponent().inject(this);

        locationFragment = LocationFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .add(locationFragment, LocationFragment.TAG)
                .commitAllowingStateLoss();
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
        this.googleMap = googleMap;

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getActivity(), SignalDetailActivity.class);
                intent.putExtra(SignalDetailActivity.SIGNAL, markerSignalMap.get(marker));
                startActivity(intent);
            }
        });

        ViewUtils.animateOut(progressView);
    }

    @Subscribe
    public void onNearbySignalsInForeground(NearbySignalsInForeground event) {
        // Ask for location permission if the target is Marshmallow or newer
        // else connect to Play Services Location API in order to get a location fix
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            locationFragment.getPermission();
        } else {
            locationFragment.connectToLocationProvider();
        }
    }

    @Subscribe
    public void onNearbySignalsInBackground(NearbySignalsInBackground event) {
        // Hide the snackbar, if present
        if (snackbar != null) {
            snackbar.dismiss();
        }
        locationFragment.disconnectLocationProvider();
    }

    /**
     * Shows a {@link Snackbar} asking the user to enable location services.
     */
    @Subscribe
    public void onShowLocationRationaleEvent(ShowLocationRationaleEvent event) {
        snackbar = Snackbar.make(getView(), getString(R.string.nearby_location_permission_rationale),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.enable_location), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        locationFragment.promptLocationPermission();
                    }
                });
        snackbar.show();
    }

    @Subscribe
    public void onLocationUpdateEvent(LocationUpdateEvent event) {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        new MockNearbySignalsTask().execute();

        googleMap.setMyLocationEnabled(true);

        // The odd case when the device doesn't have a last known location
        // Ignore for now, you only hit this case on the emulator
        Location location = event.getLocation();
        if (location == null) return;

        final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        googleMap.animateCamera(cameraUpdate);
    }

    @Subscribe
    public void onMockNearbySignalsSuccess(MockNearbySignalsSuccess event) {
        // Add the markers to the map
        List<Signal> signalList = event.getSignals();
        List<Marker> markerList = new ArrayList<>(signalList.size());

        for (int i = 0; i < signalList.size(); i++) {
            Signal signal = signalList.get(i);
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(signal.getLocation()[0], signal.getLocation()[1]))
                    .title(signal.getTitle()));
            markerList.add(marker);
            markerSignalMap.put(marker, signal);
        }

        // Calculate the bounds of all nearby signals
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = getResources().getDimensionPixelSize(R.dimen.keyline_1);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2 * padding);
        googleMap.animateCamera(cameraUpdate);
    }

    @Subscribe
    public void onMockNearbySignalsFailure(MockNearbySignalsFailure event) {
        // TODO: Handle error
    }

}
