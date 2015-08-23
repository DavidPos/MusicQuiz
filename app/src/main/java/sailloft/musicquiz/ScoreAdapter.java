package sailloft.musicquiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

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

            holder.userName = (TextView) convertView.findViewById(R.id.userNameLabel);
            holder.score = (TextView) convertView.findViewById(R.id.scoreLabel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();


        }
        HashMap<String, String> text = mScoreList.get(position);

        holder.userName.setText(text.get(mKeys[0]));
        holder.score.setText(text.get(mKeys[1]));

        return convertView;
    }


        private static class ViewHolder {

            TextView userName;
            TextView score;
        }


    }

