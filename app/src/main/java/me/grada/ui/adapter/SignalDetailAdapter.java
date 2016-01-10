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

package me.grada.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.grada.R;
import me.grada.io.model.Signal;

/**
 * Created by yavorivanov on 08/01/2016.
 */
public class SignalDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TRANSPARENT_VIEW_TYPE = 0;
    private static final int DESCRIPTION_VIEW_TYPE = 1;

    private final Signal signal;

    public SignalDetailAdapter(Signal signal) {
        this.signal = signal;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TRANSPARENT_VIEW_TYPE) {
            View transparentView = layoutInflater
                    .inflate(R.layout.list_item_transparent_view, parent, false);
            return new TransparentViewHolder(transparentView);
        } else {
            View descriptionView = layoutInflater
                    .inflate(R.layout.list_item_description_view, parent, false);
            return new DescriptionViewHolder(descriptionView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int viewType = getItemViewType(position);
        if (viewType == DESCRIPTION_VIEW_TYPE) {
            ((DescriptionViewHolder) holder).description.setText(signal.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TRANSPARENT_VIEW_TYPE;
        }
        return DESCRIPTION_VIEW_TYPE;
    }

    public class TransparentViewHolder extends RecyclerView.ViewHolder {

        public TransparentViewHolder(View itemView) {
            super(itemView);
        }

    }

    public class DescriptionViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.description)
        TextView description;

        public DescriptionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
