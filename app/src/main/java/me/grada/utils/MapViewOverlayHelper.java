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

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yavorivanov on 08/01/2016.
 */
public class MapViewOverlayHelper extends GestureDetector.SimpleOnGestureListener {

    public interface Listener {

        void showFullScreenMap();

    }

    private final View mapViewOverlay;
    private final Listener listener;

    public MapViewOverlayHelper(View mapViewOverlay, Listener listener) {
        this.mapViewOverlay = mapViewOverlay;
        this.listener = listener;

        final GestureDetector gestureDetector = new GestureDetector(mapViewOverlay.getContext(), this);
        mapViewOverlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if (listener != null) {
            listener.showFullScreenMap();
            return true;
        }
        return super.onSingleTapConfirmed(e);
    }
}
