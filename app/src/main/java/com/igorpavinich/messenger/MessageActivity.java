package com.igorpavinich.messenger;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MessageActivity extends Activity {

    Send send;
    ListView listView;
    ArrayList<Message> messages;
    MessageAdapter adapter;
    String second;
    ImageView imgSearch, imgMsg, imgProfile, sendImg, sendSound, sendSticker;
    EditText etMessage;
    private MediaRecorder mediaRecorder;
    String fileName;
    boolean recording;
    TextView opponent;
    long lastUpdate=0;
    Timer timer;
    boolean firstTime;
    TimerTask doAsynchronousTask;
    GridView gridView;
    ArrayList<Integer> items;
    StickerAdapter stickerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        second = getIntent().getStringExtra("login");
        messages = new ArrayList<>();
        adapter = new MessageAdapter(this, messages);
        opponent = findViewById(R.id.messageOpponent);
        opponent.setText(second);
        listView = findViewById(R.id.listMessages);
        listView.setAdapter(adapter);
        imgSearch = findViewById(R.id.imgSearch);
        imgMsg = findViewById(R.id.imgMsg);
        imgProfile = findViewById(R.id.imgProfile);
        imgMsg.setOnClickListener(imgClickListener);
        imgProfile.setOnClickListener(imgClickListener);
        imgSearch.setOnClickListener(imgClickListener);
        sendImg = findViewById(R.id.sendImg);
        sendImg.setOnClickListener(sendMessage);
        sendSound = findViewById(R.id.sendSound);
        sendSticker = findViewById(R.id.sendSticker);
        sendSound.setOnClickListener(sendSoundMessage);
        sendSticker.setOnClickListener(showStickers);
        etMessage = findViewById(R.id.etMessage);
        etMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                    if(gridView.isShown())
                        gridView.setVisibility(View.GONE);
            }
        });
        gridView = findViewById(R.id.gridView);
        addStickerToGridView();
        firstTime = true;
        timer = new Timer();
        callAsynchronousTask();
    }

    private void addStickerToGridView() {
        items = new ArrayList<Integer>(){{
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
        stickerAdapter = new StickerAdapter(items, R.layout.stickeritem, this);
        gridView.setAdapter(stickerAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new Send().execute(new Message(CookiesWork.cookie, second, String.valueOf(i+1), "sticker"));
            }
        });
    }

    protected void onClickToolbarMessages(View v){
        switch(v.getId()){
            case R.id.backfromMessages:
                startActivity(new Intent(MessageActivity.this, DialogsActivity.class));
                break;
            case R.id.messageOpponent:
                Intent intent = new Intent(MessageActivity.this, ProfileActivity.class);
                intent.putExtra("login", second);
                startActivity(intent);
                break;
        }
    }

    View.OnClickListener showStickers = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(gridView.isShown())
            gridView.setVisibility(View.GONE);
            else gridView.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    };

    View.OnClickListener sendSoundMessage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!recording) {
                try {
                    sendImg.setClickable(false);
                    releaseRecorder();
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    fileName = Environment.getExternalStorageDirectory() + "/bufrecord.mp3";
                    mediaRecorder.setOutputFile(fileName);
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    sendSound.setImageResource(R.drawable.icon_voice_stop);
                } catch (Exception e) {
                }
            }
            else{
                if (mediaRecorder != null) {
                    mediaRecorder.stop();
                }
                sendSound.setImageResource(R.drawable.icon_voice_start);
                new SendSound().execute();
            }
            recording = !recording;
        }
    };

    private class SendSound extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            if(code != HttpURLConnection.HTTP_OK)
                Toast.makeText(MessageActivity.this, "Ошибка отправки сообщения", Toast.LENGTH_SHORT).show();
            else{
                new Send().execute(new Message(CookiesWork.cookie, second, "sound"));
            }
            sendImg.setClickable(true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                byte[] array = toByteArray(fileName);
                url = new URL(getResources().getString(R.string.url) + "?operation=sendSound");
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "audio/mp3");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                connection.setRequestProperty("Content-Length", Integer.toString(array.length));
                DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                dos.write(array);
                dos.flush();
                dos.close();
                code = connection.getResponseCode();
            }
            catch (Exception e){}
            finally {
                if(connection!=null)
                    connection.disconnect();
            }
            return null;
        }
    }

    View.OnClickListener sendMessage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String text = etMessage.getText().toString();
            if(!text.isEmpty()){
                send = new Send();
                Message message = new Message(CookiesWork.cookie, second, text, "text");
                etMessage.setText("");
                send.execute(message);
            }
        }
    };

    int code;
    ArrayList<Message> bufMessages;


    class Send extends AsyncTask<Message, Void, Void>{

        @Override
        protected void onPreExecute() {
            doAsynchronousTask.cancel();
        //    timer.purge();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(code != HttpURLConnection.HTTP_OK)
                Toast.makeText(MessageActivity.this, "Ошибка отправки сообщения", Toast.LENGTH_SHORT).show();
            listView.setSelection(messages.size());
            callAsynchronousTask();
        }

        @Override
        protected Void doInBackground(Message... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(getResources().getString(R.string.url) + "?operation=sendmessage");
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                OutputStreamWriter wr= new OutputStreamWriter(connection.getOutputStream(),"windows-1251");
                String json = new Gson().toJson(params[0]);
                wr.write(json);
                wr.flush();
                wr.close();
                code = connection.getResponseCode();
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

    String buf;

    class HttpConnect extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
          //  progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(code == HttpURLConnection.HTTP_OK)
            {
                    for (Message message :
                            bufMessages) {
                        messages.add(message);
                    }
                if(messages.size()==0)
                    Toast.makeText(MessageActivity.this, "Сообщений нет\nНапишите первым", Toast.LENGTH_LONG).show();
                    adapter.notifyDataSetChanged();
            }
            else Toast.makeText(MessageActivity.this, "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show();
         //   progressBar.setVisibility(View.GONE);
            if(listView.getLastVisiblePosition()+2 == messages.size())
                listView.setSelection(messages.size()-1);
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(getResources().getString(R.string.url) + "?operation=messages&receiver=" + second +
                        "&all=" + params[0]);
                lastUpdate = System.currentTimeMillis();
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                code = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "windows-1251"));
                String json = in.readLine();
                buf = json;
                in.close();
                bufMessages = new Gson().fromJson(json, new TypeToken<ArrayList<Message>>(){}.getType());
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

    View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imgMsg:
                    startActivity(new Intent(MessageActivity.this, DialogsActivity.class));
                    break;
                case R.id.imgProfile:
                    startActivity(new Intent(MessageActivity.this, ProfileActivity.class));
                    break;
                case R.id.imgSearch:
                    startActivity(new Intent(MessageActivity.this, SearchActivity.class));
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseRecorder();
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    public byte[] toByteArray(String fileName) throws IOException {
        File file = new File(fileName);
        InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(file));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
            read = inputStream.read(buffer);
            if (read != -1)
                out.write(buffer,0,read);
        }
        file.delete();
        out.close();
        return out.toByteArray();
    }

    public void callAsynchronousTask() {
        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                        try {
                            if(firstTime) {
                                new HttpConnect().execute("true");
                                firstTime = false;
                            }
                            else new HttpConnect().execute("false");
                        } catch (Exception e) {
                        }
            }
        };
        timer.schedule(doAsynchronousTask, 500, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
        timer.purge();
    }
}
