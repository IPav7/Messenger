package com.igorpavinich.messenger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    ImageView imgSearch, imgMsg, imgProfile;
    User user;

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
        new GetProfile().execute("");
    }

    int code;
    String inputLine = "pidor";

    class GetProfile extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
           /* Toast.makeText(ProfileActivity.this, " " + code, Toast.LENGTH_SHORT).show();
            if(code == HttpURLConnection.HTTP_NOT_FOUND)
                Toast.makeText(ProfileActivity.this, "Ошибка получения профиля", Toast.LENGTH_SHORT).show();
            else if(code == HttpURLConnection.HTTP_OK)*/
                Toast.makeText(ProfileActivity.this, " get " + inputLine , Toast.LENGTH_LONG).show();
                if(user!= null)
                    Toast.makeText(ProfileActivity.this, user.getName() + " " + user.getSurname() + " " + user.getLogin(), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(Consts.URL + "?operation=profile");
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
               // code = connection.getResponseCode();
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
