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
	
	boolean onTouch = false;
	
	float textSizeScale = 5;
	
	// Save colors here
	Paint paintNode = new Paint();
    Paint paintText = new Paint();
    
    // Screen dimensions
    int width = 0;
    int height = 0;
    
    double rootX = 0;
	double rootY = 0;
	double childX = 0;
	double childY = 0;
    
    // Variables for touch event to distingish a click from a move
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
	
	public MusicGraphView(Context context){
		super(context);
		
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
				width = s_width;
				height = s_height;
			}
		});
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.DKGRAY);
		
		// update the animations
		animationQueue.update(System.currentTimeMillis());
		
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
         * watch the draw order! children -> root -> parent
         */
        for (GenreNode child : root.getChildren()) {
			child.draw(canvas, width, height, graph.translation);
		}
        
        root.draw(canvas, width, height, graph.translation);
        
        if(hasParent){
        	root.getParent().draw(canvas, width, height, graph.translation);
        }
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
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
                	
                	/**********************
                	 * Fancy Animation
                	 **********************/
                	
                	// shrink other siblings
                	for(int i=0; i<node.getParent().getChildren().size(); i++) {
						GenreNode sibling = node.getParent().getChildren().get(i);
                		
						// Add shrink animations with increasing delay that run parallel;
                		if(!node.getName().equalsIgnoreCase(sibling.getName())){
							animationQueue.add(new ShrinkAnimation(sibling, 150, i*50), "sib"+i);
						}
					}
                	
                	// move touched node to root position
                	animationQueue.add(new MoveAnimation(
                			node, 310, node.getParent().x, node.getParent().y), "root");
                	
                	// move root further up
                	animationQueue.add(new MoveAnimation(
                			node.getParent(), 310, node.getParent().x, 0), "rootparent");
                	
                	// run other logic when animation has finished playing
                	Handler handler = new Handler();
                	handler.postDelayed(new Runnable()
                	{
                	     @Override
                	     public void run()
                	     {
                	    	 graph.setAsRoot(node.getName());
                	     }
                	}, animationQueue.getLongestQueue()+5);
                }
                
                // touch event on root node
                else if(node != null && node.isRoot()){
                	final GenreNode parent = node.getParent();

                	// run other logic when animation has finished playing
                	Handler handler = new Handler();
                	handler.postDelayed(new Runnable()
                	{
                		@Override
                		public void run()
                		{
                			if(parent != null){
                				graph.setAsRoot(parent.getName());
                			}
                		}
                	}, animationQueue.getQueueLength("parent"));
                }
            }
            return true;
			
		default:
			return false;
		}
		
//		invalidate();
//		return true;
	};
	
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
