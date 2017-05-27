package com.toby.mymaterialdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.toby.mymaterialdemo.R;
import com.toby.mymaterialdemo.model.NewsPhotoDetail;
import com.toby.mymaterialdemo.model.NewsSummary;
import com.toby.mymaterialdemo.ui.activity.NewsDetailActivity;
import com.toby.mymaterialdemo.ui.activity.NewsPhotoDetailActivity;
import com.toby.mymaterialdemo.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Toby on 2016/11/9.
 */

public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_PHOTO_ITEM = 1;
    private Context context;
    private List<NewsSummary> newsSummaryList;

    public NewsListAdapter(Context context, List<NewsSummary> datas) {
        this.context = context;
        this.newsSummaryList = datas;
    }

    public List<NewsSummary> getNewsSummaryList() {
        return newsSummaryList;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final NewsSummary newsSummary = newsSummaryList.get(position);
        switch (getItemViewType(position)) {
            case TYPE_ITEM:
                final ViewHolder1 viewHolder1 = (ViewHolder1) holder;
                viewHolder1.news_summary_title_tv.setText(newsSummary.getTitle());
                viewHolder1.news_summary_ptime_tv.setText(newsSummary.getPtime());
                viewHolder1.news_summary_digest_tv.setText(newsSummary.getDigest());
                String imgSrc = newsSummary.getImgsrc();
                if (!TextUtils.isEmpty(imgSrc)) {
                    Picasso.with(context).load(newsSummary.getImgsrc()).placeholder(R.drawable.ic_image_loading).error(R.drawable.ic_empty_picture).into(viewHolder1.news_summary_photo_iv);
                }
                viewHolder1.rl_root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NewsDetailActivity.startAction(context, viewHolder1.news_summary_photo_iv, newsSummary.getPostid(), newsSummary.getImgsrc());
                    }
                });
                break;
            case TYPE_PHOTO_ITEM:
                ViewHolder2 viewHolder2 = (ViewHolder2) holder;
                viewHolder2.news_summary_title_tv.setText(newsSummary.getTitle());
                viewHolder2.news_summary_ptime_tv.setText(newsSummary.getPtime());
                setImageView(viewHolder2, newsSummary);
                viewHolder2.ll_root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NewsPhotoDetailActivity.startAction(context, getPhotoDetail(newsSummary));
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return newsSummaryList == null ? 0 : newsSummaryList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new ViewHolder1(LayoutInflater.from(context).inflate(R.layout.item_news, parent, false));
//            new ViewHolder1(LayoutInflater.from(context).inflate(R.layout.item_news, null));// 会导致图片填充不满整个Item
        }
        return new ViewHolder2(LayoutInflater.from(context).inflate(R.layout.item_news_photo, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        NewsSummary newsSummary = newsSummaryList.get(position);
        if (!TextUtils.isEmpty(newsSummary.getDigest())) {
            return TYPE_ITEM;
        }
        return TYPE_PHOTO_ITEM;
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {
        RelativeLayout rl_root;
        ImageView news_summary_photo_iv;
        TextView news_summary_title_tv;
        TextView news_summary_digest_tv;
        TextView news_summary_ptime_tv;

        public ViewHolder1(View view) {
            super(view);
            rl_root = (RelativeLayout) view.findViewById(R.id.rl_root);
            news_summary_photo_iv = (ImageView) view.findViewById(R.id.news_summary_photo_iv);
            news_summary_title_tv = (TextView) view.findViewById(R.id.news_summary_title_tv);
            news_summary_digest_tv = (TextView) view.findViewById(R.id.news_summary_digest_tv);
            news_summary_ptime_tv = (TextView) view.findViewById(R.id.news_summary_ptime_tv);
        }

    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {

        LinearLayout ll_root;
        TextView news_summary_title_tv;
        LinearLayout news_summary_photo_iv_group;
        ImageView news_summary_photo_iv_left;
        ImageView news_summary_photo_iv_middle;
        ImageView news_summary_photo_iv_right;
        TextView news_summary_ptime_tv;

        public ViewHolder2(View view) {
            super(view);
            ll_root = (LinearLayout) view.findViewById(R.id.ll_root);
            news_summary_title_tv = (TextView) view.findViewById(R.id.news_summary_title_tv);
            news_summary_photo_iv_group = (LinearLayout) view.findViewById(R.id.news_summary_photo_iv_group);
            news_summary_photo_iv_left = (ImageView) view.findViewById(R.id.news_summary_photo_iv_left);
            news_summary_photo_iv_middle = (ImageView) view.findViewById(R.id.news_summary_photo_iv_middle);
            news_summary_photo_iv_right = (ImageView) view.findViewById(R.id.news_summary_photo_iv_right);
            news_summary_ptime_tv = (TextView) view.findViewById(R.id.news_summary_ptime_tv);
        }

    }

    private NewsPhotoDetail getPhotoDetail(NewsSummary newsSummary) {
        NewsPhotoDetail newsPhotoDetail = new NewsPhotoDetail();
        newsPhotoDetail.setTitle(newsSummary.getTitle());
        setPictures(newsSummary, newsPhotoDetail);
        return newsPhotoDetail;
    }

    private void setPictures(NewsSummary newsSummary, NewsPhotoDetail newsPhotoDetail) {
        List<NewsPhotoDetail.Picture> pictureList = new ArrayList<>();
        if (newsSummary.getAds() != null) {
            for (NewsSummary.AdsBean entity : newsSummary.getAds()) {
                setValuesAndAddToList(pictureList, entity.getTitle(), entity.getImgsrc());
            }
        } else if (newsSummary.getImgextra() != null) {
            for (NewsSummary.ImgextraBean entity : newsSummary.getImgextra()) {
                setValuesAndAddToList(pictureList, null, entity.getImgsrc());
            }
        } else {
            setValuesAndAddToList(pictureList, null, newsSummary.getImgsrc());
        }

        newsPhotoDetail.setPictures(pictureList);
    }

    private void setValuesAndAddToList(List<NewsPhotoDetail.Picture> pictureList, String title, String imgsrc) {
        NewsPhotoDetail.Picture picture = new NewsPhotoDetail.Picture();
        if (title != null) {
            picture.setTitle(title);
        }
        picture.setImgSrc(imgsrc);

        pictureList.add(picture);
    }

    private void setImageView(ViewHolder2 holder, NewsSummary newsSummary) {
        int PhotoThreeHeight = DisplayUtil.dip2px(90);
        int PhotoTwoHeight = DisplayUtil.dip2px(120);
        int PhotoOneHeight = DisplayUtil.dip2px(150);

        String imgSrcLeft = null;
        String imgSrcMiddle = null;
        String imgSrcRight = null;
        LinearLayout news_summary_photo_iv_group = holder.news_summary_photo_iv_group;
        ViewGroup.LayoutParams layoutParams = news_summary_photo_iv_group.getLayoutParams();

        if (newsSummary.getAds() != null) {
            List<NewsSummary.AdsBean> adsBeanList = newsSummary.getAds();
            int size = adsBeanList.size();
            if (size >= 3) {
                imgSrcLeft = adsBeanList.get(0).getImgsrc();
                imgSrcMiddle = adsBeanList.get(1).getImgsrc();
                imgSrcRight = adsBeanList.get(2).getImgsrc();
                layoutParams.height = PhotoThreeHeight;
                holder.news_summary_title_tv.setText(String.format("图集:%s", adsBeanList.get(0).getTitle()));
            } else if (size >= 2) {
                imgSrcLeft = adsBeanList.get(0).getImgsrc();
                imgSrcMiddle = adsBeanList.get(1).getImgsrc();

                layoutParams.height = PhotoTwoHeight;
            } else if (size >= 1) {
                imgSrcLeft = adsBeanList.get(0).getImgsrc();

                layoutParams.height = PhotoOneHeight;
            }
        } else if (newsSummary.getImgextra() != null) {
            int size = newsSummary.getImgextra().size();
            if (size >= 3) {
                imgSrcLeft = newsSummary.getImgextra().get(0).getImgsrc();
                imgSrcMiddle = newsSummary.getImgextra().get(1).getImgsrc();
                imgSrcRight = newsSummary.getImgextra().get(2).getImgsrc();

                layoutParams.height = PhotoThreeHeight;
            } else if (size >= 2) {
                imgSrcLeft = newsSummary.getImgextra().get(0).getImgsrc();
                imgSrcMiddle = newsSummary.getImgextra().get(1).getImgsrc();

                layoutParams.height = PhotoTwoHeight;
            } else if (size >= 1) {
                imgSrcLeft = newsSummary.getImgextra().get(0).getImgsrc();

                layoutParams.height = PhotoOneHeight;
            }
        } else {
            imgSrcLeft = newsSummary.getImgsrc();

            layoutParams.height = PhotoOneHeight;
        }

        setPhotoImageView(holder, imgSrcLeft, imgSrcMiddle, imgSrcRight);
        news_summary_photo_iv_group.setLayoutParams(layoutParams);
    }

    private void setPhotoImageView(ViewHolder2 holder, String imgSrcLeft, String imgSrcMiddle, String imgSrcRight) {
        if (imgSrcLeft != null) {
            holder.news_summary_photo_iv_left.setVisibility(View.VISIBLE);
            Picasso.with(context).load(imgSrcLeft).placeholder(R.drawable.ic_image_loading).error(R.drawable.ic_empty_picture).into(holder.news_summary_photo_iv_left);
        } else {
            holder.news_summary_photo_iv_left.setVisibility(View.GONE);
        }
        if (imgSrcMiddle != null) {
            holder.news_summary_photo_iv_middle.setVisibility(View.VISIBLE);
            Picasso.with(context).load(imgSrcMiddle).placeholder(R.drawable.ic_image_loading).error(R.drawable.ic_empty_picture).into(holder.news_summary_photo_iv_middle);
        } else {
            holder.news_summary_photo_iv_middle.setVisibility(View.GONE);
        }
        if (imgSrcRight != null) {
            holder.news_summary_photo_iv_right.setVisibility(View.VISIBLE);
            Picasso.with(context).load(imgSrcRight).placeholder(R.drawable.ic_image_loading).error(R.drawable.ic_empty_picture).into(holder.news_summary_photo_iv_right);
        } else {
            holder.news_summary_photo_iv_right.setVisibility(View.GONE);
        }
    }

}
