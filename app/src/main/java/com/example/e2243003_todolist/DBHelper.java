package com.example.e2243003_todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ToDoDBHelper.db";
    public static final String TABLE_TASK = "todo";
    public static final String TABLE_USER = "user";
    private Context context;

//    public DBHelper(Context context) {
//        super(context, DATABASE_NAME, null, 1);
//    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL(
                "CREATE TABLE " + TABLE_USER + "(userid INTEGER PRIMARY KEY AUTOINCREMENT,email TEXT, username TEXT, password TEXT)"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_TASK + "(userid INTEGER, id INTEGER PRIMARY KEY, task TEXT, task_at DATETIME, status INTEGER, FOREIGN KEY(userid) REFERENCES TABLE_USER(userid))"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        onCreate(db);
    }


    //user authentication
    public Boolean insertData(String username, String email,  String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("email", email);
        contentValues.put("password", password);
        long result = db.insert(TABLE_USER, null, contentValues);

        return result != -1;
    }

//    public Boolean checkEmail(String index_no){
//        SQLiteDatabase MyDatabase = this.getWritableDatabase();
//        Cursor cursor = MyDatabase.rawQuery("Select * from " + TABLE_USER + " where email = ?", new String[]{index_no});
//
//        return cursor.getCount() > 0;
//    }

//    public int getuserid(String username)  {
////        String name = "";
//        int userid = 0;
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery("Select * from " + TABLE_USER + " where username = ?", new String[]{username});
//        if (cursor.moveToFirst()) {
//            do {
////                name = cursor.getString(2);
//                userid = cursor.getInt(0);
//            } while (cursor.moveToNext());
//        }
//        cursor.close();
//
//        return userid;
//    }

    public int getuserid(String username) {
        int userid = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery("Select * from " + TABLE_USER + " where username = ?", new String[]{username});
            if (cursor.moveToFirst()) {
                userid = cursor.getInt(0);
            }
            cursor.close();
        } catch (SQLException e) {
            Log.e("DBHelper", "getuserid failed", e);
        }
        return userid;

    }


    public Boolean checkEmailPassword(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_USER + " where username = ? and password = ?", new String[]{username, password});

        return cursor.getCount() > 0;
    }

    //for task
    public boolean insertTask(String task, String task_at) {
        Date date;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("task", task);
        //contentValues.put("userid", getuserid("Test1"));
        contentValues.put("userid", getuserid(User.getCurrentUser().getUsername()));
        contentValues.put("task_at", task_at);
        //contentValues.put("dateStr", getDate(dateStr));
        contentValues.put("status", 0);
        db.insert(TABLE_TASK, null, contentValues);

        //notification part
        if (context != null) {
            // Set up the alarm
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                Intent intent = new Intent(context, AlertReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                // Set the alarm to start at the due time.
                /*Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                // You need to parse task_at into a Calendar object*/

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = sdf.parse(task_at);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    // Set calendar to the end of the day
                    calendar.set(Calendar.HOUR_OF_DAY, 22);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    // setRepeating() lets you specify a precise custom interval--in this case, 20 minutes.
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            1000 * 60 * 20, pendingIntent);

                    SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("AlarmSet", true);
                    editor.apply();

                    Log.d("AlarmSet", "Alarm set for end of day: " + task_at);
                    Toast.makeText(context, "Alarm set " + task_at +" \n"+ calendar.getTime(), Toast.LENGTH_SHORT ).show();

                } catch (ParseException e) {
                    e.printStackTrace();
                    System.out.println("Failed to parse date string: " + task_at);
                    Toast.makeText(context, "Alarm set for end of day is failed: " + task_at, Toast.LENGTH_SHORT).show();

                }

            }
        }


        return true;
    }


    public boolean updateTask(String id, String task, String task_at) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("task", task);
        contentValues.put("task_at", task_at);

        db.update(TABLE_TASK, contentValues, "id = ? ", new String[]{id});
        return true;
    }

    public boolean deleteTask(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASK, "id = ? ", new String[]{id});
        return true;
    }

    public boolean updateTaskStatus(String id, Integer status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("status", status);

        db.update(TABLE_TASK, contentValues, "id = ? ", new String[]{id});
        return true;
    }


    public Cursor getSingleTask(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_TASK + " WHERE id = '" + id + "' order by id desc", null);
        return res;

    }

    public Cursor getTodayTask() {
        SQLiteDatabase db = this.getReadableDatabase();
        int userid = getuserid(User.getCurrentUser().getUsername());
        Cursor res = db.rawQuery("select * from " + TABLE_TASK + " WHERE userid = ? AND date(task_at) = date('now', 'localtime') order by id desc", new String[]{String.valueOf(userid)});        return res;

    }


    public Cursor getTomorrowTask() {
        SQLiteDatabase db = this.getReadableDatabase();
        int userid = getuserid(User.getCurrentUser().getUsername());
        Cursor res = db.rawQuery("select * from " + TABLE_TASK + " WHERE userid = ? AND date(task_at) = date('now','+1 day', 'localtime') order by id desc", new String[]{String.valueOf(userid)});
        return res;

    }


    public Cursor getUpcomingTask() {
        SQLiteDatabase db = this.getReadableDatabase();
        int userid = getuserid(User.getCurrentUser().getUsername());
        Cursor res = db.rawQuery("select * from " + TABLE_TASK + " WHERE userid = ? AND date(task_at) > date('now','+1 day', 'localtime') order by id desc", new String[]{String.valueOf(userid)});
        return res;

    }



    public String getemail(int userid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select email from " + TABLE_USER + " WHERE userid=? ", new String[]{String.valueOf(userid)});
        String email = null;
        if (cursor.moveToFirst()) {
            email = cursor.getString(0);
        }
        cursor.close();
        return email;
    }


}
