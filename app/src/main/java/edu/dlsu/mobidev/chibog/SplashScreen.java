package edu.dlsu.mobidev.chibog;

import android.content.Intent;
import android.os.Build;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {
    public ImageView splashImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Window w = getWindow(); // in Activity's onCreate() for instance
        w.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));


//        splashImage = (ImageView) findViewById(R.id.splashimage);
//        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(getResources(), src);
//        dr.setCornerRadius(30);
//        splashImage.setImageDrawable(dr);

        Thread myThread = new Thread(){
            @Override
            public void run() {
                try {
                    //thread sleeps at 2s
                    sleep(2000);
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);

                    //go back to main activity
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        //start the thread
        myThread.start();
    }
}
