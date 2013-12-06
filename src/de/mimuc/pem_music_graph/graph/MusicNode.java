package de.mimuc.pem_music_graph.graph;

public class MusicNode {
	
	private static final String TAG = MusicNode.class.getName();
	
	protected float x = 0;
	protected float y = 0;
	protected float radius = 10;
	
	protected String name = "";
	
	public MusicNode(float x, float y, float radius, String name){
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.name = name;
	}
	
	public MusicNode setPosition(float x, float y){
		this.x = x;
		this.y = y;
		return this;
	}
	
	public MusicNode setName(String name){
		this.name = name;
		return this;
	}
}
