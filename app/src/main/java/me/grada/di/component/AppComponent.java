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

package me.grada.di.component;

import android.app.Application;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Component;
import me.grada.di.module.AppModule;
import me.grada.io.task.ReverseGeocodeTask;
import me.grada.ui.fragment.HomeFragment;
import me.grada.ui.fragment.LocationFragment;
import me.grada.ui.fragment.SignalMapViewFragment;
import me.grada.ui.fragment.SignalListViewFragment;
import me.grada.utils.MapViewInteractor;

/**
 * Base app component.  It's likely it will be used as a dependency / downstream component
 * hence expose dependencies as you see fit.
 *
 * Created by yavorivanov on 22/12/2015.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(HomeFragment homeFragment);

    void inject(SignalListViewFragment signalListViewFragment);

    void inject(SignalMapViewFragment signalMapViewFragment);

    void inject(LocationFragment locationFragment);

    void inject(ReverseGeocodeTask reverseGeocodeTask);

    void inject(MapViewInteractor mapViewInteractor);

    Application application();

    Bus bus();

}
