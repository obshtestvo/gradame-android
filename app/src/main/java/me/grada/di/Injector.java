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

package me.grada.di;

import android.content.Context;

import me.grada.di.component.AppComponent;
import me.grada.di.component.DaggerAppComponent;
import me.grada.di.component.DaggerImageFetcherComponent;
import me.grada.di.component.DaggerNetworkComponent;
import me.grada.di.component.ImageFetcherComponent;
import me.grada.di.component.NetworkComponent;
import me.grada.di.module.AppModule;
import me.grada.di.module.PicassoModule;

/**
 * Created by yavorivanov on 23/12/2015.
 */
public enum Injector {

    INSTANCE;

    private AppComponent appComponent;
    private NetworkComponent networkComponent;
    private ImageFetcherComponent imageFetcherComponent;

    public AppComponent initializeAppComponent(AppModule appModule) {
        appComponent = DaggerAppComponent.builder()
                .appModule(appModule)
                .build();
        return appComponent;
    }

    public void initializeNetworkComponent(AppComponent appComponent) {
        networkComponent = DaggerNetworkComponent.builder()
                .appComponent(appComponent)
                .build();
    }

    public void initializeImageFetcherComponent(Context context, AppModule appModule) {
        imageFetcherComponent = DaggerImageFetcherComponent.builder()
                .picassoModule(new PicassoModule(context))
                .appModule(appModule)
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public NetworkComponent getNetworkComponent() {
        return networkComponent;
    }

    public ImageFetcherComponent getImageFetcherComponent() {
        return imageFetcherComponent;
    }

}
