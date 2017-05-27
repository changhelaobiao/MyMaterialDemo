package com.toby.mymaterialdemo.adapter;

import android.app.Activity;
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
import com.toby.mymaterialdemo.model.OnItemClickListener;
import com.toby.mymaterialdemo.model.WordsData;
import com.toby.mymaterialdemo.ui.activity.WordsDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/11/22 0022.
 */
public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.ViewHolder> {
    private List<WordsData> mMainList;
    private Activity activity;
    protected OnItemClickListener mClickListener;

    public WordsAdapter(Activity activity, List<WordsData> mainList) {
        this.activity = activity;
        if (mainList == null) {
            this.mMainList = new ArrayList<>();
        } else {
            this.mMainList = mainList;
        }
    }

    public List<WordsData> getmMainList() {
        return mMainList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_words_layout, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final WordsData wordsData = mMainList.get(position);
        Picasso.with(activity)
                .load(wordsData.getThumb())
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .into(holder.thumb);
        holder.type.setText(wordsData.getType());
        holder.author.setText(wordsData.getAuthor());
        holder.data.setText(wordsData.getDate());
        holder.title.setText(wordsData.getTitle());
        holder.des.setText(wordsData.getDes());
        holder.layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(null, position, wordsData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMainList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.layoutRoot)
        LinearLayout layoutRoot;
        @Bind(R.id.thumb)
        ImageView thumb;
        @Bind(R.id.type)
        TextView type;
        @Bind(R.id.author)
        TextView author;
        @Bind(R.id.data)
        TextView data;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.des)
        TextView des;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
