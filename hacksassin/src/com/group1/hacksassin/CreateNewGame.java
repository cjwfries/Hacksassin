package com.group1.hacksassin;

import java.io.IOException;
import java.util.ArrayList;

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
import android.widget.TextView;
import android.widget.Toast;

import com.group1.util.MyActivity;

//Main Menu -> Join Game (!isHost) || Create New Game (isHost)
/* TODO:
 * host-
 * - game key
 * - collect list of players in ArrayList
 * - display entered players
 * - begin: send game key and list of players to host
 * 
 * client-
 * - begin: move to target screen
 * 
 * 
 * 
 * Add Player -- http://hacksassin-logiebear.dotcloud.com/game/(game_id)/addplayer/(user_id)
 - This API takes both a (game_id) and a (user_id) and adds the user_id into the game as a player.
 - Returns -- JSON with user_id, game_id, game_started, and player_count (same as create game).

 Example: http://hacksassin-logiebear.dotcloud.com/game/1/addplayer/2/
 Returns: { 'user_id': 2,
 'game_id': 1,
 'game_started': false,
 'player_count': 2}
 */

public class CreateNewGame extends MyActivity {
	private static final String TAG = "CreateNewGame";
	NfcAdapter mNfcAdapter;
	// EditText mNote;

	private String URL_ADD_PLAYER = "http://hacksassin-logiebear.dotcloud.com/game/";
	final private String URL_START_GAME = "http://hacksassin-logiebear.dotcloud.com/game/";

	String mPlayerName;
	boolean isHost = false;

	PendingIntent mNfcPendingIntent;
	IntentFilter[] mWriteTagFilters;
	IntentFilter[] mNdefExchangeFilters;

	// h_ prefix for variables only used by host
	ArrayList<String> h_players;
	String h_listPlayers = "Players currently in game:\n";

	String _gameId;
	String _userId;

	String mMsg;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_game);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		Bundle b = getIntent().getExtras();
		mPlayerName = b.getString("name");
		_userId = b.getString("id");
		if ((b.getString("type")).equals("host")) {
			isHost = true;
		}

		if (!isHost) {
			setTextBody(getString(R.string.join_game_text));
			mMsg = _userId + ";;;" + mPlayerName;
		} else {
			h_players = new ArrayList<String>();
			h_players.add(mPlayerName);
			_gameId = b.getString("gameId");
			mMsg = _gameId + ";;;" + mPlayerName;
			
			setTitleText("Game ID: " + _gameId);
		}

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

	public void onBeginGameBtnClick(View view) {
		Intent i = new Intent(CreateNewGame.this, Target.class);
		Bundle b = new Bundle();

		if (isHost) {
			// =======================================================

			if (!util.isNetworkAvailable(getApplicationContext())) {
				util.ShowNoNetworkAlert(this);
			} else {
				String objRecv = util.readJSONQuery(URL_START_GAME + _gameId
						+ "/start/", TAG);

				try {
					JSONObject jsonRet = new JSONObject(objRecv);
					if (jsonRet.getString("game_started").equals("false")) {
						toast("Failed to create game. Try again.");
						return;
					}
					else
					{
						toast("Game creation successful");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// =======================================================

		}

		b.putString("gameId", _gameId);
		b.putString("userId", _userId);
		b.putString("playerName", mPlayerName);
		i.putExtras(b);
		startActivity(i);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// NDEF exchange mode
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			NdefMessage[] msgs = getNdefMessages(intent);
			String received = new String(msgs[0].getRecords()[0].getPayload());
			String[] split = received.split(";;;");
			Toast.makeText(this, received, Toast.LENGTH_SHORT).show();
			if (isHost) {
				// =======================================================
				if (!h_players.contains(split[0])) {
					// Check for Internet connection
					if (!util.isNetworkAvailable(getApplicationContext())) {
						util.ShowNoNetworkAlert(this);
					} else {
						// Send the HttpPostRequest and receive a JSONObject in
						// return

						URL_ADD_PLAYER = URL_ADD_PLAYER + _gameId
								+ "/addplayer/" + split[0] + "/";
						Log.i(TAG, URL_ADD_PLAYER);
						String objRecv = util
								.readJSONQuery(URL_ADD_PLAYER, TAG);

						if (objRecv != null) {
							h_players.add(split[0]);
							h_listPlayers += split[1];
							h_listPlayers += "\n";
							setTextBody(h_listPlayers);
						}
					}

					// =======================================================
				}
			} else {
				_gameId = split[0];
				setTextBody("Successfully entered in\n" + split[1] + "'s game");
				setTitleText("Game ID: " + _gameId);
			}

		}
	}

	private void setTextBody(String body) {
		TextView tv = (TextView) findViewById(R.id.start_game_text);
		tv.setText(body);
	}

	private void setTitleText(String body) {
		TextView tv = (TextView) findViewById(R.id.cng_game_id_text);
		tv.setText(body);
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