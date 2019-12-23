package com.example.mohamednadeem.movieapp;
import android.content.Intent;
import android.app.Fragment;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements handler {
boolean is_two_pane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        FrameLayout panel2=(FrameLayout) findViewById(R.id.flpanel_two);
        if(panel2==null){
            is_two_pane=false;
        }
        else {
            is_two_pane=true;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(null==savedInstanceState){
            MainActivityFragment mainActivityFragment=new MainActivityFragment();
            mainActivityFragment.setmovielistener(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flpanel_one,mainActivityFragment)
                    .commit();
            }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setMovie(Movie movie) {
        if(is_two_pane){
            DetailActivityFragment detailActivityFragment=new DetailActivityFragment();
            Bundle extras=new Bundle();
            extras.putParcelable("Movie",movie);
            detailActivityFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().replace(R.id.flpanel_two,detailActivityFragment).commit();
        }else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra("Movie", movie);
            startActivity(intent);
        }
    }
}
