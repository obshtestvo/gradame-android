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

package me.grada.io.task;

import android.app.Application;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Bus;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import me.grada.di.Injector;
import me.grada.io.event.GetSignalsSuccess;
import me.grada.io.model.Signal;
import me.grada.utils.RecentSignalsComparator;

/**
 * Temporary task loading a bunch of mock signals from a local file.
 * <p/>
 * Created by yavorivanov on 23/12/2015.
 */
public class GetSignalsTask extends AsyncTask<Void, Void, List<Signal>> {

    @Inject
    Application application;

    @Inject
    Bus bus;

    @Inject
    Gson gson;

    @Inject
    public GetSignalsTask() {
        Injector.INSTANCE.getNetworkComponent().inject(this);
    }

    @Override
    protected List<Signal> doInBackground(Void... params) {
        // Mock network activity
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            InputStream is = application.getAssets().open("json/mock_signals.json");
            Type type = new TypeToken<ArrayList<Signal>>() {
            }.getType();
            ArrayList<Signal> signals = gson.fromJson(IOUtils.toString(is), type);
            // Order the signals whilst on the worker thread
            Collections.sort(signals, new RecentSignalsComparator());

            return signals;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Signal> signals) {
        bus.post(new GetSignalsSuccess(signals));
    }
}
