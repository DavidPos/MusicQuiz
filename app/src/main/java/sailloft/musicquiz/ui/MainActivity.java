package sailloft.musicquiz.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.newrelic.agent.android.NewRelic;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import sailloft.musicquiz.R;
import sailloft.musicquiz.adapters.ArtistAdapter;
import sailloft.musicquiz.db.MusicQuizDataSource;
import sailloft.musicquiz.model.ScoreData;


public class MainActivity extends ListActivity {
    ArrayAdapter<Artist> mQuestionsAdapter;
    String CLIENT_ID = "3bef8533d4ed4d3cb000ccb57efc2a6a";
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "festevo://callback";
    private int points;
    private CountDownTimer song;
    PlaylistTrack playlistTrack;
    private Artist correctArtist;
    private ArrayList<Artist> mArtists = new ArrayList<>();
    private TextView countDown;
    private MediaPlayer mPlayer;
    private TextView pointsTotal;
    private int timeRemaining;
    private int indexOfCorrect;
    private Pager<PlaylistTrack> playTracks;
    private FloatingActionButton okButton;
    private SpotifyService spotify;
    private int wrongPos = 0;
    private List<PlaylistTrack> listOfTracks;
    private List<PlaylistTrack> copyOfList;
    private TextView level;
    private String user;
    private String playlistId;
    private String userName;
    protected MusicQuizDataSource mDataSource;
    private String playlistName;
    private String playlistIconUrl;
    private boolean isClicked;
    private Track track;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        points = 0;
        isClicked = false;
        setContentView(R.layout.activity_main);
        mDataSource = new MusicQuizDataSource(MainActivity.this);
        countDown = (TextView) findViewById(R.id.countdownTimer);
        pointsTotal = (TextView) findViewById(R.id.pointsLabel);
        okButton = (FloatingActionButton)findViewById(R.id.fabNext);

        okButton.setVisibility(View.INVISIBLE);
        level = (TextView)findViewById(R.id.levelLabel);
        level.setText("Remaining: --");
        NewRelic.withApplicationToken(

                "AA6b90f7803532086a3142855dff603499233bb155"
        ).start(this.getApplication());


        final Intent intent = getIntent();
        playlistId = intent.getStringExtra("playlistId");
        user = intent.getStringExtra("user");
        playlistName = intent.getStringExtra("playlistName");
        playlistIconUrl = intent.getStringExtra("playlistIcon");


        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        final AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        mArtists.clear();
        mQuestionsAdapter = new ArtistAdapter(MainActivity.this, mArtists);

        final ListView listView = getListView();
        listView.setAdapter(mQuestionsAdapter);
        pointsTotal.setText(points + "");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArtists.clear();
                if(listOfTracks.size() == 0) {
                    countDown.setText("--");
                    countDown.setTextColor(Color.WHITE);
                    ListView v = getListView();
                    listView.setEnabled(true);
                    v.getChildAt(indexOfCorrect).setBackgroundColor(Color.WHITE);
                    v.getChildAt(wrongPos).setBackgroundColor(Color.WHITE);
                    ScoreData score = new ScoreData();
                    score.setScore(points+"");
                    score.setPlaylistId(playlistId);
                    score.setUserName(userName);
                    score.setPlaylistName(playlistName);
                    score.setPlayListIcon(playlistIconUrl);
                    mDataSource.insertScore(score);

                    Intent result = new Intent(MainActivity.this, Result.class);


                    startActivity(result);


                }
                else {
                    countDown.setText("--");
                    countDown.setTextColor(Color.WHITE);
                    ListView v = getListView();
                    listView.setEnabled(true);
                    v.getChildAt(indexOfCorrect).setBackgroundColor(Color.WHITE);
                    v.getChildAt(wrongPos).setBackgroundColor(Color.WHITE);
                    int random = (int) (Math.random() * listOfTracks.size());
                    PlaylistTrack nextTrack = listOfTracks.get(random);
                    listOfTracks.remove(random);
                    level.setText("Remaining: " + listOfTracks.size());

                    track = nextTrack.track;
                    getArtist(spotify, track);

                    okButton.setVisibility(View.INVISIBLE);
                    isClicked = false;

                }

            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {


                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(response.getAccessToken());





                spotify = api.getService();

                spotify.getMe(new Callback<UserPrivate>() {
                    @Override
                    public void success(UserPrivate userPrivate, Response response) {
                        userName = userPrivate.id;
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });


                spotify.getPlaylistTracks(user, playlistId, new Callback<Pager<PlaylistTrack>>() {
                    @Override
                    public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                        playTracks = playlistTrackPager;


                        playlistTrack = playlistTrackPager.items.get(5);


                        listOfTracks = playTracks.items;

                        Collections.shuffle(listOfTracks);



                        Iterator<PlaylistTrack> iterator = listOfTracks.iterator();
                        while (iterator.hasNext()){

                            if (iterator.next().track.preview_url == null){
                                iterator.remove();

                            }
                        }
                        copyOfList = listOfTracks.subList(0, 15);
                        listOfTracks = copyOfList;

                        level.setText("Remaining: " + listOfTracks.size());

                        int rnd = (int) (Math.random() * listOfTracks.size());



                        track = listOfTracks.get(rnd).track;
                        listOfTracks.remove(rnd);
                        getArtist(spotify, track);
                        Log.i("first time artist: ", track.name );



                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("Track failure", error.toString());

                    }

                });


            }

        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        try {
            mDataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (track != null) {
            Log.i("onResume artist:  ", track.name);
            getArtist(spotify, track);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDataSource.close();
        if (mPlayer != null){
            if(mPlayer.isPlaying()){
                mPlayer.stop();
                mPlayer.release();
            }
            song.cancel();
            mArtists.clear();
            countDown.setText("--");

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //on item click check to see if answer is correct. Stop playing song .
        super.onListItemClick(l, v, position, id);
        if (!isClicked) {
            isClicked = true;
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
            song.cancel();
            okButton.setVisibility(View.VISIBLE);


            Artist selectedArtist = mArtists.get(position);
            if (selectedArtist == correctArtist) {
                countDown.setTextColor(Color.GREEN);
                countDown.setText("Correct!!!");
                v.setBackgroundColor(Color.GREEN);

                points = points + timeRemaining;
                pointsTotal.setText(points + "");


            } else {
                countDown.setTextColor(Color.RED);
                countDown.setText("Wrong");
                wrongPos = position;
                View correctAnswer = l.getChildAt(indexOfCorrect);

                correctAnswer.setBackgroundColor(Color.GREEN);
                v.setBackgroundColor(Color.RED);


            }
        }
    }

    public class QuizCountdown extends CountDownTimer {
        public QuizCountdown(long lengthInMillis, long interval) {
            super(lengthInMillis, interval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            String secondsLeft = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));
            countDown.setText(secondsLeft);
            timeRemaining = (int) millisUntilFinished;
        }

        @Override
        public void onFinish() {
            //Timer finishes reveal correct answer and stop playing song
            countDown.setText("--");
            ListView l = getListView();
            View correct = l.getChildAt(indexOfCorrect);
            correct.setBackgroundColor(Color.GREEN);
            l.setEnabled(false);
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
            okButton.setVisibility(View.VISIBLE);


        }



    }


    private void getArtist(final SpotifyService spotify, Track track) {

        final Track mTrack = track;







                ArtistSimple artist = track.artists.get(0);
                spotify.getArtist(artist.id, new Callback<Artist>() {
                    @Override
                    public void success(Artist artist, Response response) {
                        mArtists.add(artist);
                        correctArtist = artist;

                        spotify.getRelatedArtists(artist.id, new Callback<Artists>() {

                            @Override
                            public void success(Artists artists, Response response) {
                                List<Artist> related = artists.artists;
                                Collections.shuffle(related);

                                for (int i = 0; i <= 2; i++) {
                                    mArtists.add(related.get(i));


                                }

                                Collections.shuffle(mArtists);
                                indexOfCorrect = mArtists.indexOf(correctArtist);
                                mQuestionsAdapter.notifyDataSetChanged();



                            }


                            @Override
                            public void failure(RetrofitError error) {
                                Log.d("Artist failure", error.toString());
                            }
                        });

                        mPlayer = new MediaPlayer();
                        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        try {
                            mPlayer.setDataSource(mTrack.preview_url);
                        } catch (IOException e) {
                            Log.d("MEDIA_PLAYER_IO_EXP", e + "");
                        }
                        mPlayer.prepareAsync();
                        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                            @Override
                            public boolean onError(MediaPlayer mp, int what, int extra) {
                                Log.e("Media Player Error", what + ": " + extra);
                                mp.reset();
                                mp.release();
                                return false;
                            }
                        });
                        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {

                                long time = 15000;

                                mp.start();
                                song = new QuizCountdown(time, 1000);

                                if (mp.isPlaying()) {

                                    song.start();

                                } else {
                                    Log.d("Error", "Media Player is not playing");
                                }

                            }
                        });

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });




    }

}
