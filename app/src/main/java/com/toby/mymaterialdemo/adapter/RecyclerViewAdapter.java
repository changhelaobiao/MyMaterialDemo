package com.toby.mymaterialdemo.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.model.MainData;
import com.toby.mymaterialdemo.ui.activity.WordsDetailActivity;
import com.toby.mymaterialdemo.utils.AppUtils;
import com.toby.mymaterialdemo.utils.LogUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/11/22 0022.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<MainData> mMainList;
    private Activity activity;

    public RecyclerViewAdapter(Activity activity, List<MainData> mainList) {
        this.activity = activity;
        this.mMainList = mainList;
    }

    public List<MainData> getmMainList() {
        return mMainList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.recycler_view_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MainData mainData = mMainList.get(position);
        final String picUrl = mainData.getImageUrl();
        final String title = mainData.getTitle();
//        LogUtils.d("-----picUrl---->" + picUrl);
        Picasso.with(activity)
                .load(picUrl)
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .into(holder.imageView);
        holder.title.setText(title);
        holder.imageView.setTag(picUrl);
        ViewCompat.setTransitionName(holder.imageView, picUrl);
//        ViewCompat.setTransitionName(holder.title, mMainList.get(position).getTitle());
        holder.panel_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bitmap bitmap = null;
//                BitmapDrawable bitmapDrawable = (BitmapDrawable) holder.imageView.getDrawable();
//                if (bitmapDrawable != null) {
//                    bitmap = bitmapDrawable.getBitmap();
//                }
//                Intent intent = new Intent(activity, WordsDetailActivity.class);
//                intent.putExtra("title", title);
//                intent.putExtra("url", picUrl);
//                intent.putExtra("color", AppUtils.getPaletteColor(bitmap));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, holder.panel_content, "url");
//                    ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle());
//                } else {
//                    activity.startActivity(intent);
//                }
                WordsDetailActivity.navigate(activity, holder.imageView, mainData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMainList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.panel_content)
        LinearLayout panel_content;
        @Bind(R.id.imgView)
        ImageView imageView;
        @Bind(R.id.title)
        TextView title;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
