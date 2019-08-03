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

    public void populateSounds(Context context)  {

        List<String> soundNames = formatSoundNames(mRawSounds);
        Resources res = context.getResources();

        for(String soundName: soundNames)  {

            int id = res.getIdentifier(soundName, "raw", context.getPackageName());
            addToSoundDB(id, soundName);
        }

    }

    private void addToSoundDB(int id, String soundName)  {

        SQLiteDatabase db = this.getWritableDatabase();

        if(!soundIdExists(db, id))  {

            try  {

                ContentValues cv = new ContentValues();

                cv.put("id", id);
                cv.put("soundName", soundName);

                db.insert("sounds", null, cv);

            } catch (Exception ex)  {

                Log.e(TAG, "[DB] Failed to insert sound: " + ex.getMessage());

            } finally  {

                db.close();
            }
        }
    }

    private List<String> formatSoundNames(Field[] rawSounds)  {

        String[] soundNames = new String[rawSounds.length];

        for (int i = 0; i < rawSounds.length; i++)  {

            soundNames[i] = rawSounds[i].getName().toUpperCase().replace("_", " ");
        }

        return Arrays.asList(soundNames);
    }

    private String convertToRawName(String soundName)  {

        return soundName.toLowerCase().replace(" ", "_");
    }

    private boolean soundIdExists(SQLiteDatabase db, int id)  {

        int count = -1;
        Cursor cursor = null;

        try {

            String query = "SELECT * FROM sounds WHERE id = " + id;
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

    public boolean checkFave(String soundName)  {

        boolean isFave = false;

        //TODO: Implement.

        return isFave;
    }

    public boolean checkFave(int soundId)  {

        boolean isFave = false;

        //TODO: Implement.

        return isFave;
    }

    public int findSound(String soundName)  {

        //TODO: Implement.

        return -1;
    }

    public String findSound(int soundId)  {

        //TODO: Implement.

        return "dickbutt";
    }

    public List<String> getSoundList()  {

        List<String> soundList = Arrays.asList("dickbutt");

        //TODO: Implement.

        return soundList;
    }
}
