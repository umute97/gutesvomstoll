package com.reich.gutesvomstoll.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.reich.gutesvomstoll.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoundDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "sounds.db";
    private static final int DB_VERSION = 1;

    private static final String DB_CREATE = "create table sounds(id integer primary key, soundName text not null, isFav boolean not null default 0)";
    private final String TAG = "com.reich.gutesvomstoll";

    private Field[] mRawSounds = R.raw.class.getFields();

    public SoundDBHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase favDB) {

        favDB.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase favDB, int i, int i1) {

        favDB.execSQL("DROP TABLE IF EXISTS sounds");
        onCreate(favDB);
    }

    public void populateDB()  {

        List<Sound> soundList = getSoundsFromRaw();

        for(Sound sound: soundList)  {

            addToSoundDB(sound);
        }

    }

    private void addToSoundDB(Sound sound)  {

        SQLiteDatabase db = this.getWritableDatabase();

        if(!soundExists(db, sound))  {

            try  {

                ContentValues cv = new ContentValues();

                cv.put("id", sound.getID());
                cv.put("soundName", sound.getName());

                db.insert("sounds", null, cv);

            } catch (Exception ex)  {

                Log.e(TAG, "[DB] Failed to insert sound: " + ex.getMessage());

            } finally  {

                db.close();
            }
        }
    }

    private int getRawFileIdentifier(Field rawSound)  {

        int identifier = -1;

        try {

            identifier = rawSound.getInt(rawSound);

        } catch (IllegalAccessException e) {

            Log.e(TAG, e.getMessage());

        }

        return identifier;

    }

    private String formatRawToName(Field rawSound)  {

        String soundName = null;

        soundName = rawSound.getName().toUpperCase().replace("_", " ");

        return soundName;
    }

    private String convertNameToRaw(String soundName)  {

        return soundName.toLowerCase().replace(" ", "_");
    }

    private boolean soundExists(SQLiteDatabase db, Sound sound)  {

        int count = -1;
        Cursor cursor = null;

        try {

            String query = "SELECT * FROM sounds WHERE id = " + sound.getID();
            cursor = db.rawQuery(query, null);

            // If the entry with the given sound id exists get the rows _id as count value
            if (cursor.moveToFirst())  {

                count = cursor.getInt(0);
            }

            return (count > 0);

        } finally {

            // close the cursor after the verification and if it is filled with something
            if (cursor != null)  {

                cursor.close();
            }
        }

    }

    private List<Sound> getSoundsFromRaw()  {

        List<Sound> soundList = new ArrayList<Sound>();

        for(Field rawSound: mRawSounds)  {

            soundList.add(new Sound(getRawFileIdentifier(rawSound), formatRawToName(rawSound), false));
        }

        return soundList;
    }

    public List<Sound> getSoundsFromDB()  {

        SQLiteDatabase db = getReadableDatabase();
        List<Sound> soundList = new ArrayList<Sound>();

        Cursor c = db.rawQuery("select * from sounds order by soundName", null);

        c.moveToFirst();
        while(!c.isAfterLast())  {

            soundList.add(new Sound(c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("soundName")),
                    c.getInt(c.getColumnIndex("isFav")) > 0)
            );

            c.moveToNext();
        }

        return soundList;
    }

    public List<Sound> getFavesFromDB()  {

        SQLiteDatabase db = getReadableDatabase();
        List<Sound> soundList = new ArrayList<Sound>();

        Cursor c = db.rawQuery("select * from sounds where isFav = 1 order by soundName", null);

        c.moveToFirst();
        while(!c.isAfterLast())  {

            soundList.add(new Sound(c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("soundName")),
                    c.getInt(c.getColumnIndex("isFav")) > 0)
            );

            c.moveToNext();
        }

        return soundList;
    }

    public void appUpdate(){

        try {

            SQLiteDatabase database = this.getWritableDatabase();

            database.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
            database.execSQL(DB_CREATE);

            database.close();

        } catch (Exception e) {

            Log.e(TAG, "Failed to update the main table on app update: " + e.getMessage());
        }
    }

    public void setFave(Sound sound, boolean isFaved)  {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("isFav", isFaved);

        db.update("sounds",cv,"id = ?", new String[] {Integer.toString(sound.getID())});
    }

}
