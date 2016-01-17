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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
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
import me.grada.ui.adapter.SignalDetailAdapter;
import me.grada.utils.DateTimeUtils;
import me.grada.utils.MapViewInteractor;
import me.grada.utils.ViewUtils;

/**
 * Created by yavorivanov on 30/12/2015.
 */
public class SignalDetailActivity extends BaseActivity implements OnMapReadyCallback {

    public static final String SIGNAL = "signal";

    @Inject
    Picasso picasso;

    @Bind(R.id.map_view)
    MapView mapView;

    @Bind(R.id.fab)
    FloatingActionButton fabView;

    @Bind(R.id.top_view_group)
    View topViewGroup;

    @Bind(R.id.image)
    ImageView imageView;

    @Bind(R.id.publish_date)
    TextView publishDateView;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    @Bind(R.id.title)
    TextView titleView;

    private MapViewInteractor mapViewInteractor;
    private GoogleMap googleMap;

    private Signal signal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signal_detail);

        signal = getIntent().getParcelableExtra(SIGNAL);

        Injector.INSTANCE.getImageFetcherComponent().inject(this);
        ButterKnife.bind(this);

        picasso.load(signal.getImages()[0]).into(imageView);

        // Gets the MapView from the XML layout and creates it
        mapView.onCreate(savedInstanceState);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this);
        mapView.getMapAsync(this);

        titleView.setText(signal.getTitle());
        publishDateView.setText(getString(R.string.reported,
                DateTimeUtils.getElapsedTime(signal.getDateCreated())));

        SignalDetailAdapter adapter = new SignalDetailAdapter(signal);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        final int elevation = getResources().getDimensionPixelSize(R.dimen.view_default_elevation);
        ViewCompat.setElevation(topViewGroup, elevation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        // zoomMapOut();

        final View contentView = findViewById(android.R.id.content);
        ViewUtils.addOnGlobalLayoutListener(contentView, new Runnable() {
            @Override
            public void run() {
                mapViewInteractor = new MapViewInteractor.Builder()
                        .topView(topViewGroup)
                        .bottomView(recyclerView)
                        .overlayView(recyclerView.getChildAt(0), true)
                        .closeFabView(fabView)
                        .googleMap(googleMap)
                        .build();
                mapViewInteractor
                        .animateTo(new LatLng(signal.getLocation()[0], signal.getLocation()[1]));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && mapViewInteractor.onBackPressed()) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!mapViewInteractor.onBackPressed()) {
            super.onBackPressed();
        }
    }

    private void zoomMapIn() {
        zoomMap(16);
    }

    private void zoomMapOut() {
        zoomMap(11);
    }

    private void zoomMap(int level) {
        LatLng latLng = new LatLng(signal.getLocation()[0], signal.getLocation()[1]);
        googleMap.addMarker(new MarkerOptions().position(latLng));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, level);
        googleMap.animateCamera(cameraUpdate);
    }
}
