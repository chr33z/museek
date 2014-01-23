package de.mimuc.pem_music_graph.graph;

import java.util.ArrayList;
import java.util.List;

import de.mimuc.pem_music_graph.R;
import de.mimuc.pem_music_graph.utils.ApplicationController;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * A node that represents one genre on the screen.
 * 
 * @author Christopher Gebhardt
 *
 */
public class GenreNode implements IGraph, GraphDrawable, GenreGraphConstants {

	private static final String TAG = GenreNode.class.getName();

	/*
	 * positions of this node on the screen
	 * and for touch events 
	 */
	public float x = 0;
	public float y = 0;
	public float radius = 100;

	/**
	 * left, top, right, bottom
	 */
	public float[] boundary = new float[4];

	/**
	 * visible name of this node
	 */
	protected String name = "node";

	/**
	 * is this node currently root of the graph
	 */
	public boolean isRoot;

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

	public boolean fadeRoot = false;

	public double rootVisibility = 0;

	private Paint paintNode = new Paint();

	private Paint paintLine = new Paint();

	protected Paint paintText = new Paint();
	protected Paint origPaintText = new Paint();

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

	private Drawable nodeDrawable;
	private Drawable nodeInverseDrawable;

	private FontMetrics fontMetrics;

	private float width, height;

	public GenreNode(float x, float y, float radius, String name, float width, float height){
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.name = name;
		this.width = width;
		this.height = height;

		initDrawing();
	}

	/**
	 * 
	 * @param radius
	 * @param name
	 */
	public GenreNode(String name, float radius, float width, float height){
		this.radius = radius;
		this.name = name;
		this.width = width;
		this.height = height;

		initDrawing();
	}

	private void initDrawing(){
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
		paintText.setTextSize(textSize * width * GenreGraph.TEXT_FACTOR ); // FIXME make screen dependent
		paintText.setARGB(255, 220, 220, 220);
		paintText.setTextAlign(Paint.Align.RIGHT);
		paintText.setAntiAlias(true);
		origPaintText = new Paint(paintText);

		nodeDrawable = ApplicationController.getInstance()
				.getResources().getDrawable(R.drawable.music_genre_label);
		nodeInverseDrawable = ApplicationController.getInstance()
				.getResources().getDrawable(R.drawable.music_genre_label_inversed);

		fontMetrics = new FontMetrics();

		updateBoundary(0);
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
		this.setVisible(true);
		this.isRoot = true;

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
			this.isRoot = false;
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
	public void resetCascading() {
		this.setVisible(false);
		this.isRoot = false;

		if(children != null){
			for (GenreNode child : children) {
				child.resetCascading();
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

		float textWidth = origPaintText.measureText(name);
		float labelHeight = height * LABEL_HEIGHT_FACTOR;
		float labelPadding = width * LABEL_PADDING_HORIZONTAL_FACTOR;
		float left = this.x - textWidth - labelPadding;
		float top = this.y - labelHeight / 2;
		float right = this.x + labelPadding;
		float bottom = this.y + labelHeight / 2;

		if(name.equals("HipHop/Rap")){
			Log.d(TAG, "Node "+name+" origin "+this.x+" "+this.y);
			Log.d(TAG, "left "+left);
			Log.d(TAG, "top "+top);
			Log.d(TAG, "right "+right);
			Log.d(TAG, "bottom "+bottom);
		}

		if(isVisible && 
				left < x && x < right && 
				top < y && y < bottom){
			Log.d(TAG, "Node "+name+" touched in "+left+" "+top+" "+right+" "+bottom+" ");
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

		//

		//		paintLine.setStrokeWidth(width * GenreGraph.LINE_FACTOR);
		//		paintLine.setARGB(alpha, 20, 20, 20);

		//		if(parent != null && drawLines) canvas.drawLine(
		//				x, y + translation, 
		//				parent.x, parent.y + translation, 
		//				paintLine);

		// draw point larger if we are the root
		//		float radiusNew = (isRoot) ? radius * 1.2f : radius;

		// real circle
		//		paintNode.setColor(Color.HSVToColor(alpha, new float[]{ 
		//				GenreGraph.COLOR_HUE,
		//				GenreGraph.COLOR_SAT,
		//				GenreGraph.COLOR_VAL - (level * 0.05f)}));

		//		if(isRoot){
		//		canvas.drawCircle(
		//				x, y + translation, 
		//				radiusNew, paintNode);
		//		} else {
		//			
		//			float textWith = paintText.measureText(name);
		//			float padding = 2.0f;
		//			canvas.drawRect(
		//					new RectF(x - (textWith * 0.5f) - padding,
		//								y - radiusNew * 0.5f,
		//								x + (textWith * 0.5f) + padding,
		//								y + radiusNew * 0.5f), 
		//								paintNode);
		//		}

		// set bounds to match the text
		paintText.getFontMetrics(fontMetrics);
		updateBoundary(translation);
		if(false){ // FIXME make it real
			paintText.setColor(Color.HSVToColor(255, new float[]{ 
					GenreGraph.COLOR_ROOT_HUE,
					GenreGraph.COLOR_ROOT_SAT,
					GenreGraph.COLOR_ROOT_VAL}));

			nodeDrawable.setBounds(
					(int)(boundary[0]),
					(int)(boundary[1]),
					(int)(boundary[2]),
					(int)(boundary[3])
					);
			nodeDrawable.draw(canvas);

			nodeInverseDrawable.setBounds(
					(int)(boundary[0]),
					(int)(boundary[1]),
					(int)(boundary[2]),
					(int)(boundary[3])
					);
			nodeInverseDrawable.setAlpha(normalizeAlpha(rootVisibility));
			nodeInverseDrawable.draw(canvas);

		} else {
			paintText.setARGB(alpha, 220, 220, 220);

			nodeDrawable.setBounds(
					(int)(boundary[0]),
					(int)(boundary[1]),
					(int)(boundary[2]),
					(int)(boundary[3])
					);
			nodeDrawable.draw(canvas);
		}

		canvas.drawText(name, 
				x, y + -(fontMetrics.ascent + fontMetrics.descent) / 2 + translation, 
				paintText);

		drawLines = true;
		fadeRoot = false;
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

	private void updateBoundary(float translation){
		float textWidth = paintText.measureText(name);
		float labelHeight = isRoot ? height * LABEL_HEIGHT_ROOT_FACTOR : height * LABEL_HEIGHT_FACTOR;
		float labelPadding = width * LABEL_PADDING_HORIZONTAL_FACTOR;
		boundary[0] = x - textWidth - labelPadding;
		boundary[1] = y - labelHeight / 2 + translation;
		boundary[2] = x + labelPadding;
		boundary[3] = y + labelHeight / 2 + translation;
	}

	private int normalizeAlpha(double alpha){
		// normailze alpha value
		if(alpha > 1){
			return 255;
		} else if(alpha < 0){
			return 0;
		} else {
			return (int) (255 * alpha);
		}
	}
}
