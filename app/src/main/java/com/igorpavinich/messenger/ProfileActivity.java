package com.igorpavinich.messenger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CacheResponse;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    ImageView imgSearch, imgMsg, imgProfile, profileImg;
    TextView profileName, profileSurname, profileLogin;
    FloatingActionButton fab;
    String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fab = findViewById(R.id.fab);
        Intent myIntent = getIntent();
        login = myIntent.getStringExtra("login");
        if(login==null) {
            login = CookiesWork.cookie;
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_outline_blue_24dp));
        }
        else fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_message_blue_24dp));
        fab.setOnClickListener(onClickListener);
        imgSearch = findViewById(R.id.imgSearch);
        imgMsg = findViewById(R.id.imgMsg);
        imgProfile = findViewById(R.id.imgProfile);
        imgMsg.setOnClickListener(imgClickListener);
        imgProfile.setOnClickListener(imgClickListener);
        imgSearch.setOnClickListener(imgClickListener);
        profileImg = findViewById(R.id.profileImg);
        profileLogin = findViewById(R.id.profileLogin);
        profileName = findViewById(R.id.profileName);
        profileSurname = findViewById(R.id.profileSurname);
        profileImg.setOnTouchListener(new OnSwipeListener(ProfileActivity.this){
            @Override
            public void onSwipeRight() {
                startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
            }

            @Override
            public void onSwipeLeft() {
                startActivity(new Intent(ProfileActivity.this, MessagesActivity.class));
            }
        });
        new GetProfileImage().execute(login);
        new GetProfileInfo().execute(login);
    }

    int code;

    private class GetProfileInfo extends AsyncTask<String, Void, Void> {
        User user;
        @Override
        protected void onPostExecute(Void aVoid) {
            if(code == HttpURLConnection.HTTP_OK)
                showProfile();
            else Toast.makeText(ProfileActivity.this, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show();
        }

        private void showProfile() {
            profileName.setText(user.getName());
            profileSurname.setText(user.getSurname());
            profileLogin.setText(user.getLogin());
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                String str = Consts.URL + "?operation=profile&type=info&login=" + params[0];
                url = new URL(str);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                connection.setRequestProperty("Cache-Control", "no-cache");
                code = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine = in.readLine();
                in.close();
                connection.disconnect();
                user = new Gson().fromJson(inputLine, User.class);
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
    protected void onDestroy() {
        profileImg.setImageBitmap(null);
        img.recycle();
        img = null;
        super.onDestroy();
    }

    int codeImage;
    Bitmap img;

    private class GetProfileImage extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            if(codeImage == HttpURLConnection.HTTP_OK) {
                profileImg.setImageBitmap(img);
            }
            else profileImg.setImageDrawable(getResources().getDrawable(R.drawable.anonimg));
            codeImage = 0;
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                String str = Consts.URL + "?operation=profile&type=image&login=" + params[0];
                url = new URL(str);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cache-Control", "no-cache");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                codeImage = connection.getResponseCode();
                InputStream is = connection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inSampleSize = 6;
                img = BitmapFactory.decodeStream(is, null, options);
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

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(login.equals(CookiesWork.cookie)){
                CookiesWork.cookie = "";
                CookiesWork.saveCookie(getSharedPreferences("SharPrefs", MODE_PRIVATE));
                startActivity(new Intent(ProfileActivity.this, SignIn_Activity.class));
            }
        }
    };

    View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imgMsg:
                    startActivity(new Intent(ProfileActivity.this, MessagesActivity.class));
                    break;
                case R.id.imgProfile:
                    if(!login.equals(CookiesWork.cookie))
                        startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                    break;
                case R.id.imgSearch:
                    startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
                    break;
            }
        }
    };

}
