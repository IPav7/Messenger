package com.igorpavinich.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MessageActivity extends Activity {
    ProgressBar progressBar;
    HttpConnect httpConnect;
    Send send;
    ListView listView;
    ArrayList<Message> messages;
    MessageAdapter adapter;
    String second;
    ImageView imgSearch, imgMsg, imgProfile, sendImg;
    EditText etMessage;

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
        listView.setOnTouchListener(new OnSwipeListener(this){
            @Override
            public void onSwipeRight() {
            }

            @Override
            public void onSwipeLeft() {
            }

            @Override
            void onSwipeTop() {
            }

            @Override
            void onSwipeBottom() {
                httpConnect = new HttpConnect();
                httpConnect.execute();
            }
        });
        imgSearch = findViewById(R.id.imgSearch);
        imgMsg = findViewById(R.id.imgMsg);
        imgProfile = findViewById(R.id.imgProfile);
        imgMsg.setOnClickListener(imgClickListener);
        imgProfile.setOnClickListener(imgClickListener);
        imgSearch.setOnClickListener(imgClickListener);
        sendImg = findViewById(R.id.sendImg);
        sendImg.setOnClickListener(sendMessage);
        etMessage = findViewById(R.id.etMessage);
    }

    View.OnClickListener sendMessage = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String text = etMessage.getText().toString();
            if(!text.isEmpty()){
                send = new Send();
                send.execute(text);
            }
        }
    };

    int code;
    ArrayList<Message> bufMessages;
    String request;

    class Send extends AsyncTask<String, Void, Void>{
        @Override
        protected void onPreExecute() {
        }

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
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(Consts.URL + "?operation=sendmessage");
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                OutputStreamWriter wr= new OutputStreamWriter(connection.getOutputStream(),"windows-1251");
                String json = new Gson().toJson(new Message(CookiesWork.cookie, second, params[0]));
                request = json;
                wr.write(json);
                wr.flush();
                wr.close();
                code = connection.getResponseCode();
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
                url = new URL(Consts.URL + "?operation=messages&receiver=" + second);
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

}
