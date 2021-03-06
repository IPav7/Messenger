package com.igorpavinich.messenger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Igor Pavinich on 04.12.2017.
 */

public class MessageAdapter extends BaseAdapter {
    private MediaPlayer mediaPlayer;
    ArrayList<Message> messages;
    Context mContext;
    String fileName;
    private ArrayList<Integer> stickers;

    public MessageAdapter(Context mContext, ArrayList<Message> messages) {
        this.mContext = mContext;
        this.messages = messages;
        stickers = new ArrayList<Integer>(){{
            add(R.drawable.sticker1);
            add(R.drawable.sticker2);
            add(R.drawable.sticker3);
            add(R.drawable.sticker4);
            add(R.drawable.sticker5);
            add(R.drawable.sticker6);
            add(R.drawable.sticker7);
            add(R.drawable.sticker8);
            add(R.drawable.sticker9);
            add(R.drawable.sticker10);
            add(R.drawable.sticker11);
            add(R.drawable.sticker12);
            add(R.drawable.sticker13);
            add(R.drawable.sticker14);
            add(R.drawable.sticker15);
            add(R.drawable.sticker16);
            add(R.drawable.sticker17);
            add(R.drawable.sticker18);
            add(R.drawable.sticker19);
            add(R.drawable.sticker20);
            add(R.drawable.sticker21);
            add(R.drawable.sticker22);
            add(R.drawable.sticker23);
            add(R.drawable.sticker24);
            add(R.drawable.sticker25);
            add(R.drawable.sticker26);
            add(R.drawable.sticker27);
            add(R.drawable.sticker28);
        }};
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

    public View getView(final int position, View arg1, ViewGroup viewGroup){
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.message_item, viewGroup, false);
        TextView text = row.findViewById(R.id.mesText);
        Date data = new Date(messages.get(position).getDate());
        TextView date = row.findViewById(R.id.mesTime);
        ImageView img = row.findViewById(R.id.playSound);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSound(position);
            }
        });
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams layoutParams;
        if(messages.get(position).getSender().equals(CookiesWork.cookie)){
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            layoutParams.setMarginEnd(50);
            text.setTextColor(Color.WHITE);
            text.setBackgroundResource(R.drawable.rect);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.setMarginEnd(50);
        }else{
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            layoutParams.setMarginStart(50);
            params.setMarginStart(50);
            text.setBackgroundResource(R.drawable.rectwhite);
        }
        text.setLayoutParams(layoutParams);
        img.setLayoutParams(layoutParams);
        if(messages.get(position).getType().equals("text")) {
            params.addRule(RelativeLayout.BELOW, R.id.mesText);
            text.setText(messages.get(position).getMessage());
        }
        else {
            text.setVisibility(View.GONE);
            params.addRule(RelativeLayout.BELOW, R.id.playSound);
            if(messages.get(position).getType().equals("sticker"))
                img.setImageResource(stickers.get(Integer.parseInt(messages.get(position).getMessage())-1));
            else img.setImageResource(R.drawable.icon_voice_start);
            img.setVisibility(View.VISIBLE);
        }
        date.setLayoutParams(params);
        text.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "Roboto-Regular.ttf"));
        date.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "Roboto-Regular.ttf"));
            String hours = String.valueOf(data.getHours());
            String minutes = String.valueOf(data.getMinutes());
            if (data.getHours() < 10) hours = "0" + hours;
            if (data.getMinutes() < 10) minutes = "0" + minutes;
        date.setText(hours + ":" + minutes);
        return row;
    }

    private void playSound(int position) {
        try {
            releasePlayer();
            mediaPlayer = new MediaPlayer();
            fileName = Environment.getExternalStorageDirectory() + "/record-"
                    + messages.get(position).getSender() +
                    "-" + messages.get(position).getReceiver() + "-" +
                    String.valueOf(messages.get(position).getDate()/1000) + ".mp3";
            File file = new File(fileName);
            if(!file.exists())
                new GetSound().execute(messages.get(position));
            else {
                mediaPlayer.setDataSource(fileName);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private class GetSound extends AsyncTask<Message, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                mediaPlayer.setDataSource(fileName);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }catch (Exception e){}
        }

        @Override
        protected Void doInBackground(Message... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                String str = mContext.getResources().getString(R.string.url) + "?operation=getSound&sender=" +
                        params[0].getSender() + "&receiver=" + params[0].getReceiver() + "&date=" + params[0].getDate();
                url = new URL(str);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                InputStream is = connection.getInputStream();
                fileName = Environment.getExternalStorageDirectory() + "/record-"
                        + params[0].getSender() +
                        "-" + params[0].getReceiver() + "-" +
                        String.valueOf(params[0].getDate()/1000) + ".mp3";
                File path = new File(fileName);
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(path);
                    fos.write(toByteArray(is));
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                is.close();
                connection.disconnect();
            }
            catch (Exception e){
            }
            finally {
                if(connection!=null)
                    connection.disconnect();
            }
            return null;
        }
    }

    public byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
            read = inputStream.read(buffer);
            if (read != -1)
                out.write(buffer,0,read);
        }
        out.close();
        return out.toByteArray();
    }

}
