package com.igorpavinich.messenger;

import android.content.Intent;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DialogsActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Dialog> dialogs;
    DialogsAdapter adapter;
    SearchView searchView;
    ImageView imgSearch, imgMsg, imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);
        CookiesWork.loadCookie(getSharedPreferences("SharPrefs", MODE_PRIVATE));
        dialogs = new ArrayList<>();
        adapter = new DialogsAdapter(this, dialogs);
        new HttpConnect().execute();
        listView = findViewById(R.id.dialogsList);
        listView.setOnTouchListener(new OnSwipeListener(DialogsActivity.this){
            @Override
            public void onSwipeRight() {
                startActivity(new Intent(DialogsActivity.this, ProfileActivity.class));
            }

            @Override
            public void onSwipeLeft() {
                startActivity(new Intent(DialogsActivity.this, SearchActivity.class));
            }
        });
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);
        imgSearch = findViewById(R.id.imgSearch);
        imgMsg = findViewById(R.id.imgMsg);
        imgProfile = findViewById(R.id.imgProfile);
        imgMsg.setOnClickListener(imgClickListener);
        imgProfile.setOnClickListener(imgClickListener);
        imgSearch.setOnClickListener(imgClickListener);
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(DialogsActivity.this, MessageActivity.class);
            intent.putExtra("login", ((Dialog)adapter.getItem(i)).getSecond());
            startActivity(intent);
            //Toast.makeText(DialogsActivity.this, ((Dialog)adapter.getItem(i)).getSecond(), Toast.LENGTH_SHORT).show();
        }
    };

    View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imgMsg:
                    //loadMessages();
                    break;
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
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    int code;
    ArrayList<Dialog> bufDialogs;

    class HttpConnect extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            if(code == HttpURLConnection.HTTP_OK)
            {
               // Toast.makeText(DialogsActivity.this, "size: " + bufDialogs.size(), Toast.LENGTH_SHORT).show();
                for (Dialog dialog :
                        bufDialogs) {
                    dialogs.add(dialog);
                }
                adapter.notifyDataSetChanged();
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
                url = new URL(Consts.URL + "?operation=dialogs");
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                code = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
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
