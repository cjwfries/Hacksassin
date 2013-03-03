package com.group1.hacksassin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateProfile extends Activity {
	final String TAG = "CreateProfile";
	final int DEFAULT_BUFFER_SIZE = 256;

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
				String name = new String(inputBuffer);
				name = name.split(";;;")[0];
				Log.d(TAG, name);
				
				Intent i = new Intent(CreateProfile.this, MainMenu.class);
				Bundle b = new Bundle();
				b.putString("name", name);
				i.putExtras(b);
				startActivity(i);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_profile, menu);
		return true;
	}
	*/

	public void onCreateProfileBtnClick(View v) {
		// todo: input sanity check

		EditText name = (EditText) findViewById(R.id.enter_name);
		String name_text = name.getText().toString();
		if (name_text.isEmpty()) {
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
				osw.write(name_text + ";;;");

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
			b.putString("name", name_text);
			i.putExtras(b);
			startActivity(i);
		}
	}

}
