package de.mimuc.pem_music_graph.graph.animation;

import android.util.Log;
import de.mimuc.pem_music_graph.graph.GenreNode;

public class ShrinkAnimation extends GraphAnimation {

	private GenreNode node;
	
	private float origRadius;
	private float maxRadius;
	
	public ShrinkAnimation(GenreNode node, long duration){
		this.duration = duration;
		this.node = node;
		this.origRadius = node.radius;
	}
	
	public ShrinkAnimation(GenreNode node, long duration, long delay){
		this.duration = duration;
		this.delay = delay;
		this.node = node;
		this.origRadius = node.radius;
	}
	
	@Override
	protected void animate(long time) {
		// fade
		double visibility = EaseFunction.easeInQuad(time, 1, -1, duration);
		node.setVisibility(visibility);
		
		//shrink
		float newRadius;
		if(time <= duration / 10){
			newRadius = (float) EaseFunction.easeOutQuad(
					time, origRadius, origRadius + origRadius * 0.2f, duration);
			maxRadius = newRadius;
			node.radius = newRadius;
		} else {
			newRadius = (float) EaseFunction.easeInQuad(time, maxRadius, -maxRadius, duration);
			node.radius = newRadius;
		}
		
	}

	
}
