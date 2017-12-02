package com.igorpavinich.messenger;

import android.content.Intent;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessagesActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Dialog> dialogs;
    DialogsAdapter adapter;
    SearchView searchView;
    ImageView imgSearch, imgMsg, imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        CookiesWork.loadCookie(getSharedPreferences("SharPrefs", MODE_PRIVATE));
        new HttpConnect().execute();
        listView = findViewById(R.id.dialogsList);
        listView.setOnTouchListener(new OnSwipeListener(MessagesActivity.this){
            @Override
            public void onSwipeRight() {
                startActivity(new Intent(MessagesActivity.this, ProfileActivity.class));
            }

            @Override
            public void onSwipeLeft() {
                startActivity(new Intent(MessagesActivity.this, SearchActivity.class));
            }
        });
        dialogs = new ArrayList<>();
        adapter = new DialogsAdapter(this, dialogs);
        listView.setAdapter(adapter);
        imgSearch = findViewById(R.id.imgSearch);
        imgMsg = findViewById(R.id.imgMsg);
        imgProfile = findViewById(R.id.imgProfile);
        imgMsg.setOnClickListener(imgClickListener);
        imgProfile.setOnClickListener(imgClickListener);
        imgSearch.setOnClickListener(imgClickListener);
    }

    View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imgMsg:
                    loadMessages();
                    break;
                case R.id.imgProfile:
                    startActivity(new Intent(MessagesActivity.this, ProfileActivity.class));
                    break;
                case R.id.imgSearch:
                    startActivity(new Intent(MessagesActivity.this, SearchActivity.class));
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

    class HttpConnect extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            if(code == HttpURLConnection.HTTP_OK)
                loadMessages();
            else if(code == HttpURLConnection.HTTP_NOT_FOUND)
                startActivity(new Intent(MessagesActivity.this, SignIn_Activity.class));
            else Toast.makeText(MessagesActivity.this, "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show();
            }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(Consts.URL + "?operation=messages");
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
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

    private void loadMessages() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        dialogs.add(new Dialog("Igor", "Pavinich", "pidor", format.format(new Date())));
        dialogs.add(new Dialog("Evg", "Kuril", "sam", format.format(new Date())));
        adapter.notifyDataSetChanged();
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
