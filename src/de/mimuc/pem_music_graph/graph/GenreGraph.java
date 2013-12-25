package de.mimuc.pem_music_graph.graph;

import de.mimuc.pem_music_graph.utils.ApplicationController;
import android.graphics.Canvas;
import android.graphics.Color;
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
	
	public static final float PARENT_Y_FACTOR = 0.0f;
	public static final float ROOT_Y_FACTOR = 0.15f;
	public static final float CHILD_Y_FACTOR = 0.30f;
	public static final float RADIUS_FACTOR = 0.1f;
	public static final float TEXT_FACTOR = 0.005f;
	public static final float LINE_FACTOR = 0.005f;
	
	// Colors used for nodes
	public static final float COLOR_HUE = 158.0f;
	public static final float COLOR_HUE_STEP = -15.0f;
	public static final float COLOR_SAT = 0.73f;
	public static final float COLOR_VAL = 0.69f;
	
	// colors used for the rest
	public static final int COLOR_TEXT = Color.WHITE;
	public static final int COLOR_BACKGROUND = Color.DKGRAY;
	public static final int COLOR_LINE = Color.BLACK;
	
	// Animation Times
	public static final int ANIM_MOVE_DURATION = 300;
	public static final int ANIM_TOUCH_DURATION = 100;
	public static final int ANIM_DELAY = 50;
	
	// value between 0 and 1
	public static final float TRANSLATION_SNAP_FACTOR = 0.1f;
	
	public static float TRANSLATION_MAX;
	
	/*
	 * screen dimensions
	 */
	private float width;
	private float height;
	
	/**
	 * vertical translation of entire graph view
	 */
	public float translation = 0;
	
	/**
	 * the root of the graph
	 */
	private GenreNode root;
	
	/**
	 * the current displayed root of the graph
	 */
	private GenreNode currentRoot;
	
	public GenreGraph(){
		DisplayMetrics metrics = ApplicationController
				.getInstance().getResources().getDisplayMetrics();
		width = metrics.widthPixels;
		height = metrics.heightPixels;
		
		TRANSLATION_MAX = (height * ROOT_Y_FACTOR) - (height * PARENT_Y_FACTOR);
		
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
		root.level = 0;
		
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
		
		if(root.name.equalsIgnoreCase(name)){
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
			positionNodes(result);
		}
		
		return result;
	}
	
	/**
	 * Helper method
	 * Make the provided node root and position itself and
	 * its children
	 * @param node
	 */
	private void positionNodes(GenreNode newRoot){
		
		newRoot.x = width / 2.0f;
		newRoot.y = height * ROOT_Y_FACTOR;
		newRoot.radius = width * RADIUS_FACTOR;
		newRoot.setVisibility(1);
		
		if(newRoot.getParent() != null){
			newRoot.getParent().x = width / 2.0f;
			newRoot.getParent().y = height * PARENT_Y_FACTOR;
			newRoot.getParent().radius = width * RADIUS_FACTOR;
			newRoot.getParent().setVisibility(1);
		}
		
		int size = newRoot.getChildren().size();
		for (int i = 0; i < size ; i++) {
			GenreNode child = newRoot.getChildren().get(i);
			
			child.x = ((width * i) / size) + ((width * 0.5f) / size);
			child.y = height * CHILD_Y_FACTOR;
			child.radius = width * RADIUS_FACTOR;
			child.setVisibility(1);
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
		if(root.name.equalsIgnoreCase(name)){
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
