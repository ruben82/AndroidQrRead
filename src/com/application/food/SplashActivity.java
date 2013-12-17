package com.application.food;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Window;
import android.widget.TextView;

public class SplashActivity extends Activity {
    protected boolean _active = true;
    protected int _splashTime = 2900;
    private boolean isTablet = false;
    private TextView textView;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        textView=(TextView)findViewById(R.id.detailCop);
        textView.setSelected(true);
        isTablet = getResources().getBoolean(R.bool.isTablet);
        if (!isTablet)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);
                        if (_active) {
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    goToFirstage();
                    finish();
                }
            }
        };
        splashTread.start();
    }

    private void goToFirstage() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }



    @Override
    public void onStart() {
        super.onStart();
    }

  /*  @Override
    public void onResume() {
        super.onResume();
        System.out.println("Resume---->" + this.getLocalClassName() + " -----  BACK CLASS ---->" + getIntent().getStringExtra(ActivityUtils.BACK_ACTIVITY));
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("Pause---->" + this.getLocalClassName() + " -----  BACK CLASS ---->" + getIntent().getStringExtra(ActivityUtils.BACK_ACTIVITY));
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("Stop---->" + this.getLocalClassName() + " -----  BACK CLASS ---->" + getIntent().getStringExtra(ActivityUtils.BACK_ACTIVITY));
    }*/
}
