package com.arrg.android.app.umediaplayer;


import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoPlayerFragment extends Fragment {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    public VideoPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_player, container, false);
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
                        VideoAdapter musicAdapter = new VideoAdapter((AppCompatActivity) getActivity(), getDeviceVideo());

                        recyclerView.setAdapter(musicAdapter);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    }
                });
            }
        }).start();
    }

    private ArrayList<Video> getDeviceVideo() {
        ArrayList<Video> videoArrayList = new ArrayList<>();

        String selection = MediaStore.Video.VideoColumns.DATA + " = ?";

        String[] projection = {
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.ARTIST,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION

        };

        String sortOrder = MediaStore.Video.Media.TITLE + " COLLATE LOCALIZED ASC";

        Cursor cursor = null;

        try {
            Uri uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            cursor = getActivity().getContentResolver().query(uri, projection, null, null, sortOrder);

            if (cursor != null) {
                cursor.moveToFirst();

                long startTime = System.currentTimeMillis();

                while (!cursor.isAfterLast()) {
                    String title = cursor.getString(0);
                    String artist = cursor.getString(1);
                    String path = cursor.getString(2);
                    String displayName = cursor.getString(3);
                    Integer songDuration = cursor.getInt(4);

                    Video video = new Video();

                    video.setNameOfTheSong(title);
                    video.setArtistName(artist);
                    video.setPathOfFile(path);
                    video.setDisplayName(displayName);
                    video.setDuration(songDuration);
                    video.setPhotoAlbum(BitmapFactory.decodeFile(path));

                    videoArrayList.add(video);

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

        return videoArrayList;
    }
}
