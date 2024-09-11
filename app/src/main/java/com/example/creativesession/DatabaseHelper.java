package com.example.creativesession;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase database;
    public final static String DATABASE_NAME = "CREATIVE_SESSION";
    public final static String TABLE_SETTINGS = "SETTINGS";
    public final static String TABLE_TASKS = "TASKS";

    // id, focus, short_break, long_break, interval, auto_focus, auto_break, last_task
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FOCUS = "focus";
    private static final String COLUMN_SHORT_BREAK = "short_break";
    private static final String COLUMN_LONG_BREAK = "long_break";
    private static final String COLUMN_INTERVAL = "interval";
    private static final String COLUMN_AUTO_FOCUS = "auto_focus";
    private static final String COLUMN_AUTO_BREAK = "auto_break";
    private static final String COLUMN_LAST_TASK = "last_task";

    // id, title, created_at, is_completed, completed_count, desired_sessions
    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_CREATED_AT = "created_at";
    private static final String COLUMN_TASK_IS_COMPLETED = "is_completed";
    private static final String COLUMN_TASK_COMPLETED_SESSIONS = "completed_sessions";
    private static final String COLUMN_TASK_DESIRED_SESSIONS = "desired_sessions";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the settings table
        String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_SETTINGS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FOCUS + " INTEGER, " +
                COLUMN_SHORT_BREAK + " INTEGER, " +
                COLUMN_LONG_BREAK + " INTEGER, " +
                COLUMN_INTERVAL + " INTEGER, " +
                COLUMN_AUTO_FOCUS + " BOOLEAN, " +
                COLUMN_AUTO_BREAK + " BOOLEAN, " +
                COLUMN_LAST_TASK + " INTEGER);";
        db.execSQL(CREATE_TABLE_QUERY);

        // Create the tasks table
        String CREATE_TASKS_TABLE_QUERY = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK_TITLE + " TEXT, " +
                COLUMN_TASK_CREATED_AT + " TEXT, " +
                COLUMN_TASK_IS_COMPLETED + " BOOLEAN, " +
                COLUMN_TASK_COMPLETED_SESSIONS + " INTEGER, " +
                COLUMN_TASK_DESIRED_SESSIONS + " INTEGER);";
        db.execSQL(CREATE_TASKS_TABLE_QUERY);

        Settings settings = new Settings().defaultSettings();
        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_ID, 1);
        initialValues.put(COLUMN_ID, 1);
        initialValues.put(COLUMN_FOCUS, settings.getFocus());
        initialValues.put(COLUMN_SHORT_BREAK, settings.getShortBreak());
        initialValues.put(COLUMN_LONG_BREAK, settings.getLongBreak());
        initialValues.put(COLUMN_INTERVAL, settings.getInterval());
        initialValues.put(COLUMN_AUTO_FOCUS, settings.isAutoFocus());
        initialValues.put(COLUMN_AUTO_BREAK, settings.isAutoBreak());
        initialValues.put(COLUMN_LAST_TASK, settings.getLastTask());

        db.insert(TABLE_SETTINGS, null, initialValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    public Boolean addNewTask(Task task) {
        database = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(COLUMN_TASK_TITLE, task.getTitle());
        value.put(COLUMN_TASK_CREATED_AT, getCurrentTime());
        value.put(COLUMN_TASK_IS_COMPLETED, false);
        value.put(COLUMN_TASK_COMPLETED_SESSIONS, 0);
        value.put(COLUMN_TASK_DESIRED_SESSIONS, task.getDesiredSessions());

        long result = database.insert(TABLE_TASKS,null, value);
        database.close();
        return result != -1;
    }

    public List<Task> todoList() {
        database = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TASKS;
        Cursor cursor = database.rawQuery(query, null);
        List<Task> tasksList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_TASK_TITLE);
                int createdAtIndex = cursor.getColumnIndex(COLUMN_TASK_CREATED_AT);
                int isCompletedIndex = cursor.getColumnIndex(COLUMN_TASK_IS_COMPLETED);
                int completedSessionsIndex = cursor.getColumnIndex(COLUMN_TASK_COMPLETED_SESSIONS);
                int desiredSessionsIndex = cursor.getColumnIndex(COLUMN_TASK_DESIRED_SESSIONS);
                if (idIndex >= 0
                        && titleIndex >= 0
                        && createdAtIndex >= 0
                        && isCompletedIndex >= 0
                        && completedSessionsIndex >= 0
                        && desiredSessionsIndex >= 0) {
                    int id = cursor.getInt(idIndex);
                    String title = cursor.getString(titleIndex);
                    String createAt = cursor.getString(createdAtIndex);
                    boolean isCompleted = (cursor.getInt(isCompletedIndex) == 1);
                    int completedSessions = cursor.getInt(completedSessionsIndex);
                    int desiredSessions = cursor.getInt(desiredSessionsIndex);

                    Task task = new Task(id, title, createAt, isCompleted, completedSessions, desiredSessions);
                    tasksList.add(task);
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        database.close();
        return tasksList;
    }

    public Task getTaskById(int taskId) {
        database = this.getReadableDatabase();
        Task task = null;

        // Define the columns to retrieve
        String[] columns = {
                COLUMN_ID,
                COLUMN_TASK_TITLE,
                COLUMN_TASK_CREATED_AT,
                COLUMN_TASK_IS_COMPLETED,
                COLUMN_TASK_COMPLETED_SESSIONS,
                COLUMN_TASK_DESIRED_SESSIONS
        };

        // Define the selection criteria
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(taskId) };

        // Perform the query
        Cursor cursor = database.query(
                TABLE_TASKS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        // Process the result
        if (cursor != null && cursor.moveToFirst()) {
            // Ensure each column index is valid before accessing it
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(COLUMN_TASK_TITLE);
            int createdAtIndex = cursor.getColumnIndex(COLUMN_TASK_CREATED_AT);
            int isCompletedIndex = cursor.getColumnIndex(COLUMN_TASK_IS_COMPLETED);
            int completedSessionsIndex = cursor.getColumnIndex(COLUMN_TASK_COMPLETED_SESSIONS);
            int desiredSessionsIndex = cursor.getColumnIndex(COLUMN_TASK_DESIRED_SESSIONS);

            // Check if column indices are valid (i.e., not -1)
            if (titleIndex != -1 && createdAtIndex != -1 && isCompletedIndex != -1 &&
                    completedSessionsIndex != -1 && desiredSessionsIndex != -1) {

                // Extract data from the cursor
                int id = cursor.getInt(idIndex);
                String title = cursor.getString(titleIndex);
                String createdAt = cursor.getString(createdAtIndex);
                boolean isCompleted = cursor.getInt(isCompletedIndex) > 0;
                int completedSessions = cursor.getInt(completedSessionsIndex);
                int desiredSessions = cursor.getInt(desiredSessionsIndex);

                // Create and return the Task object
                task = new Task(id, title, createdAt, isCompleted, completedSessions, desiredSessions);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        database.close();

        return task;
    }


    public boolean updateTask(int taskId, String newTitle, int newDesiredSessions) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_TITLE, newTitle);
        values.put(COLUMN_TASK_DESIRED_SESSIONS, newDesiredSessions);

        int result = database.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
        database.close();
        return result > 0;
    }

    public boolean updateCompletedSessions(int taskId, int newCompletedSessions) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_COMPLETED_SESSIONS, newCompletedSessions);

        int result = database.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});

        database.close();
        return result > 0;
    }

    public boolean updateIsCompleted(int taskId, boolean isCompleted) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_IS_COMPLETED, isCompleted);

        int result = database.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});

        database.close();
        return result > 0;
    }

    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
        return result > 0;
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd - HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    public Settings getSettings() {
        Settings settings = new Settings();
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_SETTINGS, null, COLUMN_ID + " = 1", null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int focusIndex = cursor.getColumnIndex(COLUMN_FOCUS);
            int shortBreakIndex = cursor.getColumnIndex(COLUMN_SHORT_BREAK);
            int longBreakIndex = cursor.getColumnIndex(COLUMN_LONG_BREAK);
            int intervalIndex = cursor.getColumnIndex(COLUMN_INTERVAL);
            int autoFocusIndex = cursor.getColumnIndex(COLUMN_AUTO_FOCUS);
            int autoBreakIndex = cursor.getColumnIndex(COLUMN_AUTO_BREAK);
            int lastTaskIndex = cursor.getColumnIndex(COLUMN_LAST_TASK);

            // Check if all columns exist in the result set
            if (focusIndex != -1
                    && shortBreakIndex != -1
                    && longBreakIndex != -1
                    && intervalIndex != -1
                    && autoFocusIndex != -1
                    && autoBreakIndex != -1
                    && lastTaskIndex != -1) {


                settings.setFocus(cursor.getInt(focusIndex));
                settings.setShortBreak(cursor.getInt(shortBreakIndex));
                settings.setLongBreak(cursor.getInt(longBreakIndex));
                settings.setInterval(cursor.getInt(intervalIndex));
                settings.setAutoFocus(cursor.getInt(autoFocusIndex) > 0);
                settings.setAutoBreak(cursor.getInt(autoBreakIndex) > 0);
                settings.setLastTask(cursor.getInt(lastTaskIndex));

                /*int focusTime = cursor.getInt(focusIndex);
                int shortBreakTime = cursor.getInt(shortBreakIndex);
                int longBreakTime = cursor.getInt(longBreakIndex);
                int intervalCount = cursor.getInt(intervalIndex);
                boolean autoFocus = cursor.getInt(autoFocusIndex) > 0;
                boolean autoBreak = cursor.getInt(autoBreakIndex) > 0;*/

                cursor.close();
                //return new Settings(focusTime, shortBreakTime, longBreakTime, intervalCount, autoFocus, autoBreak);
                return new Settings(settings);
            } else {
                cursor.close();
                // Handle missing columns as needed, or log an error
                return null;
            }
        } else {
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }

        //return new Settings(12, 13, 14, 15, true, false);
    }

    public void updateSettings(Settings settings) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOCUS, settings.getFocus());
        values.put(COLUMN_SHORT_BREAK, settings.getShortBreak());
        values.put(COLUMN_LONG_BREAK, settings.getLongBreak());
        values.put(COLUMN_INTERVAL, settings.getInterval());
        values.put(COLUMN_AUTO_FOCUS, settings.isAutoFocus() ? 1 : 0);
        values.put(COLUMN_AUTO_BREAK, settings.isAutoBreak() ? 1 : 0);

        database.update(TABLE_SETTINGS, values, "id = ?", new String[]{"1"});
    }

    public boolean updateLastTask(int taskId) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LAST_TASK, taskId);

        int result = database.update(TABLE_SETTINGS, values, COLUMN_ID + " = ?", new String[]{"1"});
        database.close();
        return result > 0;
    }
}
