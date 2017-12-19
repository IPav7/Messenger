package com.igorpavinich.messenger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import static com.igorpavinich.messenger.CheckInput.checkLogin;
import static com.igorpavinich.messenger.CheckInput.checkPassword;

public class SignIn_Activity extends Activity {

    ProgressBar progressBar;
    HttpConnect httpConnect;
    EditText etLogin, etPassword;
    Button bSignIn;
    TextView textSignUp, textWelcome;
    CookieManager cookieManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        etLogin = findViewById(R.id.signin_username);
        etPassword = findViewById(R.id.signin_password);
        etLogin.addTextChangedListener(textWatcher);
        etPassword.addTextChangedListener(textWatcher);
        cookieManager = new CookieManager();
        bSignIn = findViewById(R.id.signin_bSignIn);
        bSignIn.setEnabled(false);
        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               progressBar.setVisibility(View.VISIBLE);
                    httpConnect = new HttpConnect();
                httpConnect.execute(etLogin.getText().toString(), etPassword.getText().toString());
               // signIn();
            }
        });
        textSignUp = findViewById(R.id.signUpText);
        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignIn_Activity.this, SignUp_Activity.class));
            }
        });
        textWelcome = findViewById(R.id.welcomeText);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Roboto-LightItalic.ttf");
        textSignUp.setTypeface(typeface);
        textWelcome.setTypeface(typeface);
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
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(code == HttpURLConnection.HTTP_OK) {
                CookiesWork.saveCookie(getSharedPreferences("SharPrefs", MODE_PRIVATE));
                startActivity(new Intent(SignIn_Activity.this, DialogsActivity.class));
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(getResources().getString(R.string.url) + "?operation=login&login=" + URLEncoder.encode(params[0], "UTF-8") + "&password=" +
                        SHA.encrypt(params[1]));
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
            if(checkLogin(etLogin.getText().toString()) && checkPassword(etPassword.getText().toString()))
                bSignIn.setEnabled(true);
            else bSignIn.setEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}
