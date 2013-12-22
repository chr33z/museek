package de.mimuc.pem_music_graph.graph;

import java.util.ArrayList;
import java.util.List;

import de.mimuc.pem_music_graph.R;
import de.mimuc.pem_music_graph.utils.ApplicationController;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

/**
 * A node that represents one genre on the screen.
 * 
 * @author Christopher Gebhardt
 *
 */
public class GenreNode implements IGraph, GraphDrawable{
	
	private static final String TAG = GenreNode.class.getName();
	
	/*
	 * positions of this node on the screen
	 * and for touch events 
	 */
	public float x = 0;
	public float y = 0;
	public float radius = 100;
	
	/**
	 * visible name of this node
	 */
	protected String name = "node";
	
	/**
	 * is this node currently root of the graph
	 */
	private boolean isRoot;
	
	/**
	 * is this node visible on screen
	 */
	private boolean isVisible;
	
	/**
	 * value between 0 and 1. 0 is invisible, 1 is visible
	 */
	private double visibility = 1;
	
	/**
	 * only animation can make this false
	 * resets after each draw cycle
	 */
	public boolean drawLines = true;
	
	/**
	 * child nodes of this node
	 */
	private List<GenreNode> children;
	
	/**
	 * parent node
	 */
	private GenreNode parent;
	
	public GenreNode(float x, float y, float radius, String name){
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.name = name;
	}
	
	/**
	 * 
	 * @param radius
	 * @param name
	 */
	public GenreNode(String name, float radius, double width, double height){
		this.radius = radius;
		this.name = name;
	}
	
	/**
	 * Set the position in pixel on the screen
	 * @param x
	 * @param y
	 * @return
	 */
	public GenreNode setPosition(float x, float y){
		this.x = x;
		this.y = y;
		return this;
	}
	
	/**
	 * @return the name of this node
	 */
	public String getName(){
		return this.name;
	}
	
	/**
	 * Set a name for this node. This name is also drawn on
	 * the canvas
	 * @param name visible name of this node
	 * @return
	 */
	public GenreNode setName(String name){
		this.name = name;
		return this;
	}

	/**
	 * @return whether this node is currently the root 
	 * of the graph
	 */
	public boolean isRoot() {
		return isRoot;
	}

	/**
	 * Set if this node is currently the root node and
	 * set all its children visible
	 * @param isRoot
	 */
	public GenreNode setRoot(boolean isRoot) {
		this.isRoot = isRoot;
		this.setVisible(true);
		
		if(children != null){
			for (GenreNode child : children) {
				child.setVisible(true);
			}
		}
		
		return this;
	}

	/**
	 * 
	 * @return whether this node is visible or not
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * Set the visibility of this node
	 * @param isVisible
	 */
	public GenreNode setVisible(boolean isVisible) {
		this.isVisible = isVisible;
		return this;
	}

	/**
	 * 
	 * @return children of this node
	 */
	public List<GenreNode> getChildren() {
		if(children == null) children = new ArrayList<GenreNode>();
		return children;
	}

	/**
	 * Set the children nodes of this node
	 * @param children
	 */
	public GenreNode setChildren(ArrayList<GenreNode> children) {
		this.children = children;
		
		if(children != null){
			for (GenreNode child : this.children) {
				child.setParent(this);
			}
		}
		return this;
	}
	
	/**
	 * Add a child to the children of this node
	 * @param child
	 * @return
	 */
	public GenreNode addChild(GenreNode child){
		if(children == null){
			children = new ArrayList<GenreNode>();
		}
		children.add(child);
		child.setParent(this);
		
		return this;
	}

	/**
	 * 
	 * @return parent node of this node
	 */
	public GenreNode getParent() {
		return parent;
	}

	/**
	 * Set the parent node of this node
	 * @param parent
	 */
	public GenreNode setParent(GenreNode parent) {
		this.parent = parent;
		return this;
	}
	
	public double getVisibility() {
		return visibility;
	}

	public void setVisibility(double visibility) {
		this.visibility = visibility;
	}

	@Override
	public void move(float x, float y) {
		this.x += x;
		this.y += y;
		
		if(children != null){
			for (GenreNode child : children) {
				child.move(x, y);
			}
		}
	}

	@Override
	public GenreNode setAsRoot(String name) {
		GenreNode result = null;
		
		if(this.getName().equalsIgnoreCase(name)){
			this.setRoot(true);
			return this;
		}
		else {
			this.setVisible(false);
			for (GenreNode child : this.getChildren()) {
				result = child.setAsRoot(name);
				if(result != null){
					return result;
				}
			}
		}
		return result;
	}

	@Override
	public void setInvisibleCascading() {
		this.setVisible(false);
		
		if(children != null){
			for (GenreNode child : children) {
				child.setInvisibleCascading();
			}
		}
	}

	@Override
	public void addChildTo(GenreNode child, String name) {
		if(this.name.equalsIgnoreCase(name)){
			addChild(child);
		}
		else {
			if(children != null){
				for (GenreNode node : children) {
					node.addChildTo(child, name);
				}
			}
		}
	}

	@Override
	public GenreNode testForTouch(float x, float y) {
		GenreNode result = null;
		
		if(isVisible && Math.sqrt( (this.x-x)*(this.x-x) + (this.y-y)*(this.y-y) ) <= this.radius ){
			Log.d(TAG, "Node touched!");
			return this;
		}
		else {
			if(children != null){
				for(GenreNode child : children){
					result = child.testForTouch(x, y);
					if(result != null){
						return result;
					}
				}
			}
		}
		return result;
	}

	@Override
	public GenreNode findNode(String name) {
		GenreNode result = null;
		
		if(this.name.equalsIgnoreCase(name)){
			return this;
		}
		else {
			if(children != null){
				for (GenreNode child : children) {
					result = child.findNode(name);
					if(result != null){
						return result;
					}
				}
			}
		}
		return result;
	}

	@Override
	public void draw(Canvas canvas, int width, int height, float translation) {
		Resources res = ApplicationController.getInstance().getResources();
		
		Paint nodeP = new Paint();
		Paint lineP = new Paint();
		Paint textP = new Paint();
		
		// normailze alpha value
		if(visibility > 1) visibility = 1;
		else if(visibility < 0) visibility = 0;
		
//		nodeP.setColor(res.getColor(R.color.graph_node_lila));
		nodeP.setARGB((int)(255 * visibility), 176, 48, 96);
		nodeP.setAntiAlias(true);
		
		lineP.setStrokeWidth(5); // FIXME make screen dependent
		lineP.setARGB((int)(255 * visibility), 20, 20, 20);
		lineP.setAntiAlias(true);
		
		textP.setColor(res.getColor(R.color.graph_text_light));
		textP.setTextSize((float) (textP.getTextSize() * 5 * visibility)); // FIXME make screen dependent
		textP.setARGB((int)(255 * visibility), 220, 220, 220);
		textP.setAntiAlias(true);
		
		if(parent != null && drawLines) canvas.drawLine(
				x, y + translation, 
				parent.x, parent.y + translation, 
				lineP);
		
		canvas.drawCircle(
				x, y + translation, 
				radius, nodeP);
		
		canvas.drawText(name, 
				(x - (radius / 2)), y + translation, 
				textP);
		
		drawLines = true;
	}
}
