package tool.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class ScaleChange extends GradualChange{
	private static final long serialVersionUID = 5724257762057768241L;
	
	float startState;
	float endState = 1;
	public float increOver1 = 1;
	public float increBelow1 = 1;
	
	ScaleChange(ChangeSequence sequence){
		this.sequence = sequence;
		startState = sequence.animation.scale;
		type = CHANGE_SCALE;
	}

	protected void updateState() {
		if(!isRunning())
			return;
		int incre = 0;
		if(startState < endState){
			incre = 1;
		}else if(startState > endState){
			incre = -1;
		}
		if(incre == 0)
			return;
		float scale = sequence.animation.scale;
		if(incre > 0){
			if(sequence.animation.scale >= 1){
				scale += increOver1;
			}else{
				scale += increBelow1;
			}
		}else if(incre < 0){
			if(sequence.animation.scale <= 1){
				scale -= increBelow1;
			}else{
				scale -= increOver1;
			}
		}
		if(incre > 0 && scale > endState || incre < 0 && scale < endState){
			if(isReverse()){
				inReverse = true;
			}
			sequence.animation.scale = endState;
			return;
		}
		sequence.animation.scale = scale;
	}

	protected void updateReverse() {
		if(!isRunning())
			return;
		int incre = 0;
		if(startState < endState){
			incre = -1;
		}else if(startState > endState){
			incre = 1;
		}
		if(incre == 0)
			return;
		float scale = sequence.animation.scale;
		if(incre > 0){
			if(sequence.animation.scale >= 1){
				scale += increOver1;
			}else{
				scale += increBelow1;
			}
		}else if(incre < 0){
			if(sequence.animation.scale <= 1){
				scale -= increBelow1;
			}else{
				scale -= increOver1;
			}
		}
		if(incre > 0 && scale > startState || incre < 0 && scale < startState){
			sequence.animation.scale = startState;
			return;
		}
		sequence.animation.scale = scale;
	}

	public void setGap(int gap) {
		if(gap <= 0 || gap >100){
			gap = GAP_DEFAULT;
			System.err.println("illegal time gap");
		}
		this.gap = gap;
		float r1 = 1 - startState;
		float r2 = endState - 1;
		float tmp1 = r1;
		float tmp2 = r2;
		if(r1 < 0) tmp1 = -tmp1;
		if(r2 < 0) tmp2 = -tmp2;
		if(r1 > 0 && r2 > 0){
			increBelow1 = tmp1 * gap / 50;
			increOver1 = tmp2 * gap / 50;
		}else if(r1 < 0 && r2 < 0){
			increBelow1 = tmp2 * gap / 50;
			increOver1 = tmp1 * gap / 50;
		}else{
			increBelow1 = increOver1 = (tmp1 + tmp2) * gap/ 100;
		}
	}

	@Override
	protected boolean reachLast() {
		float v = sequence.animation.scale;
		if(inReverse){
			return startState < endState ? v <= startState : v >= startState;
		}else{
			return startState < endState ? v >= endState : v <= endState;
		}
	}

	public float getStartState() {
		return startState;
	}

	public void setStartState(float startState) {
		this.startState = startState;
		setGap(gap);
	}

	public float getEndState() {
		return endState;
	}

	public void setEndState(float endState) {
		this.endState = endState;
		setGap(gap);
	}
	
	protected void resetState(){
		super.resetState();
		sequence.animation.setScale(startState);
	}

	@Override
	protected void construct(Queue queue) {}
}