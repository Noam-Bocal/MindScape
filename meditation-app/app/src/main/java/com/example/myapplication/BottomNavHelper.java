package com.example.myapplication;

import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BottomNavHelper {

    // Method to set up bottom navigation
    public static void setupBottomNav(BottomNavigationView bottomBar, final AppCompatActivity activity) {
        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Switch case to handle navigation menu item clicks
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        // Start MainActivity if not already the current activity
                        startIfNotCurrentActivity(activity, MainActivity.class);
                        return true;
                    case R.id.meditate:
                        // Start MeditateScreen if not already the current activity
                        startIfNotCurrentActivity(activity, MeditateScreen.class);
                        return true;
                    case R.id.nav_mood:
                        // Start MoodSelection_screen if not already the current activity
                        startIfNotCurrentActivity(activity, MoodSelection_screen.class);
                        return true;
                    case R.id.nav_reminder:
                        // Start Reminder_screen if not already the current activity
                        startIfNotCurrentActivity(activity, Reminder_screen.class);
                        return true;
                }

                return false;
            }
        });
    }

    // Method to start an activity if it is not the current activity
    private static void startIfNotCurrentActivity(AppCompatActivity currentActivity, Class<?> targetActivityClass) {
        if (!currentActivity.getClass().equals(targetActivityClass)) {
            // Create an intent to start the target activity
            Intent intent = new Intent(currentActivity, targetActivityClass);
            // Start the activity
            currentActivity.startActivity(intent);
            // Apply fade-in animation when starting the activity
            currentActivity.overridePendingTransition(android.R.anim.fade_in, 0);
        }
    }
}
