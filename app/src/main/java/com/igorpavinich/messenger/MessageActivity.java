package com.igorpavinich.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MessageActivity extends Activity {

    ListView listView;
    ArrayList<Message> messages;
    MessageAdapter adapter;
    String second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        second = getIntent().getStringExtra("login");
        messages = new ArrayList<>();
        adapter = new MessageAdapter(this, messages);
        new HttpConnect().execute();
        listView = findViewById(R.id.listMessages);
        listView.setAdapter(adapter);
    }

    int code;
    ArrayList<Message> bufMessages;

    class HttpConnect extends AsyncTask<String, Void, Void> {

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
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(Consts.URL + "?operation=messages&receiver=" + second);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                code = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
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

}
