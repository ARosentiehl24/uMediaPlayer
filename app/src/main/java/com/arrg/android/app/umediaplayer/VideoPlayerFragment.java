package com.arrg.android.app.umediaplayer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoPlayerFragment extends Fragment {


    public VideoPlayerFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
        return videoPlayerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_player, container, false);
    }
}
