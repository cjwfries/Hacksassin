package com.group1.hacksassin;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.group1.util.MyActivity;

public class EndGame extends MyActivity {
	String _playerName;
	String _id;

	boolean isWinner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_end_game);

		Bundle b = getIntent().getExtras();
		_playerName = b.getString("name");
		_id = b.getString("id");
		isWinner = b.getBoolean("victor");
		
		TextView text = (TextView) findViewById(R.id.end_game_text);

		if (isWinner) {
			ImageView image = (ImageView) findViewById(R.id.end_game_image);
			image.setImageResource(R.drawable.victory);
			text.setText("Victory");
		}
		else
		{
			text.setText("Game Over");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_profile, menu);
		return true;
	}

	public void onReturnMenuBtnClick(View view) {
		Intent i = new Intent(EndGame.this, MainMenu.class);
		Bundle b = new Bundle();
		b.putString("name", _playerName);
		b.putString("id", _id);
		i.putExtras(b);
		startActivity(i);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		//replaces the default 'Back' button action
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			Intent i = new Intent(EndGame.this, MainMenu.class);
			Bundle b = new Bundle();
			b.putString("name", _playerName);
			b.putString("id", _id);
			i.putExtras(b);
			startActivity(i);
		}
		return true;
	}

}
