package de.mimuc.pem_music_graph.graph;

import android.graphics.Color;

public interface GenreGraphConstants {
	
	public static final float PARENT_Y_FACTOR = -0.05f;
//	public static final float ROOT_Y_FACTOR = 0.13f;
//	public static final float CHILD_Y_FACTOR = 0.25f;
	public static final float ROOT_Y_FACTOR = 0.05f;
	public static final float CHILD_Y_FACTOR = 0.15f;
	public static final float RADIUS_FACTOR = 0.1f;
	public static final float TEXT_FACTOR = 0.005f;
	public static final float LINE_FACTOR = 0.005f;
	
	// Node Color
	public static final float COLOR_HUE = 310.0f;
	public static final float COLOR_HUE_STEP = -5.0f;
	public static final float COLOR_SAT = 0.73f;
	public static final float COLOR_VAL = 0.69f;
	
	// Text Color
	public static final float COLOR_ROOT_HUE = 0f;
	public static final float COLOR_ROOT_SAT = 0f;
	public static final float COLOR_ROOT_VAL = 1f;
	
	// colors used for the rest
	public static final int COLOR_TEXT = Color.WHITE;
	public static final int COLOR_BACKGROUND = Color.DKGRAY;
	public static final int COLOR_LINE = Color.BLACK;
	
	// Animation Times
	public static final int ANIM_MOVE_DURATION = 300;
	public static final int ANIM_TOUCH_DURATION = 100;
	public static final int ANIM_DELAY = 50;
	
	// Button Shadow offset
	public static final float SHADOW_OFFSET_X = 0.005f;
	public static final float SHADOW_OFFSET_Y = 0.01f;
	
	// value between 0 and 1
	public static final float TRANSLATION_SNAP_FACTOR = 0.22f;
	
	// relevant for drawing
	public static final float LABEL_PADDING_HORIZONTAL_FACTOR = 0.027f;
	public static final float LABEL_HEIGHT_FACTOR = 0.08f;
	public static final float LABEL_HEIGHT_ROOT_FACTOR = 0.09f;
	
	public static final float ROOT_NODE_FACTOR = 1.4f;
	
	public static final float SCREEN_MARGIN_FACTOR = 0.005f;
}
