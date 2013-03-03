package com.group1.hacksassin;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.group1.util.MyActivity;

public class CreateNewGame extends MyActivity {
	final String TAG = "CreateNewGame";
	String _clientMsg = "Entered into game";

	NfcAdapter _NfcAdapter;
	PendingIntent _NfcPendingIntent;
	IntentFilter[] _WriteTagFilters;
	IntentFilter[] _NdefExchangeFilters;

	NdefMessage _msgToClients;

	boolean _resumed = false;
	boolean _writeMode = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_game);

		// TODO: display players that enter

		_NfcAdapter = NfcAdapter.getDefaultAdapter(this);
		_NfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndefDetected = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);

		try {
			ndefDetected.addDataType("text/plain");
		} catch (MalformedMimeTypeException e) {
		}

		_NdefExchangeFilters = new IntentFilter[] { ndefDetected };

		IntentFilter tagDetected = new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED);
		_WriteTagFilters = new IntentFilter[] { tagDetected };

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_profile, menu);
		return true;
	}

	public void onBeginGameBtnClick(View v) {
		Intent i = new Intent(CreateNewGame.this, Target.class);
		startActivity(i);
	}

	@Override
	protected void onResume() {
		super.onResume();
		_resumed = true;

		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			NdefMessage[] messages = getNdefMessages(getIntent());
			byte[] payload = messages[0].getRecords()[0].getPayload();
			// TODO: keep track of received info
			// setNoteBody(new String(payload));
			setIntent(new Intent());

		}
		enableNdefExchangeMode();
	}

	@Override
	protected void onPause() {
		super.onPause();
		_resumed = false;
		_NfcAdapter.disableForegroundNdefPush(this);
	}

	private void enableNdefExchangeMode() {
		_NfcAdapter.enableForegroundNdefPush(CreateNewGame.this,
				nfcUtil.makeNdefMessage(_clientMsg));
		_NfcAdapter.enableForegroundDispatch(this, _NfcPendingIntent,
				_NdefExchangeFilters, null); // listening, if detect anything,
												// call onNewIntent()
	}

	// received something
	@Override
	protected void onNewIntent(Intent intent) {
		if (!_writeMode
				&& NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			NdefMessage[] msgs = getNdefMessages(intent);
			Log.d(TAG, new String(msgs[0].getRecords()[0].getPayload()));

			new AlertDialog.Builder(this).setTitle(
					new String(msgs[0].getRecords()[0].getPayload())).show();
		}

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

}
