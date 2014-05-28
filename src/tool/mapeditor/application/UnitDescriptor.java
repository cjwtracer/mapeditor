package tool.mapeditor.application;

import java.util.List;


import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.Texture;

import tool.mapeditor.Drawable;
import tool.mapeditor.application.MapDescriptor.LayerDescriptor;
import tool.mapeditor.application.ResourceDescriptor.ItemDescriptor;
import tool.model.Animation;
import tool.model.GradualChange;
import tool.model.Unit;
import tool.mapeditor.application.MapDescriptorUtil.OPERS;
import tool.util.GLUtil;

public class UnitDescriptor extends Descriptor implements Drawable {
	static Enum<?>[] list = new Enum<?>[]{
		OPERS.编辑动画效果, OPERS.SEP, OPERS.添加动画序列, OPERS.SEP, 
		OPERS.延拓轨迹,
		OPERS.SEP, OPERS.复制, OPERS.SEP, OPERS.删除, 
		OPERS.SEP, OPERS.元素属性
	};
	static final int EMPTY_SIZE = 20;
	/**
	 * the descriptor of map layer in which the unit lies in
	 */
	LayerDescriptor layer;
	/**
	 * the underlying model
	 */
	Unit unit;
	Texture texture;
	UnitDescriptor copy;
	private Rectangle bounds = new Rectangle(0, 0, 0, 0);
	AnimationDescriptor anim;
	
	public UnitDescriptor(Unit unit, LayerDescriptor layer){
		if(unit == null)
			throw new IllegalArgumentException("Unit can't be null!");
		this.unit = unit;
		this.layer = layer;
		updateImage();
		bounds.x = unit.getX();
		bounds.y = unit.getY();
		int id = unit.getAnimationID();
		Animation a = MainApplication.getInstance().project.getAnimationGroup().get(id);
		if(a != null){
			unit.setAnimation(a);
			anim = new AnimationDescriptor(a, this);
		}
	}
	
	protected void updateImage(){
		ItemDescriptor i = ResourceDescriptor.getResourceItem(unit.getResource());
//		if(i == null){
//			i = ResourceDescriptor.getResourceItem(unit.getResourceID());
//		}
		if(i != null){
			texture = i.texture;
			bounds.width = i.getBounds().width;
			bounds.height = i.getBounds().height;
		}else{
			texture = null;
			bounds.width = bounds.height = EMPTY_SIZE;
		}
	}

	public Rectangle getBounds() {
		bounds.x = unit.getX();
		bounds.y = unit.getY();
		return bounds;
	}

	public void paint(GC gc, int destX, int destY, int destWidth, int destHeight, float trans) {}
	
	public int getRotation(){
		return unit.getTrans();
	}

	public void setLocation(int x, int y) {
		unit.setX(x);
		unit.setY(y);
		bounds.x = x;
		bounds.y = y;
	}

	public void paint(Graphics graphics, int offx, int offy, int width, int height, float trans) {
		int x = offx + unit.getX(), y = offy + unit.getY();
		if(texture == null){
			GL11.glColor3f(0, 0, 0);
			GLUtil.fillRectangle(x, y, EMPTY_SIZE, EMPTY_SIZE);
		}else{
			GLUtil.drawImage(texture, x, y, bounds.width, bounds.height, unit.getTrans(), unit.getScale(), unit.getAlphaF());
		}
		if(anim != null)
			anim.paint(graphics, offx, offy, -1, -1, -1);
	}

	public void setRotation(int v) {
		unit.setRotation(v);
	}

	public void setScale(float v) {
		unit.setScale(v);
	}

	public void setAlpha(int v) {
		unit.setAlpha(v);
	}
	
	public AnimationDescriptor getAnimation(){
		return anim;
	}

	public List<int[]> getVertices() {
		return null;
	}

	public void resetVertex(int i, int x, int y) {
		
	}
	
	public int getAlpha(){
		return unit.getAlpha();
	}
	
	public float getAlphaF(){
		return unit.getAlphaF();
	}

	public float getScale() {
		return unit.getScale();
	}

	public void reset() {
		
	}

	public Enum<?>[] getOperationList() {
		return list;
	}
	
	void extendLocus(){
		checkAnimation();
		unit.extendLocus();
	}

	public boolean operate(Enum<?> i) {
		boolean b = false;
		switch(Enum.valueOf(MapDescriptorUtil.OPERS.class, i.name())){
		case 编辑动画效果:
			mainApp.showChangePropertyDialog(anim);
			b = true;
			break;
		case 添加动画序列:
			addAnimQueue();
			break;
		case 延拓轨迹:
			extendLocus();
			b = true;
			break;
//		case 轨迹属性:
//			mainApp.showLocusPropertyDialog(anim.animation);
//			break;
		case 复制:
			onCopy();
			b = true;
			break;
		case 删除:
			onDelete();
			b = true;
			break;
		case 元素属性:
			mainApp.showUnitPropertyDialog(unit);
			break;
		}
		return b;
	}

	protected void addAnimQueue() {
		checkAnimation();
		Animation anim = this.anim.animation;
		anim.newQueue();
		mainApp.showChangePropertyDialog(this.anim);
	}

	private void checkAnimation() {
		if(anim == null){
			Animation a = unit.createAnimation();
			a.setId(mainApp.project.getAvailableAnimationID());
			mainApp.project.getAnimationGroup().put(a.getId(), a);
			unit.setAnimation(a);
			anim = new AnimationDescriptor(a, this);
		}
	}

	public boolean getEditabilities(Enum<?> i) {
		switch(Enum.valueOf(MapDescriptorUtil.OPERS.class, i.name())){
		case 编辑动画效果:
			return unit.getAnimation() != null && unit.getAnimation().getChanges().size() > 0;
		}
		return true;
	}

	public void onDragOver() {
		
	}
	
	protected void onCopy(){
		copy = new UnitDescriptor(unit.getCopy(null), null);
		copy.texture = texture;
	}
	
	public void onDelete() {
		unit.onDelete();
		if(layer != null)
			layer.units.remove(this);
	}

	public Drawable getModelCopy() {
		return copy;
	}

	@Override
	public void releaseCopy() {
		copy = null;
	}

	@Override
	public String getName() {
		return null;
	}

	protected void updateAnimation() {
		if(anim == null)
			return;
		FramesDescriptor fs = anim.frames;
		if(fs != null){
			int i = fs.change.getCurrentIndex();
			if(i < fs.change.getCount()){
				FrameDescriptor f = fs.frames.get(i);
				if(f.resource != null)
					texture = f.resource.texture;
			}
		}
	}

}
