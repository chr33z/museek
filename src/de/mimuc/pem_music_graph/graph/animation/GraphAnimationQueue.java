package de.mimuc.pem_music_graph.graph.animation;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import android.util.Log;

/**
 * Animation Queue that supports multiple simultaneous animations.
 * The animations queues are identified by tags like "movetorootanimations".
 * 
 * All animations with a tag are executed subsequently
 * 
 * Animations can change properties of a node like visibility or position but 
 * not yet spawn ohter objects or draw directly onto the canvas
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
		 *  TODO catch current modification exception
		 */
		try{
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
		
		} catch(ConcurrentModificationException e){
			/*
			 * catch this exception and just skip this frame
			 */
			Log.w(TAG, "Unsynchronized access on queues. Skip this frame!");
		}
	}
	
	/**
	 * Add an animation in the queue specified by tag.
	 * Animations with different tags run simultaniosly
	 * @param animation
	 * @param tag
	 */
	public void add(GraphAnimation animation, String tag){
		animation.setAnimationListener(this);
		animation.setTag(tag);
		
		if(queues.get(tag) == null){
			LinkedList<GraphAnimation> newQueue = new LinkedList<GraphAnimation>();
			newQueue.add(animation);
			queues.put(tag, newQueue);
		} else {
			queues.get(tag).add(animation);
		}
//		Log.d(TAG, "Animation added to queue "+tag);
	}
	
	/**
	 * Add an animation to the main queue
	 * @param animation
	 */
	public void add(GraphAnimation animation){
		animation.setAnimationListener(this);
		animation.setTag(MAIN_QUEUE);
		
		if(queues.get(MAIN_QUEUE) == null){
			LinkedList<GraphAnimation> newQueue = new LinkedList<GraphAnimation>();
			newQueue.add(animation);
			queues.put(MAIN_QUEUE, newQueue);
		} else {
			queues.get(MAIN_QUEUE).add(animation);
		}
//		Log.d(TAG, "Animation added to queue "+MAIN_QUEUE);
	}

	@Override
	public void animationFinished(String tag) {
		if(queues.get(tag) != null){
			queues.get(tag).pollFirst();
		}
	}

	@Override
	public void animationCanceled(String tag) {
		if(queues.get(tag) != null){
			queues.get(tag).clear();
		}
	}
	
	/**
	 * Get animation length in milliseconds of queue with tag 
	 * @param tag
	 * @return
	 */
	public long getQueueLength(String tag){
		if(tag == null) tag = MAIN_QUEUE;
		
		long duration = 0;
		if(queues.get(tag) != null){
			for (GraphAnimation animation : queues.get(tag)) {
				duration += animation.duration;
			}
		}
		
		return duration;
	}
	
	public long getLongestQueue(){
		long duration = 0;
		
		for (Entry<String, LinkedList<GraphAnimation>> queue : queues.entrySet()) {
			
			long tmpDuration = 0;
			for (GraphAnimation animation : queue.getValue()) {
				tmpDuration += animation.duration;
			}
			if(tmpDuration > duration){
				duration = tmpDuration;
			}
		}
		return duration;
	}
}
