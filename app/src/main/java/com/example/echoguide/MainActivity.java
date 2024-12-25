package com.example.echoguide;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    TextView tvResult;
    //private RelativeLayout relativeLayout;
    private LinearLayout relativeLayout;

    private TextToSpeech textToSpeech;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
        relativeLayout = findViewById(R.id.relativeLayout);

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                String welText = "Welcome, User! To explore the features of the application, swipe right. For giving commands or instructions, swipe left.";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(welText, TextToSpeech.QUEUE_FLUSH, null, null);
                }
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language is not supported or missing data");
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });

        // Set up gesture detection
        gestureDetector = new GestureDetector(this, new MyGestureListener());

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void speak(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Right swipe
                        speak("Swipe right. Listen features.");
                        // Perform action for listening features
                        // For now, let's open a new activity as an example
                        Intent intent = new Intent(MainActivity.this, ListenFeaturesActivity.class);
                        startActivity(intent);
                    } else {
                        // Left swipe
                        speak("Swipe left. Give command.");
                        // Perform action for giving a command
                        // For now, let's open a new activity as an example
                        startVoiceInput();
                    }
                    return true;
                }
            }

            return false;
        }
    }



    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Speech recognition not supported on your device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (result != null && !result.isEmpty()) {
                String voiceInput = result.get(0);
                processVoiceInput(voiceInput);
            } else {
                // Handle case when voice command is not recognized
                // You may provide a message or take appropriate action
            }
        }
    }

    private void processVoiceInput(String voiceInput) {
        // Check for keywords and perform actions accordingly
        if (voiceInput.toLowerCase().contains("read")) {
            // Open a new activity to read text from the camera
            openReadTextActivity();
        } else if (voiceInput.toLowerCase().contains("location")) {
            // Get current location
            getLocationActivity();
        }else if (voiceInput.toLowerCase().contains("datetime")) {
            // Get current location
            getDateTime();
        }else if (voiceInput.toLowerCase().contains("object")) {
            // Get current location
            getObjectDetectActivity();
        }else if (voiceInput.toLowerCase().contains("calculator")) {
            // Get current location
            getCalculatorActivity();
        }else if (voiceInput.toLowerCase().contains("battery")) {
            // Get current location
            getBatteryDetails();
        }else if (voiceInput.toLowerCase().contains("emergency")) {
            // Get current location
            getEmergencyActivity();
        }else if (voiceInput.toLowerCase().contains("reminder")) {
            // Get current location
            getReminderActivity();
        }else if (voiceInput.toLowerCase().contains("music")) {
            // Get current location
            getMusicActivity();
        }else if (voiceInput.toLowerCase().contains("exit")) {
            // Get current location
            exit();
        }else if (voiceInput.toLowerCase().contains("currency")) {
            // Get current location
            getCurrencyDetection();
        }else if (voiceInput.toLowerCase().contains("youtube")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.youtube.com/"));
            intent.setPackage("com.google.android.youtube");

// Check if YouTube app is installed
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
               String text ="YouTube app not installed";
               speak(text);
            }
        }

    }
    private void getCurrencyDetection() {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            intent = new Intent(this, ClassifierActivity.class);
        }
        startActivity(intent);
    }
    private void openReadTextActivity() {
        Intent intent = new Intent(this, ReadTextActivity.class);
        startActivity(intent);
    }
    private void getLocationActivity() {
        Intent intent = new Intent(this, getLocationActivity.class);
        startActivity(intent);
    }
    private void getObjectDetectActivity() {
        Intent intent = new Intent(this, objectDetectionActivity.class);
        startActivity(intent);
    }
    private void getCalculatorActivity() {
        Intent intent = new Intent(this, Calculator.class);
        startActivity(intent);
    }
    private void getEmergencyActivity() {
        Intent intent = new Intent(this, ContactList.class);
        startActivity(intent);
    }
    private void getReminderActivity() {
        Intent intent = new Intent(this, Reminder.class);
        startActivity(intent);
    }
    private void getMusicActivity() {
        Intent intent = new Intent(this, MusicPlayer.class);
        startActivity(intent);
    }
    private void getDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        String currentDate = dateFormat.format(calendar.getTime());
        String currentTime = timeFormat.format(calendar.getTime());

        String dateTime = "Current date is " + currentDate + " and time is " + currentTime;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(dateTime, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }
    private void getBatteryDetails() {
        // Register a BroadcastReceiver to get battery details
        BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                float batteryPercentage = level / (float) scale * 100;

                String batteryStatus;
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    batteryStatus = "Charging";
                } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING
                        || status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                    batteryStatus = "Not Charging";
                } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                    batteryStatus = "Fully Charged";
                } else {
                    batteryStatus = "Unknown";
                }

                String batteryDetails = "Battery level is " + batteryPercentage + " percent. Battery status is " + batteryStatus;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textToSpeech.speak(batteryDetails, TextToSpeech.QUEUE_FLUSH, null, null);
                }

                unregisterReceiver(this);
            }
        };

        // Register the receiver for battery updates
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);
    }
    private void exit() {
        finish();
    }
    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
