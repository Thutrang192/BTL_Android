package model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AccountHelper extends SQLiteOpenHelper {

    public static final String DBname = "account.db";
    public static final int DBVersion = 1;
    public static final String TBName = "tblAccount";
    public  static final String ID = "id";
    public static final String User = "user";
    public static final String Pass = "pass";

    public AccountHelper(@Nullable Context context) {
        super(context, DBname, null, DBVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TBName + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                User + " TEXT, " +
                Pass + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBName);
        onCreate(db);
    }
}
