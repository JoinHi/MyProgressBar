package com.zzj.myprogressbar;

import android.app.Activity;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    private ProgressBarView progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBarView) findViewById(R.id.progressbar);
    }

    int progress = 0;
    public void start(View view){
        progressBar.start();
        progress = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                while (progress<20){
                    SystemClock.sleep(100);
                    progress++;
                    progressBar.setProgressCount(progress);
                }
                SystemClock.sleep(1000);
                while (progress<40){
                    SystemClock.sleep(50);
                    progress++;
                    progressBar.setProgressCount(progress);
                }
                SystemClock.sleep(1000);
                while (progress<50){
                    SystemClock.sleep(100);
                    progress++;
                    progressBar.setProgressCount(progress);
                }
                SystemClock.sleep(1000);
                progressBar.loadingFail();
            }
        }).start();
    }
}
