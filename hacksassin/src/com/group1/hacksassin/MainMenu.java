package com.group1.hacksassin;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainMenu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_profile, menu);
        return true;
    }
    
    public void onCreateGameBtnClick(View v)
    {
    	Intent i = new Intent(MainMenu.this, CreateNewGame.class);
    	startActivity(i);
    }
    
    public void onJoinGameBtnClick(View v)
    {
    	Intent i = new Intent(MainMenu.this, JoinGame.class);
    	startActivity(i);
    }
    
}
