package com.augusto.mymediaplayer;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PlaylistActivity extends Activity {
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setListAdapter(adapter);
        TextView textview = new TextView(this);
        textview.setText("This is the playlist tab");
        setContentView(textview);
    }


}
