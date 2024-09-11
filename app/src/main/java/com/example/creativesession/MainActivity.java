package com.example.creativesession;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseHelper databaseHelper;
    private Settings currentSettings;
    private final static int SELECT_TASK_REQUEST_CODE = 12;

    private static int FOCUS_DURATION, SHORT_BREAK_DURATION, LONG_BREAK_DURATION, LONG_BREAK_INTERVAL;
    private static final int focus = 1, shortBreak = 2, longBreak = 3;
    private long timeLeftInMillis;
    private int selectedTabIndex = 1, focusCounter = 0, lastSelectedTaskId = 0;
    private TextView timerTextView, currentStatusTextView, focusTabTextView,
            shortBreakTabTextView, longBreakTabTextView;
    private ImageButton resetImgBtn /*nextImgBtn*/;
    private Button startPauseBtn, taskNameBtn;
    private RelativeLayout statusRelativeLayout;
    private CountDownTimer countDownTimer;
    private ConstraintLayout actionBarConstraintLayout, mainConstraintLayout;
    private boolean isTimerRunning = false, isTaskSelected = false;
    private static boolean orientationSwitched = false;
    private Task selectedTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskNameBtn = findViewById(R.id.taskName_Btn);
        loadSettings();

        mainConstraintLayout = findViewById(R.id.main_ConstraintLayout);
        actionBarConstraintLayout = findViewById(R.id.actionBar_ConstraintLayout);
        timerTextView = findViewById(R.id.timer_TextView);
        currentStatusTextView = findViewById(R.id.currentStatus_TextView);
        focusTabTextView = findViewById(R.id.focusTab_TextView);
        shortBreakTabTextView = findViewById(R.id.shortBreakTab_TextView);
        longBreakTabTextView = findViewById(R.id.longBreakTab_TextView);
        statusRelativeLayout = findViewById(R.id.status_RelativeLayout);
        startPauseBtn = findViewById(R.id.startPause_Btn);
        resetImgBtn = findViewById(R.id.reset_ImgBtn);
        //nextImgBtn = findViewById(R.id.next_ImgBtn);
        ImageButton settingsBtn = findViewById(R.id.settings_ImgBtn);
        ImageButton aboutImgBtn = findViewById(R.id.about_ImgBtn);
        /*ImageButton accountImgBtn = findViewById(R.id.account_ImgBtn);*/

        timeLeftInMillis = FOCUS_DURATION;
        startPauseBtn.setOnClickListener(this);
        resetImgBtn.setOnClickListener(this);
        //nextImgBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);
        aboutImgBtn.setOnClickListener(this);
        /*accountImgBtn.setOnClickListener(this);*/
        focusTabTextView.setOnClickListener(this);
        shortBreakTabTextView.setOnClickListener(this);
        longBreakTabTextView.setOnClickListener(this);
        taskNameBtn.setOnClickListener(this);
        updateTimerText();

        if (savedInstanceState != null) {
            timeLeftInMillis = savedInstanceState.getLong("timeLeftInMillis");
            isTimerRunning = savedInstanceState.getBoolean("isTimerRunning");
            selectedTabIndex = savedInstanceState.getInt("selectedTabIndex");
            isTaskSelected = savedInstanceState.getBoolean("isTaskSelected");
            lastSelectedTaskId = savedInstanceState.getInt("lastSelectedTaskId");

            updateTimerText();
            selectedTab2(selectedTabIndex);
            updateCurrentStatusTextView();
            if (isTimerRunning) {
                startTimer();
            }
            focusCounter = savedInstanceState.getInt("focusCounter");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SettingsActivity.isSettingsChanged) {
            SettingsActivity.isSettingsChanged = false;
            loadSettings();
            resetTimer();
        }

        if (TasksActivity.isTasksChecked) {
            loadSelectedTask();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientationSwitched = true;

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Do something when in landscape mode
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Do something when in portrait mode
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("timeLeftInMillis", timeLeftInMillis);
        outState.putBoolean("isTimerRunning", isTimerRunning);
        outState.putInt("selectedTabIndex", selectedTabIndex);
        outState.putInt("focusCounter", focusCounter);
        outState.putBoolean("isTaskSelected", isTaskSelected);
        outState.putInt("lastSelectedTaskId", lastSelectedTaskId);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.settings_ImgBtn) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.about_ImgBtn) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        } /*else if (id == R.id.account_ImgBtn) {
            Intent intent = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
        }*/ /*else if (id == R.id.focusTab_TextView) {
            selectedTab(focus);
            updateCurrentStatusTextView();
        } else if (id == R.id.shortBreakTab_TextView) {
            selectedTab(shortBreak);
            updateCurrentStatusTextView();
        } else if (id == R.id.longBreakTab_TextView) {
            selectedTab(longBreak);
            updateCurrentStatusTextView();
        }*/ else if (id == R.id.startPause_Btn) {
            if (isTimerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        } else if (id == R.id.reset_ImgBtn) {
            // Handle click for stop
            pauseTimer();
            resetTimer();
        } else if (id == R.id.taskName_Btn) {
            //Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show();
            if (!isTimerRunning) {
                //taskNameBtn.setBackground(null);
                taskNameBtn.setBackgroundResource(obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground}).getResourceId(0, 0));
                Intent intent = new Intent(MainActivity.this, TasksActivity.class);
                startActivityForResult(intent, SELECT_TASK_REQUEST_CODE);
            } else {
                taskNameBtn.setBackground(null);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_TASK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            /*lastSelectedTaskId = data.getIntExtra("SELECT_TASK", -1);*/
            loadSelectedTask();
        }
    }

    private void loadSettings() {
        databaseHelper = new DatabaseHelper(this);
        currentSettings = databaseHelper.getSettings();
        FOCUS_DURATION = 60 * 1000 * currentSettings.getFocus();
        SHORT_BREAK_DURATION = 60 * 1000 * currentSettings.getShortBreak();
        LONG_BREAK_DURATION = 60 * 1000 * currentSettings.getLongBreak();
        LONG_BREAK_INTERVAL = currentSettings.getInterval();
        loadSelectedTask();
    }

    private void loadSelectedTask() {
        databaseHelper = new DatabaseHelper(this);
        currentSettings = databaseHelper.getSettings();
        lastSelectedTaskId = currentSettings.getLastTask();
        if (lastSelectedTaskId > 0) {
            databaseHelper = new DatabaseHelper(this);
            selectedTask = databaseHelper.getTaskById(lastSelectedTaskId);

            if (!selectedTask.isCompleted()) {
                String taskTitle = selectedTask.getCompletedSessions() + "/" + selectedTask.getDesiredSessions() + " " + selectedTask.getTitle();
                taskNameBtn.setText(taskTitle);
                isTaskSelected = true;
                if (focusCounter > 0) {
                    selectedTab(focus);
                    updateCurrentStatusTextView();
                }
            } else {
                if (focusCounter == 0) {
                    selectedTab(focus);
                    updateCurrentStatusTextView();
                }
                String taskTitle = "Click to select task";
                taskNameBtn.setText(taskTitle);
            }
        } else {
            String taskTitle = "Click to select task";
            taskNameBtn.setText(taskTitle);
        }
    }

    private void resetTimer() {
        if(selectedTabIndex == focus) {
            timeLeftInMillis = FOCUS_DURATION;
        } else if (selectedTabIndex == shortBreak) {
            timeLeftInMillis = SHORT_BREAK_DURATION;
        } else if (selectedTabIndex == longBreak) {
            timeLeftInMillis = LONG_BREAK_DURATION;
        }
        updateTimerText();
        //workingTimerMode();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeftFormatted);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                resetTimer();

                if (selectedTabIndex == focus) {
                    boolean resetCounter = false;
                    focusCounter++;
                    if (isTaskSelected) {
                        if (selectedTask.getId() > 0) {
                            databaseHelper = new DatabaseHelper(MainActivity.this);
                            if (!selectedTask.isCompleted()) {
                                int newCompletedSessions = selectedTask.getCompletedSessions() + 1;
                                boolean isUpdated = databaseHelper.updateCompletedSessions(selectedTask.getId(), newCompletedSessions);
                                if (isUpdated) {
                                    selectedTask = databaseHelper.getTaskById(selectedTask.getId());
                                    String taskTitle = selectedTask.getCompletedSessions() + "/" + selectedTask.getDesiredSessions() + " " + selectedTask.getTitle();
                                    taskNameBtn.setText(taskTitle);
                                    focusCounter = selectedTask.getCompletedSessions();
                                }
                                resetCounter = true;
                            }
                        }
                    }

                    // Check if it's time for a long break
                    if (focusCounter % LONG_BREAK_INTERVAL == 0) {
                        selectedTab(longBreak);
                    } else {
                        selectedTab(shortBreak);
                    }

                    if(resetCounter) {
                        focusCounter = 0;
                    } else {
                        String taskTitle = "Click to select task";
                        taskNameBtn.setText(taskTitle);
                    }
                } else {
                    selectedTab(focus);
                }

                // Start the timer automatically
                if (selectedTabIndex == focus) {
                    if (currentSettings.isAutoFocus()) {
                        startTimer();
                    } else {
                        stoppingTimerMode();
                    }
                }
                else {
                    if (currentSettings.isAutoBreak()) {
                        startTimer();
                    } else {
                        stoppingTimerMode();
                    }
                }

                updateCurrentStatusTextView();
            }
        }.start();
        workingTimerMode();
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        stoppingTimerMode();
    }

    private void stoppingTimerMode() {
        isTimerRunning = false;
        startPauseBtn.setText("START");
        startPauseBtn.setTextColor(Color.parseColor("#4CAF50"));
        //currentStatusTextView.setVisibility(View.VISIBLE);
        currentStatusTextView.setTextColor(Color.parseColor("#3F51B5"));
        taskNameBtn.setTextColor(Color.parseColor("#F44336"));
        timerTextView.setTextColor(Color.parseColor("#3C3C3C"));
        timerTextView.setBackgroundColor(Color.parseColor("#BABABA"));
        resetImgBtn.setVisibility(View.VISIBLE);
        //nextImgBtn.setVisibility(View.VISIBLE);
        statusRelativeLayout.setVisibility(View.VISIBLE);
        actionBarConstraintLayout.setVisibility(View.VISIBLE);
        mainConstraintLayout.setBackgroundColor(Color.parseColor("#F4F4F2"));
        updateCurrentStatusTextView();
        disableFullScreenMode();
    }

    private void workingTimerMode() {
        isTimerRunning = true;
        startPauseBtn.setText("PAUSE");
        startPauseBtn.setTextColor(Color.parseColor("#aaaaaa")); // F44336
        //currentStatusTextView.setVisibility(View.INVISIBLE);
        currentStatusTextView.setTextColor(Color.parseColor("#aaaaaa")); // F44336
        taskNameBtn.setTextColor(Color.parseColor("#aaaaaa")); // F44336
        timerTextView.setTextColor(Color.parseColor("#aaaaaa")); // F44336
        timerTextView.setBackgroundColor(Color.parseColor("#000000")); // BABABA
        resetImgBtn.setVisibility(View.INVISIBLE);
        //nextImgBtn.setVisibility(View.INVISIBLE);
        statusRelativeLayout.setVisibility(View.INVISIBLE);
        actionBarConstraintLayout.setVisibility(View.INVISIBLE);
        mainConstraintLayout.setBackgroundColor(Color.parseColor("#000000")); // BABABA
        updateCurrentStatusTextView();
        enableFullScreenMode();
    }

    private void selectedTab(int tabIndex) {
        /*pauseTimer();*/
        TextView selectedTextView = focusTabTextView;
        TextView nonSelectedTextView1 = shortBreakTabTextView;
        TextView nonSelectedTextView2 = longBreakTabTextView;

        if (tabIndex == focus) {
            selectedTextView = focusTabTextView;
            nonSelectedTextView1 = shortBreakTabTextView;
            nonSelectedTextView2 = longBreakTabTextView;
            timeLeftInMillis = FOCUS_DURATION;
            updateTimerText();
        } else if (tabIndex == shortBreak) {
            selectedTextView = shortBreakTabTextView;
            nonSelectedTextView1 = focusTabTextView;
            nonSelectedTextView2 = longBreakTabTextView;
            timeLeftInMillis = SHORT_BREAK_DURATION;
            updateTimerText();
        } else if (tabIndex == longBreak) {
            selectedTextView = longBreakTabTextView;
            nonSelectedTextView1 = focusTabTextView;
            nonSelectedTextView2 = shortBreakTabTextView;
            timeLeftInMillis = LONG_BREAK_DURATION;
            updateTimerText();
        }

        // Update the selected tab
        selectedTextView.setBackgroundResource(R.drawable.selected_tab_back);
        selectedTextView.setTypeface(null, Typeface.BOLD);

        // Update the non-selected tabs
        nonSelectedTextView1.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        nonSelectedTextView1.setTypeface(null, Typeface.NORMAL);

        nonSelectedTextView2.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        nonSelectedTextView2.setTypeface(null, Typeface.NORMAL);

        /*float slideTo = (tabIndex - selectedTabIndex) * selectedTextView.getWidth();
        TranslateAnimation translateAnimation = new TranslateAnimation(0, slideTo, 0, 0);
        translateAnimation.setDuration(100);
        if (selectedTabIndex == 1) {
            focusTabTextView.startAnimation(translateAnimation);
        } else if (selectedTabIndex == 2) {
            shortBreakTabTextView.startAnimation(translateAnimation);
        } else if (selectedTabIndex == 3) {
            longBreakTabTextView.startAnimation(translateAnimation);
        }
        final TextView finalSelectedTextView = selectedTextView; // Store the final reference
        final TextView finalNonSelectedTextView1 = nonSelectedTextView1; // Store the final reference
        final TextView finalNonSelectedTextView2 = nonSelectedTextView2; // Store the final reference
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                finalSelectedTextView.setBackgroundResource(R.drawable.selected_tab_back);
                finalSelectedTextView.setTypeface(null, Typeface.BOLD);
                //finalSelectedTextView.setTextColor(Color.BLACK);

                //finalNonSelectedTextView1.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                finalNonSelectedTextView1.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                finalNonSelectedTextView1.setTypeface(null, Typeface.NORMAL);

                //finalNonSelectedTextView2.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                finalNonSelectedTextView2.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                finalNonSelectedTextView2.setTypeface(null, Typeface.NORMAL);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });*/
        selectedTabIndex = tabIndex;
    }

    private void selectedTab2(int tabIndex) {
        /*pauseTimer();*/
        TextView selectedTextView = focusTabTextView;
        TextView nonSelectedTextView1 = shortBreakTabTextView;
        TextView nonSelectedTextView2 = longBreakTabTextView;

        if (tabIndex == focus) {
            selectedTextView = focusTabTextView;
            nonSelectedTextView1 = shortBreakTabTextView;
            nonSelectedTextView2 = longBreakTabTextView;
            /*timeLeftInMillis = FOCUS_DURATION;*/
        } else if (tabIndex == shortBreak) {
            selectedTextView = shortBreakTabTextView;
            nonSelectedTextView1 = focusTabTextView;
            nonSelectedTextView2 = longBreakTabTextView;
            /*timeLeftInMillis = SHORT_BREAK_DURATION;*/
        } else if (tabIndex == longBreak) {
            selectedTextView = longBreakTabTextView;
            nonSelectedTextView1 = focusTabTextView;
            nonSelectedTextView2 = shortBreakTabTextView;
            /*timeLeftInMillis = LONG_BREAK_DURATION;*/
        }

        // Update the selected tab
        selectedTextView.setBackgroundResource(R.drawable.selected_tab_back);
        selectedTextView.setTypeface(null, Typeface.BOLD);

        // Update the non-selected tabs
        nonSelectedTextView1.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        nonSelectedTextView1.setTypeface(null, Typeface.NORMAL);

        nonSelectedTextView2.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        nonSelectedTextView2.setTypeface(null, Typeface.NORMAL);

        /*float slideTo = (tabIndex - selectedTabIndex) * selectedTextView.getWidth();
        TranslateAnimation translateAnimation = new TranslateAnimation(0, slideTo, 0, 0);
        translateAnimation.setDuration(100);
        if (selectedTabIndex == 1) {
            focusTabTextView.startAnimation(translateAnimation);
        } else if (selectedTabIndex == 2) {
            shortBreakTabTextView.startAnimation(translateAnimation);
        } else if (selectedTabIndex == 3) {
            longBreakTabTextView.startAnimation(translateAnimation);
        }
        final TextView finalSelectedTextView = selectedTextView; // Store the final reference
        final TextView finalNonSelectedTextView1 = nonSelectedTextView1; // Store the final reference
        final TextView finalNonSelectedTextView2 = nonSelectedTextView2; // Store the final reference
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                finalSelectedTextView.setBackgroundResource(R.drawable.selected_tab_back);
                finalSelectedTextView.setTypeface(null, Typeface.BOLD);
                //finalSelectedTextView.setTextColor(Color.BLACK);

                //finalNonSelectedTextView1.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                finalNonSelectedTextView1.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                finalNonSelectedTextView1.setTypeface(null, Typeface.NORMAL);

                //finalNonSelectedTextView2.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                finalNonSelectedTextView2.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                finalNonSelectedTextView2.setTypeface(null, Typeface.NORMAL);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });*/
        selectedTabIndex = tabIndex;
    }

    private void updateCurrentStatusTextView() {
        if(selectedTabIndex == focus) {
            currentStatusTextView.setText("Time to focus!");
        } else if (selectedTabIndex == shortBreak) {
            currentStatusTextView.setText("Time for a short break!");
        } else if (selectedTabIndex == longBreak) {
            currentStatusTextView.setText("Time for a long break!");
        }
    }


    private void enableFullScreenMode() {
        // Hide the status bar and the navigation bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void disableFullScreenMode() {
        // Hide the status bar and the navigation bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}