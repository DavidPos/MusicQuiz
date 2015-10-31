package sailloft.musicquiz.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.hasFocus();
        final CursorAdapter suggestionAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[]{android.R.id.text1},
                0);
        final List<String> suggestions = new ArrayList<>();
        searchView.requestFocus();

        searchView.setSuggestionsAdapter(suggestionAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                searchView.setQuery(suggestions.get(position), false);
                searchView.clearFocus();


                return true;
            }
        });

       /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           @Override
           public boolean onQueryTextSubmit(String query) {

           }

           @Override
           public boolean onQueryTextChange(String newText) {
                suggestions.clear();
               SpotifyApi api = new SpotifyApi();
               SpotifyService spotify = api.getService();
               spotify.searchPlaylists(newText, new Callback<PlaylistsPager>() {
                   @Override
                   public void success(PlaylistsPager playlistsPager, Response response) {
                       Log.i("Playlist : Items :", playlistsPager.playlists.items +"");
                       Iterator<PlaylistSimple> iterator = playlistsPager.playlists.items.iterator();
                       while (iterator.hasNext()) {
                           suggestions.add(iterator.next().name);


                       }
                       String[] columns = {
                               BaseColumns._ID,
                               SearchManager.SUGGEST_COLUMN_TEXT_1,
                               SearchManager.SUGGEST_COLUMN_INTENT_DATA
                       };

                       MatrixCursor cursor = new MatrixCursor(columns);

                       for (int i = 0; i < suggestions.size(); i++) {
                           String[] tmp = {Integer.toString(i), suggestions.get(i), suggestions.get(i)};
                           cursor.addRow(tmp);
                       }
                       suggestionAdapter.swapCursor(cursor);





                   }

                   @Override
                   public void failure(RetrofitError error) {
                       Toast.makeText(PlaylistSearch.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                   }
               });
               return false;
           }
       });*/
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
