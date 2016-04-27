package com.arrg.android.app.umediaplayer;


import android.app.ActivityManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.arrg.android.app.umediaplayer.Constants.AUDIO_TO_PLAY_EXTRA;


/**
 * A simple {@link Fragment} subclass.
 */
public class MusicPlayerFragment extends Fragment {

    private static final int UPDATE_FREQUENCY = 500;
    private static final int STEP = 5000;

    private Boolean isStarted = false;
    private Handler handler;
    private Integer index = 0;
    private MediaPlayer mediaPlayer;
    private MusicAdapter musicAdapter;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updatePosition();
            if (mediaPlayer.isPlaying()) {
                int position = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                progress.setMax(duration);
                progress.setProgress(position);

                handler.postDelayed(this, 100);
            }
        }
    };

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.progress)
    AppCompatSeekBar progress;

    @Bind(R.id.tvInitialTime)
    TextView tvInitialTime;

    @Bind(R.id.tvDuration)
    TextView tvDuration;

    @Bind(R.id.bPlay)
    ImageButton bPlay;

    @OnClick({R.id.bPrevious, R.id.bPlay, R.id.bNext})
    public void OnClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.bPrevious:
                if (index > 0) {
                    int index = this.index - 1;

                    playSong(musicAdapter.getSong(index), index);
                }
                break;
            case R.id.bPlay:
                break;
            case R.id.bNext:
                if (index < musicAdapter.getItemCount() - 1) {
                    int index = this.index + 1;

                    playSong(musicAdapter.getSong(index), index);
                }
                break;
        }
    }

    public MusicPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
        mediaPlayer = new MediaPlayer();
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

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playNextTrack();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        musicAdapter = new MusicAdapter((AppCompatActivity) getActivity(), getDeviceMusic());

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

    @Override
    public void onDestroy() {
        handler.removeCallbacks(runnable);

        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();

        mediaPlayer = null;

        super.onDestroy();
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

    public void playSong(Music music, int layoutPosition) {
        musicAdapter.isPlaying(index, false);

        index = layoutPosition;

        progress.setProgress(0);

        mediaPlayer.stop();
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(music.getPathOfFile());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        progress.setMax(mediaPlayer.getDuration());

        bPlay.setImageResource(R.drawable.ic_pause);

        musicAdapter.isPlaying(index, true);

        updatePosition();

        isStarted = true;

        Music musicAdapterSong = musicAdapter.getSong(index);

        updateAlbumView(musicAdapterSong);
    }

    public void playNextTrack() {
        stopPlay();
    }

    public void pausePlay() {
        mediaPlayer.pause();

        bPlay.setImageResource(android.R.drawable.ic_media_play);

        handler.removeCallbacks(runnable);

        progress.setProgress(mediaPlayer.getCurrentPosition());

        isStarted = false;
    }

    private void stopPlay() {
        mediaPlayer.stop();

        mediaPlayer.reset();

        bPlay.setImageResource(android.R.drawable.ic_media_play);

        handler.removeCallbacks(runnable);

        progress.setProgress(0);

        isStarted = false;
    }

    public void updatePosition() {
        handler.removeCallbacks(runnable);

        progress.setProgress(mediaPlayer.getCurrentPosition());

        handler.postDelayed(runnable, UPDATE_FREQUENCY);
    }

    public void startProgress(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;

        int position = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();

        progress.setMax(duration);
        progress.setProgress(position);

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 100);
    }
}

