package com.arrg.android.app.umediaplayer;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.BinderThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoPlayerActivity extends AppCompatActivity {

    private static final int UPDATE_FREQUENCY = 1000;
    private static final int STEP = 5000;

    private Boolean isMovingSeekBar = false;
    private Boolean isStarted = false;
    private Handler handler;
    private Integer index = 0;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updatePosition();
        }
    };

    @Bind(R.id.progress)
    AppCompatSeekBar progress;

    @Bind(R.id.tvInitialTime)
    TextView tvInitialTime;

    @Bind(R.id.tvDuration)
    TextView tvDuration;

    @Bind(R.id.bPlay)
    ImageButton bPlay;

    @Bind(R.id.bigButtonPlay)
    ImageButton bigButtonPlay;

    @Bind(R.id.videoPlayer)
    VideoView videoPlayer;

    @OnClick({R.id.bigButtonPlay, R.id.bPlay})
    public void OnClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.bigButtonPlay:
                bigButtonPlay.setVisibility(View.INVISIBLE);

                videoPlayer.start();

                progress.setProgress(0);

                bPlay.setImageResource(R.drawable.ic_pause);

                isStarted = true;

                updatePosition();
                break;
            case R.id.bPlay:
                if (videoPlayer.isPlaying()) {
                    pausePlay();
                } else {
                    if (isStarted) {
                        videoPlayer.start();

                        bPlay.setImageResource(R.drawable.ic_pause);

                        updatePosition();
                    } else {
                        //playSong(musicAdapter.getSong(index), index);
                    }
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        ButterKnife.bind(this);

        Util.setImmersiveMode(this);

        handler = new Handler();

        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isMovingSeekBar) {
                    videoPlayer.seekTo(progress);

                    long time = videoPlayer.getCurrentPosition();

                    long second = (time / 1000) % 60;
                    long minute = (time / (1000 * 60)) % 60;

                    tvInitialTime.setText(String.format(Locale.US, "%02d:%02d", minute, second));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isMovingSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isMovingSeekBar = false;
            }
        });

        Video video = (Video) getIntent().getSerializableExtra(Constants.VIDEO_TO_PLAY_EXTRA);

        videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                bPlay.setImageResource(R.drawable.ic_pause);

                isStarted = true;

                bigButtonPlay.setVisibility(View.INVISIBLE);
            }
        });
        videoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                bPlay.setImageResource(R.drawable.ic_play_arrow);

                isStarted = false;

                bigButtonPlay.setVisibility(View.VISIBLE);
            }
        });
        videoPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        videoPlayer.setVideoPath(video.getPathOfFile());
        videoPlayer.start();

        long duration = video.getDuration();

        long second = (duration / 1000) % 60;
        long minute = (duration / (1000 * 60)) % 60;

        tvDuration.setText(String.format(Locale.US, "%02d:%02d", minute, second));

        progress.setMax((int) duration);

        progress.setProgress(0);

        bPlay.setImageResource(R.drawable.ic_pause);

        isStarted = true;

        updatePosition();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void pausePlay() {
        videoPlayer.pause();

        bPlay.setImageResource(R.drawable.ic_play_arrow);

        handler.removeCallbacks(runnable);

        progress.setProgress(videoPlayer.getCurrentPosition());
    }

    public void updatePosition() {
        handler.removeCallbacks(runnable);

        long time = videoPlayer.getCurrentPosition();

        long second = (time / 1000) % 60;
        long minute = (time / (1000 * 60)) % 60;

        tvInitialTime.setText(String.format(Locale.US, "%02d:%02d", minute, second));

        progress.setProgress(videoPlayer.getCurrentPosition());

        handler.postDelayed(runnable, UPDATE_FREQUENCY);
    }
}
