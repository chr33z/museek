package de.mimuc.pem_music_graph.graph.animation;

import android.util.Log;
import de.mimuc.pem_music_graph.graph.GenreNode;

/**
 * A touch animation played when a node is touched
 * @author Christopher Gebhardt
 *
 */
public class TouchAnimation extends GraphAnimation {
	
	private GenreNode node;
	
	private float radius = 0f;
	private float maxRadius = 0f;
	private float difference = 0f;

	public TouchAnimation(GenreNode node, int duration){
		this.node = node;
		this.radius = node.radius;
		this.duration = duration;
		
		this.difference = radius * 0.2f;
	}

	@Override
	protected void animate(long time) {
		float newRadius;
		
		if(time <= duration / 2){
			newRadius = (float) EaseFunction.easeOutQuad(time, radius, difference, duration);
			maxRadius = newRadius;
			node.radius = newRadius;
			Log.d("move1", newRadius+"");
		} else {
			newRadius = (float) EaseFunction.easeInQuad(time, maxRadius, -difference, duration);
			node.radius = newRadius;
			Log.d("move2", newRadius+"");
		}
	}
}
