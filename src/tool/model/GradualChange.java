package tool.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * A gradual change is a kind of change performs according to the loop executes
 * the animation.
 * 
 * @author caijw
 * 
 */
public abstract class GradualChange implements Serializable{
	private static final long serialVersionUID = 102178702735252879L;
	
	public static final byte CHANGE_ALPHA = 0;
	public static final byte CHANGE_ROTATION = 1;
	public static final byte CHANGE_SCALE = 2;
	public static final byte CHANGE_POSITION = 3;
	public static final byte CHANGE_FRAME = 4;
	
	public static final int GAP_DEFAULT = 1;
	
	static String text(GradualChange instance){
		String txt = "change";
		if(instance != null){
			switch(instance.type){
			case CHANGE_ALPHA:
				txt = "透明度";
				break;
			case CHANGE_ROTATION:
				txt = "旋转";
				break;
			case CHANGE_SCALE:
				txt = "缩放";
				break;
			case CHANGE_POSITION:
				txt = "轨迹";
				break;
			case CHANGE_FRAME:
				txt = "帧变换";
				break;
			}
		}
		return txt;
	}
	
	static GradualChange load(Queue queue, ObjectInputStream ois) throws IOException, ClassNotFoundException{
		GradualChange c = (GradualChange)ois.readObject();
		c.queue = queue;
		c.sequence = queue.anim.sequence;
		c.construct(queue);
		return c;
	}
	
	static void save(GradualChange change, ObjectOutputStream oos) throws IOException{
		oos.writeObject(change);
	}
	
	public byte type;
	public double duration;
	boolean reverse;
	int gap = GAP_DEFAULT;

	/**
	 * if reverse is true, indicating the state that the change is in the
	 * reverse change of itself, it shouldn't be assigned false until the queue
	 * which is repeatable is in the next cycle. It is used for making the
	 * decision of calling the update method or its reverse counterpart when
	 * this change needs updating.
	 */
	transient boolean inReverse;
	transient protected ChangeSequence sequence;
	transient Queue queue;

	public String getText() {
		return text(this);
	}
	
	protected boolean isRunning(){
		int idx = queue.changes.indexOf(this);
		GradualChange former = null;
		if(queue.inReverse){
			if(idx < queue.changes.size() - 1)
				former = queue.changes.get(idx + 1);
		}else{
			if(idx > 0)
				former = queue.changes.get(idx - 1);
		}
		if(former != null){
			if(!former.reachLast())
				return false;
			else
				if(reachLast())
					return false;
		}else{
			if(reachLast())
				return false;
		}
		return true;
	}
	
	public boolean isReverse(){
		return reverse;
	}
	
	public void setReverse(boolean b){
		reverse = b;
	}
	
	/**
	 * Update the state of the change from start state to end state.
	 */
	protected abstract void updateState();

	/**
	 * Reset the state of the change to its start state.
	 */
	protected void resetState(){
		inReverse = false;
	}

	public int getGap() {
		return gap;
	}
	
	/**
	 * Update the state of the change from end state to start state.
	 */
	protected abstract void updateReverse();

	/**
	 * Set the increment of each rounds of updating, call it when the start state, the end
	 * state or the gap is modified.
	 * 
	 * @param timeGap
	 */
	public abstract void setGap(int timeGap);

	protected abstract boolean reachLast();
	
	protected abstract void construct(Queue queue);
	
}
