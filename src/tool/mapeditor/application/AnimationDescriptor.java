package tool.mapeditor.application;

import org.newdawn.slick.Graphics;

import tool.model.Animation;
import tool.model.FrameChange;
import tool.model.GradualChange;

public class AnimationDescriptor {
	
	Animation animation;
	LocusDescriptor locus;
	FramesDescriptor frames;
	UnitDescriptor unit;
	
	public AnimationDescriptor(Animation animation, UnitDescriptor unit){
		if(animation == null)
			throw new NullPointerException();
		this.animation = animation;
		locus = new LocusDescriptor(animation.getLocus(), animation);
		for(GradualChange c : animation.getChanges()){
			if(c.type == GradualChange.CHANGE_FRAME){
				frames = new FramesDescriptor((FrameChange)c, unit);
				break;
			}
		}
		this.unit = unit;
	}

	public void paint(Graphics graphics, int offx, int offy, int width, int height, int trans) {
		locus.paint(graphics, offx, offy, -1, -1, -1);
//		if(frames != null)
//			frames.paint(graphics, offx + animation.getX(), offy + animation.getY());
	}

	public int getAlpha() {
		return animation.getAlpha();
	}

	public void setAlpha(int v) {
		animation.setAlpha(v);
	}

	public int getRotation() {
		return animation.getRotation();
	}

	public void setRotation(int v) {
		animation.setRotation(v);
	}

	public float getScale() {
		return animation.getScale();
	}

	public void setScale(float s) {
		animation.setScale(s);
	}
	
	public void reset(){
		animation.reset();
	}

	public float getAlphaF() {
		return animation.getAlphaF();
	}

	public Animation getUnderlying() {
		return animation;
	}

	public FramesDescriptor newFrames(FrameChange change) {
		frames = new FramesDescriptor(change, unit);
		return frames;
	}

	public FramesDescriptor getFrames() {
		return frames;
	}

	public void setFrames(FramesDescriptor frames) {
		this.frames = null;
	}

}
