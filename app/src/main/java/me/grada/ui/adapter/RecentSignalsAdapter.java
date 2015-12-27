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

package me.grada.ui.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.grada.R;
import me.grada.di.Injector;
import me.grada.io.model.Signal;

/**
 * Created by yavorivanov on 27/12/2015.
 */
public class RecentSignalsAdapter extends RecyclerView.Adapter<RecentSignalsAdapter.ViewHolder> {

    @Inject
    Picasso picasso;

    private List<Signal> signals = new ArrayList<>();

    /**
     * Index of the most recent rendered view.
     */
    private int lastRenderedView = -1;

    public RecentSignalsAdapter() {
        Injector.INSTANCE.getImageFetcherComponent().inject(this);
    }

    public void setData(List<Signal> signals) {
        this.signals = signals;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View container = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_signal, parent, false);
        return new ViewHolder(container);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Signal signal = signals.get(position);

        picasso.load(signal.getImages()[0]).into(holder.imageView);

        // Populate the views
        holder.descriptionView.setText(signal.getDescription());

        // Only animate the bottom view
        if (position > lastRenderedView) {
            Animation animation = AnimationUtils.loadAnimation(holder.cardView.getContext(),
                    R.anim.default_recycler_view_anim);
            holder.cardView.startAnimation(animation);
            lastRenderedView = position;
        }
    }

    @Override
    public int getItemCount() {
        return signals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.card_view)
        CardView cardView;

        @Bind(R.id.image_view)
        ImageView imageView;

        @Bind(R.id.description)
        TextView descriptionView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


    }

}
