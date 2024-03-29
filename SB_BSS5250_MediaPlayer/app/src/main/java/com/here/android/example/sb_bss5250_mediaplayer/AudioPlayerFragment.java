package com.here.android.example.sb_bss5250_mediaplayer;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.VideoView;

import java.io.IOException;
import java.net.URI;

public class AudioPlayerFragment extends Fragment {

    private MediaPlayer mediaPlayer = null;
    VideoView videoView = null;
    Button playButton, videoButton;
    GestureDetector gestureDetector;
    private int currentPosition = 0;
    private boolean whichOne = true;

    View v = null;
    private String MP3URL = "https://www.nasa.gov/mp3/640149main_Computers%20are%20in%20Control.mp3";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conatiner,
                             Bundle saveInstanceState) {

        if(v == null) {
            // Inflate the layout for this fragment
            v = inflater.inflate(R.layout.fragment_audio_player, conatiner, false);

            //create custom gesture detector
            gestureDetector  =  new GestureDetector(getActivity(), simpleOnGestureListener);
            videoView  = (VideoView) v.findViewById(R.id.video_view);
            //listen for touch events on videoView
            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    //pass touch events to custom gesture detector
                    return gestureDetector.onTouchEvent(motionEvent);
                }
            });


            playButton = (Button) v.findViewById(R.id.play_button);
            playButton.setOnClickListener(playClickedListener);
            Button stopButton = (Button) v.findViewById(R.id.stop_button);
            stopButton.setOnClickListener(stopClickedListener);
            Button pauseButton = (Button) v.findViewById(R.id.pause_button);
            pauseButton.setOnClickListener(pauseClickedListener);
            Button changeButton = (Button) v.findViewById(R.id.change_button);
            changeButton.setOnClickListener(changeClickedListener);
            videoButton = (Button) v.findViewById(R.id.video_button);
            videoButton.setOnClickListener(videoClickedListener);

            videoView = (VideoView) v.findViewById(R.id.video_view);

        }
        return v;
    }

    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    float diff = e2.getX() - e1.getX();
                    seekMedia(diff);
                    return super.onScroll(e1, e2, distanceX, distanceY);
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    // return super.onDown(e);
                    return true;
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    //return super.onDoubleTap(e);
                    if (videoView != null) {
                        if (videoView.isPlaying()) {
                            currentPosition = videoView.getCurrentPosition();
                            videoView.pause();
                        }else {
                            videoView.seekTo(currentPosition);
                            videoView.start();
                        }
                    }
                    return true;
                }
            };

    private void seekMedia(float offset){
        int newPos = (int)videoView.getCurrentPosition() + (int)offset;
        videoView.seekTo(newPos);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(videoView.isPlaying()) {
            videoView.pause();
            outState.putInt("videoPosition", videoView.getCurrentPosition());
            //Log.d(LOG_ID, String.value0f(videoView.getCurrentPosition()));
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState !=  null) {
            videoView.seekTo(savedInstanceState.getInt( "videoPosition"));
            videoView.start();
        }
    }

    private void loadMP3URL(String path) {
        mediaPlayer = new MediaPlayer();
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build();
        mediaPlayer.setAudioAttributes(audioAttributes);
        try {
            mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            Log.d("APF", e.toString());
        }

        mediaPlayer.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        mediaPlayer.setOnPreparedListener(null);
                    }
                }
        );

        mediaPlayer.prepareAsync();

        mediaPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        playButton.setText("Play");
                        mediaPlayer.stop();
                        mediaPlayer.prepareAsync();
                    }
                }
        );
    }

    private void playMedia(@Nullable String path) {
        if (mediaPlayer == null) {
            if(path == null) {
                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.one_small_step);
                mediaPlayer.start();
            } else {
                loadMP3URL(path);
            }
            playButton.setText("Play");
            //mediaPlayer = MediaPlayer.create((getActivity(), R.raw.one_small_step))
        } else if (!mediaPlayer.isPlaying()) {  //if mediaPlayer is not playing
            mediaPlayer = MediaPlayer.create(getActivity(), R.raw.countdown_launch);
            mediaPlayer.start();    //start from the beginning
            playButton.setText("Pause");
        } else {
            //pauseMedia();
            loadMP3URL(path);
        }
    }

    private void playVideo(@NonNull  int resID) {
        if (videoView == null) {
            if(resID == 0) {
                Uri uri = Uri.parse("android.resource://"+this.getActivity().getPackageName()+"/"+R.raw.samp);
                videoView.setVideoURI(uri);
                videoView.start();
            } else {
                Uri uri = Uri.parse("android.resource://"+this.getActivity().getPackageName()+"/"+resID);
                videoView.setVideoURI(uri);
                videoView.start();
            }
            videoButton.setText("Play Video");
        } else if (!videoView.isPlaying()) {  //if videoView is not playing
            Uri uri = Uri.parse("android.resource://"+this.getActivity().getPackageName()+"/"+R.raw.samp);
            videoView.setVideoURI(uri);
            videoView.start();    //start from the beginning
        }
    }



    private void pauseMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            playButton.setText("Play");
        }
    }

    private void stopMedia() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
            playButton.setText("Play");
        }
    }

    private void changeAudio() {
        //swap the audio
        if (mediaPlayer != null) {
            if (whichOne) {
                mediaPlayer.pause();
                loadMP3URL("https://www.nasa.gov/mp3/569462main_eagle_has_landed.mp3");
                whichOne = false;
            }else{
                mediaPlayer.pause();
                loadMP3URL("https://www.nasa.gov/mp3/590325main_ringtone_kennedy_WeChoose.mp3");
                whichOne = true;
            }
        }
    }

    private void changeVideo() {
        // swap the video
        if (whichOne) {
            Uri uri = Uri.parse("android.resource://" + this.getActivity().getPackageName() + "/" + R.raw.small);
            videoView.stopPlayback();
            videoView.setVideoURI(uri);
            videoView.start();
            whichOne = false;
        } else {
            Uri uri = Uri.parse("android.resource://" + this.getActivity().getPackageName() + "/" + R.raw.samp);
            videoView.stopPlayback();
            videoView.setVideoURI(uri);
            videoView.start();
            whichOne = true;
        }
    }

    private View.OnClickListener playClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            playMedia(null);
        }
    };

    private View.OnClickListener stopClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            stopMedia();
            }
    };

    private View.OnClickListener pauseClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            pauseMedia();
        }
    };

    private View.OnClickListener changeClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (videoView != null) {
                if (videoView.isPlaying()) {
                    //playVideo(R.raw.small);
                    changeVideo();
                }
            }
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    //playMedia("https://history.nasa.gov/afj/ap11fj/audio/1892800.mp3");
                    changeAudio();
                }
            }
        }
    };

    private View.OnClickListener videoClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            playVideo(0);
        }
    };
}

