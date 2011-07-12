package com.augusto.mymediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Home extends Activity implements OnClickListener {
	
	private Button playQueue;
	private Button browseAll;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		playQueue = (Button) findViewById(R.id.play_queue);
		browseAll = (Button) findViewById(R.id.browse_all);
		
		playQueue.setOnClickListener(this);
		browseAll.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	public void onClick(View view) {
		Class<?> activityClass = null;
		
		switch(view.getId()) {
		case R.id.play_queue:
			activityClass = PlayQueueActivity.class;
			break;
		case R.id.browse_all:
			activityClass = BrowseActivity.class;
			break;
		}
		
		
		Intent newActivity = new Intent(this, activityClass);
		startActivity(newActivity);
	}
	

}
