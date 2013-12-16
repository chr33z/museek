package de.mimuc.pem_music_graph;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.View;

public class CombinedView extends Activity {
	
	private Fragment fragmentGraph;
	
	private Fragment fragmentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_combined_view);
		
		fragmentList = 
		
		fragmentGraph = getFragmentManager().
		fragmentList = findViewById(R.id.fragment_list);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.combined_view, menu);
		return true;
	}

}
