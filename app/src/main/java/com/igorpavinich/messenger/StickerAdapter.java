package com.igorpavinich.messenger;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Igor Pavinich on 20.12.2017.
 */

class StickerAdapter extends ArrayAdapter {

    private ArrayList<Integer> items;
    private int layoutResourceId;
    private Context context;

    public StickerAdapter(ArrayList<Integer> items, int layoutResourceId, Context context){
        super(context, layoutResourceId, items);
        this.items = items;
        this.layoutResourceId = layoutResourceId;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.image = row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        holder.image.setImageResource(items.get(position));
        return row;
    }

    static class ViewHolder {
        ImageView image;
    }
}
