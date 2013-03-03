package com.group1.hacksassin;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainMenu extends Activity {
	String _playerName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		Bundle b = getIntent().getExtras();
		_playerName = b.getString("name");
		TextView name_tv = (TextView) findViewById(R.id.main_menu_name);
		name_tv.setText(_playerName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_profile, menu);
		return true;
	}

	public void onCreateGameBtnClick(View v) {
		Intent i = new Intent(MainMenu.this, CreateNewGame.class);
		startActivity(i);
	}

	public void onJoinGameBtnClick(View v) {
		Intent i = new Intent(MainMenu.this, JoinGame.class);
		startActivity(i);
	}

	public void onDeleteProfileBtnClick(View v) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setMessage(
				"Are you sure you want to delete your profile?")
				.setCancelable(false)
				.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								File profileFile = MainMenu.this.getFileStreamPath("profile.txt");
								if (profileFile.exists()) {
									profileFile.delete();
								}
								
								Intent i = new Intent(MainMenu.this, CreateProfile.class);
								startActivity(i);

							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alertD = alert.create();

		alertD.show();
	}

}
