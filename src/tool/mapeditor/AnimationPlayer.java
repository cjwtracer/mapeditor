package tool.mapeditor;

import org.eclipse.swt.widgets.Canvas;

import tool.util.Constants;
import tool.util.WidgetUtil;

abstract class AnimationPlayer {
	
	Thread playThread;
	
	AnimationPlayer(final Canvas canvas){
		playThread = new Thread(){
			Runnable update = new Runnable(){
				public void run(){
					doUpdate();
					if(WidgetUtil.valid(canvas))
						canvas.redraw();
				}
			};
			
			public void run(){
				while(true){
					if(!isPlayStatus()){
						synchronized(playThread){
							beforeWait();
							try {
								wait();
							} catch (InterruptedException e) {
							}
						}
					}
					canvas.getDisplay().syncExec(update);
					try{
						Thread.sleep(Constants.TIME_GAP);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		playThread.start();
	}
	
	void notifyPlay(){
		if(isPlayStatus()){
			synchronized(playThread){
				beforeNotify();
				playThread.notify();
			}
		}
	}
	
	protected abstract void beforeWait();

	protected abstract void doUpdate();
	
	protected abstract boolean isPlayStatus();
	
	protected abstract void beforeNotify();

}
