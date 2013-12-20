package de.mimuc.pem_music_graph.graph.animation;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import de.mimuc.pem_music_graph.graph.GenreNode;

/**
 * Fades the node visible or invisible. Use the mode SHOW or HIDE to specify 
 * the animation. Standard is HIDE
 * 
 * @author Christopher Gebhardt
 *
 */
public class FadeAnimation extends GraphAnimation {
	
	public static final int SHOW = 0;
	public static final int HIDE = 1;
	
	private int mode = SHOW;

	private List<GenreNode> nodes;
	
	public FadeAnimation(GenreNode node, long duration, int mode){
		nodes = new ArrayList<GenreNode>();
		nodes.add(node);
		
		this.duration = duration;
		
		if(mode == SHOW || mode == HIDE){
			this.mode = mode;
		}
	}
	
	public FadeAnimation(List<GenreNode> node, long duration, int mode){
		nodes = new ArrayList<GenreNode>();
		nodes.addAll(node);
		
		this.duration = duration;
		
		if(mode == SHOW || mode == HIDE){
			this.mode = mode;
		}
	}
	
	@Override
	protected void animate(long time) {
		double visibility = 0;
		
		switch(mode){
		
		case SHOW:
			visibility = EaseFunction.easeInQuad(time, 0, 1, duration);
			for (GenreNode node : nodes) {
				node.setVisibility(visibility);
			}
			break;
		
		case HIDE:
			visibility = EaseFunction.easeInQuad(time, 1, -1, duration);
			for (GenreNode node : nodes) {
				node.setVisibility(visibility);
			}
			break;
		}
	}

}
