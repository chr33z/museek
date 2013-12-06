package de.mimuc.pem_music_graph.graph;

/**
 * This is a wrapper class for the Music-Graph Nodes. It controls motion and which level
 * of the graph is shown
 * 
 * 
 * @author Christopher Gebhardt
 *
 */
public class MusicGraph implements IGraph {

	private static final String TAG = MusicNode.class.getName();
	
	/**
	 * the root of the graph
	 */
	private MusicNode root;
	
	@Override
	public void setAsRoot(String name){
		setInvisibleCascading();
		
		if(root.getName().equalsIgnoreCase(name)){
			root.setRoot(true);
		}
		else {
			root.setVisible(false);
			if(root.getChildren() != null){
				for (MusicNode child : root.getChildren()) {
					child.setAsRoot(name);
				}
			}
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
	public void addChildTo(MusicNode child, String name) {
		if(root.getName().equalsIgnoreCase(name)){
			root.addChild(child);
		}
		else {
			root.addChildTo(child, name);
		}
	}
}
