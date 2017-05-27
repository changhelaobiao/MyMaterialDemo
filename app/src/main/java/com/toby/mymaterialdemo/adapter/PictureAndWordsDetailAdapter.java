package com.toby.mymaterialdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.model.PictureAndWordsDetailDes;
import com.toby.mymaterialdemo.model.PictureAndWordsDetailImg;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * 作者：hyr on 2016/8/31 14:34
 * 邮箱：2045446584@qq.com
 */
public class PictureAndWordsDetailAdapter
        extends RecyclerView.Adapter<PictureAndWordsDetailAdapter.PictureAndWordsDetailViewHolder> {

    private List<PictureAndWordsDetailDes> detailDes = new ArrayList<>();

    private List<PictureAndWordsDetailImg> detailImgs = new ArrayList<>();

    private Context mContext;


    public List<PictureAndWordsDetailImg> getDetailImgs() {
        return detailImgs;
    }


    public List<PictureAndWordsDetailDes> getDetailDes() {
        return detailDes;
    }


    public void setDetailDes(List<PictureAndWordsDetailDes> detailDes) {
        this.detailDes = detailDes;
    }


    public void setDetailImgs(List<PictureAndWordsDetailImg> detailImgs) {
        this.detailImgs = detailImgs;
    }


    public PictureAndWordsDetailAdapter(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public PictureAndWordsDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext)
                .inflate(R.layout.item_picture_and_words_detail_layout, parent, false);
        return new PictureAndWordsDetailViewHolder(inflate);
    }


    @Override
    public void onBindViewHolder(PictureAndWordsDetailViewHolder holder, int position) {

        Picasso.with(mContext)
                .load(detailImgs.get(position).getImg())
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .into(holder.imageView);

        Log.i("size", "des======" + detailDes.size() + "");

        Log.i("size", "imgs=====" + detailImgs.size() + "");
        Log.i("size", "position=====" + position + "");
        if (detailDes.size() > position) {
            holder.des.setText(detailDes.get(position).getDes());
        } else {
            holder.des.setVisibility(View.INVISIBLE);

        }

    }


    @Override
    public int getItemCount() {

        return detailImgs != null ? detailImgs.size() : 0;
    }


    public class PictureAndWordsDetailViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView des;


        public PictureAndWordsDetailViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image);

            des = (TextView) itemView.findViewById(R.id.des);
        }
    }

}
