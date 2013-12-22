package de.mimuc.pem_music_graph.graph;

import java.util.Calendar;

import de.mimuc.pem_music_graph.graph.animation.FadeAnimation;
import de.mimuc.pem_music_graph.graph.animation.GraphAnimationQueue;
import de.mimuc.pem_music_graph.graph.animation.IdleAnimation;
import de.mimuc.pem_music_graph.graph.animation.MoveAnimation;
import de.mimuc.pem_music_graph.graph.animation.ShrinkAnimation;
import de.mimuc.pem_music_graph.graph.animation.TouchAnimation;
import de.mimuc.pem_music_graph.utils.ApplicationController;

import de.mimuc.pem_music_graph.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
		
		canvas.drawColor(Color.DKGRAY);

		// draw fps
		canvas.drawText("FPS: "+ Math.round(1000.0 / (time - lastTime)), 10, 200, paintFps);
		
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
         * watch the draw order! children -> root -> siblings -> parent
         */
        for (GenreNode child : root.getChildren()) {
			child.draw(canvas, (int)width, (int)height, graph.translation);
		}
        
        root.draw(canvas, (int)width, (int)height, graph.translation);
        
        if(hasParent){
	        for (GenreNode sibling : root.getParent().getChildren()) {
				if(sibling.isVisible()){
					sibling.draw(canvas, (int)width, (int)height, graph.translation);
				}
			}
        }
        
        if(hasParent){
        	root.getParent().draw(canvas, (int)width, (int)height, graph.translation);
        }
        
        lastTime = time;
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(touchLocked) return false;
		
		onThreadResume();
		
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
				graph.translation += (eventY-lastTouchY);
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
                	}, animationQueue.getLongestQueue()+10);
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
                	}, animationQueue.getLongestQueue()+100);
                }
            }
            return true;
			
		default:
			
			onThreadPause();
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
	
	private boolean graphDownAnimation(GenreNode node){
    	// shrink other siblings
    	for(int i=0; i<node.getParent().getChildren().size(); i++) {
			GenreNode sibling = node.getParent().getChildren().get(i);
    		
			// Add shrink animations with increasing delay that run parallel;
    		if(!node.getName().equalsIgnoreCase(sibling.getName())){
				animationQueue.add(new ShrinkAnimation(sibling, 200, i*50), "sib"+i);
			}
		}
    	
    	// move touched node to root position
    	animationQueue.add(new MoveAnimation(
    			node, 300, node.getParent().x, node.getParent().y), "root");
    	animationQueue.add(new TouchAnimation(node, 300), "roottouch");
    	
    	// move root further up
    	animationQueue.add(new MoveAnimation(
    			node.getParent(), 300, node.getParent().x, 0), "rootparent");
    	
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
}
