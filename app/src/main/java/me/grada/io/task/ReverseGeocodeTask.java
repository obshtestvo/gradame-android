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

package me.grada.io.task;

import android.app.Application;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import me.grada.di.Injector;
import me.grada.io.event.ReverseGeocodingEvent;

/**
 * Created by yavorivanov on 16/01/2016.
 */
public class ReverseGeocodeTask extends AsyncTask<LatLng, Void, Address> {

    @Inject
    Application application;

    @Inject
    Bus bus;

    private final Geocoder geocoder;

    public ReverseGeocodeTask() {
        Injector.INSTANCE.getAppComponent().inject(this);
        geocoder = new Geocoder(application);
    }

    @Override
    protected Address doInBackground(LatLng... params) {
        LatLng latLng = params[0];

        List<Address> results = null;

        try {
            results = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (results != null && results.size() > 0) {
            return results.get(0);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Address address) {
        super.onPostExecute(address);
        if (address != null) {
            bus.post(new ReverseGeocodingEvent(address.getAddressLine(0)));
        }
    }
}
