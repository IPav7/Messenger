package com.igorpavinich.messenger;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Igor Pavinich on 04.12.2017.
 */

public class MessageAdapter extends BaseAdapter {
    ArrayList<Message> messages;
    Context mContext;

    public MessageAdapter(Context mContext, ArrayList<Message> messages) {
        this.mContext = mContext;
        this.messages = messages;
    }

    public int getCount() {
        return messages.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View arg1, ViewGroup viewGroup){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.message_item, viewGroup, false);
        TextView text = row.findViewById(R.id.mesText);
        Date data = new Date(messages.get(position).getDate());
        TextView date = row.findViewById(R.id.mesTime);
        if(messages.get(position).getSender().equals(CookiesWork.cookie)){
            text.setGravity(Gravity.RIGHT);
            date.setGravity(Gravity.RIGHT);
        }
        text.setText(messages.get(position).getMessage());
        String hours = String.valueOf(data.getHours());
        String minutes = String.valueOf(data.getMinutes());
        if(data.getHours()<10) hours = "0" + hours;
        if(data.getMinutes()<10) minutes = "0" + minutes;
        date.setText(hours + ":" + minutes);
       // date.setText("22:22");
        return row;
    }
}
