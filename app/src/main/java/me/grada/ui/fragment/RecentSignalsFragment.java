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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import me.grada.R;
import me.grada.di.Injector;
import me.grada.io.event.MockRecentSignalsFailure;
import me.grada.io.event.MockRecentSignalsSuccess;
import me.grada.io.model.Signal;
import me.grada.io.task.MockRecentSignalsTask;
import me.grada.ui.activity.SignalDetailActivity;
import me.grada.ui.adapter.RecentSignalsAdapter;
import me.grada.ui.view.MaterialProgressView;
import me.grada.utils.ViewUtils;

/**
 * Created by yavorivanov on 22/12/2015.
 */
public class RecentSignalsFragment extends BaseFragment
        implements RecentSignalsAdapter.OnClickListener  {

    @Inject
    Bus bus;

    @Bind(R.id.progress_view)
    MaterialProgressView progressView;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private RecentSignalsAdapter recentSignalsAdapter;

    public static RecentSignalsFragment newInstance() {
        return new RecentSignalsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.INSTANCE.getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recent_signals, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the recycler view
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recentSignalsAdapter = new RecentSignalsAdapter());
        recentSignalsAdapter.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
        new MockRecentSignalsTask().execute();
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onGetSignalsSuccess(MockRecentSignalsSuccess event) {
        ViewUtils.animateOut(progressView);
        recentSignalsAdapter.setData(event.getSignals());
    }

    @Subscribe
    public void onGetSignalsFailure(MockRecentSignalsFailure event) {
        Toast.makeText(getActivity(), "TODO: Handle error", Toast.LENGTH_SHORT).show();
        ViewUtils.animateOut(progressView);
    }

    @Override
    public void onClick(Signal signal) {
        Intent intent = new Intent(getActivity(), SignalDetailActivity.class);
        intent.putExtra(SignalDetailActivity.SIGNAL, signal);
        startActivity(intent);
    }

}
