package com.igorpavinich.messenger;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DialogsActivity extends Activity {
    ProgressBar progressBar;
    ListView listView;
    ArrayList<Dialog> dialogs;
    ArrayList<Dialog> buffer;
    DialogsAdapter adapter;
    ImageView imgSearch, imgMsg, imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialogs);
        CookiesWork.loadCookie(getSharedPreferences("SharPrefs", MODE_PRIVATE));
        progressBar = findViewById(R.id.progressBar);
        listView = findViewById(R.id.dialogsList);
        listView.setOnItemClickListener(onItemClickListener);
        imgSearch = findViewById(R.id.imgSearch);
        imgMsg = findViewById(R.id.imgMsg);
        imgProfile = findViewById(R.id.imgProfile);
        imgMsg.setOnClickListener(imgClickListener);
        imgProfile.setOnClickListener(imgClickListener);
        imgSearch.setOnClickListener(imgClickListener);
    }

    protected void refreshDialogs(View view){
        new HttpConnect().execute();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(DialogsActivity.this, MessageActivity.class);
            intent.putExtra("login", ((Dialog)adapter.getItem(i)).getSecond());
            startActivity(intent);
        }
    };

    View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imgProfile:
                    startActivity(new Intent(DialogsActivity.this, ProfileActivity.class));
                    break;
                case R.id.imgSearch:
                    startActivity(new Intent(DialogsActivity.this, SearchActivity.class));
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        dialogs = new ArrayList<>();
        buffer = new ArrayList<>();
        adapter = new DialogsAdapter(this, dialogs);
        listView.setAdapter(adapter);
        new HttpConnect().execute();
    }

    int code;
    ArrayList<Dialog> bufDialogs;

    class HttpConnect extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            dialogs.clear();
            buffer.clear();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(code == HttpURLConnection.HTTP_OK)
            {
                for (Dialog dialog :
                        bufDialogs) {
                    buffer.add(dialog);
                }
                new GetImage().execute(buffer);
            }
            else if(code == HttpURLConnection.HTTP_NOT_FOUND)
                startActivity(new Intent(DialogsActivity.this, SignIn_Activity.class));
            else Toast.makeText(DialogsActivity.this, "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show();
            }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(getResources().getString(R.string.url) + "?operation=dialogs");
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                code = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "windows-1251"));
                String json = in.readLine();
                in.close();
                connection.disconnect();
                bufDialogs = new Gson().fromJson(json, new TypeToken<ArrayList<Dialog>>(){}.getType());
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

    class GetImage extends AsyncTask<ArrayList<Dialog>, Void, Boolean>{

        @Override
        protected void onPostExecute(Boolean aVoid) {
            for (Dialog dialog :
                    buffer) {
                dialogs.add(dialog);
            }
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(ArrayList<Dialog>... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                for (Dialog dialog: params[0]) {
                    String str = getResources().getString(R.string.url) + "?operation=profile&type=image&size=small&login=" + dialog.getSecond();
                    url = new URL(str);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Cookie", CookiesWork.cookie);
                    int code = connection.getResponseCode();
                    if(code==HttpURLConnection.HTTP_OK){
                        InputStream is = connection.getInputStream();
                        Bitmap img = BitmapFactory.decodeStream(is);
                        dialog.setPicture(img);
                        is.close();
                    }
                    connection.disconnect();
                }
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
