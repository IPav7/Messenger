package com.igorpavinich.messenger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    ImageView imgSearch, imgMsg, imgProfile, profileImg;
    TextView profileName, profileSurname, profileLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
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
        new GetProfileImage().execute("");
        new GetProfileInfo().execute("");
    }

    int code;
    String inputLine;
    Bitmap img;

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
            profileImg.setImageBitmap(img);
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                String str = Consts.URL + "?operation=profile&type=info";
                if (!params[0].isEmpty())
                    str += "&login=" + params[0];
                url = new URL(str);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                code = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                inputLine = in.readLine();
                user = new Gson().fromJson(inputLine, User.class);
                in.close();
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

    private class GetProfileImage extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                String str = Consts.URL + "?operation=profile&type=image";
                if (!params[0].isEmpty())
                    str += "&login=" + params[0];
                url = new URL(str);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                code = connection.getResponseCode();
                InputStream is = connection.getInputStream();
                img = BitmapFactory.decodeStream(is);
                is.close();
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
                    startActivity(new Intent(ProfileActivity.this, MessagesActivity.class));
                    break;
                case R.id.imgProfile:
                    break;
                case R.id.imgSearch:
                    startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
                    break;
            }
        }
    };

}
