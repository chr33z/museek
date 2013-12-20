package de.mimuc.pem_music_graph.graph.animation;

/**
 * Just wait for the specified time to pass
 * 
 * @author Christopher Gebhardt
 *
 */
public class IdleAnimation extends GraphAnimation {

	public IdleAnimation(long duration){
		this.duration = duration;
	}
	
	@Override
	protected void animate(long timeStep) {
		// do nothing because... idle
	}
}
