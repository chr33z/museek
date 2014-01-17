package de.mimuc.pem_music_graph.graph.animation;

import de.mimuc.pem_music_graph.graph.GenreNode;

public class MoveAnimation extends GraphAnimation{
	
	private float x_start;
	private float y_start;
	
	private float x_end;
	private float y_end;
	
	private GenreNode node;

	public MoveAnimation(GenreNode node, long duration, float x, float y){
		this.duration = duration;
		
		this.x_start = node.x;
		this.y_start = node.y;
		this.x_end = x;
		this.y_end = y;
		this.node = node;
	}
	
	public MoveAnimation(GenreNode node, long duration, float x, float y, long delay){
		this.duration = duration;
		this.delay = delay;
		
		this.x_start = node.x;
		this.y_start = node.y;
		this.x_end = x;
		this.y_end = y;
		this.node = node;
	}
	
	@Override
	protected void animate(long time) {
		double x_new = EaseFunction.easeInQuad(time, x_start, x_end-x_start, duration);
		double y_new = EaseFunction.easeInQuad(time, y_start, y_end-y_start, duration);
		
		// prevent motion from "overshooting"
		if( Math.abs(y_end - y_start) < Math.abs(y_new - y_start)) y_new = y_end;
		if( Math.abs(x_end - x_start) < Math.abs(x_new - x_start)) x_new = x_end;
		
		node.drawLines = false;
		node.setPosition((float)x_new, (float)y_new);
	}

	
}
