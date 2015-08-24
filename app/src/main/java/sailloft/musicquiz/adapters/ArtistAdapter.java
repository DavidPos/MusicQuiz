package sailloft.musicquiz.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;
import sailloft.musicquiz.R;

/**
 * Created by David's Laptop on 8/2/2015.
 */
public class ArtistAdapter extends ArrayAdapter<Artist> {
    protected Context mContext;
    protected ArrayList<Artist> mArtists;

    public ArtistAdapter(Context context, ArrayList<Artist> artists) {
        super(context, R.layout.list_item, artists);
        mContext = context;
        mArtists = artists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.artistImage = (ImageView)convertView.findViewById(R.id.artistImageView);
            holder.artistName = (TextView)convertView.findViewById(R.id.artistLabel);
            holder.albumLabel = (TextView)convertView.findViewById(R.id.albumLabel);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

        Artist artist = mArtists.get(position);
        holder.artistName.setText(artist.name);
        List<Image> images = artist.images;

        Image mImage = images.get(0);


        Log.d("Picture", artist.id);
        Picasso.with(mContext)
                .load(mImage.url)
                .resize(150, 150)
                .into(holder.artistImage);
        holder.albumLabel.setVisibility(View.INVISIBLE);


        return convertView;
    }

    public static class ViewHolder{
        ImageView artistImage;
        TextView artistName;
        TextView albumLabel;
    }
}
