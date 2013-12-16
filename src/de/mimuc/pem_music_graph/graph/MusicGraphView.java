package de.mimuc.pem_music_graph.graph;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import de.mimuc.pem_music_graph.utils.ApplicationController;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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

	private Context context;
	
	/**
	 * The genre graph contains all genre nodes
	 */
	GenreGraph graph;
	
	private Thread thread;
	private SurfaceHolder surfaceHolder;
	
	volatile boolean running;
	
	// Save the last touch position to calculate relative movement on the screen
	float lastTouchX = 0;
	float lastTouchY = 0;
	
	float textSizeScale = 5;
	
	// Save colors here
	Paint paintNode = new Paint();
    Paint paintText = new Paint();
    
    // Screen width and other positions
    int width = 0;
    int height = 0;
    float rootX = 0;
	float rootY = 0;
	float childX = 0;
	float childY = 0;
    
    // Variables for touch event to distingish a click from a move
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
	
	public MusicGraphView(Context context){
		super(context);
		this.context = context;
		
		paintNode.setColor(context.getResources().getColor(android.R.color.holo_red_light));
		paintText.setColor(context.getResources().getColor(android.R.color.white));
		paintText.setTextSize(paintText.getTextSize() * textSizeScale);
		
		// Build GenreGraph and add nodes
		graph = new GenreGraph();
		
		// initialize dimensions
		DisplayMetrics metrics = ApplicationController
				.getInstance().getResources().getDisplayMetrics();
		width = metrics.widthPixels;
		height = metrics.heightPixels;

		// TODO make dimensions resolution dependent and synced with the node position itself
		rootX = width / 2.0f;
		rootY = 200;
		childY = 300 * 2;
		
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
			
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				
			}
		});
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
        /**
         * TODO
         * Idea we need to 
         */
		
		canvas.drawColor(Color.DKGRAY);
        
        // get current node and save for easier access
        GenreNode root = graph.getCurrentRoot();
        
        /*
         *  draw the root node
         *  use not the node coordinates but set the coordinates yourself
         */
        canvas.drawCircle(rootX, rootY, root.radius, paintNode);
        canvas.drawText(root.getName(), rootX - (root.radius / 2), rootY, paintText);
        
        // iterate through root children and draw them and a line to root
        for (GenreNode child : root.getChildren()) {
        	
        	canvas.drawLine(child.x, child.y, root.x, root.y, paintNode);
        	canvas.drawCircle(child.x, child.y, child.radius, paintNode);
        	
        	canvas.drawText(child.getName(), (child.x - (child.radius / 2)), child.y, paintText);
		}
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float eventX = event.getX();
		float eventY = event.getY();
		
		GenreNode node = null;
		
		switch(event.getAction()){
		
		case MotionEvent.ACTION_DOWN:
			lastTouchX = eventX;
			lastTouchY = eventY;
			
			startClickTime = Calendar.getInstance().getTimeInMillis();
			return true;
			
		case MotionEvent.ACTION_MOVE:
			node = graph.testForTouch(eventX, eventY);
			if(node != null){
				/*
				 *  determine the relative move direction 
				 *  and move nodes accordingly in y direction only
				 *  TODO only allow pull down motion
				 *  FIXME root node always drawn at same position
				 *  so it does not move with child nodes
				 */
				graph.move(0, eventY-lastTouchY);
				lastTouchX = eventX;
				lastTouchY = eventY;
			}
			break;
			
		case MotionEvent.ACTION_UP:
			// we measure the click time and decide if its a click or not
			long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
            if(clickDuration < MAX_CLICK_DURATION) {
                node = graph.testForTouch(eventX, eventY);
                
                // if we touched a node that is currently not root, set as root
                if(node != null && !(graph.getCurrentRoot().getName())
                		.equalsIgnoreCase(node.getName()) ){
                	
                	graph.setAsRoot(node.getName());
                	Log.d(TAG, "Set node \""+node.getName()+"\" as Root");
                	Log.d(TAG, "New Root "+graph.getCurrentRoot().getName());
                }
                
                // TODO if we touch the root, make its parent root
            }
			break;
			
		default:
			return false;
		}
		
		invalidate();
		return true;
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
