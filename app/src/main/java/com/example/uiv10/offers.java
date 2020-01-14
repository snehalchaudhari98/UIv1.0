package com.example.uiv10;

import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class offers extends AppCompatActivity {
    Button closeBtn,popBtn;
    LinearLayout mypopup , overbox;
    ImageView ilogo;
    Animation fromsmall ,Fromnothing,forlogo ,togo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        closeBtn=findViewById(R.id.closeBtn);
        popBtn=findViewById(R.id.openBtn);

        mypopup=findViewById(R.id.mypopup);
        overbox=findViewById(R.id.overbook);

        ilogo=findViewById(R.id.ilogo);
        fromsmall= AnimationUtils.loadAnimation(this,R.anim.fromsmall);
        Fromnothing= AnimationUtils.loadAnimation(this,R.anim.fromsmall);
        forlogo= AnimationUtils.loadAnimation(this,R.anim.forlogo);
        togo= AnimationUtils.loadAnimation(this,R.anim.togo);

        mypopup.setAlpha(0);
        overbox.setAlpha(0);
        ilogo.setVisibility(View.GONE);

        popBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ilogo.setVisibility(View.VISIBLE);
                ilogo.startAnimation(forlogo);
                overbox.setAlpha(1);
                mypopup.setAlpha(1);
                mypopup.startAnimation(fromsmall);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overbox.startAnimation(togo);
                mypopup.startAnimation(togo);
                ilogo.startAnimation(togo);
                ilogo.setVisibility(View.GONE);

                ViewCompat.animate(mypopup).setStartDelay(1000).alpha(0).start();
                ViewCompat.animate(overbox).setStartDelay(1000).alpha(0).start();


            }
        });


    }
}
