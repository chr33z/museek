package de.mimuc.pem_music_graph.graph;

import de.mimuc.pem_music_graph.utils.ApplicationController;
import android.graphics.Canvas;
import android.graphics.Paint;
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
	
	private static final float PARENT_Y_FACTOR = 0.0f;
	private static final float ROOT_Y_FACTOR = 0.15f;
	private static final float CHILD_Y_FACTOR = 0.30f;
	private static final float RADIUS_FACTOR = 0.1f;
	
	// value between 0 and 1
	public static final float TRANSLATION_SNAP_FACTOR = 0.1f;
	
	/*
	 * screen dimensions
	 */
	private float width;
	private float height;
	
	/**
	 * 
	 */
	public float translation = 0;
	
	/**
	 * the root of the graph
	 */
	private GenreNode root;
	
	private GenreNode currentRoot;
	
	public GenreGraph(){
		DisplayMetrics metrics = ApplicationController
				.getInstance().getResources().getDisplayMetrics();
		width = metrics.widthPixels;
		height = metrics.heightPixels;
		
		buildGraph();
	}
	
	/**
	 * Build the graph and add nodes 
	 * TODO build automatically from json or something
	 */
	private void buildGraph(){
		Log.d(TAG, "Building the graph nodes...");
		long startTime = System.currentTimeMillis();
		
		root = buildNode("Music");
		
		root.addChild(buildNode("Rock"));
		root.addChild(buildNode("Pop"));
		root.addChild(buildNode("Electro"));
		root.addChild(buildNode("House"));
		
		// Level 2
		GenreNode rock_hard = buildNode("Hard Rock");
		GenreNode rock_progressive = buildNode("Progressive");
		GenreNode rock_alternative = buildNode("Alternative");
		root.addChildTo(rock_hard, "Rock");
		root.addChildTo(rock_progressive, "Rock");
		root.addChildTo(rock_alternative, "Rock");
		
		GenreNode electro_dubstep = buildNode("Dubstep");
		GenreNode electro_techno = buildNode("Techno");
		GenreNode electro_extrem = buildNode("Extrem");
		root.addChildTo(electro_dubstep, "Electro");
		root.addChildTo(electro_techno, "Electro");
		root.addChildTo(electro_extrem, "Electro");
		
		currentRoot = setAsRoot("Music");
		
		Log.d(TAG, "...finished in "+(System.currentTimeMillis() - startTime)+" ms!");
	}
	
	/**
	 * Helper method for shortening the code
	 */
	private GenreNode buildNode(String name){
		return new GenreNode(0, 0, width * RADIUS_FACTOR, name);
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
			positionNodes(name);
		}
		
		return result;
	}
	
	/**
	 * Helper method
	 * Make the provided node root and position itself and
	 * its children
	 * @param node
	 */
	private void positionNodes(String name){
		GenreNode newRoot = findNode(name);
		
		newRoot.x = width / 2.0f;
		newRoot.y = height * ROOT_Y_FACTOR;
		
		if(newRoot.getParent() != null){
			newRoot.getParent().x = width / 2.0f;
			newRoot.getParent().y = height * PARENT_Y_FACTOR;
			newRoot.radius = width * RADIUS_FACTOR;
		}
		
		int size = newRoot.getChildren().size();
		for (int i = 0; i < size ; i++) {
			GenreNode child = newRoot.getChildren().get(i);
			
			child.x = ((width * i) / size) + ((width * 0.5f) / size);
			child.y = height * CHILD_Y_FACTOR;
			child.radius = width * RADIUS_FACTOR;
		}
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

	@Override
	public GenreNode findNode(String name) {
		return root.findNode(name);
	}
}
