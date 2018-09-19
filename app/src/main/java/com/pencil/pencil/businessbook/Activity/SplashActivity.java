package com.pencil.pencil.businessbook.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.zooming_entrances.ZoomInDownAnimator;
import com.pencil.pencil.businessbook.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {
    ConstraintLayout splash_layout;
   ImageView splashIv;
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bindVariable();
//        TranslateAnimation animation = new TranslateAnimation(0.0f, 400.0f,
//                0.0f, 0.0f);
//        animation.setDuration(SPLASH_DISPLAY_LENGTH);
//        animation.setRepeatCount(1);
//// if you want infinite
//
//        animation.setRepeatMode(2);
//        animation.setFillAfter(true);
//        splashIv.startAnimation(animation);
//        new Handler().postDelayed(new Runnable(){
//            @Override
//            public void run() {
//                /* Create an Intent that will start the Menu-Activity. */
//                Intent mainIntent = new Intent(SplashActivity.this,HomeActivity.class);
//                startActivity(mainIntent);
//                finish();
//            }
//        }, SPLASH_DISPLAY_LENGTH);

        StartAnimation();
    }
    private void bindVariable(){
        splashIv=findViewById(R.id.icon);
        splash_layout=findViewById(R.id.splash_layout);
    }
    //START_ANIMATION
    private void StartAnimation() {

//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
//        animation.reset();
//        splash_layout.clearAnimation();
//        splash_layout.startAnimation(animation);
//        animation = AnimationUtils.loadAnimation(this, R.anim.translate);
//        animation.reset();


        //SAVE_PLIST_FILE
//        SharedPreferences preferences = getSharedPreferences(APP_REF , MODE_PRIVATE);
//        Boolean isUserValid = preferences.getBoolean(User.VALID , false);
//        String api_token = preferences.getString(API_TOKEN , "");

        new ZoomInDownAnimator().setTarget(splashIv).setDuration(2000).animate();


        Thread mSplashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int wait = 0;
                    while (wait < 3500) {
                        sleep(100);
                        wait += 100;
                    }




                        Intent intent = new Intent(SplashActivity.this , HomeActivity.class);
                        startActivity(intent);
                        finish();




                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mSplashTread.start();

    }

}
