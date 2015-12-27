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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import butterknife.Bind;
import me.grada.R;
import me.grada.ui.view.MaterialProgressView;
import me.grada.utils.ViewUtils;

/**
 * Created by yavorivanov on 22/12/2015.
 */
public class NearbySignalsFragment extends MapFragment implements OnMapReadyCallback {

    @Bind(R.id.progress_view)
    MaterialProgressView progressView;

    @Bind(R.id.map_view)
    MapView mapView;

    public static NearbySignalsFragment newInstance() {
        return new NearbySignalsFragment();
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
    public void onResume() {
        mapView.onResume();
        super.onResume();
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
}
