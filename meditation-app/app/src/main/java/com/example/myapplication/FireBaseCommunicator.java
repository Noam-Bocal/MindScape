package com.example.myapplication;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class FireBaseCommunicator {
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Callback interface for fetching user gender
    public interface GenderCallback {
        void onGenderFetched(String gender);
    }

    // Callback interface for fetching meditations
    public interface MeditationCallback {
        void onMeditationsFetched(Map<String, String> soundData, Map<String, String> soundDurations);
    }

    // Callback interface for fetching sounds
    public interface SoundsCallback{
        void onSoundsFetched(Map<String, String> soundData, Map<String, String> soundDurations);
    }

    // Callback interface for fetching moods
    public interface MoodsCallback {
        void onMoodsFetched(Map<String, String> moodsData);
    }

    // Method to fetch user's gender
    public static void getUserGender(GenderCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("User")
                    .document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        // Retrieve gender from Firestore document
                        String gender = documentSnapshot.getString("gender");
                        // Pass the gender to the callback
                        callback.onGenderFetched(gender);
                    });
        }
    }

    // Method to fetch meditations based on emotion type and gender
    public static void fetchMeditations(String emotionType, String gender, MeditationCallback callback) {
        Map<String, String> soundData = new HashMap<>();
        Map<String, String> soundDurations = new HashMap<>();

        Query query = db.collection("Meditation")
                .whereIn("gender", Arrays.asList(gender, "both"));

        if (!TextUtils.isEmpty(emotionType)) {
            // Filter by emotion type if provided
            query = query.whereEqualTo("description", emotionType);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    // Retrieve meditation details from Firestore document
                    String soundName = document.getString("title");
                    String soundURL = document.getString("audio link");
                    String soundDuration = document.getString("duration");
                    soundData.put(soundName, soundURL);
                    soundDurations.put(soundName, soundDuration);
                }
                // Pass the fetched meditations to the callback
                callback.onMeditationsFetched(soundData, soundDurations);
            }
        });
    }

    // Method to fetch sounds
    public static void fetchSounds(SoundsCallback callback){
        Map<String, String> soundData = new HashMap<>();
        Map<String, String> soundDurations = new HashMap<>();

        db.collection("Sounds").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Retrieve sound details from Firestore document
                            String soundName = document.getString("name");
                            String soundURL = document.getString("url");
                            String soundDuration = document.getString("duration");
                            soundData.put(soundName, soundURL);
                            soundDurations.put(soundName, soundDuration);
                        }
                        // Pass the fetched sounds to the callback
                        callback.onSoundsFetched(soundData, soundDurations);
                    }
                });
    }

    // Method to fetch moods
    public static void fetchMoods(MoodsCallback callback)
    {
        Map<String, String> moodsData = new HashMap<>();
        db.collection("Moods").get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            // Retrieve mood details from Firestore document
                            String name = documentSnapshot.getString("name");
                            String meditation = documentSnapshot.getString("meditation");
                            moodsData.put(name, meditation);
                        }
                        // Pass the fetched moods to the callback
                        callback.onMoodsFetched(moodsData);
                    }
                });
    }

    // Method to get user's name
    public static String getUserName()
    {
        AtomicReference<String> user = new AtomicReference<>(""); //in order to use in the lambda
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("User")
                    .document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        // Retrieve username from Firestore document
                        user.set(documentSnapshot.getString("username"));
                    });
        }
        return user.get();
    }
}
