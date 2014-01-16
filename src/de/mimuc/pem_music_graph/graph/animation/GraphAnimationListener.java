package de.mimuc.pem_music_graph.graph.animation;

/**
 * This interface provide Callbacks for the Animations
 * 
 * @author Christopher Gebhardt
 *
 */
public interface GraphAnimationListener {

	/**
	 * Call this when the animation finished
	 */
	public void animationFinished(String tag);
	
	/**
	 * Call this when the animation is canceled
	 */
	public void animationCanceled(String tag);
}
