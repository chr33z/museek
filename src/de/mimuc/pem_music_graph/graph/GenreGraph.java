package de.mimuc.pem_music_graph.graph;

import de.mimuc.pem_music_graph.utils.ApplicationController;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * This is a wrapper class for the Music-Graph Nodes. It controls motion and which level
 * of the graph is shown
 * 
 * 
 * @author Christopher Gebhardt
 *
 */
public class GenreGraph implements IGraph {

	private static final String TAG = GenreNode.class.getName();
	
	/**
	 * the root of the graph
	 */
	private GenreNode root;
	
	private GenreNode currentRoot;
	
	public GenreGraph(){
		buildGraph();
	}
	
	/**
	 * Build the graph and add nodes 
	 * TODO build automatically from json or something
	 */
	private void buildGraph(){
		Log.d(TAG, "Building the graph nodes...");
		long startTime = System.currentTimeMillis();
		
		DisplayMetrics metrics = ApplicationController
				.getInstance().getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;

		float rootX = width / 2.0f;
		float rootY = 200;
		float childY = 500;
		
		root = new GenreNode(rootX, rootY, "Music");
		
		root.addChild(new GenreNode( (width/5.0f * 1), childY, "Rock" ));
		root.addChild(new GenreNode( (width/5.0f * 2), childY, "Pop" ));
		root.addChild(new GenreNode( (width/5.0f * 3), childY, "Electro" ));
		root.addChild(new GenreNode( (width/5.0f * 4), childY, "House" ));
		
		// Level 2
		GenreNode rock_hard = new GenreNode( (width/4.0f * 1), childY, "Hard Rock" );
		GenreNode rock_progressive = new GenreNode( (width/4.0f * 2), childY, "Progressive" );
		GenreNode rock_alternative = new GenreNode( (width/4.0f * 3), childY, "Alternative" );
		root.addChildTo(rock_hard, "Rock");
		root.addChildTo(rock_progressive, "Rock");
		root.addChildTo(rock_alternative, "Rock");
		
		GenreNode electro_dubstep = new GenreNode( (width/4.0f * 1), childY, "Dubstep" );
		GenreNode electro_techno = new GenreNode( (width/4.0f * 2), childY, "Techno" );
		GenreNode electro_extrem = new GenreNode( (width/4.0f * 3), childY, "Extrem" );
		root.addChildTo(electro_dubstep, "Electro");
		root.addChildTo(electro_techno, "Electro");
		root.addChildTo(electro_extrem, "Electro");
		
		currentRoot = setAsRoot("Music");
		
		Log.d(TAG, "...finished in "+(System.currentTimeMillis() - startTime)+" ms!");
	}
	
	@Override
	public GenreNode setAsRoot(String name){
		GenreNode result = null;
		
		/*
		 *  set all nodes invisible.
		 *  setRoot() makes a node and its children visible
		 */
		setInvisibleCascading();
		
		if(root.getName().equalsIgnoreCase(name)){
			root.setRoot(true);
			result =  root;
		}
		else {
			root.setVisible(false);
			for (GenreNode child : root.getChildren()) {
				result = child.setAsRoot(name);
				if(result != null){
					break;
				}
			}
		}
		
		if(result != null){
			currentRoot = result;
		}
		
		return result;
	}
	
	@Override
	public void move(float x, float y) {
		root.move(x, y);
	}

	@Override
	public void setInvisibleCascading() {
		root.setInvisibleCascading();
	}

	@Override
	public void addChildTo(GenreNode child, String name) {
		if(root.getName().equalsIgnoreCase(name)){
			root.addChild(child);
		}
		else {
			root.addChildTo(child, name);
		}
	}

	@Override
	public GenreNode testForTouch(float x, float y) {
		return root.testForTouch(x, y);
	}
	
	/**
	 * 
	 * @return Return the node that is currently the visible root of our graph
	 */
	public GenreNode getCurrentRoot(){
		return currentRoot;
	}
}
