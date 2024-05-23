package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    public static boolean isPlaying = false;
    public static boolean isPaused = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            String action = intent.getStringExtra("action");

            // Handle different actions based on the intent received
            if ("PLAY".equals(action)) {
                String soundLink = intent.getStringExtra("soundLink");
                playSound(soundLink);
            } else if ("PAUSE".equals(action)) {
                pauseSound();
            } else if ("RESUME".equals(action)) {
                resumeSound();
            } else if ("STOP".equals(action)) {
                stopSound();
            }
        }
        return START_NOT_STICKY;
    }

    // Method to play the specified sound
    private void playSound(String soundLink) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        } else {
            mediaPlayer = new MediaPlayer();
        }

        try {
            mediaPlayer.setDataSource(soundLink);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPlaying = true;
                isPaused = false;

                // Set a listener for completion of playback
                mp.setOnCompletionListener(mediaPlayer -> {
                    Intent completionIntent = new Intent("com.example.myapplication.MUSIC_COMPLETED");
                    sendBroadcast(completionIntent);
                    mediaPlayer.reset();
                    isPlaying = false;
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to pause the currently playing sound
    private void pauseSound() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            isPaused = true;
        }
    }

    // Method to resume playback if it was paused
    private void resumeSound() {
        if (mediaPlayer != null && isPaused) {
            mediaPlayer.start();
            isPlaying = true;
            isPaused = false;
        }
    }

    // Method to stop the playback of the sound
    private void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            isPlaying = false;
            isPaused = false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not a bound service.
    }
}
