package com.igorpavinich.messenger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {
    ProgressBar progressBar;
    GetProfileImage getProfileImage;
    GetProfileInfo getProfileInfo;
    ImageView imgSearch, imgMsg, imgProfile, profileImg;
    TextView profileName, profileSurname, profileLogin;
    FloatingActionButton fab;
    String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        progressBar = findViewById(R.id.progressBar);
        fab = findViewById(R.id.fab);
        Intent myIntent = getIntent();
        login = myIntent.getStringExtra("login");
        if(login == null)
            login = CookiesWork.cookie;
        if(!login.equals(CookiesWork.cookie))
            fab.setImageDrawable(getResources().getDrawable(R.drawable.icon_chat));
        else fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_outline_blue_24dp));
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
        getProfileImage = new GetProfileImage();
        getProfileImage.execute(login);
        getProfileInfo = new GetProfileInfo();
        getProfileInfo.execute(login);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getProfileInfo.cancel(true);
        getProfileImage.cancel(true);
    }

    int code;

    private class GetProfileInfo extends AsyncTask<String, Void, Void> {
        User user;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

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
                String str = getResources().getString(R.string.url) + "?operation=profile&type=info&login=" + params[0];
                url = new URL(str);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                connection.setRequestProperty("Cache-Control", "no-cache");
                code = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), "windows-1251"));
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
            if(codeImage == HttpURLConnection.HTTP_OK && img!=null) {
                profileImg.setImageBitmap(img);
            }
            else profileImg.setImageDrawable(getResources().getDrawable(R.drawable.anonimg));
            codeImage = 0;
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                String str = getResources().getString(R.string.url) + "?operation=profile&type=image&login=" + params[0];
                url = new URL(str);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
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
            }else{
                Intent intent = new Intent(ProfileActivity.this, MessageActivity.class);
                intent.putExtra("login", login);
                startActivity(intent);
            }
        }
    };

    View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imgMsg:
                    startActivity(new Intent(ProfileActivity.this, DialogsActivity.class));
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