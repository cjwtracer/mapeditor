package tool.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class RotationChange extends GradualChange{
	private static final long serialVersionUID = -6864589040268667734L;
	
	int startState;
	int endState;
	public int incre;
	
	RotationChange(ChangeSequence sequence){
		this.sequence = sequence;
		startState = sequence.animation.rotation;
		type = CHANGE_ROTATION;
	}

	protected void updateState() {
		if(!isRunning())
			return;
		int incre = 0;
		if(endState < startState){
			incre = -this.incre;
		}else if(endState > startState){
			incre = this.incre;
		}
		if(incre == 0)
			return;
		int trans = incre + sequence.animation.rotation;
		if(incre > 0 && trans > endState || incre < 0 && trans < endState){
			if(isReverse()){
				inReverse = true;
			}
			sequence.animation.rotation = endState;
			return;
		}
		sequence.animation.rotation = trans;
	}

	protected void updateReverse() {
		if(!isRunning())
			return;
		int incre = 0;
		if(endState < startState){
			incre = this.incre;
		}else if(endState > startState){
			incre = -this.incre;
		}
		if(incre == 0)
			return;
		int trans = incre + sequence.animation.rotation;
		if(incre > 0 && trans > startState || incre < 0 && trans < startState){
			sequence.animation.rotation = startState;
			return;
		}
		sequence.animation.rotation = trans;
	}

	public void setGap(int gap) {
		if(gap <= 0 || gap >100){
			gap = GAP_DEFAULT;
			System.err.println("illegal time gap");
		}
		this.gap = gap;
		incre = (int)((endState - startState) * gap / (float)100);
		if(incre < 0)
			incre = -incre;
		if(incre == 0)
			incre = 1;
	}

	@Override
	protected boolean reachLast() {
		int v = sequence.animation.rotation;
		if(inReverse){
			return startState < endState ? v <= startState : v >= startState;
		}else{
			return startState < endState ? v >= endState : v <= endState;
		}
	}

	public int getStartState() {
		return startState;
	}

	public void setStartState(int startState) {
		this.startState = startState;
		setGap(gap);
	}

	public int getEndState() {
		return endState;
	}

	public void setEndState(int endState) {
		this.endState = endState;
		setGap(gap);
	}
	
	protected void resetState(){
		super.resetState();
		sequence.animation.setRotation(startState);
	}

	@Override
	protected void construct(Queue queue) {}

}