package com.igorpavinich.messenger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class SignIn_Activity extends Activity {

    EditText etLogin, etPassword;
    Button bSignIn, bSignUp;
    final String URL = "http://192.168.137.1:8080/mess/";
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        etLogin = findViewById(R.id.signin_username);
        etPassword = findViewById(R.id.signin_password);
        loadUser();
        if(!etLogin.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty())
        trySignIn();
        bSignIn = findViewById(R.id.signin_bSignIn);
        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    trySignIn();
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
    protected void onResume() {
        super.onResume();
        loadUser();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveUser();
    }

    private void trySignIn() {
        String url = URL + "login?name=" + etLogin.getText().toString() + "&password=" + etPassword.getText().toString();
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("OK"))
                    startActivity(new Intent(SignIn_Activity.this, MessagesActivity.class));
                else
                    Toast.makeText(SignIn_Activity.this, "NO", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignIn_Activity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(SignIn_Activity.this).addToRequestQueue(request);
    }

    private void saveUser() {
        preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name", etLogin.getText().toString());
        editor.putString("password", etPassword.getText().toString());
        editor.apply();
    }

    private void loadUser() {
        preferences = getPreferences(MODE_PRIVATE);
        etLogin.setText(preferences.getString("name", ""));
        etPassword.setText(preferences.getString("password", ""));
    }
}
