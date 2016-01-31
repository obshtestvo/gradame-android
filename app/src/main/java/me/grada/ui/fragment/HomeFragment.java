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

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import me.grada.R;
import me.grada.di.Injector;
import me.grada.io.event.LocationGrantedEvent;
import me.grada.io.event.LocationUpdateEvent;
import me.grada.io.event.ShowLocationRationaleEvent;
import me.grada.ui.activity.AddSignalActivity;
import me.grada.ui.adapter.HomePageAdapter;
import me.grada.utils.AttachmentHelper;
import me.grada.utils.LocationProvider;

/**
 * Created by yavorivanov on 28/12/2015.
 */
public class HomeFragment extends BaseFragment {

    @Inject
    Bus bus;

    @Bind(R.id.view_pager)
    ViewPager viewPager;

    @Bind(R.id.tab_layout)
    TabLayout tabLayout;

    @Bind(R.id.fab_speed_dial)
    FabSpeedDial fabSpeedDial;

    private boolean showMapPerspective;

    private MenuItem mapMenuItem;
    private MenuItem listMenuItem;

    private LocationFragment locationFragment;
    private HomePageAdapter homePageAdapter;


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.INSTANCE.getAppComponent().inject(this);
        setHasOptionsMenu(true);

        locationFragment = LocationFragment.newInstance();
        getChildFragmentManager().beginTransaction()
                .add(locationFragment, LocationFragment.TAG)
                .commitAllowingStateLoss();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mapMenuItem = menu.findItem(R.id.action_map_perspective);
        listMenuItem = menu.findItem(R.id.action_list_perspective);
        updateMenuItemVisibility();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_home, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        homePageAdapter = new HomePageAdapter(getChildFragmentManager());

        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_file) {
                    Intent intent = AttachmentHelper.getFileChooserIntent();
                    startActivityForResult(Intent.createChooser(intent, "Select a file"),
                            AttachmentHelper.TYPE_FILE_CHOOSER);
                } else if (menuItem.getItemId() == R.id.action_picture) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivityForResult(intent, AttachmentHelper.TYPE_PICTURE);
                    }
                } else {
                    startActivity(new Intent(getActivity(), AddSignalActivity.class));
                }
                return super.onMenuItemSelected(menuItem);
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list_perspective:
            case R.id.action_map_perspective:
                showMapPerspective = item.getItemId() == R.id.action_map_perspective;
                homePageAdapter.setShowMapPerspective(showMapPerspective);
                homePageAdapter.notifyDataSetChanged();
                updateMenuItemVisibility();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AttachmentHelper.TYPE_FILE_CHOOSER && resultCode == Activity.RESULT_OK) {
            Intent i = new Intent(getActivity(), AddSignalActivity.class);
            i.putExtra(AddSignalActivity.ATTACHMENT_URI, data.getData());
            startActivity(i);
        } else if (requestCode == AttachmentHelper.TYPE_PICTURE && resultCode == Activity.RESULT_OK) {
            Intent i = new Intent(getActivity(), AddSignalActivity.class);
            i.putExtra(AddSignalActivity.ATTACHMENT_URI, data.getData());
            startActivity(i);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();

        bus.register(this);

        // Ask for location permission if the target is Marshmallow or newer
        // else connect to Play Services Location API in order to get a location fix
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            locationFragment.getPermission();
        } else {
            locationFragment.connectToLocationProvider();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        locationFragment.disconnectLocationProvider();
        bus.unregister(this);
    }

    @Subscribe
    public void onLocationGrantedEvent(LocationGrantedEvent locationGrantedEvent) {

    }

    /**
     * Shows a {@link Snackbar} asking the user to enable location services.
     */
    @Subscribe
    public void onShowLocationRationaleEvent(ShowLocationRationaleEvent event) {
        Snackbar snackbar = Snackbar.make(getView(), getString(R.string.nearby_location_permission_rationale),
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
        // The odd case when the device doesn't have a last known location
        // Ignore for now, you only hit this case on the emulator
        Location location = event.getLocation();
        if (location == null) return;

        LocationProvider.INSTANCE.set(location);

        homePageAdapter.setShowMapPerspective(showMapPerspective);
        viewPager.setAdapter(homePageAdapter);

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_mood_bad_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_mood_black_24dp);
    }

    private void updateMenuItemVisibility() {
        mapMenuItem.setVisible(!showMapPerspective);
        listMenuItem.setVisible(showMapPerspective);
    }
}
