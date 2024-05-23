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

public class MoodSelection_screen extends AppCompatActivity implements ShakeDetector.OnShakeListener{

    public MusicManager musicManager;
    private ShakeDetector shakeDetector;

    private BroadcastReceiver musicCompletionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Reset UI when music completes
            if (intent.getAction().equals("com.example.myapplication.MUSIC_COMPLETED")) {
                resetUI(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_selection_screen);

        // Initialize UI components and fetch moods
        initializeUIComponents();
        fetchMoods();
        setupShakeDetection();
    }

    @Override
    public void onShake() {
        // Stop music and reset UI on shake
        if(musicManager.isPlaying())
            stopAndResetUI();
    }


    private void setupShakeDetection() {
        // Setup shake detection
        shakeDetector = new ShakeDetector(this);
        shakeDetector.setOnShakeListener(this);
    }

    private void initializeUIComponents() {
        // Initialize UI components and register music completion receiver
        musicManager = new MusicManager(getApplicationContext());

        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> stopAndResetUI());

        BottomNavigationView bottomBar = findViewById(R.id.bottom_navigation);
        BottomNavHelper.setupBottomNav(bottomBar, this);

        registerMusicCompletionReceiver();
    }

    private void registerMusicCompletionReceiver() {
        // Register music completion receiver
        IntentFilter filter = new IntentFilter("com.example.myapplication.MUSIC_COMPLETED");
        registerReceiver(musicCompletionReceiver, filter);
    }

    private void stopAndResetUI() {
        // Stop music and reset UI
        musicManager.stopSound();
        resetUI(false);
    }

    public void fetchMoods() {
        // Fetch moods and create buttons in UI
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        tableLayout.removeAllViews();
        tableLayout.setVisibility(View.VISIBLE);
        FireBaseCommunicator.fetchMoods((moodsData) ->
                Helper.createMoodsButtons(tableLayout, this, moodsData,
                        view -> {
                            String moodName = ((Button) view).getText().toString().split(" ")[0]; // Extract moodName from the button's text.
                            fetchMeditations(moodsData.get(moodName));
                        })
        );
    }

    private void fetchMeditations(String emotionType) {
        // Fetch meditations based on emotionType and create buttons in UI
        FireBaseCommunicator.getUserGender(gender -> {
            resetUI(true);
            TableLayout tableLayout = findViewById(R.id.tableLayout);
            tableLayout.removeAllViews();
            tableLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.backBtn).setVisibility(View.VISIBLE);
            FireBaseCommunicator.fetchMeditations(emotionType, gender, (soundData, soundDurations) ->
                    Helper.createButtons(tableLayout, this, soundData, soundDurations,
                            view -> {
                                String soundName = ((Button) view).getText().toString().split(" ")[0]; // Extract soundName from the button's text.
                                displayPlayButtonUsingHelper(soundName, soundData.get(soundName));
                            })
            );
        });
    }

    private void displayPlayButtonUsingHelper(String soundName, String soundLink) {
        // Display play button for selected sound
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        ImageButton playButton = findViewById(R.id.playImageButton);
        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setVisibility(View.VISIBLE);
        Helper.setPlayButtonClickListener(playButton, musicManager, tableLayout, soundLink);
    }

    private void resetUI(boolean calledByFetchMeditations) {
        // Reset UI components
        if(!calledByFetchMeditations)
            fetchMoods();
        ImageButton playButton = findViewById(R.id.playImageButton);
        Button backBtn = findViewById(R.id.backBtn);

        playButton.setVisibility(View.GONE);
        backBtn.setVisibility(View.GONE);
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
