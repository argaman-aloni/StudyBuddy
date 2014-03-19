package com.technion.studybuddy.Views;

import android.app.Activity;
import android.os.Bundle;

public abstract class StudyBuddyActivity extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.technion.coolie.CoolieActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// DataStore.initHelper(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// DataStore.destroyHelper();
	}

}
