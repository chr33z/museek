package de.mimuc.pem_music_graph;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ExpandableListView;
import de.mimuc.pem_music_graph.graph.MusicGraphView;

public class CombinedView extends Activity {
	
	private MusicGraphView graphView;
	
	private ExpandableListView locationListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_combined_view);
		
		graphView = (MusicGraphView) findViewById(R.id.graph_view);
		graphView.onThreadResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.combined_view, menu);
		return true;
	}

}
