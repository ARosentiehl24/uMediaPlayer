package com.arrg.android.app.umediaplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.easyandroidanimations.library.FadeOutAnimation;
import com.vistrav.ask.Ask;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashScreenActivity extends AppCompatActivity {

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        Util.setImmersiveMode(this);

        Ask.on(this)
                .forPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .when(new Ask.Permission() {
                    @Override
                    public void granted(List<String> permissions) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new FadeOutAnimation(progressBar).setDuration(1000).animate();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }
                        }, 2500);
                    }

                    @Override
                    public void denied(List<String> permissions) {
                        boolean isAllPermissionGranted = true;

                        for (String permission : permissions) {
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_DENIED) {
                                isAllPermissionGranted = false;
                                break;
                            }
                        }

                        if (!isAllPermissionGranted) {
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        }
                    }
                }).go();
    }
}
