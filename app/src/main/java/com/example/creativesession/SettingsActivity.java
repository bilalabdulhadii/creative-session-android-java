package com.example.creativesession;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private Settings currentSettings;
    private TextView focusTextView, shortBreakTextView, longBreakTextView, intervalTextView, resetSettingsTextView;
    private ImageButton goBackImgBtn, focusDownImgBtn, focusUpImgBtn,
            shortBreakDownImgBtn, shortBreakUpImgBtn,
            longBreakDownImgBtn, longBreakUpImgBtn,
            intervalDownImgBtn, intervalUpImgBtn;
    private Switch autoFocusSwitch, autoBreakSwitch;
    private static final int maxValue = 120, minValue = 5, intervalMaxValue = 120, intervalMinValue = 2;
    private int currentValue;
    public static boolean isSettingsChanged = false;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        resetSettingsTextView = findViewById(R.id.resetSettings_TextView);
        focusTextView = findViewById(R.id.focus_TextView);
        shortBreakTextView = findViewById(R.id.shortBreak_TextView);
        longBreakTextView = findViewById(R.id.longBreak_TextView);
        intervalTextView = findViewById(R.id.interval_TextView);
        autoFocusSwitch = findViewById(R.id.autoFocus_Switch);
        autoBreakSwitch = findViewById(R.id.autoBreak_Switch);

        goBackImgBtn = findViewById(R.id.goBack_ImgBtn);
        focusDownImgBtn = findViewById(R.id.focusDown_ImgBtn);
        focusUpImgBtn = findViewById(R.id.focusUp_ImgBtn);
        shortBreakDownImgBtn = findViewById(R.id.shortBreakDown_ImgBtn);
        shortBreakUpImgBtn = findViewById(R.id.shortBreakUp_ImgBtn);
        longBreakDownImgBtn = findViewById(R.id.longBreakDown_ImgBtn);
        longBreakUpImgBtn = findViewById(R.id.longBreakUp_ImgBtn);
        intervalDownImgBtn = findViewById(R.id.intervalDown_ImgBtn);
        intervalUpImgBtn = findViewById(R.id.intervalUp_ImgBtn);

        goBackImgBtn.setOnClickListener(this);
        resetSettingsTextView.setOnClickListener(this);
        focusDownImgBtn.setOnClickListener(this);
        focusUpImgBtn.setOnClickListener(this);
        shortBreakDownImgBtn.setOnClickListener(this);
        shortBreakUpImgBtn.setOnClickListener(this);
        longBreakDownImgBtn.setOnClickListener(this);
        longBreakUpImgBtn.setOnClickListener(this);
        intervalDownImgBtn.setOnClickListener(this);
        intervalUpImgBtn.setOnClickListener(this);

        autoFocusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateSettings();
            }
        });
        autoBreakSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateSettings();
            }
        });

        //current_settings = databaseHelper.getSettings();
        loadSettings();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.goBack_ImgBtn) {
            // Return to MainActivity
            finish();
        } else if (id == R.id.resetSettings_TextView) {
            Settings settings = new Settings().defaultSettings();
            databaseHelper.updateSettings(settings);
            loadSettings();
            isSettingsChanged = true;
        } else if (id == R.id.focusDown_ImgBtn) {
            decrementValue(focusTextView, minValue);
            updateSettings();
        } else if (id == R.id.focusUp_ImgBtn) {
            incrementValue(focusTextView, maxValue);
            updateSettings();
        } else if (id == R.id.shortBreakDown_ImgBtn) {
            decrementValue(shortBreakTextView, minValue);
            updateSettings();
        } else if (id == R.id.shortBreakUp_ImgBtn) {
            incrementValue(shortBreakTextView, maxValue);
            updateSettings();
        } else if (id == R.id.longBreakDown_ImgBtn) {
            decrementValue(longBreakTextView, minValue);
            updateSettings();
        } else if (id == R.id.longBreakUp_ImgBtn) {
            incrementValue(longBreakTextView, maxValue);
            updateSettings();
        } else if (id == R.id.intervalDown_ImgBtn) {
            decrementValue(intervalTextView, intervalMinValue);
            updateSettings();
        } else if (id == R.id.intervalUp_ImgBtn) {
            incrementValue(intervalTextView, intervalMaxValue);
            updateSettings();
        }
    }

    private void loadSettings() {
        currentSettings = databaseHelper.getSettings();
        if (currentSettings != null) {
            focusTextView.setText(String.valueOf(currentSettings.getFocus()));
            shortBreakTextView.setText(String.valueOf(currentSettings.getShortBreak()));
            longBreakTextView.setText(String.valueOf(currentSettings.getLongBreak()));
            intervalTextView.setText(String.valueOf(currentSettings.getInterval()));
            autoFocusSwitch.setChecked(currentSettings.isAutoFocus());
            autoBreakSwitch.setChecked(currentSettings.isAutoBreak());
        }
    }

    private void incrementValue(TextView textView, int max) {
        currentValue = Integer.parseInt(textView.getText().toString());
        if (currentValue < max) {
            textView.setText(String.valueOf(currentValue + 1));
        }
    }

    private void decrementValue(TextView textView, int min) {
        currentValue = Integer.parseInt(textView.getText().toString());
        if (currentValue > min) {
            textView.setText(String.valueOf(currentValue - 1));
        }
    }

    private void updateSettings() {
        Settings updatedSettings = new Settings();
        updatedSettings.setFocus(Integer.parseInt(focusTextView.getText().toString()));
        updatedSettings.setShortBreak(Integer.parseInt(shortBreakTextView.getText().toString()));
        updatedSettings.setLongBreak(Integer.parseInt(longBreakTextView.getText().toString()));
        updatedSettings.setInterval(Integer.parseInt(intervalTextView.getText().toString()));
        updatedSettings.setAutoFocus(autoFocusSwitch.isChecked());
        updatedSettings.setAutoBreak(autoBreakSwitch.isChecked());

        /*int focus = Integer.parseInt(focusTextView.getText().toString());
        int shortBreak = Integer.parseInt(shortBreakTextView.getText().toString());
        int longBreak = Integer.parseInt(longBreakTextView.getText().toString());
        int interval = Integer.parseInt(intervalTextView.getText().toString());
        boolean autoFocus = autoFocusSwitch.isChecked();
        boolean autoBreak = autoBreakSwitch.isChecked();*/

        Settings settings = new Settings(updatedSettings);
        databaseHelper.updateSettings(settings);
        isSettingsChanged = true;
    }
}