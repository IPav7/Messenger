package com.igorpavinich.messenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

public class SearchActivity extends AppCompatActivity {

    ImageView imgSearch, imgMsg, imgProfile;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        imgSearch = findViewById(R.id.imgSearch);
        imgMsg = findViewById(R.id.imgMsg);
        imgProfile = findViewById(R.id.imgProfile);
        imgMsg.setOnClickListener(imgClickListener);
        imgProfile.setOnClickListener(imgClickListener);
        imgSearch.setOnClickListener(imgClickListener);
        listView = findViewById(R.id.searchList);
        listView.setOnTouchListener(new OnSwipeListener(SearchActivity.this){
            @Override
            public void onSwipeRight() {
                startActivity(new Intent(SearchActivity.this, MessagesActivity.class));
            }

            @Override
            public void onSwipeLeft() {
                startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
            }
        });
    }

    View.OnClickListener imgClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.imgMsg:
                    startActivity(new Intent(SearchActivity.this, MessagesActivity.class));
                    break;
                case R.id.imgProfile:
                    startActivity(new Intent(SearchActivity.this, ProfileActivity.class));
                    break;
                case R.id.imgSearch:
                    break;
            }
        }
    };

}
