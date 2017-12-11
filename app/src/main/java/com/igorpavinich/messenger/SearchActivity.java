package com.igorpavinich.messenger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ProgressBar progressBar;
    HttpConnect httpConnect;
    GetImage getImage;
    ImageView imgSearch, imgMsg, imgProfile;
    ListView listView;
    ArrayList<User> users;
    ArrayList<User> buffer;
    SearchAdapter adapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        progressBar = findViewById(R.id.progressBar);
        users = new ArrayList<>();
        buffer = new ArrayList<>();
        adapter = new SearchAdapter(SearchActivity.this, users);
        try {
            httpConnect = new HttpConnect();
            httpConnect.execute();
        }catch (Exception e){}
        imgSearch = findViewById(R.id.imgSearch);
        imgMsg = findViewById(R.id.imgMsg);
        imgProfile = findViewById(R.id.imgProfile);
        imgMsg.setOnClickListener(imgClickListener);
        imgProfile.setOnClickListener(imgClickListener);
        imgSearch.setOnClickListener(imgClickListener);
        listView = findViewById(R.id.searchList);
        listView.setAdapter(adapter);
        listView.setOnTouchListener(new OnSwipeListener(SearchActivity.this){
            @Override
            public void onSwipeRight() {
                startActivity(new Intent(SearchActivity.this, DialogsActivity.class));
            }

            @Override
            public void onSwipeLeft() {
                startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
            }
            @Override
            void onSwipeTop() {

            }

            @Override
            void onSwipeBottom() {

            }
        });
        listView.setOnItemClickListener(onItemClickListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        httpConnect.cancel(true);
        getImage.cancel(true);
    }

    int code;
    ArrayList<User> bufUsers;
    class HttpConnect extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            if (code == HttpURLConnection.HTTP_OK) {
                for (User user :
                        bufUsers) {
                    buffer.add(user);
                }
                getImage = new GetImage();
                getImage.execute(buffer);
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(Consts.URL + "?operation=search");
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                code = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "windows-1251"));
                String json = in.readLine();
                in.close();
                connection.disconnect();
                bufUsers = new Gson().fromJson(json, new TypeToken<ArrayList<User>>(){}.getType());
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

    class GetImage extends AsyncTask<ArrayList<User>, Void, Boolean>{

        @Override
        protected void onPostExecute(Boolean aVoid) {
            for (User user :
                    buffer) {
                users.add(user);
            }
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(ArrayList<User>... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                for (User user: params[0]) {
                    String str = Consts.URL + "?operation=profile&type=image&login=" + user.getLogin();
                    url = new URL(str);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Cookie", CookiesWork.cookie);
                    int code = connection.getResponseCode();
                    if(code==HttpURLConnection.HTTP_OK){
                    InputStream is = connection.getInputStream();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    options.inSampleSize = 20;
                    Bitmap img = BitmapFactory.decodeStream(is, null, options);
                    user.setPicture(img);
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

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
            intent.putExtra("login", ((User)adapter.getItem(i)).getLogin());
            startActivity(intent);
        }
    };

    View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imgMsg:
                    startActivity(new Intent(SearchActivity.this, DialogsActivity.class));
                    break;
                case R.id.imgProfile:
                    startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
                    break;
                case R.id.imgSearch:
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

}
