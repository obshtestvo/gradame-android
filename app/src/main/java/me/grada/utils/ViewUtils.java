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

package me.grada.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;

/**
 * Created by yavorivanov on 23/12/2015.
 */
public class ViewUtils {

    public static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();

    private ViewUtils() {
    }

    /**
     * Resolves a color resource taking into account {@link android.os.Build.VERSION#SDK_INT}.
     *
     * @param resources Activity's resources.
     * @param color     Color's resource id.
     * @return Color's hex.
     */
    public static int getColor(Resources resources, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return resources.getColor(color, null);
        } else {
            return resources.getColor(color);
        }
    }

    /**
     * Sets a drawable as background taking into account {@link android.os.Build.VERSION#SDK_INT}.
     *
     * @param view     The view to receive a new background.
     * @param drawable The drawable to be set as background.
     */
    public static void setBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    /**
     * Animates in the passed in view by scaling its x and y in to 1.0f and setting its alpha to 1.0f.
     *
     * @param view The view to receive the animation.
     */
    public static void animateIn(final View view) {
        if (view.getVisibility() != View.VISIBLE) {
            if (ViewCompat.isLaidOut(view) && !view.isInEditMode()) {
                view.setScaleX(0.0f);
                view.setScaleY(0.0f);
                view.setAlpha(0.0f);
                view.animate()
                        .scaleX(1.0F)
                        .scaleY(1.0F)
                        .alpha(1.0F)
                        .setDuration(200L)
                        .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                view.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }
    }

    /**
     * Animates out the passed in view by scaling its x and y out to .0f and setting its alpha to .0f.
     *
     * @param view The view to receive the animation.
     */
    public static void animateOut(final View view) {
        if (view.getVisibility() == View.VISIBLE) {
            if (ViewCompat.isLaidOut(view) && !view.isInEditMode()) {
                view.animate()
                        .scaleX(0.0F)
                        .scaleY(0.0F)
                        .alpha(0.0F)
                        .setDuration(200L)
                        .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                view.setVisibility(View.GONE);
                            }
                        });
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }

    public static void switchVisibility(final View invisibleView, final View visibleView) {
        invisibleView.setAlpha(0.0f);
        invisibleView.animate()
                .alpha(1.0F)
                .setDuration(200L)
                .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        invisibleView.setVisibility(View.VISIBLE);
                    }
                });

        visibleView.animate()
                .alpha(0.0F)
                .setDuration(200L)
                .setInterpolator(FAST_OUT_SLOW_IN_INTERPOLATOR)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        visibleView.setVisibility(View.GONE);
                    }
                });

    }

    public static void addOnGlobalLayoutListener(@NonNull final View listenerReceiver,
                                                 @NonNull final Runnable runnable) {
        listenerReceiver.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                            listenerReceiver.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            listenerReceiver.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                        runnable.run();
                    }
                });

    }

}
