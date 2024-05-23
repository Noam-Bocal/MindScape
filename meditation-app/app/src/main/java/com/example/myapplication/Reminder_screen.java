package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.rpc.Help;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class Reminder_screen extends AppCompatActivity implements ShakeDetector.OnShakeListener{
    // Notification ID for the reminder notification
    private static final int NOTIFICATION_ID = 1;

    // Music manager for playing meditation sounds
    public MusicManager musicManager;

    // Shake detector for handling shake events
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_screen);

        // Setup UI components such as bottom navigation, music manager, and notification channel
        setupUIComponents();

        // Setup shake detection for handling shake events
        setupShakeDetection();

        // Check if the activity was started from a notification and handle it
        if (cameFromNotification()) {
            handleNotificationIntent();
        }
    }

    @Override
    public void onShake() {
        // Stop playing the sound if currently playing and navigate to the main activity
        if(musicManager.isPlaying())
            navigateToMainActivity();
    }

    // Setup shake detection using the ShakeDetector class
    private void setupShakeDetection() {
        shakeDetector = new ShakeDetector(this);
        shakeDetector.setOnShakeListener(this);
    }

    // Initialize UI components such as bottom navigation, music manager, and notification channel
    private void setupUIComponents() {
        setupBottomNavigation();
        musicManager = new MusicManager(getApplicationContext());
        createNotificationChannel();
        // Fetch meditations based on user's gender
        FireBaseCommunicator.getUserGender(this::fetchMeditations);

        Button backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> navigateToMainActivity());
    }

    // Setup bottom navigation using the BottomNavigationView class
    private void setupBottomNavigation() {
        BottomNavigationView bottomBar = findViewById(R.id.bottom_navigation);
        BottomNavHelper.setupBottomNav(bottomBar, this);
    }

    // Check if the activity was started from a notification
    private boolean cameFromNotification() {
        return getIntent().getBooleanExtra("FROM_NOTIFICATION", false);
    }

    // Handle the intent received from the notification
    private void handleNotificationIntent() {
        Toast.makeText(this, "Came from notification!", Toast.LENGTH_SHORT).show();
        String soundLink = getIntent().getStringExtra("MEDITATION_LINK");
        // Display play button for the meditation sound from the notification
        displayPlayButton(soundLink);
    }

    // Display a play button for the selected meditation sound
    private void displayPlayButton(String soundLink) {
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        ImageButton playButton = findViewById(R.id.playImageButton);
        findViewById(R.id.timePicker).setVisibility(View.GONE);
        findViewById(R.id.backBtn).setVisibility(View.VISIBLE);
        Helper.setPlayButtonClickListener(playButton, musicManager, tableLayout, soundLink);
    }

    // Navigate to the main activity
    private void navigateToMainActivity() {
        musicManager.stopSound();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, 0);
    }

     // Fetch meditations based on user's gender and populate the UI
    private void fetchMeditations(String gender) {
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        FireBaseCommunicator.fetchMeditations("", gender, (soundData, soundDurations) ->
                Helper.createButtons(tableLayout, this, soundData, soundDurations,
                        view -> {
                            String soundName = ((Button) view).getText().toString().split(" ")[0]; // Extracting soundName from the button's text.
                            // Set a reminder for the selected meditation sound
                            setReminder(soundName, soundData.get(soundName));
                        })
        );
    }

    // Set a reminder for the selected meditation sound
    private void setReminder(String meditationName, String meditationLink) {
        TimePicker timePicker = findViewById(R.id.timePicker);
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        Calendar targetCal = Calendar.getInstance();
        targetCal.set(Calendar.HOUR_OF_DAY, hour);
        targetCal.set(Calendar.MINUTE, minute);

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("MEDITATION_NAME", meditationName);
        intent.putExtra("MEDITATION_LINK", meditationLink);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
    }

    // Create a notification channel for meditation reminders
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MeditationReminderChannel";
            String description = "Channel for Meditation Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("YOUR_CHANNEL_ID", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        // Unregister the shake detector
        if (shakeDetector != null) {
            shakeDetector.unregisterListener();
        }
    }
}
