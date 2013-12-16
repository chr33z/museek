package de.mimuc.pem_music_graph.graph;

import de.mimuc.pem_music_graph.R;
import de.mimuc.pem_music_graph.R.layout;
import de.mimuc.pem_music_graph.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

/**
 * Music Graph Activity
 * 
 * @author Christopher Gebhardt
 *
 */
public class MusicGraph extends Activity {
	
	MusicGraphView musicGraphView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		musicGraphView = new MusicGraphView(this);
		setContentView(musicGraphView);
		
		musicGraphView.onThreadResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.graph_test, menu);
		return true;
	}
	
	protected void onPause(){
		super.onPause();
		
		musicGraphView.onThreadPause();
	}

}
