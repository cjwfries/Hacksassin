package com.group1.util;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public abstract class MyActivity extends Activity{
	protected NfcUtil nfcUtil;
	protected HelperMethods util;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		nfcUtil = NfcUtil.getInstance();	
		util = HelperMethods.getInstance();
	}
	
}
