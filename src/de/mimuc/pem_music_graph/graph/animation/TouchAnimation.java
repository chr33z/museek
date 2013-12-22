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
	
	private float origRadius = 0f;
	private float maxRadius = 0f;
	private float difference = 0f;

	public TouchAnimation(GenreNode node, int duration){
		this.node = node;
		this.origRadius = node.radius;
		this.duration = duration;
		
		this.difference = origRadius * 0.2f;
	}

	@Override
	protected void animate(long time) {
		float newRadius;
		
		if(time <= duration / 2){
			newRadius = (float) EaseFunction.easeOutQuad(time, origRadius, difference, duration);
			maxRadius = newRadius;
			
			// check for "overshoot"
			if(newRadius > origRadius + difference) newRadius = origRadius + difference;
			
			node.radius = newRadius;
		} else {
			maxRadius = origRadius + difference;
			newRadius = (float) EaseFunction.easeInQuad(time, maxRadius, -difference, duration);
			
			// check for "overshoot"
			if(newRadius < origRadius) newRadius = origRadius;
			
			node.radius = newRadius;
		}
	}
}
