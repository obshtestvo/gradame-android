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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by yavorivanov on 10/01/2016.
 */
public class MapViewInteractor extends GestureDetector.SimpleOnGestureListener {

    private final View topView;
    private final View bottomView;
    private final View overlayView;
    private final View fabView;

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
                              View fabView,
                              GoogleMap googleMap) {
        this.topView = topView;
        this.bottomView = bottomView;
        this.overlayView = overlayView;
        this.fabView = fabView;
        this.googleMap = googleMap;
    }

    private void setUp(boolean isOverlayPartOfBottomView) {
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
        fabView.setOnClickListener(new View.OnClickListener() {
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
                        animateShowFab();
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

    private void animateShowFab() {
        fabView.setScaleX(0.25f);
        fabView.setScaleY(0.25f);

        fabView.animate()
                .setStartDelay(mediumAnimDuration)
                .setDuration(mediumAnimDuration)
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        fabView.setVisibility(View.VISIBLE);
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

    private void animateHideFab() {
        fabView.animate()
                .scaleX(0.25f)
                .scaleY(0.25f)
                .setDuration(mediumAnimDuration / 2)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        fabView.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    public boolean onBackPressed() {
        if (isMapFullScreen) {
            animateHideFab();
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

    public static class Builder {

        private View topView;
        private View bottomView;
        private View overlayView;
        private GoogleMap googleMap;
        private View fabView;
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

        public Builder fabView(View fabView) {
            this.fabView = fabView;
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

            if (fabView == null) {
                throw new AndroidRuntimeException("You must set fabView.");
            }

            if (googleMap == null) {
                throw new AndroidRuntimeException("You must set googleMap.");
            }

            if (fabView == null) {
                throw new AndroidRuntimeException("You must set fabView.");
            }

            MapViewInteractor mapViewInteractor =
                    new MapViewInteractor(topView, bottomView, overlayView, fabView, googleMap);
            mapViewInteractor.setUp(isOverlayParOfBottomView);
            return mapViewInteractor;
        }

    }

}
