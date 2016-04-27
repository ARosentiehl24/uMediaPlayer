package com.arrg.android.app.umediaplayer;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicPlayerFragment extends Fragment {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.tvInitialTime)
    TextView tvInitialTime;

    @Bind(R.id.tvDuration)
    TextView tvDuration;

    @OnClick({R.id.bPrevious, R.id.bPlay, R.id.bNext})
    public void OnClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.bPrevious:
                break;
            case R.id.bPlay:
                break;
            case R.id.bNext:
                break;
        }
    }

    public MusicPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_music_player, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MusicAdapter musicAdapter = new MusicAdapter((AppCompatActivity) getActivity(), getDeviceMusic());

                        recyclerView.setAdapter(musicAdapter);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                        if (musicAdapter.getItemCount() > 0) {
                            Music music = musicAdapter.getSong(0);
                            updateAlbumView(music);
                            updateViews(music);
                        }
                    }
                });
            }
        }).start();
    }

    private void updateAlbumView(Music music) {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.updateAlbumView(music);
    }

    private ArrayList<Music> getDeviceMusic() {
        ArrayList<Music> musicArrayList = new ArrayList<>();

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";

        Cursor cursor = null;

        try {
            Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            cursor = getActivity().getContentResolver().query(uri, projection, selection, null, sortOrder);

            if (cursor != null) {
                cursor.moveToFirst();

                long startTime = System.currentTimeMillis();

                while (!cursor.isAfterLast()) {
                    String title = cursor.getString(0);
                    String artist = cursor.getString(1);
                    String path = cursor.getString(2);
                    String displayName = cursor.getString(3);
                    Integer songDuration = cursor.getInt(4);

                    Music music = new Music();

                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(path);

                    byte bitmap[] = mediaMetadataRetriever.getEmbeddedPicture();

                    if (bitmap == null) {
                        music.setPhotoAlbum(BitmapFactory.decodeResource(getResources(), R.drawable.ic_music_player_default_cover));
                    } else {
                        music.setPhotoAlbum(BitmapUtil.getBitmapFromByteArray(bitmap, 0, bitmap.length, 250, 250));
                    }

                    music.setNameOfTheSong(title);
                    music.setArtistName(artist);
                    music.setPathOfFile(path);
                    music.setDisplayName(displayName);
                    music.setDuration(songDuration);

                    musicArrayList.add(music);

                    cursor.moveToNext();
                }

                long endTime = System.currentTimeMillis();

                long duration = (endTime - startTime);

                Log.d("FolderFinish", "Duration: " + TimeUnit.MILLISECONDS.toSeconds(duration) + " seconds" + " - " + duration + " milliseconds");
            }
        } catch (Exception e) {
            Log.e("Folder", e.toString(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return musicArrayList;
    }

    public void updateViews(Music music) {
        long duration = music.getDuration();

        long second = (duration / 1000) % 60;
        long minute = (duration / (1000 * 60)) % 60;

        tvDuration.setText(String.format(Locale.US, "%02d:%02d", minute, second));
    }
}

