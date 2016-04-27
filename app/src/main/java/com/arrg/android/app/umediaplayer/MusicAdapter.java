package com.arrg.android.app.umediaplayer;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaredrummler.fastscrollrecyclerview.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private AppCompatActivity activity;
    private ArrayList<Music> musics;
    private Music music;

    public MusicAdapter(AppCompatActivity activity, ArrayList<Music> musics) {
        this.activity = activity;
        this.musics = musics;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);

        View files = inflater.inflate(R.layout.music_list_item_row, parent, false);

        return new ViewHolder(files);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        music = musics.get(position);

        holder.nameOfTheSong.setText(music.getNameOfTheSong());
        holder.artistName.setText(music.getArtistName());
        holder.photoAlbum.setImageBitmap(music.getPhotoAlbum());

        if (music.getPlaying()) {
            holder.nameOfTheSong.setTextColor(ContextCompat.getColor(activity, R.color.holo_blue_bright));
            holder.artistName.setTextColor(ContextCompat.getColor(activity, R.color.holo_blue_bright));
        } else {
            holder.nameOfTheSong.setTextColor(ContextCompat.getColor(activity, R.color.background_light));
            holder.artistName.setTextColor(ContextCompat.getColor(activity, R.color.background_light));
        }
    }

    @Override
    public int getItemCount() {
        return musics.size();
    }

    public Music getSong(int index) {
        return musics.get(index);
    }

    public void isPlaying(int layoutPosition, boolean isPlaying) {
        musics.get(layoutPosition).setPlaying(isPlaying);

        notifyItemChanged(layoutPosition);
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return musics.get(position).getNameOfTheSong().substring(0, 1).toUpperCase(Locale.ENGLISH);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.photoAlbum)
        ImageView photoAlbum;

        @Bind(R.id.artistName)
        TextView artistName;

        @Bind(R.id.nameOfTheSong)
        TextView nameOfTheSong;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Music music = musics.get(getLayoutPosition());

            if (music.getPhotoAlbum() == null) {
                Toast.makeText(activity, "The bitmap is null.", Toast.LENGTH_SHORT).show();
            } else {
                MainActivity musicPlayerActivity = (MainActivity) activity;

                musicPlayerActivity.updateAlbumView(music);
                musicPlayerActivity.playSong(music);

                MusicPlayerFragment musicPlayerFragment = (MusicPlayerFragment) Util.getInstance(activity, MusicPlayerFragment.class);
                musicPlayerFragment.updateViews(music);
            }
        }
    }
}
