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
import java.util.HashMap;

import sailloft.musicquiz.R;

/**
 * Created by davidpos on 8/22/15.
 */
public class ScoreAdapter extends ArrayAdapter<HashMap<String, String>>{

        protected Context mContext;
        protected ArrayList<HashMap<String, String>> mScoreList;
        protected String[] mKeys;


    public ScoreAdapter(Context context, ArrayList<HashMap<String, String>> scores, String[] keys) {
        super(context, R.layout.score_list_item, scores);
        mContext = context;
        mScoreList = scores;
        mKeys = keys;

    }


    @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.score_list_item, null);
            holder = new ViewHolder();
            holder.playlistIcon = (ImageView)convertView.findViewById(R.id.pListIconCard);
            holder.userName = (TextView) convertView.findViewById(R.id.userLabelCard);
            holder.score = (TextView) convertView.findViewById(R.id.scoreTextCard);
            holder.playListName = (TextView)convertView.findViewById(R.id.playlistLabel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();


        }
        HashMap<String, String> text = mScoreList.get(position);


        holder.userName.setText(text.get(mKeys[0]));
        holder.score.setText(text.get(mKeys[1]));
        holder.playListName.setText(text.get(mKeys[3]));
        Picasso.with(mContext)
                .load(text.get(mKeys[2]))
                .resize(150, 150)
                .into(holder.playlistIcon);

        return convertView;
    }


        private static class ViewHolder {

            TextView userName;
            TextView score;
            ImageView playlistIcon;
            TextView playListName;
        }


    }

