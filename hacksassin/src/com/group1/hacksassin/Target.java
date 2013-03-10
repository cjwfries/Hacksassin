package com.group1.hacksassin;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.group1.util.MyActivity;

public class Target extends MyActivity {
	private static final String TAG = "CreateNewGame";
	NfcAdapter mNfcAdapter;
	// EditText mNote;

	final private String URL = "http://hacksassin-logiebear.dotcloud.com/game/";

	String mPlayerName;
	boolean isKillMode = false;

	PendingIntent mNfcPendingIntent;
	IntentFilter[] mWriteTagFilters;
	IntentFilter[] mNdefExchangeFilters;

	String _gameId;
	String _userId;

	String mMsg;

	String _targetId = "-1";
	String _targetName = "Target";
	String _killCount = "0";
	String _playerCount = "-1";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_target);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		Bundle b = getIntent().getExtras();
		mPlayerName = b.getString("playerName");
		_userId = b.getString("userId");
		_gameId = b.getString("gameId");
		mMsg = mPlayerName;

		// Handle all of our received NFC intents in this activity.
		mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Intent filters for reading a note from a tag or exchanging over p2p.
		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType("text/plain");
		} catch (MalformedMimeTypeException e) {
		}
		mNdefExchangeFilters = new IntentFilter[] { ndefDetected };
		
		getGameStatus();

	}

	// returns whether or not could reach server
	protected boolean getGameStatus() {
		if (!util.isNetworkAvailable(getApplicationContext())) {
			util.ShowNoNetworkAlert(this);
		} else {
			String objRecv = util.readJSONQuery(URL + _gameId + "/status/"
					+ _userId, TAG);

			try {
				JSONObject jsonRet = new JSONObject(objRecv);
				if (jsonRet.getString("dead").equals("true")) {
					// TODO: Game over
					toast("Game Over.");
				}
				TextView target = (TextView) findViewById(R.id.target_title);
				target.setText(jsonRet.getString("target_username") + "/"
						+ jsonRet.getString("target_id"));
				TextView playersRemain = (TextView) findViewById(R.id.players_remaining);
				playersRemain.setText("Players Remaining: "
						+ jsonRet.getString("player_count"));
				TextView playerKlls = (TextView) findViewById(R.id.player_kills);
				playerKlls.setText("Kills: " + jsonRet.getString("kill_count"));

				return true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return false;
	}

	public void setKillMode(View view) {
		if (!isKillMode) {
			isKillMode = true;
			RelativeLayout layout = (RelativeLayout) findViewById(R.id.target_layout);
			layout.setBackgroundColor(getResources().getColor(R.color.red));
			TextView target = (TextView) findViewById(R.id.target_title);
			target.setTextColor(getResources().getColor(R.color.grey));
		}
	}

	protected void turnOffKillMode() {
		isKillMode = false;
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.target_layout);
		layout.setBackgroundColor(getResources().getColor(R.color.black));
		TextView target = (TextView) findViewById(R.id.target_title);
		target.setTextColor(getResources().getColor(R.color.holo_blue));
	}
	
	public void onGameStatusClick(View view) {
		getGameStatus();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Sticky notes received from Android
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			setIntent(new Intent()); // Consume this intent.
		}
		enableNdefExchangeMode();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mNfcAdapter.disableForegroundNdefPush(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// NDEF exchange mode
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			NdefMessage[] msgs = getNdefMessages(intent);
			String received = new String(msgs[0].getRecords()[0].getPayload());
			Toast.makeText(this, received, Toast.LENGTH_SHORT).show();
			if (isKillMode) {
				if (!util.isNetworkAvailable(getApplicationContext())) {
					util.ShowNoNetworkAlert(this);
				} else {
					util.readJSONQuery(
							URL + _gameId + "/reportkill/" + _userId, TAG);
					turnOffKillMode();

					getGameStatus();
				}
			} else {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getGameStatus();
			}

		}
	}

	private NdefMessage getMsgAsNdef(String msg) {
		byte[] textBytes = msg.getBytes();
		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"text/plain".getBytes(), new byte[] {}, textBytes);
		return new NdefMessage(new NdefRecord[] { textRecord });
	}

	NdefMessage[] getNdefMessages(Intent intent) {
		// Parse the intent
		NdefMessage[] msgs = null;
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
		} else {
			Log.d(TAG, "Unknown intent.");
			finish();
		}
		return msgs;
	}

	private void enableNdefExchangeMode() {
		mNfcAdapter.enableForegroundNdefPush(this, getMsgAsNdef(mMsg));
		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
				mNdefExchangeFilters, null);
	}

	boolean writeTag(NdefMessage message, Tag tag) {
		int size = message.toByteArray().length;

		try {
			Ndef ndef = Ndef.get(tag);
			if (ndef != null) {
				ndef.connect();

				if (!ndef.isWritable()) {
					toast("Tag is read-only.");
					return false;
				}
				if (ndef.getMaxSize() < size) {
					toast("Tag capacity is " + ndef.getMaxSize()
							+ " bytes, message is " + size + " bytes.");
					return false;
				}

				ndef.writeNdefMessage(message);
				toast("Wrote message to pre-formatted tag.");
				return true;
			} else {
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null) {
					try {
						format.connect();
						format.format(message);
						toast("Formatted tag and wrote message");
						return true;
					} catch (IOException e) {
						toast("Failed to format tag.");
						return false;
					}
				} else {
					toast("Tag doesn't support NDEF.");
					return false;
				}
			}
		} catch (Exception e) {
			toast("Failed to write tag");
		}

		return false;
	}

	private void toast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}