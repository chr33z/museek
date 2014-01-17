package de.mimuc.pem_music_graph.graph;

import java.util.ArrayList;
import java.util.List;

import de.mimuc.pem_music_graph.R;
import de.mimuc.pem_music_graph.utils.ApplicationController;

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
	
	public int level = 0; 
	
	/**
	 * only animation can make this false
	 * resets after each draw cycle
	 */
	public boolean drawLines = true;
	
	private Paint paintNode = new Paint();
	
	private Paint paintLine = new Paint();
	
	private Paint paintText = new Paint();
	
	private float textSize = 0;

	/**
	 * The color of the node is always the arithetic mean
	 * of the color intervall. All childrens color intervalls 
	 * are inside this colorintervall
	 */
	private float[] colorIntervall = new float[2];
	
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
		
		initColor();
	}
	
	/**
	 * 
	 * @param radius
	 * @param name
	 */
	public GenreNode(String name, float radius, double width, double height){
		this.radius = radius;
		this.name = name;
		
		initColor();
	}
	
	private void initColor(){
		textSize = paintText.getTextSize();
		
		paintNode.setColor(Color.HSVToColor(new float[]{
				GenreGraph.COLOR_HUE,
				GenreGraph.COLOR_SAT,
				GenreGraph.COLOR_VAL
				}));
		paintNode.setAntiAlias(true);
		
		paintLine.setStrokeWidth(5); // FIXME make screen dependent
		paintLine.setARGB(255, 20, 20, 20);
		paintLine.setAntiAlias(true);
		
		paintText.setColor(ApplicationController.getInstance()
				.getResources().getColor(R.color.graph_text_light));
		paintText.setTextSize(textSize); // FIXME make screen dependent
		paintText.setARGB(255, 220, 220, 220);
		paintText.setAntiAlias(true);
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
	 * Set the parent node of this node and determine the level of this node
	 * in the tree
	 * @param parent
	 */
	public GenreNode setParent(GenreNode parent) {
		this.parent = parent;
		setLevel();
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
		
		if(this.name.equalsIgnoreCase(name)){
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
	
	/**
	 * Set the level of this node in the tree. is called as soon as this node has
	 * a parent
	 */
	private void setLevel(){
		if(parent == null) level = 1;
		else level = parent.level + 1;
	}
	
	/**
	 * Blend the foreground color onto the background color based on the alpha channel
	 * of the foreground color. 
	 * 
	 * @param colorForeground
	 * @param colorBackground
	 * @return new color
	 */
	private int calcTransparancy(int colorForeground, int colorBackground){
		int alphaf = Color.alpha(colorForeground);
		int redf = Color.red(colorForeground);
		int greenf = Color.green(colorForeground);
		int bluef = Color.blue(colorForeground);
		
		int redb = Color.red(colorBackground);
		int greenb = Color.green(colorBackground);
		int blueb = Color.blue(colorBackground);
		
		float alpha = (alphaf / 255.0f);
		int redn = (int) ((redf * alpha) + ((1 - alpha) * redb));
		int greenn = (int) ((greenf * alpha) + ((1 - alpha) * greenb));
		int bluen = (int) ((bluef * alpha) + ((1 - alpha) * blueb));
		
		return Color.rgb(redn, greenn, bluen);
	}

	@Override
	public void draw(Canvas canvas, int width, int height, float translation) {
		//Resources res = ApplicationController.getInstance().getResources();
		
		// normailze alpha value
		int alpha = 0;
		if(visibility > 1){
			alpha = 255;
		} else if(visibility < 0){
			alpha = 0;
		} else {
			alpha = (int) (255 * visibility);
		}
		
		paintNode.setColor(Color.HSVToColor(alpha, new float[]{ 
				360 * ((colorIntervall[0] + colorIntervall[1]) / 2),
				GenreGraph.COLOR_SAT,
				GenreGraph.COLOR_VAL}));

		paintLine.setStrokeWidth(width * GenreGraph.LINE_FACTOR);
		paintLine.setARGB(alpha, 20, 20, 20);
		
		paintText.setTextSize((float) (textSize * visibility * width * GenreGraph.TEXT_FACTOR));
		paintText.setARGB(alpha, 220, 220, 220);
		
		if(parent != null && drawLines) canvas.drawLine(
				x, y + translation, 
				parent.x, parent.y + translation, 
				paintLine);
		
		canvas.drawCircle(
				x, y + translation, 
				radius, paintNode);
		
		canvas.drawText(name, 
				(x - (radius / 2)), y + translation, 
				paintText);
		
		drawLines = true;
	}

	@Override
	public void setColorIntervall(float min, float max) {
		colorIntervall[0] = min;
		colorIntervall[1] = max;
		
		Log.d(TAG, "level "+level+" intervall "+colorIntervall[0]+" "+colorIntervall[1]);
		
		if(children != null && children.size() > 0){
			for(int i=0, num = children.size(); i < num; i++){
				children.get(i).setColorIntervall(
						min + (((max-min)/num) * i), 
						min + (((max-min)/num) * (i+1))
				);
			}
		}
	}
}
