package com.example.myapplication;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Helper {

    // Constants for button design
    private static final int BUTTON_PADDING_LEFT = 16;
    private static final int BUTTON_PADDING_TOP = 8;
    private static final int BUTTON_PADDING_RIGHT = 16;
    private static final int BUTTON_PADDING_BOTTOM = 8;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 40;


    /**
     * Create and populate a table layout with buttons from the given sound data and durations.
     *
     * @param tableLayout the table layout to populate
     * @param context the current context
     * @param soundData a map of sound names to sound links
     * @param soundDurations a map of sound names to their durations
     * @param clickListener the listener for button click events
     */
    public static void createButtons(TableLayout tableLayout, Context context, Map<String, String> soundData, Map<String, String> soundDurations, View.OnClickListener clickListener) {
        List<String> soundNames = new ArrayList<>(soundData.keySet());
        int index = 0;
        while (index < soundNames.size()) {
            TableRow row = new TableRow(context);
            for (int i = 0; i < 2 && index < soundNames.size(); i++) {
                String soundName = soundNames.get(index);
                Button button = new Button(context);
                button.setText(soundName + " (" + soundDurations.get(soundName) + ")");
                button.setPadding(BUTTON_PADDING_LEFT, BUTTON_PADDING_TOP, BUTTON_PADDING_RIGHT, BUTTON_PADDING_BOTTOM);
                button.setWidth(BUTTON_WIDTH);
                button.setHeight(BUTTON_HEIGHT);
                button.setOnClickListener(clickListener);
                row.addView(button);
                index++;
            }
            tableLayout.addView(row);
        }
    }

    public static void createMoodsButtons(TableLayout tableLayout, Context context, Map<String, String> moodsData, View.OnClickListener clickListener)
    {
        List<String> moodsNames = new ArrayList<>(moodsData.keySet());
        int index = 0;
        while(index < moodsNames.size()){
            TableRow row = new TableRow(context);
            for(int i = 0; i < 2 && index < moodsNames.size(); i++){
                String moodName = moodsNames.get(index);
                Button button = new Button(context);
                button.setText(moodName);
                button.setPadding(BUTTON_PADDING_LEFT, BUTTON_PADDING_TOP, BUTTON_PADDING_RIGHT, BUTTON_PADDING_BOTTOM);
                button.setWidth(BUTTON_WIDTH);
                button.setHeight(BUTTON_HEIGHT);
                button.setOnClickListener(clickListener);
                row.addView(button);
                index++;
            }
            tableLayout.addView(row);
        }
    }

    /**
     * Sets the on click listener for the play button to toggle play/pause.
     *
     * @param playButton the play/pause button
     * @param musicManager the music manager to handle play and pause functionalities
     * @param tableLayout the table layout containing sound buttons
     * @param soundLink the link to the sound file
     */
    public static void setPlayButtonClickListener(ImageButton playButton, MusicManager musicManager, TableLayout tableLayout, String soundLink) {
        tableLayout.setVisibility(View.GONE);
        playButton.setVisibility(View.VISIBLE);
        if(!musicManager.isPlaying() && !musicManager.isPaused())
            playButton.setImageResource(R.drawable.play_icon);

        playButton.setOnClickListener(view -> {
            if (musicManager.isPlaying()) {
                musicManager.pauseSound();
                playButton.setImageResource(R.drawable.play_icon);
            }
            else if(musicManager.isPaused()){
                musicManager.resumeSound();
                playButton.setImageResource(R.drawable.pause_icon);
            }
            else {
                musicManager.playSound(soundLink);
                playButton.setImageResource(R.drawable.pause_icon);
            }
        });
    }
}
