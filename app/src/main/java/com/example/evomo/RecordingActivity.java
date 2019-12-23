package com.example.evomo;


import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


public class RecordingActivity extends AppCompatActivity {
    public DBFile file = new DBFile();
    LinearLayout LinearLayout;
    Button StartEndbutton;
    Button ClearButton;
    Sensors sensors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recording);
        LinearLayout = findViewById(R.id.LinearLayout);
        StartEndbutton = findViewById(R.id.startendbutton);
        ClearButton = findViewById(R.id.ClearButton);
        sensors = new Sensors(getApplicationContext(), LinearLayout);

        ClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensors.clearRecords(getApplicationContext());
                LinearLayout.removeAllViewsInLayout();
            }
        });
        StartEndbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensors.IsStarted = !sensors.IsStarted;
                if (sensors.IsStarted) {//started
                    StartEndbutton.setText("Stop");


                }
                if (!sensors.IsStarted) {
                    StartEndbutton.setText("Start");


                }
            }
        });

    }
    ///////

    @Override
    protected void onPause() {//to stop reading data from sensor while application is in background
        super.onPause();
        StartEndbutton.setText("Start");
        sensors.IsStarted = false;

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    ////////

}
