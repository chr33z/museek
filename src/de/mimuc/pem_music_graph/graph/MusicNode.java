package de.mimuc.pem_music_graph.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * A node that represents one genre on the screen.
 * 
 * @author Christopher Gebhardt
 *
 */
public class MusicNode implements IGraph{
	
	private static final String TAG = MusicNode.class.getName();
	
	protected float x = 0;
	protected float y = 0;
	protected float radius = 100;
	
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
	 * child nodes of this node
	 */
	private List<MusicNode> children;
	
	/**
	 * parent node
	 */
	private MusicNode parent;
	
	public MusicNode(float x, float y, String name){
		this.x = x;
		this.y = y;
		this.name = name;
	}
	
	/**
	 * Set the position in pixel on the screen
	 * @param x
	 * @param y
	 * @return
	 */
	public MusicNode setPosition(float x, float y){
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
	public MusicNode setName(String name){
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
	public MusicNode setRoot(boolean isRoot) {
		this.isRoot = isRoot;
		this.setVisible(true);
		
		if(children != null){
			for (MusicNode child : children) {
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
	public MusicNode setVisible(boolean isVisible) {
		this.isVisible = isVisible;
		return this;
	}

	/**
	 * 
	 * @return children of this node
	 */
	public List<MusicNode> getChildren() throws NullPointerException {
		return children;
	}

	/**
	 * Set the children nodes of this node
	 * @param children
	 */
	public MusicNode setChildren(ArrayList<MusicNode> children) {
		this.children = children;
		
		if(children != null){
			for (MusicNode child : this.children) {
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
	public MusicNode addChild(MusicNode child){
		if(children == null){
			children = new ArrayList<MusicNode>();
		}
		children.add(child);
		child.setParent(this);
		
		return this;
	}

	/**
	 * 
	 * @return parent node of this node
	 */
	public MusicNode getParent() {
		return parent;
	}

	/**
	 * Set the parent node of this node
	 * @param parent
	 */
	public MusicNode setParent(MusicNode parent) {
		this.parent = parent;
		return this;
	}
	
	@Override
	public void move(float x, float y) {
		this.x += x;
		this.y += y;
		
		for (MusicNode child : children) {
			child.move(x, y);
		}
	}

	@Override
	public void setAsRoot(String name) {
		if(this.getName().equalsIgnoreCase(name)){
			this.setRoot(true);
		}
		else {
			this.setVisible(false);
			for (MusicNode child : this.getChildren()) {
				child.setAsRoot(name);
			}
		}
	}

	@Override
	public void setInvisibleCascading() {
		this.setVisible(false);
		
		if(children != null){
			for (MusicNode child : children) {
				child.setInvisibleCascading();
			}
		}
	}

	@Override
	public void addChildTo(MusicNode child, String name) {
		if(this.name.equalsIgnoreCase(name)){
			addChild(child);
		}
		else {
			if(children != null){
				for (MusicNode node : children) {
					node.addChildTo(child, name);
				}
			}
		}
	}
}
