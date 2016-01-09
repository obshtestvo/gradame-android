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
import android.support.v4.view.animation.FastOutSlowInInterpolator;
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
import butterknife.OnClick;
import me.grada.R;
import me.grada.di.Injector;
import me.grada.io.model.Signal;
import me.grada.ui.adapter.SignalDetailAdapter;
import me.grada.utils.DateTimeUtils;

/**
 * Created by yavorivanov on 30/12/2015.
 */
public class SignalDetailActivity extends BaseActivity implements OnMapReadyCallback,
        SignalDetailAdapter.OnClickListener {

    public static final String SIGNAL = "signal";

    @Inject
    Picasso picasso;

    @Bind(R.id.map_view)
    MapView mapView;

    @Bind(R.id.signal_location_fab)
    FloatingActionButton signalLocationFab;

    @Bind(R.id.my_location_fab)
    FloatingActionButton myLocationFab;

    @Bind(R.id.close_map_fab)
    FloatingActionButton closeMapFab;

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

    private GoogleMap googleMap;

    private Signal signal;

    private boolean isMapFullScreen;

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

        SignalDetailAdapter adapter = new SignalDetailAdapter(signal, this);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        final int elevation = getResources().getDimensionPixelSize(R.dimen.view_default_elevation);
        ViewCompat.setElevation(topViewGroup, elevation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        zoomMapOut();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && isMapFullScreen) {
            animateHideMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showFullScreenMap() {
        animateShowMap();
    }

    @Override
    public void onBackPressed() {
        if (isMapFullScreen) {
            animateHideMap();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.signal_location_fab)
    public void onSignalLocationFab(View view) {
        zoomMapIn();
    }

    @OnClick(R.id.my_location_fab)
    public void onMyLocationFab(View view) {

    }

    @OnClick(R.id.close_map_fab)
    public void onCloseFullScreenMap(View view) {
        animateHideMap();
    }

    private void animateShowMap() {
        isMapFullScreen = true;

        int animDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        ViewCompat.animate(topViewGroup)
                .translationY((float) -topViewGroup.getHeight())
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animDuration)
                .start();

        ViewCompat.animate(recyclerView)
                .translationY((float) recyclerView.getHeight())
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animDuration)
                .start();

        ViewCompat.setScaleX(closeMapFab, 0.25f);
        ViewCompat.setScaleY(closeMapFab, 0.25f);

        animateFabsIn();
        zoomMapIn();
    }

    private void animateFabsIn() {
        signalLocationFab.setVisibility(View.VISIBLE);
        myLocationFab.setVisibility(View.VISIBLE);
        closeMapFab.setVisibility(View.VISIBLE);

        int animDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        int delay = animDuration / 2;

        ViewCompat.setAlpha(signalLocationFab, 0.0f);
        ViewCompat.setScaleX(signalLocationFab, 0.25f);
        ViewCompat.setScaleY(signalLocationFab, 0.25f);

        ViewCompat.setAlpha(myLocationFab, 0.0f);
        ViewCompat.setScaleX(myLocationFab, 0.25f);
        ViewCompat.setScaleY(myLocationFab, 0.25f);

        ViewCompat.setScaleX(closeMapFab, 0.25f);
        ViewCompat.setScaleY(closeMapFab, 0.25f);

        ViewCompat.animate(signalLocationFab)
                .setStartDelay(delay)
                .scaleX(1f)
                .scaleY(1f)
                .alpha(.5f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animDuration)
                .start();

        ViewCompat.animate(myLocationFab)
                .setStartDelay(delay)
                .scaleX(1f)
                .scaleY(1f)
                .alpha(.5f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animDuration)
                .start();

        ViewCompat.animate(closeMapFab)
                .setStartDelay(delay)
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animDuration)
                .start();
    }

    private void animateHideMap() {
        isMapFullScreen = false;

        int animDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        ViewCompat.animate(topViewGroup)
                .translationY(0.25f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animDuration)
                .start();

        ViewCompat.animate(recyclerView)
                .translationY(0.25f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animDuration)
                .start();

        animateFabsOut();
        zoomMapOut();
    }

    private void animateFabsOut() {
        int animDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        ViewCompat.animate(signalLocationFab)
                .scaleX(0.25f)
                .scaleY(0.25f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animDuration)
                .start();

        ViewCompat.animate(myLocationFab)
                .scaleX(0.25f)
                .scaleY(0.25f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animDuration)
                .start();

        ViewCompat.animate(closeMapFab)
                .scaleX(0.25f)
                .scaleY(0.25f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(animDuration)
                .start();

        signalLocationFab.setVisibility(View.GONE);
        myLocationFab.setVisibility(View.GONE);
        closeMapFab.setVisibility(View.GONE);
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
