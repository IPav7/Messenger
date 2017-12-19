package com.igorpavinich.messenger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Igor Pavinich on 02.12.2017.
 */

public class SearchAdapter extends BaseAdapter {
    ArrayList<User> users;
    Context mContext;

    public SearchAdapter(Context mContext, ArrayList<User> users) {
        this.mContext = mContext;
        this.users = users;
    }

    public int getCount() {
        return users.size();
    }

    public Object getItem(int arg0) {
        return users.get(arg0);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View arg1, ViewGroup viewGroup){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.user_item, viewGroup, false);
        TextView fullName = row.findViewById(R.id.user_name);
        fullName.setText(users.get(position).getSurname() + " " + users.get(position).getName());
        fullName.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "Roboto-Bold.ttf"));
        ImageView imageView = row.findViewById(R.id.user_img);
        Bitmap img = users.get(position).getPicture();
        if(img!=null)
        imageView.setImageBitmap(users.get(position).getPicture());
        else imageView.setImageResource(R.drawable.ic_mood_black_72dp);
        ImageView online = row.findViewById(R.id.onlineImg);
        if(users.get(position).isOnline())
          online.setVisibility(View.VISIBLE);
        return row;
    }
}
