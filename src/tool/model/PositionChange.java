package tool.model;

import org.newdawn.slick.geom.Curve;
import org.newdawn.slick.geom.Vector2f;

public class PositionChange extends GradualChange{
	private static final long serialVersionUID = -7158476847962894677L;
	
	transient Locus locus;
	int curveIndex = -1;
	float percentage;
	
	PositionChange(ChangeSequence sequence){
		this.sequence = sequence;
		type = GradualChange.CHANGE_POSITION;
		locus = sequence.animation.locus;
	}
	
	public boolean isRepeat(){
		return locus.repeat;
	}
	
	public void setRepeat(boolean b){
		locus.repeat = b;
	}
	
	public boolean isReverse(){
		return locus.reverse;
	}
	
	public void setReverse(boolean b){
		locus.reverse = b;
	}
	
	public boolean isInReverse(){
		return locus.inReverse;
	}
	
	public void setInReverse(boolean b){
		locus.inReverse = b;
	}

	@Override
	protected void updateState() {
		if(curveIndex < 0 || curveIndex >= locus.curves.size() || locus.segmentIncres == null)
			return;
		if(!isRunning())
			return;
		percentage += locus.segmentIncres[curveIndex];
		Vector2f p = locus.getLocation(curveIndex, percentage);
		sequence.animation.x = (int)p.x;
		sequence.animation.y = (int)p.y;
	}

	@Override
	protected void resetState() {
		super.resetState();
		percentage = 0;
		if(curveIndex < 0 || curveIndex >= locus.curves.size())
			return;
		Curve cv = locus.curves.get(curveIndex);
		Vector2f p = cv.getStartPoint();
		sequence.animation.x = (int)p.x;
		sequence.animation.y = (int)p.y;
	}

	@Override
	protected void updateReverse() {
		if(inValidLocus())
			return;
		if(!isRunning())
			return;
		percentage -= locus.segmentIncres[curveIndex];
		Vector2f p = locus.getLocation(curveIndex, percentage);
		sequence.animation.x = (int)p.x;
		sequence.animation.y = (int)p.y;
	}
	
	public Locus getLocus(){
		return locus;
	}
	
	boolean inValidLocus(){
		return curveIndex < 0 || curveIndex >= locus.curves.size() || locus.segmentIncres == null;
	}

	/**
	 * Call the method when the curve is changed too.
	 */
	@Override
	public void setGap(int gap) {
		if(inValidLocus())
			return;
		if(gap <= 0 || gap > 100){
			gap = GAP_DEFAULT;
		}
		this.gap = gap;
		locus.segmentIncres[curveIndex] = gap / (float)100;
	}
	
	public void setCurve(int index){
		if(index >= locus.curves.size()){
			System.err.println("curve of this index does not exist");
			return;
		}
		curveIndex = index;
		Curve c = locus.curves.get(index);
		sequence.animation.x = (int)c.getStartPoint().x;
		sequence.animation.y = (int)c.getStartPoint().y;
		setGap(gap);
	}
	
	public int getCurve(){
		return curveIndex;
	}

	@Override
	protected boolean reachLast() {
		if(queue.inReverse)
			return percentage <= 0;
		return percentage >= 1;
	}

	@Override
	protected void construct(Queue queue) {
		locus = queue.anim.locus;
	}
	
	public int getCurveIndex(){
		return curveIndex;
	}
	
}