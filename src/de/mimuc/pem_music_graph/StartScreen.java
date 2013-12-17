package de.mimuc.pem_music_graph;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class StartScreen extends Activity {

	private static final String TAG = StartScreen.class.getName();

	Button btnGraph;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startscreen);

		btnGraph = (Button) findViewById(R.id.btn_combined_view);
		
		btnGraph.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch(v.getId()){
				case R.id.btn_combined_view:
					startActivity(new Intent(getApplicationContext(), CombinedView.class));
					break;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
