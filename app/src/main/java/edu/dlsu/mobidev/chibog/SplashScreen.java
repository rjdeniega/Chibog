package edu.dlsu.mobidev.chibog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread myThread = new Thread(){
            @Override
            public void run() {
                try {
                    //thread sleeps at 2s
                    sleep(1500);
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
