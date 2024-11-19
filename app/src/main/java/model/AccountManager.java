package model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class AccountManager {
    private Context context;
    private SQLiteDatabase db;
    private AccountHelper helper;

    public AccountManager(Context context) {
        this.context = context;
    }

    public AccountManager open() {
        helper = new AccountHelper(context);
        db = helper.getWritableDatabase();
        return this;
    }

    public void close() {
        helper.close();
    }

    public void insertAccount(Account acc) {
        ContentValues values = new ContentValues();
        values.put(helper.User, acc.getUser());
        values.put(helper.Pass, acc.getPass());

        db.insert(helper.TBName, null, values);
    }


}
