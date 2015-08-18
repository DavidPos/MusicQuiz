package sailloft.musicquiz;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


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
    private Button okButton;
    private SpotifyService spotify;
    private int wrongPos = 0;
    private List<PlaylistTrack> listOfTracks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        points = 0;
        setContentView(R.layout.activity_main);
        countDown = (TextView) findViewById(R.id.countdownTimer);
        pointsTotal = (TextView) findViewById(R.id.pointsLabel);
        okButton = (Button)findViewById(R.id.okButton);
        okButton.setVisibility(View.INVISIBLE);

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        mQuestionsAdapter = new ArtistAdapter(MainActivity.this, mArtists);

        final ListView listView = getListView();
        listView.setAdapter(mQuestionsAdapter);
        pointsTotal.setText(points + "");
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArtists.clear();

                countDown.setText("00");
                countDown.setTextColor(Color.BLACK);
                ListView v = getListView();
                listView.setEnabled(true);
                v.getChildAt(indexOfCorrect).setBackgroundColor(Color.WHITE);
                v.getChildAt(wrongPos).setBackgroundColor(Color.WHITE);
                int random = (int) (Math.random() * listOfTracks.size());
                PlaylistTrack nextTrack = listOfTracks.get(random);


                getArtist(spotify, nextTrack.track);

                okButton.setVisibility(View.INVISIBLE);
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

                Log.d("Token expires in: ", "" + response.getExpiresIn() + response.getCode());


                spotify = api.getService();


                spotify.getPlaylistTracks("1211477835", "4iL2ZX9HMrYs8nQqR4w3Gb", new Callback<Pager<PlaylistTrack>>() {
                    @Override
                    public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                        playTracks = playlistTrackPager;


                        playlistTrack = playlistTrackPager.items.get(5);


                        listOfTracks = playTracks.items;
                        for (int i = 0; i <= listOfTracks.size(); i++){
                            if (listOfTracks.get(i).track.preview_url == null){
                                listOfTracks.remove(i);
                            }
                        }
                        int rnd = (int) (Math.random() * listOfTracks.size());
                        Log.d("Length of Playlist", listOfTracks.size() + "" );


                        Track track = listOfTracks.get(rnd).track;

                        getArtist(spotify, track);



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
        super.onListItemClick(l, v, position, id);
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
            countDown.setText("00");
            ListView l = getListView();
            View correct = l.getChildAt(indexOfCorrect);
            correct.setBackgroundColor(Color.GREEN);
            l.setEnabled(false);
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();


        }

    }

    private void getArtist(final SpotifyService spotify, Track track) {

        final Track mTrack = track;

        Log.d("Artist", mTrack.artists.get(0).name + "" + mTrack.preview_url);





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
                                Log.d("Response", response.getReason() + " " + response.getUrl());

                                Log.d("Length of Related", related.size() + "");
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
                                long time = 10000;

                                mp.start();
                                song = new QuizCountdown(time, 1000);

                                if (mp.isPlaying()) {

                                    song.start();
                                    Log.d("Media Player", "Media player is playing");
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
