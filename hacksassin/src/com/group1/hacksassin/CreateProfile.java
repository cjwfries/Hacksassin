package com.group1.hacksassin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.group1.util.HttpClient;
import com.group1.util.MyActivity;

public class CreateProfile extends MyActivity {
	final String TAG = "CreateProfile";
	final int DEFAULT_BUFFER_SIZE = 256;
	final String URL = "http://hacksassin-logiebear.dotcloud.com/profile/create/";

	String _name;
	String _password = "test";
	String _id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_profile);

		File profileFile = this.getFileStreamPath("profile.txt");
		if (profileFile.exists()) {
			try {
				// Reading the file back...

				/*
				 * We have to use the openFileInput()-method the ActivityContext
				 * provides. Again for security reasons with openFileInput(...)
				 */

				FileInputStream fIn = openFileInput("profile.txt");
				InputStreamReader isr = new InputStreamReader(fIn);

				/*
				 * Prepare a char-Array that will hold the chars we read back
				 * in.
				 */
				char[] inputBuffer = new char[DEFAULT_BUFFER_SIZE];

				// Fill the Buffer with data from the file

				isr.read(inputBuffer);

				// Transform the chars to a String
				String inputStr = new String(inputBuffer);
				String[] components = inputStr.split(";;;");
				if (!components[1].equals("null")) {
					Intent i = new Intent(CreateProfile.this, MainMenu.class);
					Bundle b = new Bundle();
					b.putString("name", components[0]);
					b.putString("id", components[1]);
					i.putExtras(b);
					startActivity(i);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.create_profile, menu); return true; }
	 */

	public void onCreateProfileBtnClick(View v) {
		// todo: input sanity check

		EditText name = (EditText) findViewById(R.id.enter_name);
		_name = name.getText().toString();
		if (_name.isEmpty()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setMessage("Please enter a name.")
					.setCancelable(false)
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alertD = alert.create();

			alertD.show();
		} else {

			// Check for Internet connection
			if (!util.isNetworkAvailable(getApplicationContext())) {
				util.ShowNoNetworkAlert(this);
			} else {
				// JSON object to hold the information, which is sent to the
				// server
				JSONObject jsonObjSend = new JSONObject();

				try {
					// Add key/value pairs
					jsonObjSend.put("username", _name);
					jsonObjSend.put("password", _password);
					/*
					 * // Add a nested JSONObject (e.g. for header information)
					 * JSONObject header = new JSONObject();
					 * header.put("deviceType", "Android"); // Device type
					 * header.put("deviceVersion", "2.0"); // Device OS version
					 * header.put("language", "es-es"); // Language of the
					 * Android // client jsonObjSend.put("header", header);
					 */

					// Output the JSON object we're sending to Logcat:
					Log.i(TAG, jsonObjSend.toString(2));

				} catch (JSONException e) {
					e.printStackTrace();
				}

				// Send the HttpPostRequest and receive a JSONObject in return
				JSONObject jsonObjRecv = HttpClient.SendHttpPost(URL,
						jsonObjSend);

				/*
				 * From here on do whatever you want with your JSONObject, e.g.
				 * 1) Get the value for a key: jsonObjRecv.get("key"); 2) Get a
				 * nested JSONObject: jsonObjRecv.getJSONObject("key") 3) Get a
				 * nested JSONArray: jsonObjRecv.getJSONArray("key")
				 */

				try {
					_id = jsonObjRecv.getString("user_id");
					Log.i(TAG, "ID: " + _id);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try {
				// catches IOException below

				/*
				 * We have to use the openFileOutput()-method the
				 * ActivityContext provides, to protect your file from others
				 * and This is done for security-reasons. We chose
				 * MODE_WORLD_READABLE, because we have nothing to hide in our
				 * file
				 */
				FileOutputStream fOut = openFileOutput("profile.txt",
						MODE_WORLD_READABLE);
				OutputStreamWriter osw = new OutputStreamWriter(fOut);

				// Write the string to the file
				osw.write(_name + ";;;" + _id + ";;;");

				/*
				 * ensure that everything is really written out and close
				 */
				osw.flush();
				osw.close();

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			Intent i = new Intent(CreateProfile.this, MainMenu.class);
			Bundle b = new Bundle();
			b.putString("name", _name);
			b.putString("id", _id);
			i.putExtras(b);
			startActivity(i);
		}
	}
	/*
	 * private class DownloadTask extends AsyncTask<String, Void, Object> {
	 * 
	 * protected Void doInBackground(final String... args) { // JSON object to
	 * hold the information, which is sent to the server JSONObject jsonObjSend
	 * = new JSONObject();
	 * 
	 * try { // Add key/value pairs jsonObjSend.put("username", _name);
	 * jsonObjSend.put("password", _password);
	 * 
	 * // Add a nested JSONObject (e.g. for header information) JSONObject
	 * header = new JSONObject(); header.put("deviceType","Android"); // Device
	 * type header.put("deviceVersion","2.0"); // Device OS version
	 * header.put("language", "es-es"); // Language of the Android client
	 * jsonObjSend.put("header", header);
	 * 
	 * // Output the JSON object we're sending to Logcat: Log.i(TAG,
	 * jsonObjSend.toString(2));
	 * 
	 * } catch (JSONException e) { e.printStackTrace(); }
	 * 
	 * // Send the HttpPostRequest and receive a JSONObject in return JSONObject
	 * jsonObjRecv = HttpClient.SendHttpPost(URL, jsonObjSend);
	 * 
	 * 
	 * 
	 * try { _id = jsonObjRecv.getString("user_id"); Log.i(TAG, "ID: " + _id); }
	 * catch (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * return null; }
	 * 
	 * protected void onPostExecute(Object result) { }
	 * 
	 * }
	 */
}