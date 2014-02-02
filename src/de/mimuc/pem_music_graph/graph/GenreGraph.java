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
public class GenreGraph implements IGraph, GenreGraphConstants {

	private static final String TAG = GenreGraph.class.getSimpleName();

	/*
	 * screen dimensions
	 */
	protected float width;
	protected float height;

	/**
	 * maximal translation of the graph during swipe. dependent on height
	 */
	public float translationMax = 0;

	/**
	 * current vertical translation of entire graph view
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

		translationMax = (height * ROOT_Y_FACTOR) - (height * PARENT_Y_FACTOR);

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

		root.addChild(buildNode("Dance/Electro"));
		root.addChild(buildNode("Alternative"));
		root.addChild(buildNode("Pop"));
		root.addChild(buildNode("Schlager"));
		root.addChild(buildNode("Black"));
		root.addChild(buildNode("HipHop/Rap"));
		root.addChild(buildNode("Rock/Metal"));

		// LEVEL 2  - ELECTRO
		root.addChildTo(buildNode("Dubstep"), "Dance/Electro");
		root.addChildTo(buildNode("Techno"), "Dance/Electro");
		root.addChildTo(buildNode("Trance"), "Dance/Electro");
		root.addChildTo(buildNode("House"), "Dance/Electro");
		root.addChildTo(buildNode("Hardcore"), "Dance/Electro");

		// Level 2
		root.addChildTo(buildNode("Rock 'n' Roll"), "Rock/Metal");
		root.addChildTo(buildNode("Hard Rock"), "Rock/Metal");
		root.addChildTo(buildNode("Progressive"), "Rock/Metal");
		root.addChildTo(buildNode("Rockabilly"), "Rock/Metal");
		root.addChildTo(buildNode("Metal"), "Rock/Metal");

		root.addChildTo(buildNode("Black/Death Metal"), "Metal");
		root.addChildTo(buildNode("Trash"), "Metal");
		root.addChildTo(buildNode("Metalcore"), "Metal");
		root.addChildTo(buildNode("Classic"), "Metal");
		root.addChildTo(buildNode("Powermetal"), "Metal");

		// LEVEL 2  - ALTERNATIVE
		root.addChildTo(buildNode("Brit Pop"), "Alternative");
		root.addChildTo(buildNode("Goth/Industrial"), "Alternative");
		root.addChildTo(buildNode("Indie"), "Alternative");
		root.addChildTo(buildNode("Punk"), "Alternative");
		root.addChildTo(buildNode("Hardcore"), "Alternative");
		
		// LEVEL 2 - HIPHOP/RAP
		root.addChildTo(buildNode("Deutsch Rap"), "HipHop/Rap");
		root.addChildTo(buildNode("HipHop&R'n'B"), "HipHop/Rap");
		root.addChildTo(buildNode("Old-Skool"), "HipHop/Rap");
		root.addChildTo(buildNode("Freestyle"), "HipHop/Rap");
		
		// LEVEL 2 - BLACK
		root.addChildTo(buildNode("R'n'B"), "Black");
		root.addChildTo(buildNode("Soul"), "Black");

		currentRoot = setAsRoot("Music");

		Log.d(TAG, "...finished in "+(System.currentTimeMillis() - startTime)+" ms!");
	}

	/**
	 * Helper method for shortening the code
	 */
	private GenreNode buildNode(String name){
		return new GenreNode(0, 0, width * RADIUS_FACTOR, name, width, height);
	}

	@Override
	public GenreNode setAsRoot(String name){
		GenreNode result = null;

		/*
		 *  set all nodes invisible.
		 *  setRoot() makes a node and its children visible
		 */
		resetCascading();

		if(root.name.equalsIgnoreCase(name)){
			root.setRoot(true);
			result =  root;
		}
		else {
			root.isRoot = false;
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
		float paddingScreen = width * SCREEN_MARGIN_FACTOR;
		float paddingLabel = width * LABEL_PADDING_HORIZONTAL_FACTOR;
		float labelHeight = height * LABEL_HEIGHT_FACTOR;

		newRoot.x = width - paddingScreen - paddingLabel;
		newRoot.y = height * ROOT_Y_FACTOR;
		newRoot.radius = width * RADIUS_FACTOR;
		newRoot.setVisibility(1);

		// set parent
		if(newRoot.getParent() != null){
			newRoot.getParent().x = newRoot.x;
			newRoot.getParent().y = height * PARENT_Y_FACTOR;
			newRoot.getParent().radius = width * RADIUS_FACTOR;
			newRoot.getParent().setVisibility(1);
		}

		int size = newRoot.getChildren().size();
		float currentX = newRoot.x;
		float currentY = height * CHILD_Y_FACTOR;

		for (int i = 0; i < size ; i++) {
			GenreNode child = newRoot.getChildren().get(i);

			child.x = currentX;
			child.y = currentY;

			// determine position of next child
			if((i+1 < size)){
				GenreNode nextChild = newRoot.getChildren().get(i+1);
				String nextName = nextChild.name;
				float textLength = nextChild.paintText.measureText(nextName);

				currentX -= (child.origPaintText.measureText(child.name)
						+ paddingLabel * 2 + paddingScreen);
				if(currentX < textLength + paddingScreen * 2 + paddingLabel * 2){
					currentX = newRoot.x;
					currentY += labelHeight + paddingScreen;
				}
			}
		}
	}

	@Override
	public void move(float x, float y) {
		root.move(x, y);
	}

	@Override
	public void resetCascading() {
		root.resetCascading();
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

	@Override
	public void setColorIntervall(float min, float max) {
		root.setColorIntervall(min, max);
	}

	/**
	 * 
	 * @return Get the lower boundary of the graph from the top
	 */
	public int measureHeight(){
		int size = currentRoot.getChildren().size();
		GenreNode node;

		if(size > 0){
			return (int) currentRoot.getChildren()
					.get(size -1).boundary[3];
		} else {
			return (int) currentRoot.boundary[3];
		}
	}
}
