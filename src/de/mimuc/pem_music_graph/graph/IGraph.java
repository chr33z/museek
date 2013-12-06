package de.mimuc.pem_music_graph.graph;

/**
 * 
 * @author Christopher Gebhardt
 *
 */
public interface IGraph {

	/**
	 * Move the node x and y pixel on the screen
	 * @param x in pixel
	 * @param y in pixel
	 */
	public void move(float x, float y);
	
	/**
	 * Search the node with the given name in the graph and
	 * set is as root. Sets all nodes invisible at first and makes the
	 * new root and its children visible
	 * @param name
	 */
	public void setAsRoot(String name);
	
	/**
	 * Add a child to the node with the given name
	 * @param child
	 * @param name node name to add the child to
	 */
	public void addChildTo(MusicNode child, String name);
	
	/**
	 * Set all nodes invisible.
	 */
	public void setInvisibleCascading();
}
