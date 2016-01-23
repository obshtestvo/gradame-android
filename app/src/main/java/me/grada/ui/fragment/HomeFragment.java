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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;
import me.grada.R;
import me.grada.di.Injector;
import me.grada.ui.activity.AddSignalActivity;
import me.grada.ui.adapter.HomePageAdapter;
import me.grada.utils.AttachmentHelper;

/**
 * Created by yavorivanov on 28/12/2015.
 */
public class HomeFragment extends BaseFragment {

    private static final int NEARBY_PAGE_INDEX = 1;

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

    private HomePageAdapter homePageAdapter;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.INSTANCE.getAppComponent().inject(this);
        setHasOptionsMenu(true);
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
        homePageAdapter.setShowMapPerspective(showMapPerspective);
        viewPager.setAdapter(homePageAdapter);

        // Give the TabLayout the ViewPager
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_mood_bad_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_mood_black_24dp);

        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_file) {
                    Intent intent = AttachmentHelper.getFileChooserIntent();
                    startActivityForResult(Intent.createChooser(intent, "Select a file"),
                            AttachmentHelper.TYPE_FILE_CHOOSER);
                } else {
                    startActivity(new Intent(getActivity(), AddSignalActivity.class));
                }
                return super.onMenuItemSelected(menuItem);
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showMapPerspective = item.getItemId() == R.id.action_map_perspective;
        homePageAdapter.setShowMapPerspective(showMapPerspective);
        homePageAdapter.notifyDataSetChanged();
        updateMenuItemVisibility();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AttachmentHelper.TYPE_FILE_CHOOSER && resultCode == Activity.RESULT_OK) {
            Intent i = new Intent(getActivity(), AddSignalActivity.class);
            i.putExtra(AddSignalActivity.ATTACHMENT_URI, data.getData());
            startActivity(i);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateMenuItemVisibility() {
        mapMenuItem.setVisible(!showMapPerspective);
        listMenuItem.setVisible(showMapPerspective);
    }
}
