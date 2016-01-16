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

package me.grada.ui.activity;

import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.grada.R;
import me.grada.di.Injector;
import me.grada.io.event.LocationUpdateEvent;
import me.grada.io.event.ReverseGeocodingEvent;
import me.grada.io.event.ShowLocationRationaleEvent;
import me.grada.io.task.ReverseGeocodeTask;
import me.grada.ui.fragment.LocationFragment;
import me.grada.utils.MapViewInteractor;
import me.grada.utils.ViewUtils;

/**
 * Created by yavorivanov on 05/01/2016.
 */
public class AddSignalActivity extends BaseActivity implements OnMapReadyCallback {

    public static final String ATTACHMENT_URI = "attachment_uri";

    @Inject
    Picasso picasso;

    @Inject
    Bus bus;

    @Bind(R.id.top_view_group)
    ViewGroup topViewGroup;

    @Bind(R.id.bottom_view_group)
    ViewGroup bottomViewGroup;

    @Bind(R.id.map_view)
    MapView mapView;

    @Bind(R.id.fab)
    FloatingActionButton fabView;

    @Bind(R.id.map_view_overlay)
    View mapViewOverlay;

    @Bind(R.id.category_view)
    Spinner categoryView;

    @Bind(R.id.address_view)
    EditText addressView;

    @Bind(R.id.attachment)
    ImageView attachmentView;

    private Snackbar snackbar;
    private LocationFragment locationFragment;

    private MapViewInteractor mapViewInteractor;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_signal);

        Injector.INSTANCE.getImageFetcherComponent().inject(this);
        ButterKnife.bind(this);

        // Gets the MapView from the XML layout and creates it
        mapView.onCreate(savedInstanceState);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this);
        mapView.getMapAsync(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_dropdown_item);
        categoryView.setAdapter(categoryAdapter);

        if (getIntent().hasExtra(ATTACHMENT_URI)) {
            Uri uri = getIntent().getParcelableExtra(ATTACHMENT_URI);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                attachmentView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        locationFragment = LocationFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(locationFragment, LocationFragment.TAG)
                .commitAllowingStateLoss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            locationFragment.getPermission();
        } else {
            locationFragment.connectToLocationProvider();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStop() {
        locationFragment.disconnectLocationProvider();
        bus.unregister(this);
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && mapViewInteractor.onBackPressed()) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        final View contentView = findViewById(android.R.id.content);
        ViewUtils.addOnGlobalLayoutListener(contentView, new Runnable() {
            @Override
            public void run() {
                mapViewInteractor = new MapViewInteractor.Builder()
                        .topView(topViewGroup)
                        .bottomView(bottomViewGroup)
                        .overlayView(mapViewOverlay)
                        .fabView(fabView)
                        .googleMap(googleMap)
                        .build();
            }
        });
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

    public void onBackPressed() {
        if (!mapViewInteractor.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.submit_view)
    public void onSubmit(View view) {
        mapViewInteractor.animateTo(new LatLng(51.4572006, 0.0306386));
    }

    @Subscribe
    public void onShowLocationRationaleEvent(ShowLocationRationaleEvent event) {
        snackbar = Snackbar.make(findViewById(android.R.id.content),
                getString(R.string.nearby_location_permission_rationale),
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
    public void onReverseGeocodingEvent(ReverseGeocodingEvent event) {
        addressView.setText(event.getAddress());
    }


    @Subscribe
    public void onLocationUpdateEvent(LocationUpdateEvent event) {
        Location location = event.getLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mapViewInteractor.animateTo(latLng);
        new ReverseGeocodeTask().execute(latLng);
    }
}
