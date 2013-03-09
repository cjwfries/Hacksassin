package com.group1.util;

import android.app.Activity;
import android.os.Bundle;

public abstract class MyActivity extends Activity{
	protected NfcUtil nfcUtil;
	protected HelperMethods util;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nfcUtil = NfcUtil.getInstance();	
		util = HelperMethods.getInstance();
	}
	
}
