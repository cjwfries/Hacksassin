package com.group1.hacksassin;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.group1.util.HttpClient;
import com.group1.util.MyActivity;

public class MainMenu extends MyActivity {
	final String URL = "http://hacksassin-logiebear.dotcloud.com/profile/";
	final String TAG = "MainMenu";
	String _playerName;
	String _id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		
		Bundle b = getIntent().getExtras();
		_playerName = b.getString("name");
		_id = b.getString("id");
		TextView name_tv = (TextView) findViewById(R.id.main_menu_name);
		name_tv.setText(_playerName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_profile, menu);
		return true;
	}

	// Bundle: name, type, id, gameId
	public void onCreateGameBtnClick(View v) {
		Intent i = new Intent(MainMenu.this, CreateNewGame.class);
		Bundle b = new Bundle();
		b.putString("name", _playerName);
		b.putString("type", "host");
		b.putString("id", _id);
		
		// =======================================================
		
		// Check for Internet connection
		if (!util.isNetworkAvailable(getApplicationContext())) {
			util.ShowNoNetworkAlert(this);
		} else {
			// Send the HttpPostRequest and receive a JSONObject in return
			String objRecv = util.readJSONQuery(URL + _id + "/creategame/", TAG); 


			try {
				JSONObject jsonObjRecv = new JSONObject(objRecv);
				String gameId = jsonObjRecv.getString("game_id");
				b.putString("gameId", gameId);
				Log.i(TAG, "Game ID: " + gameId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// =======================================================
		
		i.putExtras(b);
		startActivity(i);
	}

	public void onJoinGameBtnClick(View v) {
		Intent i = new Intent(MainMenu.this, CreateNewGame.class);
		Bundle b = new Bundle();
		b.putString("name", _playerName);
		b.putString("type", "client");
		b.putString("id", _id);
		i.putExtras(b);
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
