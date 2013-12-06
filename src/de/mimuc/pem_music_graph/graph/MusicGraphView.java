package de.mimuc.pem_music_graph.graph;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MusicGraphView extends SurfaceView implements Runnable {
	
	private static final String TAG = MusicGraphView.class.getName();

	private Context context;
	
	List<MusicNode> musicNodes;
	
	private Thread thread;
	private Canvas mCanvas;
	private SurfaceHolder surfaceHolder;
	private Rect surfaceRect;
	
	volatile boolean running;
	
	public MusicGraphView(Context context){
		super(context);
		this.context = context;
		
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
		
		musicNodes = new LinkedList<MusicNode>();
		musicNodes.add(new MusicNode(
				300, 
				300, 
				100, 
				"Bert"));
		
	}
	
	@Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.DKGRAY);
        
        Paint paint = new Paint();
        paint.setColor(context.getResources().getColor(android.R.color.holo_red_light));
        
        for (MusicNode node : musicNodes) {
        	Log.d(TAG, "Position:" + node.x + "" + node.y);
        	
        	canvas.drawCircle(
        			node.x, 
        			node.y, 
        			node.radius, 
        			paint);
		}
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float eventX = event.getX();
		float eventY = event.getY();
		
		MusicNode node = null;
		
		switch(event.getAction()){
		
		case MotionEvent.ACTION_DOWN:
			Log.i(TAG, "Action DOWN: "+eventX+" | "+eventY);
			
			node = checkCollision(eventX, eventY);
			return true;
			
		case MotionEvent.ACTION_MOVE:
			Log.i(TAG, "Action Move: "+eventX+" | "+eventY);
			
			node = checkCollision(eventX, eventY);
			if(node != null){
				node.setPosition(eventX, eventY);
			}
			break;
			
		case MotionEvent.ACTION_UP:
			Log.i(TAG, "Action UP: "+eventX+" | "+eventY);
			break;
			
		default:
			return false;
		}
		
		invalidate();
		return true;
	};
	
	private MusicNode checkCollision(float x, float y){
		for (MusicNode node : musicNodes) {
			if(Math.abs((node.x - x)) <= node.radius &&
					Math.abs((node.y - y)) <= node.radius){
				Log.d(TAG, "Node touched!");
				return node;
			}
		}
		return null;
	}
	
	public void setRunning(boolean running){
		this.running = running;
	}
	
	public void onThreadResume(){
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
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
