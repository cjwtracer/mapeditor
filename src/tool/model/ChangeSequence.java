package tool.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Object of the class contains a list of gradual changes performing
 * synchronizingly.
 * 
 * @author caijw
 * 
 */
class ChangeSequence {
	
	List<GradualChange> changes = new ArrayList<GradualChange>();
	Animation animation;
	
	ChangeSequence(Animation animation){
		if(animation == null)
			throw new NullPointerException();
		this.animation = animation;
	}

	/**
	 * Add a gradual change to the list of changes according to the specified
	 * type.
	 * 
	 * @param type
	 * @return
	 */
	GradualChange addChange(byte type) {
		GradualChange change = null;
		switch(type){
		case GradualChange.CHANGE_ALPHA:
			change = new AlphaChange(this);
			break;
		case GradualChange.CHANGE_ROTATION:
			change = new RotationChange(this);
			break;
		case GradualChange.CHANGE_SCALE:
			change = new ScaleChange(this);
			break;
		case GradualChange.CHANGE_POSITION:
			change = new PositionChange(this);
			break;
		case GradualChange.CHANGE_FRAME:
			FrameChange fc = new FrameChange(this);
			fc.addFrame(0);
			change = fc;
			break;
		}
		if(change != null){
			change.sequence = this;
			changes.add(change);
		}
		return change;
	}
	
	public List<GradualChange> getChanges(){
		return changes;
	}

	/**
	 * Update the state of each change within the sequence.
	 */
	public void updateState() {
		for(GradualChange cg : changes){
			if(cg.inReverse){
				cg.updateReverse();
			}else{
				cg.updateState();
			}
		}
	}

	/**
	 * Reset the state of each change within the sequence.
	 */
	public void resetState() {
		for(GradualChange cg : changes){
			cg.resetState();
		}
	}
}
