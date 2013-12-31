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
	 * @return Node that is now the root of the graph
	 */
	public GenreNode setAsRoot(String name);
	
	/**
	 * Add a child to the node with the given name
	 * @param child
	 * @param name node name to add the child to
	 */
	public void addChildTo(GenreNode child, String name);
	
	/**
	 * Set all nodes invisible.
	 */
	public void setInvisibleCascading();

	/**
	 * Set a color intervall for the children
	 */
	public void setColorIntervall(float min, float max);
	
	/**
	 * Find a node with name
	 * @param name
	 * @return the node or null
	 */
	public GenreNode findNode(String name);

	/**
	 * Test for touch on node and return that node
	 * @param x
	 * @param y
	 * @return GenreNode that has been touched
	 */
	public GenreNode testForTouch(float x, float y);
}
