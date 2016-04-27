package com.arrg.android.app.umediaplayer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private ArrayList<Video> videos;
    private AppCompatActivity activity;

    public VideoAdapter(AppCompatActivity activity, ArrayList<Video> videos) {
        this.activity = activity;
        this.videos = videos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View files = inflater.inflate(R.layout.video_list_item_row, parent, false);

        return new ViewHolder(files);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Video video = videos.get(position);

        Glide.with(activity).load(video.getPathOfFile()).asBitmap().into(holder.videoPreview);

        //holder.videoPreview.setImageBitmap(video.getPhotoAlbum());
        holder.videoName.setText(video.getNameOfTheSong());

        long duration = video.getDuration();

        long second = (duration / 1000) % 60;
        long minute = (duration / (1000 * 60)) % 60;
        long hour = (duration / (1000 * 60 * 60)) % 24;

        holder.videoDuration.setText(String.format(Locale.US, "%02d:%02d:%02d", hour, minute, second));
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.videoPreview)
        ImageView videoPreview;

        @Bind(R.id.videoName)
        AppCompatTextView videoName;

        @Bind(R.id.videoDuration)
        AppCompatTextView videoDuration;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Video video = videos.get(getLayoutPosition());

            Intent intent = new Intent(activity, VideoPlayerActivity.class);
            intent.putExtra(Constants.VIDEO_TO_PLAY_EXTRA, video);
            activity.startActivity(intent);
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}
