package com.example.videoplayer1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jiajunhui.xapp.medialoader.bean.VideoItem;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private Context context;
    private List<VideoItem> list;

    public VideoAdapter(Context context, List<VideoItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull VideoAdapter.ViewHolder holder, int position) {

        String duration = formateMilliSeccond(list.get(position).getDuration());
        holder.tvDuration.setText(duration);

        Glide.with(context)
                .load(list.get(position).getPath())
                .placeholder(R.drawable.ic_baseline_videocam_24)
                .into(holder.ivVideoThumb);

        holder.rlVideoItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoPlayingActivity.class);
                intent.putExtra("Position", position);
                intent.putExtra("List", (Serializable)list);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rlVideoItem;
        private ImageView ivVideoThumb;
        private TextView tvDuration;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);

            rlVideoItem = itemView.findViewById(R.id.rlVideoItem);
            ivVideoThumb = itemView.findViewById(R.id.ivVideoThumb);
            tvDuration = itemView.findViewById(R.id.tvDuration);

            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            int finalWidth = (width / 3) - (int)context.getResources().getDimension(R.dimen._4sdp);

            rlVideoItem.getLayoutParams().width = finalWidth;
        }
    }

    public static String formateMilliSeccond(long timeMs) {
        int totalSeconds = (int)timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
}
