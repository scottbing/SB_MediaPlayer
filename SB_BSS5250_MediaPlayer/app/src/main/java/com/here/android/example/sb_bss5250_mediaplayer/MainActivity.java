package com.here.android.example.sb_bss5250_mediaplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static String fragTag = "com.here.android.example.sb_audio_player";
    AudioPlayerFragment audioPlayerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        //look for the retained fragment if it exists
        audioPlayerFragment = (AudioPlayerFragment) fragmentManager.findFragmentByTag(fragTag);
        if (audioPlayerFragment == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            audioPlayerFragment = new AudioPlayerFragment();
            audioPlayerFragment.setRetainInstance(true);
            fragmentTransaction.add(android.R.id.content, audioPlayerFragment, fragTag);
            fragmentTransaction.commit();
        }
    }
}
