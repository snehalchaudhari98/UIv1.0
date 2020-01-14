package com.example.uiv10;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;


public class MainActivity extends AppCompatActivity {

    Button pop;
    SpaceNavigationView navigationView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling,menu);


        PrefManager prefManager= new PrefManager(getApplicationContext());
        if(prefManager.isFirstTimeLaunch()){
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
        finish();
        }


        return super.onCreateOptionsMenu(menu);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // toolbar.inflateMenu(R.menu.menu_scrolling);

        pop=findViewById(R.id.popto);

        navigationView=findViewById(R.id.space);

        navigationView.initWithSaveInstanceState(savedInstanceState);
        navigationView.addSpaceItem(new SpaceItem("HOME", R.drawable.ic_home_black_24dp));
      //  navigationView.addSpaceItem(new SpaceItem("Voice", R.drawable.ic_keyboard_voice_black_24dp));
        navigationView.addSpaceItem(new SpaceItem("Setting", R.drawable.ic_settings_applications_black_24dp));

        navigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Toast.makeText(MainActivity.this,"onCentreButtonClick", Toast.LENGTH_SHORT).show();
                 navigationView.setCentreButtonSelectable(true);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(MainActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.profile) {
                    Toast.makeText(MainActivity.this, "Bohot kharab PROFILE hai aapki", Toast.LENGTH_SHORT).show();
                } else if (menuItem.getItemId() == R.id.settings) {
                    Toast.makeText(MainActivity.this, "Take you toward setting", Toast.LENGTH_SHORT).show();
                } else if (menuItem.getItemId() == R.id.help) {
                    Toast.makeText(MainActivity.this, "Aapko help lagegi", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });


        pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,offers.class);
                startActivity(intent);
            }
        });
    }


}
