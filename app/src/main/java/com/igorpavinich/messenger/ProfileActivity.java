package com.igorpavinich.messenger;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends Activity {
    ProgressBar progressBar;
    GetProfileImage getProfileImage;
    GetProfileInfo getProfileInfo;
    ImageView imgSearch, imgMsg, imgProfile, profileImg;
    TextView profileName, profileSurname, profileLogin;
    FloatingActionButton fab, fabEdit;
    TextView prName, prSurname, prLogin;
    String login;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        progressBar = findViewById(R.id.progressBar);
        fab = findViewById(R.id.fab);
        fabEdit = findViewById(R.id.fabEdit);
        fabEdit.setOnClickListener(editListener);
        Intent myIntent = getIntent();
        login = myIntent.getStringExtra("login");
        if(login == null)
            login = CookiesWork.cookie;
        if(!login.equals(CookiesWork.cookie)) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.icon_chat_white));
        }
        else {
            fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_exit_to_app_black_24dp));
        }
        fab.setOnClickListener(onClickListener);
        imgSearch = findViewById(R.id.imgSearch);
        imgMsg = findViewById(R.id.imgMsg);
        imgProfile = findViewById(R.id.imgProfile);
        imgMsg.setOnClickListener(imgClickListener);
        imgProfile.setOnClickListener(imgClickListener);
        imgSearch.setOnClickListener(imgClickListener);
        profileImg = findViewById(R.id.profileImg);
        Typeface tf = Typeface.createFromAsset(getAssets(), "Roboto-MediumItalic.ttf");
        profileLogin = findViewById(R.id.profileLogin);
        profileName = findViewById(R.id.profileName);
        profileSurname = findViewById(R.id.profileSurname);
        profileLogin.setTypeface(tf);
        profileName.setTypeface(tf);
        profileSurname.setTypeface(tf);
        prName = findViewById(R.id.prName);
        prSurname = findViewById(R.id.prSurname);
        prLogin = findViewById(R.id.prLogin);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf");
        prName.setTypeface(typeface);
        prSurname.setTypeface(typeface);
        prLogin.setTypeface(typeface);
        getProfileImage = new GetProfileImage();
        getProfileImage.execute(login);
        getProfileInfo = new GetProfileInfo();
        getProfileInfo.execute(login);
    }

    View.OnClickListener chooseImg = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            editImage.setImageURI(data.getData());
            imageUpdated = true;
        }
        else
            Toast.makeText(ProfileActivity.this, "Фото не загружено", Toast.LENGTH_SHORT).show();
    }

    boolean imageUpdated;
    ImageView editImage;

    View.OnClickListener editListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(login.equals(CookiesWork.cookie)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                final View editView = getLayoutInflater().inflate(R.layout.editprofile, null);
                builder.setTitle("Изменить профиль");
                final EditText name = editView.findViewById(R.id.edit_name);
                name.setText(profileName.getText().toString());
                final EditText surname = editView.findViewById(R.id.edit_surname);
                surname.setText(profileSurname.getText().toString());
                editImage = editView.findViewById(R.id.editImage);
                editImage.setImageBitmap(((BitmapDrawable) profileImg.getDrawable()).getBitmap());
                editImage.setOnClickListener(chooseImg);
                builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new EditProfileInfo().execute(name.getText().toString(),
                                surname.getText().toString());
                        if (imageUpdated)
                            new EditProfileImage().execute(((BitmapDrawable) editImage.getDrawable()).getBitmap());
                    }
                });
                builder.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setView(editView);
                dialog = builder.create();
                dialog.show();
            }
            else{
                    new AddFriend().execute(String.valueOf(!user.isFriend()));
            }
        }
    };

    class AddFriend extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            new GetProfileInfo().execute(login);
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(getResources().getString(R.string.url) + "?operation=addFriend&second=" + login + "&add=" + params[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                connection.setRequestProperty("Cache-Control", "no-cache");
                code = connection.getResponseCode();
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

    class EditProfileImage extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
            if(code == HttpURLConnection.HTTP_NOT_FOUND)
                Toast.makeText(ProfileActivity.this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
            imageUpdated = false;
            new GetProfileInfo().execute(login);
            new GetProfileImage().execute(login);
        }

        @Override
        protected Void doInBackground(Bitmap... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                params[0].compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byteArray = stream.toByteArray();
                url = new URL(getResources().getString(R.string.url) + "?operation=editProfileImage");
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                connection.setRequestProperty("Content-Type", "application/octet-stream");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length", Integer.toString(byteArray.length));
                DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                dos.write(byteArray);
                dos.flush();
                dos.close();
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

    private class EditProfileInfo extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            if(!imageUpdated) {
                new GetProfileInfo().execute(login);
                new GetProfileImage().execute(login);
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                String str = getResources().getString(R.string.url) + "?operation=editProfile&name=" +
                        params[0] + "&surname=" + params[1];
                url = new URL(str);
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Cookie", CookiesWork.cookie);
                connection.setRequestProperty("Cache-Control", "no-cache");
                code = connection.getResponseCode();
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

    int code;
    User user;

    private class GetProfileInfo extends AsyncTask<String, Void, Void> {

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
            if(!login.equals(CookiesWork.cookie)) {
                if (user.isFriend())
                    fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_remove_black_24dp));
                else
                    fabEdit.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_black_24dp));
            }
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
     if(img!=null) {
         img.recycle();
         img = null;
     }
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
                String str = getResources().getString(R.string.url) + "?operation=profile&type=image&size=full&login=" + params[0];
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
