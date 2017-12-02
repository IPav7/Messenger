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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ImageView imgSearch, imgMsg, imgProfile;
    ListView listView;
    ArrayList<User> users;
    SearchAdapter adapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        users = new ArrayList<>();
        new HttpConnect().execute();
        imgSearch = findViewById(R.id.imgSearch);
        imgMsg = findViewById(R.id.imgMsg);
        imgProfile = findViewById(R.id.imgProfile);
        imgMsg.setOnClickListener(imgClickListener);
        imgProfile.setOnClickListener(imgClickListener);
        imgSearch.setOnClickListener(imgClickListener);
        listView = findViewById(R.id.searchList);
        users = new ArrayList<>();
        adapter = new SearchAdapter(SearchActivity.this, users);
        listView.setAdapter(adapter);
        listView.setOnTouchListener(new OnSwipeListener(SearchActivity.this){
            @Override
            public void onSwipeRight() {
                startActivity(new Intent(SearchActivity.this, MessagesActivity.class));
            }

            @Override
            public void onSwipeLeft() {
                startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
            }
        });
    }

    int code;
    ArrayList<User> bufUsers;
    class HttpConnect extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (code == HttpURLConnection.HTTP_OK) {
                for (User user : bufUsers) {
                    try {
                       // new GetImage().execute(bufUsers.get(i));
                        users.add(user);
                    } catch (Exception e) {
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(Consts.URL + "?operation=search");
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                code = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
               // String inputLine = in.readLine();
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

    class GetImage extends AsyncTask<User, Void, Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            users.add(users.get(0));
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(User... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                String str = Consts.URL + "?operation=profile&type=image&login=" + params[0].getLogin();
                url = new URL(str);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                InputStream is = connection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inSampleSize = 20;
                Bitmap img = BitmapFactory.decodeStream(is, null, options);
                params[0].setPicture(img);
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

    View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imgMsg:
                    startActivity(new Intent(SearchActivity.this, MessagesActivity.class));
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
