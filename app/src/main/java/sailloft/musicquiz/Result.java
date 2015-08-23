package sailloft.musicquiz;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class Result extends ListActivity {
    protected MusicQuizDataSource mDataSource;
    private String KEY_NAME = "Name";
    private  String KEY_SCORE = "Score";
    private ArrayList<HashMap<String, String>> allScores;
    private Button playAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mDataSource = new MusicQuizDataSource(this);
        allScores = new ArrayList<>();
        playAgain = (Button)findViewById(R.id.finishButton);



    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Cursor cursor = mDataSource.selectAllScores();
        updateList(cursor);
        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Result.this, PlaylistSearch.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        mDataSource.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void updateList(Cursor scores){
        scores.moveToFirst();
        while (!scores.isAfterLast()) {

            int i = scores.getColumnIndex(MusicQuizHelper.COLUMN_NAME);

            int z = scores.getColumnIndex(MusicQuizHelper.COLUMN_SCORE);
            String name = scores.getString(i);
            String score = scores.getString(z);


            HashMap<String, String> highScores = new HashMap<String, String>();
            highScores.put(KEY_NAME, name);
            highScores.put(KEY_SCORE, score);
            allScores.add(highScores);

            scores.moveToNext();
        }

        String[] keys = {KEY_NAME, KEY_SCORE};
        ScoreAdapter adapter = new ScoreAdapter(this, allScores, keys);
        Collections.sort(allScores,new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> t0, HashMap<String, String> t1) {
                return t1.get(KEY_SCORE).compareTo(t0.get(KEY_SCORE));
            }
        });



        setListAdapter(adapter);




    }
}
