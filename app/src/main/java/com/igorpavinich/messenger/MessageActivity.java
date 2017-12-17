package com.igorpavinich.messenger;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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

public class MessageActivity extends AppCompatActivity {

    ProgressBar progressBar;
    HttpConnect httpConnect;
    Send send;
    ListView listView;
    ArrayList<Message> messages;
    MessageAdapter adapter;
    String second;
    ImageView imgSearch, imgMsg, imgProfile, sendImg, sendSound;
    EditText etMessage;
    private MediaRecorder mediaRecorder;
    String fileName;
    boolean recording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        progressBar = findViewById(R.id.progressBar);
        second = getIntent().getStringExtra("login");
        messages = new ArrayList<>();
        adapter = new MessageAdapter(this, messages);
        httpConnect = new HttpConnect();
        httpConnect.execute();
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
        sendSound.setOnClickListener(sendSoundMessage);
        etMessage = findViewById(R.id.etMessage);
    }

    long time;

    View.OnClickListener sendSoundMessage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!recording) {
                try {
                    sendImg.setClickable(false);
                    releaseRecorder();
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    time = Calendar.getInstance().getTimeInMillis();
                    fileName = Environment.getExternalStorageDirectory() + "/record-"
                            + CookiesWork.cookie + "-" + second + "-" +  String.valueOf(time/1000) + ".3gpp";
                    Toast.makeText(MessageActivity.this, fileName, Toast.LENGTH_SHORT).show();
                    mediaRecorder.setOutputFile(fileName);
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    sendSound.setImageResource(R.drawable.ic_music_stop_24dp);
                } catch (Exception e) {
                }
            }
            else{
                if (mediaRecorder != null) {
                    mediaRecorder.stop();
                }
                sendSound.setImageResource(R.drawable.ic_music_note_black_24dp);
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
            else new Send().execute(new Message(CookiesWork.cookie, second, "sound", time));
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
                connection.setRequestProperty("Content-Type", "video/3gpp");
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
                Message message = new Message(CookiesWork.cookie, second, text, "text",Calendar.getInstance().getTimeInMillis());
                send.execute(message);
            }
        }
    };

    int code;
    ArrayList<Message> bufMessages;

    class Send extends AsyncTask<Message, Void, Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            if(code != HttpURLConnection.HTTP_OK)
                Toast.makeText(MessageActivity.this, "Ошибка отправки сообщения", Toast.LENGTH_SHORT).show();
            else {
                etMessage.setText("");
                httpConnect = new HttpConnect();
                httpConnect.execute();
            }
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

    class HttpConnect extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            messages.clear();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(code == HttpURLConnection.HTTP_OK)
            {
                for (Message message :
                        bufMessages) {
                    messages.add(message);
                }
                adapter.notifyDataSetChanged();
            }
            else Toast.makeText(MessageActivity.this, "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show();
            listView.setSelection(messages.size());
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(getResources().getString(R.string.url) + "?operation=messages&receiver=" + second);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                code = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "windows-1251"));
                String json = in.readLine();
                in.close();
                connection.disconnect();
                bufMessages = new Gson().fromJson(json, new TypeToken<ArrayList<Message>>(){}.getType());
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
        InputStream inputStream = getContentResolver().openInputStream(Uri.fromFile(new File(fileName)));
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
