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

package me.grada.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AndroidRuntimeException;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import me.grada.di.Injector;
import me.grada.io.event.ReverseGeocodingEvent;
import me.grada.io.task.ReverseGeocodeTask;

/**
 * Created by yavorivanov on 10/01/2016.
 */
public class MapViewInteractor extends GestureDetector.SimpleOnGestureListener {

    @Inject
    Bus bus;

    private final View topView;
    private final View bottomView;
    private final View overlayView;
    private final View closeFabView;
    private final View editFabView;
    private final View editAddressView;
    private final View placeView;

    private final boolean isEditable;
    private final GoogleMap googleMap;
    private LatLng latLng;
    private Marker marker;
    private boolean isMapFullScreen;

    private int mediumAnimDuration;
    private int lngFullScreenTarget;
    private int lngPartScreenTarget;
    private int latScreenTarget;

    private MapViewInteractor(View topView,
                              View bottomView,
                              View overlayView,
                              View closeFabView,
                              View editFabView,
                              View editAddressView,
                              View placeView,
                              GoogleMap googleMap,
                              boolean isEditable) {
        this.topView = topView;
        this.bottomView = bottomView;
        this.overlayView = overlayView;
        this.closeFabView = closeFabView;
        this.editFabView = editFabView;
        this.editAddressView = editAddressView;
        this.placeView = placeView;
        this.googleMap = googleMap;
        this.isEditable = isEditable;
    }

    private void setUp(boolean isOverlayPartOfBottomView) {
        Injector.INSTANCE.getAppComponent().inject(this);

        if (isEditable()) {
            bus.register(this);

            editFabView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enableEditAddress(true);
                }
            });

        }

        if (latLng != null) {
            new ReverseGeocodeTask().execute(latLng);
        }

        // Determine the view coordinates of the latitude and longitude for both
        // part and full screen modes
        latScreenTarget = topView.getRight() / 2;
        lngFullScreenTarget = bottomView.getBottom() / 2;
        if (isOverlayPartOfBottomView) {
            lngPartScreenTarget = overlayView.getBottom() - ((overlayView.getBottom() - topView.getBottom()) / 2);
        } else {
            lngPartScreenTarget = bottomView.getTop() - ((bottomView.getTop() - topView.getBottom()) / 2);

        }

        // Determine the animation constants
        mediumAnimDuration = topView.getResources().getInteger(android.R.integer.config_mediumAnimTime);

        // Set up the various listeners
        closeFabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final GestureDetector gestureDetector = new GestureDetector(overlayView.getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        animateShowMap();
                        animateShowView(closeFabView);

                        if (isEditable()) {
                            animateShowView(editFabView);
                            animateShowView(editAddressView);
                        }

                        return true;
                    }
                });

        overlayView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

    }

    public void onStart() {
        bus.register(this);
    }

    public void onStop() {
        bus.unregister(this);
    }

    private void animateShowMap() {
        isMapFullScreen = true;
        overlayView.setClickable(false);
        overlayView.setEnabled(false);

        topView.animate()
                .translationY((float) -topView.getHeight())
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(mediumAnimDuration)
                .start();

        bottomView.animate()
                .translationY((float) bottomView.getHeight())
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(mediumAnimDuration)
                .start();

        centerLatLng(latScreenTarget, lngFullScreenTarget);
    }

    private void animateShowView(final View view) {
        view.setScaleX(0.25f);
        view.setScaleY(0.25f);

        view.animate()
                .setStartDelay(mediumAnimDuration)
                .setDuration(mediumAnimDuration)
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                })
                .start();
    }

    private void animateHideMap() {
        isMapFullScreen = false;
        overlayView.setClickable(true);
        overlayView.setEnabled(true);

        topView.animate()
                .translationY(0.25f)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(mediumAnimDuration)
                .start();

        bottomView.animate()
                .translationY(0.25f)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(mediumAnimDuration)
                .start();

        centerLatLng(latScreenTarget, lngPartScreenTarget);
    }

    private void animateHideView(final View view) {
        view.animate()
                .scaleX(0.25f)
                .scaleY(0.25f)
                .setDuration(mediumAnimDuration / 2)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    public boolean onBackPressed() {
        if (isMapFullScreen) {
            enableEditAddress(false);
            animateHideView(closeFabView);

            if (isEditable()) {
                animateHideView(editFabView);
                animateHideView(editAddressView);
                // Post back the latest address to the full-screen view
                bus.post(new ReverseGeocodingEvent(((EditText) editAddressView).getText().toString()));
            }

            animateHideMap();
            return true;
        }
        return false;
    }

    public void animateTo(@NonNull LatLng latLng) {
        if (latLng.equals(this.latLng)) return;
        this.latLng = latLng;

        googleMap.clear();

        if (isMapFullScreen) {
            centerLatLng(latScreenTarget, lngFullScreenTarget);
        } else {
            centerLatLng(latScreenTarget, lngPartScreenTarget);
        }
    }

    private void centerLatLng(int viewX, int viewY) {
        if (latLng == null) return;
        if (marker == null) {
            marker = googleMap.addMarker(new MarkerOptions().position(latLng));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
        Point point = googleMap.getProjection().toScreenLocation(latLng);
        googleMap.animateCamera(CameraUpdateFactory.scrollBy(point.x - viewX, point.y - viewY));
    }

    private void enableEditAddress(boolean enable) {
        if (!isEditable()) return;

        editAddressView.setEnabled(enable);

        if (enable) {
            animateShowView(placeView);
            googleMap.clear();
            marker = null;
            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    latLng = cameraPosition.target;
                    new ReverseGeocodeTask().execute(latLng);
                }
            });
        } else {
            googleMap.setOnCameraChangeListener(null);
            animateHideView(placeView);
        }

    }

    private boolean isEditable() {
        return isEditable;
    }

    @Subscribe
    public void onReverseGeocodingEvent(ReverseGeocodingEvent event) {
        if (isEditable()) {
            ((EditText) editAddressView).setText(event.getAddress());
        }
    }

    public static class Builder {

        private View topView;
        private View bottomView;
        private View overlayView;
        private GoogleMap googleMap;
        private View closeFabView;
        private View editFabView;
        private View placeView;
        private View editAddressView;
        private boolean isEditable;
        private boolean isOverlayParOfBottomView;

        public Builder topView(View topView) {
            this.topView = topView;
            return this;
        }

        public Builder bottomView(View bottomView) {
            this.bottomView = bottomView;
            return this;
        }

        public Builder overlayView(View overlayView) {
            return overlayView(overlayView, false);
        }

        public Builder overlayView(View overlayView, boolean isPartOfBottomView) {
            this.overlayView = overlayView;
            this.isOverlayParOfBottomView = isPartOfBottomView;
            return this;
        }

        public Builder closeFabView(View closeFabView) {
            this.closeFabView = closeFabView;
            return this;
        }

        public Builder editModeViews(View editFabView, View editAddressView, View markerView) {
            this.editFabView = editFabView;
            this.editAddressView = editAddressView;
            this.placeView = markerView;
            isEditable = true;
            return this;
        }

        public Builder googleMap(GoogleMap googleMap) {
            this.googleMap = googleMap;
            return this;
        }

        public MapViewInteractor build() {
            if (topView == null) {
                throw new AndroidRuntimeException("You must set topView.");
            }

            if (bottomView == null) {
                throw new AndroidRuntimeException("You must set bottomView.");
            }

            if (overlayView == null) {
                throw new AndroidRuntimeException("You must set overlayView.");
            }

            if (closeFabView == null) {
                throw new AndroidRuntimeException("You must set closeFabView.");
            }

            if (googleMap == null) {
                throw new AndroidRuntimeException("You must set googleMap.");
            }

            if (closeFabView == null) {
                throw new AndroidRuntimeException("You must set closeFabView.");
            }

            MapViewInteractor mapViewInteractor =
                    new MapViewInteractor(topView, bottomView, overlayView, closeFabView,
                            editFabView, editAddressView, placeView, googleMap, isEditable);
            mapViewInteractor.setUp(isOverlayParOfBottomView);
            return mapViewInteractor;
        }

    }

}
