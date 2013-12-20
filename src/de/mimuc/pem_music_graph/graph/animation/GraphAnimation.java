package de.mimuc.pem_music_graph.graph.animation;

public abstract class GraphAnimation {
	
	protected GraphAnimationListener callbackReceiver;

	protected long duration = 0;
	
	protected long startTime = 0;
	
	protected boolean running = false;
	
	protected String tag;
	
	/**
	 * Update the animation. When first started the System time is
	 * saved and a time step is calculated
	 * 
	 * @param time current system time
	 */
	protected void update(long time){
		if(!running){
			startTime = System.currentTimeMillis();
			running = true;
			return;
		}
		
		if(time - startTime > duration){
			animate(duration);
			callbackReceiver.animationFinished(tag);
		}
			
		animate(time - startTime);
	}
	
	/**
	 * Implement animation here. Setup and calculation of timestep is taken care of in
	 * @link{update(long time)}. Don't call this method directly!
	 * 
	 * @param time
	 */
	protected abstract void animate(long time);
	
	/**
	 * Adds a animation listener to the animation
	 * 
	 * @param callbackReceiver
	 */
	protected void setAnimationListener(GraphAnimationListener callbackReceiver){
		this.callbackReceiver = callbackReceiver;
	}
	
	/**
	 * Set the tag for the animation to identify the animation in the queue
	 * @param tag
	 */
	protected void setTag(String tag){
		this.tag = tag;
	}
}
