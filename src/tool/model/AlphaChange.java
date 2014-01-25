package tool.model;



public class AlphaChange extends GradualChange{
	private static final long serialVersionUID = 1387233766079559966L;
	
	int startState;
	int endState = 255;
	public int incre;
	
	AlphaChange(ChangeSequence sequence){
		this.sequence = sequence;
		type = CHANGE_ALPHA;
		startState = sequence.animation.alpha;
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
		int alpha = incre + sequence.animation.alpha;
		if(incre > 0 && alpha >= endState || incre < 0 && alpha <= endState){
			if(reverse){
				inReverse = true;
			}
			sequence.animation.setAlpha(endState);
			return;
		}
		sequence.animation.setAlpha(alpha);
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
		int alpha = incre + sequence.animation.alpha;
		if(incre > 0 && alpha > startState || incre < 0 && alpha < startState){
			sequence.animation.setAlpha(startState);
			return;
		}
		sequence.animation.setAlpha(alpha);
	}
	
	public void setGap(int gap){
		if(gap <= 0 || gap >100){
			gap = GAP_DEFAULT;
		}
		this.gap = gap;
		incre = (int)((endState - startState) * gap / (float)100);
		if(incre < 0)
			incre = -incre;
		if(incre == 0)
			incre = 1;
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

	@Override
	protected boolean reachLast() {
		int v = sequence.animation.alpha;
		if(inReverse){
			return startState < endState ? v <= startState : v >= startState;
		}else{
			return startState < endState ? v >= endState : v <= endState;
		}
	}
	
	protected void resetState(){
		super.resetState();
		sequence.animation.setAlpha(startState);
	}

	@Override
	protected void construct(Queue queue) {}
}