package de.mimuc.pem_music_graph.graph;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Nodes that implement this interface have to draw their representation
 * on a canvas
 * 
 * @author Christopher Gebhardt
 *
 */
public interface GraphDrawable {

	/**
	 * Node draws itself on the canvas
	 * 
	 * @param canvas
	 */
	public void draw(Canvas canvas, int width, int height, float translation);
}
