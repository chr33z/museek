package de.mimuc.pem_music_graph.graph.animation;

/**
 * Several ease-in and ease-out functions taken from
 * gizma.com/easing/
 * 
 * @author Christopher Gebhardt
 *
 */
public class EaseFunction {

	/**
	 * Linear interpolation
	 * @param time
	 * @param start
	 * @param change
	 * @param duration
	 * @return
	 */
	public static double linearTween(long time, double start, double change, long duration) {
    	return change*((double)time)/((double)duration) + start;
    };
    
    /**
     * quadratic easing in - Acceleration from zero
     * @param time
     * @param start
     * @param change
     * @param duration
     * @return
     */
    public static double easeInQuad(long time, double start, double change, long duration) {
    	double tmp = (double)time / (double)duration;
    	return change*tmp*tmp + start;
    };
    
    /**
     * quadratic easing out - decelerating to zero velocity
     * @param time
     * @param start
     * @param change
     * @param duration
     * @return
     */
    public static double easeOutQuad(long time, double start, double change, long duration) {
    	double tmp = (double)time / (double)duration;
    	return -change*tmp*(tmp - 2) + start;
    };
    
    /**
     * quadratic easing in and out - acceleration then deceleration
     * @param time
     * @param start
     * @param change
     * @param duration
     * @return
     */
    public static double easeInOutQuad(long time, double start, double change, long duration) {
    	time /= duration/2;
    	if (time < 1) return change/2*time*time + start;
    	time--;
    	return -change/2 * (time*(time-2) - 1) + start;
    };

}
