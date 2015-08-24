package sailloft.musicquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by davidpos on 8/22/15.
 */
public class MusicQuizDataSource {

    private SQLiteDatabase mDatabase;
    private MusicQuizHelper mMusicQuizHelper;
    private Context mContext;

    public MusicQuizDataSource(Context context){
        mContext = context;
        mMusicQuizHelper = new MusicQuizHelper(mContext);

    }
    //open
    public void open() throws SQLException {
        mDatabase = mMusicQuizHelper.getWritableDatabase();
    }
    //close
    public void close(){
        mDatabase.close();
    }
    //insert

    public void insertScore(ScoreData scoreData) {
        mDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();

            values.put(MusicQuizHelper.COLUMN_NAME, scoreData.getUserName());
            values.put(MusicQuizHelper.COLUMN_SCORE, scoreData.getScore());
            values.put(MusicQuizHelper.COLUMN_PLAYLIST_ID, scoreData.getPlaylistId());
            values.put(MusicQuizHelper.COLUMN_PLAYLIST_NAME, scoreData.getPlaylistName());
            values.put(MusicQuizHelper.COLUMN_PLAYLIST_ICON, scoreData.getPlayListIcon());


            mDatabase.insert(MusicQuizHelper.TABLE_SCORES, null, values);
            mDatabase.setTransactionSuccessful();

        } finally {
            mDatabase.endTransaction();
        }
    }

    //select
    public Cursor selectAllScores() {
        Cursor cursor = mDatabase.query(
                MusicQuizHelper.TABLE_SCORES,//table
                new String[]{MusicQuizHelper.COLUMN_ID, MusicQuizHelper.COLUMN_NAME,
                        MusicQuizHelper.COLUMN_SCORE, MusicQuizHelper.COLUMN_PLAYLIST_ID, MusicQuizHelper.COLUMN_PLAYLIST_NAME,
                        MusicQuizHelper.COLUMN_PLAYLIST_ICON},//column names
                null,//where clause
                null,//where params
                null,//groupby
                null,//having
                null//orderby
        );
        return cursor;
    }

    public Cursor selectScoresForPlaylist(String playlist) {
        Cursor cursor = mDatabase.query(
                MusicQuizHelper.TABLE_SCORES,//table
                new String[]{MusicQuizHelper.COLUMN_ID, MusicQuizHelper.COLUMN_NAME,
                        MusicQuizHelper.COLUMN_SCORE, MusicQuizHelper.COLUMN_PLAYLIST_ID,
                        MusicQuizHelper.COLUMN_PLAYLIST_NAME, MusicQuizHelper.COLUMN_PLAYLIST_ICON},//column names
                MusicQuizHelper.COLUMN_PLAYLIST_ID + " = ?",//where clause
                new String[]{playlist},//where params
                null,//groupby
                null,//having
                null//orderby
        );
        return cursor;
    }

    public Cursor selectScoresForUser(String user) {
        Cursor cursor = mDatabase.query(
                MusicQuizHelper.TABLE_SCORES,//table
                new String[]{MusicQuizHelper.COLUMN_ID, MusicQuizHelper.COLUMN_NAME,
                        MusicQuizHelper.COLUMN_SCORE, MusicQuizHelper.COLUMN_PLAYLIST_ID,
                        MusicQuizHelper.COLUMN_PLAYLIST_NAME, MusicQuizHelper.COLUMN_PLAYLIST_ICON},//column names
                MusicQuizHelper.COLUMN_NAME + " = ?",//where clause
                new String[]{user},//where params
                null,//groupby
                null,//having
                null//orderby
        );
        return cursor;
    }


    }
