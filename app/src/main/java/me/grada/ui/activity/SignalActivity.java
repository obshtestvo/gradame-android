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

package me.grada.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.grada.R;
import me.grada.di.Injector;
import me.grada.io.model.Signal;

/**
 * Created by yavorivanov on 30/12/2015.
 */
public class SignalActivity extends BaseActivity implements OnMapReadyCallback {

    public static final String SIGNAL = "signal";

    @Inject
    Picasso picasso;

    @Bind(R.id.map_view)
    MapView mapView;

    @Bind(R.id.image_view)
    ImageView topView;

    @Bind(R.id.bottom_view_group)
    View bottomViewGroup;

    @Bind(R.id.title)
    TextView titleView;

    @Bind(R.id.description)
    TextView descriptionView;

    private Signal signal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal);

        signal = getIntent().getParcelableExtra(SIGNAL);

        Injector.INSTANCE.getImageFetcherComponent().inject(this);
        ButterKnife.bind(this);

        picasso.load(signal.getImages()[0]).into(topView);

        // Gets the MapView from the XML layout and creates it
        mapView.onCreate(savedInstanceState);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this);
        mapView.getMapAsync(this);

        titleView.setText(signal.getTitle());
        descriptionView.setText(signal.getDescription());

        final int elevation = getResources().getDimensionPixelSize(R.dimen.view_default_elevation);
        ViewCompat.setElevation(topView, elevation);
        ViewCompat.setElevation(bottomViewGroup, elevation);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(signal.getLocation()[0], signal.getLocation()[1]);
        googleMap.addMarker(new MarkerOptions()
                .position(latLng));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 14);
        googleMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
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



}
