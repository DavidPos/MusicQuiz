package sailloft.musicquiz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by davidpos on 8/22/15.
 */
public class MusicQuizHelper extends SQLiteOpenHelper {
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_SCORE = "SCORE";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_PLAYLIST_ID = "PLAYLIST_ID";
    public static final String COLUMN_PLAYLIST_NAME = "PLAYLIST_NAME";
    public static final String TABLE_SCORES = "SCORES";

    private static final String DB_NAME = "musicQuiz.db";
    private static final int DB_VERSION = 1;

    private static final String CREATE_TABLE_SCORES =
            "CREATE TABLE " + TABLE_SCORES + " ("+
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_PLAYLIST_ID + " TEXT, " +
                    COLUMN_PLAYLIST_NAME + " TEXT, " +
                    COLUMN_SCORE + " TEXT)";

    public MusicQuizHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SCORES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
