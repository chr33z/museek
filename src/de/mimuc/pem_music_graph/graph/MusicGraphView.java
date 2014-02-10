package de.mimuc.pem_music_graph.graph;

import java.util.Calendar;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import de.mimuc.pem_music_graph.R;
import de.mimuc.pem_music_graph.graph.animation.GraphAnimationQueue;
import de.mimuc.pem_music_graph.graph.animation.MoveAnimation;
import de.mimuc.pem_music_graph.utils.ApplicationController;

/**
 * Music Graph SurfaceView. Draws the Node elements and handles touches and interaction
 * on the nodes
 * 
 * @author Christopher Gebhardt
 *
 */
public class MusicGraphView extends SurfaceView implements Runnable {

	private static final String TAG = MusicGraphView.class.getSimpleName();

	/**
	 * The genre graph contains all genre nodes
	 */
	GenreGraph graph;

	GenreGraphListener graphListener;

	/**
	 * manages all animations that are played
	 */
	GraphAnimationQueue animationQueue;

	private Thread thread;
	private SurfaceHolder surfaceHolder;

	volatile boolean running;

	// Save the last touch position to calculate relative movement on the screen
	double lastTouchX = 0;
	double lastTouchY = 0;

	float textSizeScale = 5;

	// Save colors here
	Paint paintNode = new Paint();
	Paint paintText = new Paint();
	Paint paintFps = new Paint();

	// Screen dimensions
	float width = 0;
	float height = 0;

	// Variables for touch event to distingish a click from a move
	private static final int MAX_CLICK_DURATION = 200;
	private long startClickTime;

	/**
	 * True as long a touch event is going on
	 */
	boolean onTouch = false;

	/**
	 * true when the surface is locked from touch events.
	 * used to block touch input during animations
	 */
	boolean touchLocked = false;

	// Save last time for framerate
	private long lastTime = 0;

	// update fps every ... milliseconds
	private static final int FPS_UPDATE_DURATION = 250;

	private int fps = 0;

	private long fpsTimer = 0;

	public MusicGraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initMusicGraph(context);
	}

	public MusicGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initMusicGraph(context);
	}

	public MusicGraphView(Context context){
		super(context);
		initMusicGraph(context);
	}

	private void initMusicGraph(Context context){
		// Build GenreGraph and add nodes
		graph = new GenreGraph();

		// initialize animation queue
		animationQueue = new GraphAnimationQueue();

		// initialize dimensions
		DisplayMetrics metrics = ApplicationController
				.getInstance().getResources().getDisplayMetrics();
		width = metrics.widthPixels;
		height = metrics.heightPixels;

		paintFps.setColor(Color.RED);
		paintFps.setTextSize(paintFps.getTextSize() * width * GenreGraph.TEXT_FACTOR * 0.5f);

		paintNode.setColor(context.getResources().getColor(R.color.graph_node_lila));
		paintText.setColor(Color.WHITE);
		paintText.setTextSize(paintText.getTextSize() * textSizeScale);

		surfaceHolder = getHolder();
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {

			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {

			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int s_width,
					int s_height) {
			}
		});
	}

	@Override
	protected void onDraw(Canvas canvas) {
		long time = System.currentTimeMillis();

		// for performance debugging
		long timeTmp = 0;
		long timeLast = 0;
		long timeChildChild = 0;
		long timeChild = 0;
		long timeSibling = 0;
		long timeAnimation = 0;

		canvas.drawColor(Color.DKGRAY);

		//		// draw and update fps
		//		if(fpsTimer > FPS_UPDATE_DURATION){
		//			fps = (int) Math.round(1000.0 / (time - lastTime));
		//			fpsTimer = 0;
		//		}
		//		fpsTimer += (time - lastTime);
		//		canvas.drawText("FPS: "+fps , 10, height * GenreGraph.ROOT_Y_FACTOR, paintFps);

		// update the animations
		animationQueue.update(time);

		// get current node and save for easier access
		GenreNode root = graph.getCurrentRoot();

		boolean hasParent = (root.getParent() != null);

		/*
		 * slowly move the translation back to original
		 * if the graph is not clicked but the graph was moved
		 */
		if(!onTouch && graph.translation > 0){
			graph.translation -= 
					graph.translation * GenreGraph.TRANSLATION_SNAP_FACTOR;

			if(graph.translation < 0){
				graph.translation = 0;
			}
		}

		/*
		 * watch the draw order! "children of children" -> "children" -> "root" -> "siblings" -> "parent"
		 */
		timeTmp = System.currentTimeMillis();
		for (GenreNode child : root.getChildren()) {
			for (GenreNode childchild : child.getChildren()) {
				if(childchild.isVisible()){
					childchild.draw(canvas, (int)width, (int)height, graph.translation);
				}
			}
		}
		timeLast = System.currentTimeMillis();
		timeChildChild = timeLast - time;

		timeTmp = System.currentTimeMillis();
		for (GenreNode child : root.getChildren()) {
			child.draw(canvas, (int)width, (int)height, graph.translation);
		}
		timeLast = System.currentTimeMillis();
		timeChild = timeLast - time;

		timeTmp = System.currentTimeMillis();
		if(hasParent){
			for (GenreNode sibling : root.getParent().getChildren()) {
				if(sibling.isVisible()){
					sibling.draw(canvas, (int)width, (int)height, graph.translation);
				}
			}
		}
		timeLast = System.currentTimeMillis();
		timeSibling = timeLast - time;

		root.draw(canvas, (int)width, (int)height, graph.translation);

		if(hasParent){
			root.getParent().draw(canvas, (int)width, (int)height, graph.translation);
		}

		//		Log.d(TAG, "cchild: "+timeChildChild+" child: "+timeChild+" siblings: "+timeSibling+" animation: "+timeAnimation);

		lastTime = time;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if(touchLocked) return false;

		float eventX = event.getX();
		float eventY = event.getY();

		switch(event.getAction()){

		case MotionEvent.ACTION_DOWN:
			onTouch = true;

			// if not already happened, start draw thread
			if(!running){
				onThreadResume();
			}

			lastTouchX = eventX;
			lastTouchY = eventY;

			startClickTime = Calendar.getInstance().getTimeInMillis();
			return true;

		case MotionEvent.ACTION_MOVE:
			/*
			 *  determine the relative move direction 
			 *  and move nodes accordingly in y direction only
			 *  so it does not move with child nodes
			 */
			if((eventY-lastTouchY) >= 0){
				graph.translation += (eventY-lastTouchY) * 
						(1 - (graph.translation / (graph.translationMax * 1.5f)));
			} else if((eventY-lastTouchY) <= 0 && graph.translation > 0){
				graph.translation += (eventY-lastTouchY) * 
						(1 - (graph.translation / (graph.translationMax * 1.5f)));
			}

			lastTouchX = eventX;
			lastTouchY = eventY;

			return true;

		case MotionEvent.ACTION_UP:
			onTouch = false;

			// we measure the click time and decide if its a click or a swipe
			long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
			if(clickDuration < MAX_CLICK_DURATION) {
				final GenreNode node = graph.testForTouch(eventX, eventY);

				// touch event on child nodes
				if(node != null && !(graph.getCurrentRoot().name)
						.equalsIgnoreCase(node.name) ){

					// run animations
					graphDownAnimation(node);

					// lock touch input until animation finished
					touchLocked = true;

					// run other logic when animation has finished playing
					Handler handler = new Handler();
					handler.postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							graph.setAsRoot(node.name);
							touchLocked = false;

							if(graphListener != null) {
								graphListener.onGraphUpdate(node, graph.measureHeight());
							}
						}
					}, animationQueue.getLongestQueue()+50);
				}

				// touch event on root node
				else if(node != null && node.isRoot()){
					graphUpAnimation(node);

					touchLocked = true;

					// run other logic when animation has finished playing
					Handler handler = new Handler();
					handler.postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							if(node.getParent() != null){
								graph.setAsRoot(node.getParent().name);
							}
							if(graphListener != null) {
								if(node.getParent()!= null){
									graphListener.onGraphUpdate(node.getParent(), graph.measureHeight());
								}else{
									graphListener.onGraphUpdate(node, graph.measureHeight());
								}
							}
							touchLocked = false;
						}
					}, animationQueue.getLongestQueue()+50);
				}
			}

			// else it was a move gesture
			else {
				if(graph.translation > graph.translationMax * 0.6f){
					final GenreNode node = graph.getCurrentRoot();

					graphUpAnimation(node);

					// run other logic when animation has finished playing
					Handler handler = new Handler();
					handler.postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							if(node.getParent() != null){
								graph.setAsRoot(node.getParent().name);
								graphListener.onGraphUpdate(node.getParent(), graph.measureHeight());
							}
							touchLocked = false;
						}
					}, animationQueue.getLongestQueue()+50);
				}
			}

			return true;

		default:
			return false;
		}
	};

	private boolean graphUpAnimation(GenreNode node){
		float paddingScreen = width * GenreGraph.SCREEN_MARGIN_FACTOR;
		float paddingLabel = width * GenreGraph.LABEL_PADDING_HORIZONTAL_FACTOR;
		float labelHeight = height * GenreGraph.LABEL_HEIGHT_FACTOR;

		final GenreNode parent = node.getParent();

		// if there is no parent, we are already at the root node
		if(parent == null){
			touchLocked = true;
		} 
		else {
			// first move parent to current location of root
			animationQueue.add(new MoveAnimation(parent, GenreGraph.ANIM_MOVE_DURATION, node.x, node.y), "parent");

			/*
			 * Determine the positions of all new children. Move the current root
			 * to his new position and fade in new children
			 * 
			 * look at GenreGraph for source of that formula
			 */
			int size = parent.getChildren().size();
			float currentX = node.x;
			float currentY = height * GenreGraph.CHILD_Y_FACTOR;

			for (int i = 0; i < size ; i++) {
				GenreNode sibling = parent.getChildren().get(i);

				// where they go
				float x_new = currentX;
				float y_new = currentY;

				// save the new position of our current root and move it there
				if(node.name.equalsIgnoreCase(sibling.name)){
					animationQueue.add(new MoveAnimation(node, GenreGraph.ANIM_MOVE_DURATION, x_new, y_new), "root");
				} else {
					// from where they come
					sibling.x = x_new - width;
					sibling.y = y_new;
					sibling.setVisible(true);
					sibling.setVisibility(1);

					animationQueue.add(new MoveAnimation(sibling, GenreGraph.ANIM_MOVE_DURATION, x_new, y_new), "siblingm"+i);
				}

				// determine position of next child
				if((i+1 < size)){
					GenreNode nextChild = parent.getChildren().get(i+1);
					String nextName = nextChild.name;
					float textLength = nextChild.origPaintText.measureText(nextName);

					currentX -= (sibling.origPaintText.measureText(sibling.name)
							+ paddingLabel * 2 + paddingScreen);
					if(currentX < textLength + paddingScreen * 2 + paddingLabel * 2){
						currentX = node.x;
						currentY += labelHeight + paddingScreen;
					}
				}
			}

			// shrink out current children
			for(int i=0; i<node.getChildren().size(); i++) {
				GenreNode child = node.getChildren().get(i);

				// Add shrink and move down animations
				if(!node.name.equalsIgnoreCase(child.name)){
					animationQueue.add(new MoveAnimation(child, 
							GenreGraph.ANIM_MOVE_DURATION, 
							child.x + width, child.y), 
							"currChildm"+i);
				}
			}
		}
		return true;
	}

	private boolean graphDownAnimation(GenreNode node){
		float paddingScreen = width * GenreGraph.SCREEN_MARGIN_FACTOR;
		float paddingLabel = width * GenreGraph.LABEL_PADDING_HORIZONTAL_FACTOR;
		float labelHeight = height * GenreGraph.LABEL_HEIGHT_FACTOR;

		// shrink other siblings and lat them fall down
		for(int i=0; i<node.getParent().getChildren().size(); i++) {
			GenreNode sibling = node.getParent().getChildren().get(i);

			// Add shrink animations with increasing delay that run parallel;
			if(!node.name.equalsIgnoreCase(sibling.name)){
				animationQueue.add(new MoveAnimation(sibling, 
						GenreGraph.ANIM_MOVE_DURATION, 
						sibling.x - width, sibling.y), 
						"siblingm"+i);
			}
		}

		// move touched node to root position
		animationQueue.add(new MoveAnimation(
				node, GenreGraph.ANIM_MOVE_DURATION, node.getParent().x, node.getParent().y), "root");

		// move root further up
		animationQueue.add(new MoveAnimation(
				node.getParent(), GenreGraph.ANIM_MOVE_DURATION, node.getParent().x, height * GenreGraphConstants.PARENT_Y_FACTOR), "rootparent");

		// fade in new children
		int size = node.getChildren().size();
		float currentX = node.getParent().x;
		float currentY = height * GenreGraph.CHILD_Y_FACTOR;

		for (int i = 0; i < size; i++) {
			GenreNode child = node.getChildren().get(i);

			// where they go
			float x_new = currentX;
			float y_new = currentY;

			// from where they come
			child.x = x_new + width;
			child.y = y_new;
			child.setVisible(true);
			child.setVisibility(1);

			animationQueue.add(new MoveAnimation(child, GenreGraph.ANIM_MOVE_DURATION, x_new, y_new), "newchildm"+i);

			// determine position of next child
			if((i+1 < size)){
				GenreNode nextChild = node.getChildren().get(i+1);
				String nextName = nextChild.name;
				float textLength = nextChild.origPaintText.measureText(nextName);

				currentX -= (child.origPaintText.measureText(child.name)
						+ paddingLabel * 2 + paddingScreen);
				if(currentX < textLength + paddingScreen * 2 + paddingLabel * 2){
					currentX = node.getParent().x;
					currentY += labelHeight + paddingScreen;
				}
			}
		}

		return true;
	}

	private void setRunning(boolean running){
		this.running = running;
	}

	/**
	 * Start the drawing thread
	 */
	public void onThreadResume(){
		if(!running){
			Log.d(TAG, "Thread started running");
			running = true;
			thread = new Thread(this);
			thread.start();
		}
	}

	/**
	 * Stop the drawing thread
	 */
	public void onThreadPause(){
		if(running){
			boolean retry = true;
			running = false;
			while(retry){
				try {
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Log.d(TAG, "Thread stopped running");
		}
	}

	@Override
	public void run() {
		while(running){
			if(!surfaceHolder.getSurface().isValid()){
				continue;
			} else {
				Canvas canvas = surfaceHolder.lockCanvas();

				if(canvas != null){
					onDraw(canvas);
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	public void setGenreGraphListener(GenreGraphListener listener){
		this.graphListener = listener;
	}

	/**
	 * True if the root node is the highest possible root node
	 * @return
	 */
	public boolean isAtRoot(){
		return (graph.getCurrentRoot().getParent() == null);
	}

	/**
	 * Navigate the graph to the upper root if possible
	 */
	public void graphNavigateBack(){
		if(graph.getCurrentRoot().getParent() != null){
			final GenreNode node = graph.getCurrentRoot();

			graphUpAnimation(node);

			touchLocked = true;

			// run other logic when animation has finished playing
			Handler handler = new Handler();
			handler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					if(node.getParent() != null){
						graph.setAsRoot(node.getParent().name);
						graphListener.onGraphUpdate(node.getParent(), graph.measureHeight());
						
					}
					touchLocked = false;
				}
			}, animationQueue.getLongestQueue()+50);
		}
	}

	public GenreNode getRootNode(){
		return graph.getCurrentRoot();
	}
	
	public int getGraphHeight(){
		return graph.measureHeight();
	}
}
