package com.example.mohamednadeem.dsdv;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by vamsikrishna on 12-Feb-15.
 */
public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent = new Intent(Splash.this,IPandPort.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        finish();
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
    /*@Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        // Get the Camera instance as the activity achieves full user focus
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }*/

}
