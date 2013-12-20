package de.mimuc.pem_music_graph.graph.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import android.util.Log;

/**
 * Animation Queue that supportes multiple simultanios animations.
 * The animations queues are identified by tags like "movetorootanimations"
 * 
 * @author Christopher Gebhardt
 *
 */
public class GraphAnimationQueue implements GraphAnimationListener {
	
	private static final String TAG = GraphAnimationQueue.class.getSimpleName();
	
	/**
	 * 
	 */
	private static final String MAIN_QUEUE = "main";
	
	/**
	 * A HashMap containing all queues
	 */
	private HashMap<String, LinkedList<GraphAnimation>> queues;
	
	public GraphAnimationQueue(){
		queues = new HashMap<String, LinkedList<GraphAnimation>>();
	}
	
	/**
	 * Update all animation queues
	 * @param time
	 */
	public void update(long time){
		
		/*
		 *  remove empty queues - store values in list first before you remove
		 *  otherwise prepare for exception
		 */
		ArrayList<String> queuesToRemove = new ArrayList<String>();
		for (Entry<String, LinkedList<GraphAnimation>> queue : queues.entrySet()) {
			if(!queue.getKey().equals(MAIN_QUEUE) && 
					queue.getValue().size() == 0){
				queuesToRemove.add(queue.getKey());
			}
		}
		for (String queue : queuesToRemove) {
			queues.remove(queue);
		}
		
		// update rest of the queues
		for (Entry<String, LinkedList<GraphAnimation>> queue : queues.entrySet()) {
			if(queue.getValue().size() > 0){
				queue.getValue().getFirst().update(time);
			} else {
				queues.remove(queue.getKey());
			}
		}
	}
	
	/**
	 * Add an animation in the queue specified by tag.
	 * Animations with different tags run simultaniosly
	 * @param animation
	 * @param tag
	 */
	public void addAnimation(GraphAnimation animation, String tag){
		animation.setAnimationListener(this);
		animation.setTag(tag);
		
		if(queues.get(tag) == null){
			LinkedList<GraphAnimation> newQueue = new LinkedList<GraphAnimation>();
			newQueue.add(animation);
			queues.put(tag, newQueue);
		} else {
			queues.get(tag).add(animation);
		}
		Log.d(TAG, "Animation added to queue "+tag);
	}
	
	/**
	 * Add an animation to the main queue
	 * @param animation
	 */
	public void addAnimation(GraphAnimation animation){
		animation.setAnimationListener(this);
		animation.setTag(MAIN_QUEUE);
		
		if(queues.get(MAIN_QUEUE) == null){
			LinkedList<GraphAnimation> newQueue = new LinkedList<GraphAnimation>();
			newQueue.add(animation);
			queues.put(MAIN_QUEUE, newQueue);
		} else {
			queues.get(MAIN_QUEUE).add(animation);
		}
		Log.d(TAG, "Animation added to queue "+MAIN_QUEUE);
	}

	@Override
	public void animationFinished(String tag) {
		if(queues.get(tag) != null){
			queues.get(tag).pollFirst();
		}
		Log.d(TAG, "Animation finished");
	}

	@Override
	public void animationCanceled(String tag) {
		if(queues.get(tag) != null){
			queues.get(tag).clear();
		}
	}
}
