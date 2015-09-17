package sailloft.musicquiz.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import sailloft.musicquiz.R;
import sailloft.musicquiz.adapters.PlaylistAdapter;

public class PlaylistSearch extends AppCompatActivity {
    private String KEY_PLAYLIST_NAME = "name";
    private ListView listview;
    private ArrayList<PlaylistSimple> playList = new ArrayList<>();
    private TextView emptyString;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_search);
        final PlaylistAdapter adapter = new PlaylistAdapter(PlaylistSearch.this, playList);
        listview = (ListView)findViewById(R.id.listView);
        emptyString = (TextView)findViewById(R.id.empty);
        listview.setEmptyView(emptyString);



        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            emptyString.setVisibility(View.INVISIBLE);
            String query = intent.getStringExtra(SearchManager.QUERY);
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            spotify.searchPlaylists(query, new Callback<PlaylistsPager>() {
                @Override
                public void success(PlaylistsPager playlistsPager, Response response) {
                    Iterator<PlaylistSimple> iterator = playlistsPager.playlists.items.iterator();
                    while (iterator.hasNext()) {
                        playList.add(iterator.next());
                    }

                    listview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d("playlist selected is", playList.get(position).id + playList.get(position).name);
                Intent intent1 = new Intent(PlaylistSearch.this, MainActivity.class);
                intent1.putExtra("playlistId", playList.get(position).id);
                intent1.putExtra("user", playList.get(position).owner.id);
                intent1.putExtra("playlistName", playList.get(position).name);
                intent1.putExtra("playlistIcon", playList.get(position).images.get(0).url);
                startActivity(intent1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playlist_search, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
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
}
