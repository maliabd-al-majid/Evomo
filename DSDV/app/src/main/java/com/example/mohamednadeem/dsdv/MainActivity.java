package com.example.mohamednadeem.dsdv;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import 	android.util.Log;
public class MainActivity extends Activity {
    static int i = 1;
    TouchImageView img ;
    Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        img = new TouchImageView(this);
        img.setMaxZoom(4f);
        setContentView(img);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (i == 2) i = 1;

                new DownloadImageTask(img)
                        .execute("http://" + Nums.IP + ":" + Nums.PORT + "/image/get.php?id=" + i);// 10.0.3.2 ---- 192.168.137.1
                i++;

            }
        }, 0, 20);
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        TouchImageView bmImage;

        public DownloadImageTask(TouchImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
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
    @Override
    public void onResume(){
        super.onResume();
        //timer.cancel();
        /*timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(i==2) i =1;

                new DownloadImageTask(img)
                        .execute("http://"+Nums.IP+":"+Nums.PORT+"/image/get.php?id="+i);// 10.0.3.2 ---- 192.168.137.1
                i++;

            }
        }, 0, 20);*/
       // finish();
    }
    /*private class ReloadImageWorkker extends AsyncTask<Void ,Void,Void>{
        protected Void doInBackground(Void... params){
            new java.util.Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                            .execute("http://10.0.3.2:8080/image/get.php?id=1");
                }
            }, 0, 200);
            return null;
        }
    }*/
}
