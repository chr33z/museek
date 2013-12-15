package de.mimuc.pem_music_graph;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class StartScreen extends Activity {

	ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startscreen);
		
		// get the listview
        expListView = (ExpandableListView) findViewById(R.id.expandableListView1);
        listAdapter = new ExpandableListAdapter2(this);
        // setting list adapter
        expListView.setAdapter(listAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
