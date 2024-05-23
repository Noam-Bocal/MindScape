package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MusicManager {
    private Intent musicServiceIntent;
    private Context context;

    // Constructor to initialize MusicManager with a Context and create an intent for the MusicService
    MusicManager(Context cx) {
        this.context = cx;
        this.musicServiceIntent = new Intent(cx, MusicService.class);
    }

    // Send an intent to MusicService to play the specified sound
    public void playSound(String soundLink) {
        musicServiceIntent.putExtra("action", "PLAY");
        musicServiceIntent.putExtra("soundLink", soundLink);
        Log.d("MusicManager", "Play " + soundLink); // Log the sound being played
        context.startService(musicServiceIntent);
    }

    // Send an intent to MusicService to pause the currently playing sound
    public void pauseSound() {
        musicServiceIntent.putExtra("action", "PAUSE");
        context.startService(musicServiceIntent);
    }

    // Send an intent to MusicService to resume playback if it was paused
    public void resumeSound() {
        musicServiceIntent.putExtra("action", "RESUME");
        context.startService(musicServiceIntent);
    }

    // Send an intent to MusicService to stop the playback of the sound
    public void stopSound() {
        musicServiceIntent.putExtra("action", "STOP");
        context.startService(musicServiceIntent);
    }

    // Check if MusicService is currently playing a sound
    public boolean isPlaying() {
        return MusicService.isPlaying;
    }

    // Check if MusicService is currently paused
    public boolean isPaused() {
        return MusicService.isPaused;
    }
}
