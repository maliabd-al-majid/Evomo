package com.example.evomo;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    public void recordingpage(View view) {
        startActivity(new Intent(view.getContext(), RecordingActivity.class));
        //  Toast.makeText(view.getContext(),textView.getText(),Toast.LENGTH_SHORT).show();

    }

}
