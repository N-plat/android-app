package com.nplat;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.nplat.ui.main.SectionsPagerAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String id_token;

    private FirebaseAuth mAuth;

    private static final String TAG="MainActivity";

    public class Post {
        String text;
        String username;
        String timestamp;
    }

    List<MainActivity.Post> post_list = null;

    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        Button postbutton = (Button) findViewById(R.id.btnPostActivity);

        postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent(MainActivity.this, PostActivity.class);

                startActivity(mIntent);

                }
        });

        Button followbutton = (Button) findViewById(R.id.btnFollowActivity);

        followbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mIntent = new Intent(MainActivity.this, FollowActivity.class);

                startActivity(mIntent);

            }
        });


    }
}