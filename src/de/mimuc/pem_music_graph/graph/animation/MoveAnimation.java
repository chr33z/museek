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
	
	@Override
	protected void animate(long time) {
		double pos_x = EaseFunction.easeInQuad(time, x_start, x_end-x_start, duration);
		double pos_y = EaseFunction.easeInQuad(time, y_start, y_end-y_start, duration);
		
		node.drawLines = false;
		node.setPosition((float)pos_x, (float)pos_y);
	}

	
}
