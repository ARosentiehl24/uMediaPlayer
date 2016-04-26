package com.arrg.android.app.umediaplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @OnClick({R.id.bMusic, R.id.bVideo})
    public void OnClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.bMusic:
                MusicPlayerFragment musicPlayerFragment = (MusicPlayerFragment) Util.getInstance(this, MusicPlayerFragment.class);

                if (musicPlayerFragment == null) {
                    Util.launchFragment(this, MusicPlayerFragment.class, R.id.container, false, true);
                }
                break;
            case R.id.bVideo:
                VideoPlayerFragment videoPlayerFragment = (VideoPlayerFragment) Util.getInstance(this, VideoPlayerFragment.class);

                if (videoPlayerFragment == null) {
                    Util.launchFragment(this, VideoPlayerFragment.class, R.id.container, false, true);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Util.setImmersiveMode(this);

        Util.launchFragment(this, MusicPlayerFragment.class, R.id.container, false, false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
