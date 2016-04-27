package com.arrg.android.app.umediaplayer;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Util {

    public static void setImmersiveMode(Activity immersiveMode) {
        immersiveMode.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    public static void launchFragment(AppCompatActivity appCompatActivity, Class fragmentLaunch, int container, boolean addToBackStack, boolean popBackStack) {
        Class fragmentClass;
        Fragment fragment = null;
        FragmentManager fragmentManager;

        fragmentClass = fragmentLaunch;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager = appCompatActivity.getSupportFragmentManager();

        if (popBackStack) {
            fragmentManager.popBackStack();
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (addToBackStack) {
            transaction.addToBackStack(fragmentLaunch.getName());
            //transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            //transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        }

        transaction.replace(container, fragment, fragmentLaunch.getName());
        transaction.commit();
    }

    public static Fragment getInstance(AppCompatActivity appCompatActivity, Class fragment) {
        return appCompatActivity.getSupportFragmentManager().findFragmentByTag(fragment.getName());
    }
}
