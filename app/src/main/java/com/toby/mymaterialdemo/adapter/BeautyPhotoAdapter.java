package com.toby.mymaterialdemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.model.BeautyPhoto;
import com.toby.mymaterialdemo.ui.activity.PreviewBeautyPhotoActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/11/22 0022.
 */
public class BeautyPhotoAdapter extends RecyclerView.Adapter<BeautyPhotoAdapter.ViewHolder> {

    private List<BeautyPhoto> dataList;
    private Activity context;

    public BeautyPhotoAdapter(Activity context, List<BeautyPhoto> dataList) {
        this.context = context;
        if (dataList == null) {
            this.dataList = new ArrayList<>();
        } else {
            this.dataList = dataList;
        }
    }

    public List<BeautyPhoto> getDataList() {
        return dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_beauty_photo_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BeautyPhoto beautyPhoto = dataList.get(position);
        final String url = beautyPhoto.getUrl();
        holder.textDesc.setText(String.format("福利:%s", beautyPhoto.getDesc()));
        holder.textAuthor.setText(String.format("via:%s", beautyPhoto.getWho()));
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .into(holder.imgBeauty);
        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PreviewBeautyPhotoActivity.class);
                intent.putExtra("picUrl", url);
                context.startActivity(intent);
                context.overridePendingTransition(R.anim.zoom_enter, 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.layoutItem)
        LinearLayout layoutItem;
        @Bind(R.id.imgBeauty)
        ImageView imgBeauty;
        @Bind(R.id.textDesc)
        TextView textDesc;
        @Bind(R.id.textAuthor)
        TextView textAuthor;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
