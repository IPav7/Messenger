package com.igorpavinich.messenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SignUp_Activity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView;
    Button bSignUp, bSignIn;
    EditText etName, etSurname, etLogin, etPassword;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        imageView = findViewById(R.id.signup_image);
        bSignIn = findViewById(R.id.signup_bSignIn);
        bSignUp = findViewById(R.id.signup_bSignUp);
        etName = findViewById(R.id.signup_name);
        etSurname = findViewById(R.id.signup_surname);
        etLogin = findViewById(R.id.signup_username);
        etPassword = findViewById(R.id.signup_password);
        bSignUp.setOnClickListener(this);
        bSignIn.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            imageView.setImageURI(data.getData());
        }
        else
            Toast.makeText(SignUp_Activity.this, "Open photo error", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveUser();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signup_bSignIn:
                finish();
                break;
            case R.id.signup_bSignUp:

                break;
            case R.id.signup_image:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
                break;
        }
    }

    private void saveUser() {
        preferences = getSharedPreferences("SignIn_Activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name", etLogin.getText().toString());
        editor.putString("password", etPassword.getText().toString());
        editor.apply();
    }

}
