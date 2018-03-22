package com.palash.sampleapp.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.palash.sampleapp.utilities.Constants;

public class DatabaseContract {

    public static final String DATABASE_NAME = "InamdarFeedbackDB.db";

    public static final int DATABASE_VERSION = 1;

    public class Sync implements BaseColumns {

        public static final String TABLE_NAME = "T_Sync";

        public static final String DATE_TIME = "DateTime";
        public static final String CURRENT_SYNC = "Current_Sync";
        public static final String IS_SYNCING = "Is_Syncing";

        private static final String CREATE_TABLE = " CREATE TABLE IF NOT EXISTS "
                + Sync.TABLE_NAME
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Sync.DATE_TIME
                + " TEXT NULL, "
                + Sync.CURRENT_SYNC
                + " TEXT NULL, "
                + Sync.IS_SYNCING
                + " TEXT NULL)";
    }

    public static final class Login implements BaseColumns {

        private Login() {
        }

        public static final String TABLE_NAME = "T_Login";

        public static final String COLUMN_NAME_USER_ID = "UserId";
        public static final String COLUMN_NAME_LOGIN_NAME = "LoginName";
        public static final String COLUMN_NAME_PASSWORD = "Password";
        public static final String COLUMN_NAME_LOGIN_STATUS = "LoginStatus";
        public static final String COLUMN_NAME_REMEMBER_ME = "RememberMe";

        static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + "("
                + Login._ID
                + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,"
                + COLUMN_NAME_USER_ID
                + " TEXT,"
                + COLUMN_NAME_LOGIN_NAME
                + " TEXT,"
                + COLUMN_NAME_PASSWORD
                + " TEXT,"
                + COLUMN_NAME_LOGIN_STATUS
                + " TEXT,"
                + COLUMN_NAME_REMEMBER_ME
                + " TEXT"
                + ")";
    }

    private final Context context;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseContract(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(this.context);
    }

    public SQLiteDatabase open() throws SQLException {
        sqLiteDatabase = this.databaseHelper.getWritableDatabase();
        return sqLiteDatabase;
    }

    public void close() {
        if (sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                Log.d(Constants.TAG, "Creating table 1 Login: " + Login.CREATE_TABLE);
                db.execSQL(Login.CREATE_TABLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
