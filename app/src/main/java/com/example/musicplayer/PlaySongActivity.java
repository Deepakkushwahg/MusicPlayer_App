package com.example.musicplayer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.accessibilityservice.FingerprintGestureController;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.biometrics.BiometricManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;


import android.view.View;

import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class PlaySongActivity extends AppCompatActivity {
    static MediaPlayer mediaPlayer;
    ImageView playPause,previous,next,repeat,silentMode;
    TextView startingTime,endingTime,songName;
    SeekBar seekBar;
    Runnable runnable;
    Bundle songExtraData;
    int index;
    boolean isSongPlaying;
    boolean repeatFlag = false,silentFlag = false;
    Handler handler;
    ArrayList<File> songsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        playPause = findViewById(R.id.playPause);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        repeat = findViewById(R.id.repeat);
        silentMode = findViewById(R.id.silentMode);
        startingTime = findViewById(R.id.startingTime);
        songName = findViewById(R.id.songName);
        songName.setSelected(true);
        endingTime = findViewById(R.id.endingTime);
        seekBar = findViewById(R.id.seekBar);
        try {
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    if(index==0)
                        index = songsList.size()-1;
                    else
                    {
                        index--;
                    }
                    songName.setText(songsList.get(index).getName());
                    songName.setSelected(true);
                    Uri uri = Uri.parse(songsList.get(index).toString());
                    mediaPlayer = MediaPlayer.create(PlaySongActivity.this, uri);
                    seekBar.setMax(mediaPlayer.getDuration());
                    updateSeekBar();
                    mediaPlayer.start();
                    playPause.setImageResource(android.R.drawable.ic_media_pause);
                    onCompletion();
                }
            });
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    musicChangedToNext();
                }
            });
            if(mediaPlayer!=null)
            {
                mediaPlayer.stop();
            }
            Intent intent = getIntent();
            songExtraData = intent.getExtras();
            songsList = (ArrayList) songExtraData.getParcelableArrayList("allSongsOfMobilePhone");
            index = songExtraData.getInt("indexNoOfSongs",0);
            isSongPlaying = songExtraData.getBoolean("goToMusicActivity",true);
            if(mediaPlayer!=null && mediaPlayer.isPlaying() && !isSongPlaying)
            {
                mediaPlayer.reset();
            }
            songName.setText(songsList.get(index).getName());
            songName.setSelected(true);
            Uri uri = Uri.parse(songsList.get(index).toString());
            mediaPlayer = MediaPlayer.create(PlaySongActivity.this, uri);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    seekBar.setMax(mediaPlayer.getDuration());
                    playPause.setImageResource(android.R.drawable.ic_media_pause);
                    mediaPlayer.start();
                    updateSeekBar();
                }
            });
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            silentMode.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View view) {
                    if(silentFlag)
                    {
                        silentMode.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
                        audioManager.adjustVolume(AudioManager.ADJUST_UNMUTE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                    }
                    else
                    {
                        silentMode.setImageResource(android.R.drawable.ic_lock_silent_mode);
                        audioManager.adjustVolume(AudioManager.ADJUST_MUTE,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                    }
                    silentFlag = !silentFlag;
                }
            });
            repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(repeatFlag)
                        repeat.setImageResource(R.drawable.ic_baseline_repeat_24);
                    else
                        repeat.setImageResource(R.drawable.ic_baseline_repeat_one_24);
                    repeatFlag = !repeatFlag;
                }
            });
            seekBar.setMax(mediaPlayer.getDuration());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser)
                    {
                        mediaPlayer.seekTo(progress);
                        seekBar.setProgress(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            handler = new Handler();
            playPause.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
                    seekBar.setMax(mediaPlayer.getDuration());
                    if(mediaPlayer.isPlaying())
                    {
                        mediaPlayer.pause();
                        playPause.setImageResource(android.R.drawable.ic_media_play);
                    }
                    else
                    {
                        mediaPlayer.start();
                        playPause.setImageResource(android.R.drawable.ic_media_pause);
                    }
                }
            });
            onCompletion();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void onCompletion()
    {
        try {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(repeatFlag)
                    {
                        mediaPlayer.start();
                    }
                    else if(index == songsList.size()-1)
                        playPause.setImageResource(android.R.drawable.ic_media_play);
                    else
                    {
                        musicChangedToNext();
                    }
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void musicChangedToNext()
    {
        try{
            mediaPlayer.stop();
            mediaPlayer.release();
            index = (index+1)%songsList.size();
            songName.setText(songsList.get(index).getName());
            songName.setSelected(true);
            Uri uri = Uri.parse(songsList.get(index).toString());
            mediaPlayer = MediaPlayer.create(PlaySongActivity.this, uri);
            seekBar.setMax(mediaPlayer.getDuration());
            updateSeekBar();
            mediaPlayer.start();
            playPause.setImageResource(android.R.drawable.ic_media_pause);
            onCompletion();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void updateSeekBar() {
        try{
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            runnable = new Runnable() {
                @Override
                public void run() {
                    updateSeekBar();
                    long currentDuration = mediaPlayer.getCurrentPosition();
                    startingTime.setText(""+milliSecondsToTimer(currentDuration));
                    endingTime.setText(""+milliSecondsToTimer(mediaPlayer.getDuration()));
                }
            };
            handler.postDelayed(runnable,1000);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }


}