package com.igorpavinich.messenger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import static com.igorpavinich.messenger.CheckInput.checkLogin;
import static com.igorpavinich.messenger.CheckInput.checkName;
import static com.igorpavinich.messenger.CheckInput.checkPassword;

public class SignUp_Activity extends Activity implements View.OnClickListener {

    ProgressBar progressBar;
    ImageView imageView;
    Button bSignUp;
    EditText etName, etSurname, etLogin, etPassword;
    TextView textSignIn;
    boolean imageUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        imageView = findViewById(R.id.signup_image);
        bSignUp = findViewById(R.id.signup_bSignUp);
        etName = findViewById(R.id.signup_name);
        etSurname = findViewById(R.id.signup_surname);
        etLogin = findViewById(R.id.signup_username);
        etPassword = findViewById(R.id.signup_password);
        etName.addTextChangedListener(textWatcher);
        etSurname.addTextChangedListener(textWatcher);
        etLogin.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
        bSignUp.setOnClickListener(this);
        bSignUp.setEnabled(false);
        textSignIn = findViewById(R.id.textSignIn);
        textSignIn.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-LightItalic.ttf"));
        textSignIn.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            imageView.setImageURI(data.getData());
            imageUpdated = true;
        }
        else
            Toast.makeText(SignUp_Activity.this, "Фото не загружено", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.textSignIn:
                startActivity(new Intent(SignUp_Activity.this, SignIn_Activity.class));
                break;
            case R.id.signup_bSignUp:
                if(imageUpdated){
                    Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                    new PostImage().execute(bitmap);
                }
                String name = etName.getText().toString();
                String surname = etSurname.getText().toString();
                String login = etLogin.getText().toString();
                String password = etPassword.getText().toString();
                new SendProfile().execute(name, surname, login, password);
                break;
            case R.id.signup_image:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
                break;
        }
    }

    int code;

    class SendProfile extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            code = 0;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(code == HttpURLConnection.HTTP_OK) {
                CookiesWork.saveCookie(getSharedPreferences("SharPrefs", MODE_PRIVATE));
                startActivity(new Intent(SignUp_Activity.this, DialogsActivity.class));
            }
            else
                Toast.makeText(SignUp_Activity.this, "Ошибка регистрации\nВозможно, логин уже занят", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(getResources().getString(R.string.url) + "?operation=register&name=" + URLEncoder.encode(params[0], "UTF-8") + "&surname=" + URLEncoder.encode(params[1], "UTF-8")
                 + "&login=" + URLEncoder.encode(params[2], "UTF-8") + "&password=" + SHA.encrypt(params[3]));
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                List<String> cookies = connection.getHeaderFields().get(CookiesWork.COOKIES_HEADER);
                String[] vals = cookies.get(0).split("=");
                CookiesWork.cookie = vals[1];
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

    class PostImage extends AsyncTask<Bitmap, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
            if(code == HttpURLConnection.HTTP_NOT_FOUND)
                Toast.makeText(SignUp_Activity.this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Bitmap... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                params[0].compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byteArray = stream.toByteArray();
                url = new URL(getResources().getString(R.string.url) + "?operation=register");
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
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

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(checkLogin(etLogin.getText().toString()))
                etLogin.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_valid), null);
            else etLogin.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            if(checkPassword(etPassword.getText().toString()))
                etPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_valid), null);
            else etPassword.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            if(checkName(etName.getText().toString()))
                etName.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_valid), null);
            else etName.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            if(checkName(etSurname.getText().toString()))
                etSurname.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_valid), null);
            else etSurname.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);
            if(checkLogin(etLogin.getText().toString()) && checkPassword(etPassword.getText().toString()) &&
               checkName(etName.getText().toString()) && checkName(etSurname.getText().toString()))
                bSignUp.setEnabled(true);
            else bSignUp.setEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


}
