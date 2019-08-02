package com.reich.gutesvomstoll.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SoundDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "favs.db";
    private static final int DB_VERSION = 1;

    private static final String DB_CREATE = "create table favs(_id integer primary key autoincrement, soundName text not null)";

    public SoundDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase favDB) {

        favDB.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase favDB, int i, int i1) {

        favDB.execSQL("DROP TABLE IF EXISTS book");
        onCreate(favDB);
    }

    public boolean checkFave(String soundName)  {

        boolean isFave = false;

        //TODO: Check database if soundName is included.
        //      Might consider a different schema, where EVERY sound is in a database named 'sounds'
        //      with a boolean field indicating that a particular sound is faved.

        return isFave;
    }
}
