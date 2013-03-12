package com.group1.hacksassin;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import com.facebook.*;
import com.facebook.model.*;
import android.util.Log;


public class CreateProfile extends Activity {
	final String TAG = "CreateProfile";
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_create_profile);

    // start Facebook Login
    Session.openActiveSession(this, true, new Session.StatusCallback() {

      // callback when session changes state
      @Override
      public void call(Session session, SessionState state, Exception exception) {
        if (session.isOpened()) {

          // make request to the /me API
          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

            // callback after Graph API response with user object
            @Override
            public void onCompleted(GraphUser user, Response response) {
              if (user != null) {
            	String name = user.getName();
  				name = name.split(";;;")[0];
  				Log.d(TAG, name);
  				
  				Intent i = new Intent(CreateProfile.this, MainMenu.class);
  				Bundle b = new Bundle();
  				b.putString("name", name);
  				i.putExtras(b);
  				startActivity(i);
              }
            }
          });
        }
      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
  }

}