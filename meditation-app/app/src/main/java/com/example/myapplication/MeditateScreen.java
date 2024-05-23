package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

public class MeditateScreen extends AppCompatActivity implements ShakeDetector.OnShakeListener {

    public MusicManager musicManager;
    private ShakeDetector shakeDetector;

    private BroadcastReceiver musicCompletionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Reset UI when music completes
            if (intent.getAction().equals("com.example.myapplication.MUSIC_COMPLETED")) {
                resetUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditate_screen);

        initializeUIComponents();
        setupShakeDetection();
    }

    @Override
    public void onShake() {
        // Stop music and reset UI on shake
        if(musicManager.isPlaying())
            stopAndResetUI();
    }


    private void setupShakeDetection() {
        shakeDetector = new ShakeDetector(this);
        shakeDetector.setOnShakeListener(this);
    }

    private void initializeUIComponents() {
        musicManager = new MusicManager(getApplicationContext());

        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> stopAndResetUI());

        BottomNavigationView bottomBar = findViewById(R.id.bottom_navigation);
        BottomNavHelper.setupBottomNav(bottomBar, this);

        registerMusicCompletionReceiver();
    }

    private void registerMusicCompletionReceiver() {
        // Register receiver for music completion
        IntentFilter filter = new IntentFilter("com.example.myapplication.MUSIC_COMPLETED");
        registerReceiver(musicCompletionReceiver, filter);
    }

    private void stopAndResetUI() {
        // Stop music and reset UI
        musicManager.stopSound();
        resetUI();
    }

    /**
     * Fetch the sound data and populate the UI with buttons for each sound.
     *
     * @param v the view that triggered this method (button, etc.)
     */
    public void fetchSounds(View v) {
        // Fetch sounds and create buttons in UI
        resetUI();
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        tableLayout.removeAllViews();
        tableLayout.setVisibility(View.VISIBLE);
        FireBaseCommunicator.fetchSounds((soundData, soundDurations) ->
                Helper.createButtons(tableLayout, this, soundData, soundDurations,
                        view -> {
                            // Extract sound name from button and play it
                            String soundName = ((Button) view).getText().toString().split(" ")[0];
                            String soundLink = soundData.get(soundName);
                            displayPlayButtonUsingHelper(soundName, soundLink);
                        })
        );
    }

    public void getUserGender(View v)
    {
        // Fetch user's gender and fetch corresponding meditations
        FireBaseCommunicator.getUserGender(this::fetchMeditations);
    }

    public void fetchMeditations(String gender) {
        // Fetch meditations based on gender and create buttons in UI
        resetUI();
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        tableLayout.removeAllViews();
        tableLayout.setVisibility(View.VISIBLE);
        FireBaseCommunicator.fetchMeditations("", gender, (soundData, soundDurations) ->
                Helper.createButtons(tableLayout, this, soundData, soundDurations,
                        view -> {
                            String soundName = ((Button) view).getText().toString().split(" ")[0]; // Extracting soundName from the button's text.
                            displayPlayButtonUsingHelper(soundName, soundData.get(soundName));
                        })
        );
    }

    private void displayPlayButtonUsingHelper(String soundName, String soundLink) {
        // Display play button for selected sound
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        ImageButton playButton = findViewById(R.id.playImageButton);
        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setVisibility(View.VISIBLE);
        Helper.setPlayButtonClickListener(playButton, musicManager, tableLayout, soundLink);
    }

    private void resetUI() {
        // Reset UI components
        ImageButton playButton = findViewById(R.id.playImageButton);
        Button meditationBtn = findViewById(R.id.meditateBtn);
        Button backBtn = findViewById(R.id.backBtn);
        Button musicBtn = findViewById(R.id.musicBtn);

        playButton.setVisibility(View.GONE);
        meditationBtn.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.GONE);
        musicBtn.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the shake detector and music completion receiver
        if (shakeDetector != null) {
            shakeDetector.unregisterListener();
        }
        unregisterReceiver(musicCompletionReceiver);
    }
}
