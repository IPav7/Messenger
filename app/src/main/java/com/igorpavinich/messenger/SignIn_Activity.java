package com.igorpavinich.messenger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SignIn_Activity extends Activity {

    EditText etLogin, etPassword;
    Button bSignIn, bSignUp;
    CookieManager cookieManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        etLogin = findViewById(R.id.signin_username);
        etPassword = findViewById(R.id.signin_password);
        cookieManager = new CookieManager();
        bSignIn = findViewById(R.id.signin_bSignIn);
        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    new HttpConnect().execute(etLogin.getText().toString(), etPassword.getText().toString());
               // signIn();
            }
        });
        bSignUp = findViewById(R.id.signin_bSignUp);
        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn_Activity.this, SignUp_Activity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookiesWork.saveCookie(getSharedPreferences("SharPrefs", MODE_PRIVATE));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    int code=0;

    class HttpConnect extends AsyncTask<String, Void, Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            if(code == HttpURLConnection.HTTP_OK) {
                CookiesWork.saveCookie(getSharedPreferences("SharPrefs", MODE_PRIVATE));
                startActivity(new Intent(SignIn_Activity.this, MessagesActivity.class));
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(Consts.URL + "?operation=login&login=" + params[0] + "&password=" + params[1]);
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

}
