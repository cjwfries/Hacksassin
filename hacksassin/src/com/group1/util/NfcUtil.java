package com.group1.util;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

public class NfcUtil {
	static NfcUtil instance;
	
	public static NfcUtil getInstance()
	{
		if(instance == null)
		{
			instance = new NfcUtil();
		}
		
		return instance;
	}
	
	public NdefMessage makeNdefMessage(String msg)
	{
		byte[] textBytes = msg.getBytes();
		NdefRecord textRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				"text/plain".getBytes(), new byte[] {}, textBytes);
		return new NdefMessage(new NdefRecord[] {textRecord});
	}
	
}
