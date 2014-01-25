package tool.model;

import java.util.ArrayList;
import java.util.List;

import tool.util.Constants;

public class FrameChange extends GradualChange {
	private static final long serialVersionUID = -5634204815148318407L;
	
	static final long GAP = 200 / Constants.TIME_GAP;
	
	List<ClipFrame> frames = new ArrayList<ClipFrame>();
	int count;
	transient int index;
	public transient boolean standAlone;
	
	FrameChange(ChangeSequence sequence){
		this.sequence = sequence;
		type = GradualChange.CHANGE_FRAME;
	}

	@Override
	public void updateState() {
		if(!standAlone && !isRunning())
			return;
		if(index < count && index >= 0){
			ClipFrame f = frames.get(index);
			++f.cycle;
			if(f.cycle < f.duration)
				return;
			f.cycle = 0;
		}
		++index;
		if(index > count)
			index = count;
	}

	@Override
	protected void updateReverse() {
		if(!isRunning())
			return;
		int tmp = index - 1;
		if(tmp >= 0 && tmp < count){
			ClipFrame f = frames.get(tmp);
			++f.cycle;
			if(f.cycle < f.duration)
				return;
			f.cycle = 0;
		}
		--index;
		if(index < 0)
			index = 0;
	}

	@Override
	public void resetState() {
		index = 0;
	}

	@Override
	public void setGap(int gGap) {
		// TODO Auto-generated method stub

	}

	public List<ClipFrame> getFrames() {
		return frames;
	}

	@Override
	protected boolean reachLast() {
		return inReverse ? index == 0 : index == count;
	}

	@Override
	protected void construct(Queue queue) {
		// TODO Auto-generated method stub
		
	}
	
	public ClipFrame addFrame(int index){
		ClipFrame f = new ClipFrame();
		if(index < 0)
			frames.add(f);
		else
			frames.add(index, f);
		++count;
		return f;
	}

	public ClipFrame deleteFrame(int index) {
		if(index < 0 || index >= frames.size()){
			System.err.println("index out of bounds");
			return null;
		}
		--count;
		return frames.remove(index);
	}
	
	public int getCurrentIndex(){
		return index;
	}
	
	public int getCount(){
		return count;
	}

}
