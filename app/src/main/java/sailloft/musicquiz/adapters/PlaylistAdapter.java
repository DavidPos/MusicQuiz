package sailloft.musicquiz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;
import sailloft.musicquiz.R;

/**
 * Created by davidpos on 8/19/15.
 */
public class PlaylistAdapter extends ArrayAdapter<PlaylistSimple> {
    protected Context mContext;
    protected ArrayList<PlaylistSimple> mPlayList;

    public PlaylistAdapter(Context context, ArrayList<PlaylistSimple> playlist) {
        super(context, R.layout.playlist_item, playlist);
        mContext = context;
        mPlayList = playlist;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.playlist_item, null);
            holder = new ViewHolder();
            holder.playlistIcon = (ImageView) convertView.findViewById(R.id.playlistIcon);
            holder.playlistName = (TextView) convertView.findViewById(R.id.playlistNameLabel);
            holder.sizePlaylist = (TextView) convertView.findViewById(R.id.sizeLabel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();


        }
        holder.playlistName.setText(mPlayList.get(position).name);
        holder.sizePlaylist.setText("Tracks in Playlist: " + mPlayList.get(position).tracks.total + "");

        Picasso.with(mContext)
                .load(mPlayList.get(position).images.get(0).url)
                .resize(150, 150)
                .into(holder.playlistIcon);
        return convertView;
    }


    private static class ViewHolder {
        ImageView playlistIcon;
        TextView playlistName;
        TextView sizePlaylist;
    }


}