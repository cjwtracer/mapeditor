package tool.mapeditor.application;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Graphics;

import tool.model.ClipFrame;
import tool.model.FrameChange;
import tool.util.GLUtil;

public class FramesDescriptor {
	
	List<FrameDescriptor> frames = new ArrayList<FrameDescriptor>();
	FrameChange change;
	
	public FramesDescriptor(FrameChange change) {
		this.change = change;
		for(ClipFrame c : change.getFrames()){
			frames.add(new FrameDescriptor(c));
		}
	}

	public List<FrameDescriptor> getFrames(){
		return frames;
	}

	void paint(Graphics graphics, int x, int y) {
		if(change.getCount() == 0)
			return;
		if(change.getCurrentIndex() < change.getCount()){
			FrameDescriptor f = frames.get(change.getCurrentIndex());
			if(f.resource != null){
				GLUtil.drawImage(f.resource.texture, x, y);
			}
		}
	}

}
