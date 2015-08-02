package sailloft.musicquiz;

import android.os.AsyncTask;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by David's Laptop on 7/28/2015.
 */
public class TrackTask extends AsyncTask<String, Void, String> {
    String trackName;

    public String getTrackName(){
        return trackName;
    }

    @Override
    protected String doInBackground(String... params) {

        SpotifyApi twoApi = new SpotifyApi();
        SpotifyService twoSpotifiy = twoApi.getService();
        twoSpotifiy.getPlaylistTracks("1211477835", "4iL2ZX9HMrYs8nQqR4w3Gb", new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                PlaylistTrack first = playlistTrackPager.items.get(1);
                Track firstTrack = first.track;
                if (firstTrack != null) {
                    trackName =firstTrack.name;
                } else {

                    trackName = "";
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        return null;
    }
}
