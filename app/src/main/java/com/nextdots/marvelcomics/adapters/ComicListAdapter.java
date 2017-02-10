package com.nextdots.marvelcomics.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.nextdots.marvelcomics.R;
import com.nextdots.marvelcomics.rest.comics.Result;
import com.nextdots.marvelcomics.utils.PFRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ComicListAdapter extends PFRecyclerViewAdapter<Result> {

    public ComicListAdapter(Context context, OnViewHolderClick listener) {
        super(context, listener);
    }

    @Override
    protected View createView(Context context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_comic, viewGroup, false);

        return view;
    }

    @Override
    protected void bindView(Result item, PFRecyclerViewAdapter.ViewHolder viewHolder) {
        if (item != null) {
            TextView tvNombre = (TextView) viewHolder.getView(R.id.tvNombre);
            TextView tvPrecio = (TextView) viewHolder.getView(R.id.tvPrecio);
            final ImageView ivComic = (ImageView) viewHolder.getView(R.id.ivComic);

            tvNombre.setText(item.getTitle());
            if(item.getPrices() != null && !item.getPrices().isEmpty()) {
                if(!item.getPrices().get(0).getPrice().equals(0.0)) {
                    tvPrecio.setText("$"+item.getPrices().get(0).getPrice().toString());
                } else {
                    tvPrecio.setText(getContext().getString(R.string.agotado));
                }
            } else {
                tvPrecio.setText(getContext().getString(R.string.agotado));
            }
            String image = item.getThumbnail().getPath()+"/standard_medium."+item.getThumbnail().getExtension();
            Glide.with(getContext()).load(image).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivComic) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    ivComic.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            RelativeLayout rlItem = (RelativeLayout) viewHolder.getView(R.id.rlItem);
            rlItem.setVisibility(View.GONE);
        }
    }

    public void filter(String query) {
        getList().clear();
        for (Result result: super.mListAux) {
            if(result.getTitle().toLowerCase().trim().contains(query.toLowerCase().trim())){
                getList().add(result);
            }
        }
        notifyDataSetChanged();
    }

    public void updateAuxList() {
        mListAux = new ArrayList<>(getList());
    }
}