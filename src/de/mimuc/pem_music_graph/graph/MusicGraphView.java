package de.mimuc.pem_music_graph.graph;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import de.mimuc.pem_music_graph.R;
import de.mimuc.pem_music_graph.graph.animation.GraphAnimationQueue;
import de.mimuc.pem_music_graph.graph.animation.MoveAnimation;
import de.mimuc.pem_music_graph.graph.animation.ShrinkAnimation;
import de.mimuc.pem_music_graph.graph.animation.TouchAnimation;
import de.mimuc.pem_music_graph.utils.ApplicationController;

/**
 * Music Graph SurfaceView. Draws the Node elements and handles touches and interaction
 * on the nodes
 * 
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
	 * When a touch event occured
	 */
	boolean onTouch = false;

	/**
	 * whether the surface can receive touch events
	 */
	boolean touchLocked = false;

	// Save last time for framerate
	private long lastTime = 0;

	// update fps every ... milliseconds
	private static final int FPS_UPDATE_DURATION = 250;

	private int fps = 0;

	private long fpsTimer = 0;

	public MusicGraphView(Context context){
		super(context);

		paintFps.setColor(Color.RED);
		paintFps.setTextSize(paintFps.getTextSize() * 3);

		paintNode.setColor(context.getResources().getColor(R.color.graph_node_lila));
		paintText.setColor(Color.WHITE);
		paintText.setTextSize(paintText.getTextSize() * textSizeScale);

		// Build GenreGraph and add nodes
		graph = new GenreGraph();

		// initialize animation queue
		animationQueue = new GraphAnimationQueue();

		// initialize dimensions
		DisplayMetrics metrics = ApplicationController
				.getInstance().getResources().getDisplayMetrics();
		width = metrics.widthPixels;
		height = metrics.heightPixels;

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

		long timeTmp = 0;
		long timeLast = 0;
		long timeChildChild = 0;
		long timeChild = 0;
		long timeRoot = 0;
		long timeSibling = 0;
		long timeParent = 0;
		long timeAnimation = 0;

		canvas.drawColor(Color.DKGRAY);

		// draw and update fps
		if(fpsTimer > FPS_UPDATE_DURATION){
			fps = (int) Math.round(1000.0 / (time - lastTime));
			fpsTimer = 0;
		}
		fpsTimer += (time - lastTime);
		canvas.drawText("FPS: "+fps , 10, 200, paintFps);

		// update the animations
		animationQueue.update(time);

		// get current node and save for easier access
		GenreNode root = graph.getCurrentRoot();
		
		boolean hasParent = (root.getParent() != null);

		/*
		 * slowly move the translation back to original
		 * if the graph is not touched but the graph was moved
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

		root.draw(canvas, (int)width, (int)height, graph.translation);

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

		if(hasParent){
			root.getParent().draw(canvas, (int)width, (int)height, graph.translation);
		}

		Log.d(TAG, "cchild: "+timeChildChild+" child: "+timeChild+" siblings: "+timeSibling+" animation: "+timeAnimation);

		lastTime = time;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if(touchLocked) return false;

		//		onThreadResume();

		float eventX = event.getX();
		float eventY = event.getY();

		switch(event.getAction()){

		case MotionEvent.ACTION_DOWN:
			onTouch = true;

			lastTouchX = eventX;
			lastTouchY = eventY;

			startClickTime = Calendar.getInstance().getTimeInMillis();
			return true;

		case MotionEvent.ACTION_MOVE:
			Log.d(TAG, "Node moved!");
			/*
			 *  determine the relative move direction 
			 *  and move nodes accordingly in y direction only
			 *  so it does not move with child nodes
			 */
			if((eventY-lastTouchY) >= 0){
				graph.translation += (eventY-lastTouchY) * 
						(1 - (graph.translation / (graph.TRANSLATION_MAX * 1.5f)));
			} else if((eventY-lastTouchY) <= 0 && graph.translation > 0){
				graph.translation += (eventY-lastTouchY) * 
						(1 - (graph.translation / (graph.TRANSLATION_MAX * 1.5f)));
			}

			lastTouchX = eventX;
			lastTouchY = eventY;

			return true;

		case MotionEvent.ACTION_UP:
			onTouch = false;

			// we measure the click time and decide if its a click or not
			long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
			if(clickDuration < MAX_CLICK_DURATION) {
				Log.d(TAG, "Node click event!");

				final GenreNode node = graph.testForTouch(eventX, eventY);

				// touch event on child nodes
				if(node != null && !(graph.getCurrentRoot().getName())
						.equalsIgnoreCase(node.getName()) ){

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
							graph.setAsRoot(node.getName());
							touchLocked = false;
						}
					}, animationQueue.getLongestQueue()+20);
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
								graph.setAsRoot(node.getParent().getName());
							}
							touchLocked = false;
						}
					}, animationQueue.getLongestQueue()+20);
				}
			}

			// else it was a move gesture
			else {
				if(graph.translation > graph.TRANSLATION_MAX * 0.8f){
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
								graph.setAsRoot(node.getParent().getName());
							}
							touchLocked = false;
						}
					}, animationQueue.getLongestQueue()+20);
				}
			}

			return true;

		default:

			//			onThreadPause();
			return false;
		}

		//		invalidate();
		//		return true;
	};

	private boolean graphUpAnimation(GenreNode node){
		final GenreNode parent = node.getParent();

		// if there is no parent, we are already at the root node
		if(parent == null){
			animationQueue.add(new TouchAnimation(node, 100));
			touchLocked = true;
		} else {

			// first move parent to current location of root
			animationQueue.add(new MoveAnimation(parent, 300, node.x, node.y), "parent");

			/*
			 * Determine the positions of all new children. Move the current root
			 * to his new position and fade in new children
			 * 
			 * look at GenreGraph for source of that formula
			 */
			int size = parent.getChildren().size();
			for (int i = 0; i < size ; i++) {
				GenreNode sibling = parent.getChildren().get(i);

				// save the new position of our current root and move it there
				if(node.name.equalsIgnoreCase(sibling.name)){
					float x = ((width * i) / size) + ((width * 0.5f) / size);
					float y = height * GenreGraph.CHILD_Y_FACTOR;

					animationQueue.add(new MoveAnimation(node, 300, x, y), "root");
					animationQueue.add(new TouchAnimation(node, 300), "roottouch");
					continue;
				}

				sibling.x = node.x;
				sibling.y = node.y;
				sibling.radius = width * GenreGraph.RADIUS_FACTOR;
				sibling.setVisible(true);
				sibling.setVisibility(1);
				float x_new = ((width * i) / size) + ((width * 0.5f) / size);
				float y_new = height * GenreGraph.CHILD_Y_FACTOR;

				animationQueue.add(new ShrinkAnimation(sibling, 300, true), "sibling"+i);
				animationQueue.add(new MoveAnimation(sibling, 300, x_new, y_new), "siblingm"+i);
			}

			// shrink out current children
			for(int i=0; i<node.getChildren().size(); i++) {
				GenreNode child = node.getChildren().get(i);

				// Add shrink and move down animations with increasing delay that run parallel;
				if(!node.name.equalsIgnoreCase(child.name)){
					animationQueue.add(new ShrinkAnimation(child, 300, i*50), "currchild"+i);
					animationQueue.add(new MoveAnimation(child, 300, child.x, 
							height * GenreGraph.CHILD_Y_FACTOR * 2, i*50), "currChildm"+i);
				}
			}
		}
		return true;
	}

	private boolean graphUpSwipeAnimation(GenreNode node){
		final GenreNode parent = node.getParent();

		// if there is no parent, we are already at the root node
		if(parent == null){
			animationQueue.add(new TouchAnimation(node, 100));
			touchLocked = true;
		} else {

			// first move parent to current location of root
			animationQueue.add(new MoveAnimation(parent, 300, node.x, node.y), "parent");

			/*
			 * Determine the positions of all new children. Move the current root
			 * to his new position and fade in new children
			 * 
			 * look at GenreGraph for source of that formula
			 */
			int size = parent.getChildren().size();
			for (int i = 0; i < size ; i++) {
				GenreNode sibling = parent.getChildren().get(i);

				// save the new position of our current root and move it there
				if(node.name.equalsIgnoreCase(sibling.name)){
					float x = ((width * i) / size) + ((width * 0.5f) / size);
					float y = height * GenreGraph.CHILD_Y_FACTOR;

					animationQueue.add(new MoveAnimation(node, 300, x, y), "root");
					animationQueue.add(new TouchAnimation(node, 300), "roottouch");
					continue;
				}

				sibling.x = node.x;
				sibling.y = node.y;
				sibling.radius = width * GenreGraph.RADIUS_FACTOR;
				sibling.setVisible(true);
				sibling.setVisibility(1);
				float x_new = ((width * i) / size) + ((width * 0.5f) / size);
				float y_new = height * GenreGraph.CHILD_Y_FACTOR;

				animationQueue.add(new ShrinkAnimation(sibling, 300, true), "sibling"+i);
				animationQueue.add(new MoveAnimation(sibling, 300, x_new, y_new), "siblingm"+i);
			}

			// shrink out current children
			for(int i=0; i<node.getChildren().size(); i++) {
				GenreNode child = node.getChildren().get(i);

				// Add shrink and move down animations with increasing delay that run parallel;
				if(!node.name.equalsIgnoreCase(child.name)){
					animationQueue.add(new ShrinkAnimation(child, 300, i*50), "currchild"+i);
					animationQueue.add(new MoveAnimation(child, 300, child.x, 
							height * GenreGraph.CHILD_Y_FACTOR * 2, i*50), "currChildm"+i);
				}
			}
		}
		return true;
	}

	private boolean graphDownAnimation(GenreNode node){
		// shrink other siblings and lat them fall down
		for(int i=0; i<node.getParent().getChildren().size(); i++) {
			GenreNode sibling = node.getParent().getChildren().get(i);

			// Add shrink animations with increasing delay that run parallel;
			if(!node.getName().equalsIgnoreCase(sibling.getName())){
				animationQueue.add(new ShrinkAnimation(sibling, 300, i*50), "sibling"+i);
				animationQueue.add(new MoveAnimation(sibling, 300, sibling.x, 
						height * GenreGraph.CHILD_Y_FACTOR * 2, i*50), "siblingm"+i);
			}
		}

		// move touched node to root position
		animationQueue.add(new MoveAnimation(
				node, 300, node.getParent().x, node.getParent().y), "root");
		animationQueue.add(new TouchAnimation(node, 300), "roottouch");

		// move root further up
		animationQueue.add(new MoveAnimation(
				node.getParent(), 300, node.getParent().x, 0), "rootparent");

		// fade in new children
		int size = node.getChildren().size();
		for (int i = 0; i < size; i++) {
			GenreNode child = node.getChildren().get(i);

			child.x = ((width * i) / size) + ((width * 0.5f) / size);
			child.y = height * GenreGraph.CHILD_Y_FACTOR;
			child.x = node.x;
			child.y = node.y;
			child.radius = width * GenreGraph.RADIUS_FACTOR;
			child.setVisible(true);
			float x_new = ((width * i) / size) + ((width * 0.5f) / size);
			float y_new = height * GenreGraph.CHILD_Y_FACTOR;

			animationQueue.add(new ShrinkAnimation(child, 300, true), "newchild"+i);
			animationQueue.add(new MoveAnimation(child, 300, x_new, y_new), "newchildm"+i);

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
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * Stop the drawing thread
	 */
	public void onThreadPause(){
		boolean retry = true;
		running = false;
		while(retry){
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		while(running){
			if(!surfaceHolder.getSurface().isValid()){
				continue;
			} else {
				Canvas canvas = surfaceHolder.lockCanvas();
				onDraw(canvas);
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
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
						graph.setAsRoot(node.getParent().getName());
					}
					touchLocked = false;
				}
			}, animationQueue.getLongestQueue()+20);
		}
	}
}
