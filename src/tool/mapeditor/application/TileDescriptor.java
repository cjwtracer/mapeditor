package tool.mapeditor.application;

import java.util.List;


import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.Texture;

import tool.mapeditor.Drawable;
import tool.mapeditor.application.MapDescriptorUtil.OPERS;
import tool.mapeditor.application.ResourceDescriptor.ItemDescriptor;
import tool.model.RegionPolygon;
import tool.model.Tile;
import tool.util.GLUtil;

/**
 * The descriptor of a tile model.
 * @author caijw
 *
 */
public class TileDescriptor extends Descriptor implements Drawable {
	static Enum<?>[] list = new Enum<?>[]{OPERS.碰撞, OPERS.遮挡, OPERS.降落点, OPERS.清空};
	
	/**
	 * the underlying model
	 */
	Tile tile;
	Texture texture;
	Rectangle bounds;
	
	public TileDescriptor(Tile tile){
		if(tile == null)
			throw new IllegalArgumentException("Tile can't be null!");
		this.tile = tile;
		bounds = new Rectangle(tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight());
		updateImage();
	}
	
	protected void updateImage(){
		if(tile.getResource() == null){
			return;
		}
		ItemDescriptor item = ResourceDescriptor.getResourceItem(tile.getResource());
		if(item != null){
			texture = item.texture;
		}else{
			texture = null;
		}
	}

	/**
	 * Get bounds of a tile.
	 */
	public Rectangle getBounds() {
		return bounds;
	}
	
	public void paint(GC gc, int destX, int destY, int destWidth, int destHeight, float trans) {
		
	}
	
	float getTrans(){
		return tile.getTrans();
	}

	public void setLocation(int i, int j) {
		
	}

	public void paint(Graphics graphics, int x, int y, int width, int height, float trans) {
		if(texture != null){
			GLUtil.drawImage(texture, x, y, width, height, tile.getTrans(), tile.getScale(), tile.getAlphaF());
		}
		if(MapDescriptor.isRehearsal)
			return;
		float[] c = getColor();
		if(c != null){
			GL11.glColor4f(c[0], c[1], c[2], 0.4f);
			GLUtil.fillRectangle(x, y, width, height);
		}
	}
	
	/**
	 * Get the color to fill according to the type of the tile.
	 * @return
	 */
	float[] getColor(){
		float[] c = null;
		switch(tile.getType()){
		case RegionPolygon.TYPE_COLLISION:
			c = GLUtil.RED;
			break;
		case RegionPolygon.TYPE_SHUTTER:
			c = GLUtil.BLUE;
			break;
		case RegionPolygon.TYPE_TELEPORT:
			c = GLUtil.PURPLE;
			break;
		case RegionPolygon.TYPE_LAND:
			c = GLUtil.YELLOW;
			break;
		}
		return c;
	}

	public void setRotation(int v) {
		tile.setTrans(v);
	}

	public void setScale(float v) {
		tile.setScale(v);
	}

	public void setAlpha(int v) {
		tile.setAlpha(v);
	}

	public List<int[]> getVertices() {
		return null;
	}

	public void resetVertex(int i, int x, int y) {
		
	}

	public void reset() {
		
	}

	public Enum<?>[] getOperationList() {
		return list;
	}

	public boolean operate(Enum<?> i) {
		boolean b = false;
		int type = -1;
		switch(Enum.valueOf(MapDescriptorUtil.OPERS.class, i.name())){
		case 碰撞:
			type = Tile.currentType = RegionPolygon.TYPE_COLLISION;
			b = true;
			break;
		case 遮挡: 
			type = Tile.currentType = RegionPolygon.TYPE_SHUTTER;
			b = true;
			break;
		case 降落点:
			type = Tile.currentType = RegionPolygon.TYPE_LAND;
			b = true;
			break;
		case 清空:
			type = Tile.currentType = RegionPolygon.TYPE_NONE;
			b = true;
			break;
		}
		tile.setType(type);
		return b;
	}

	public boolean getEditabilities(Enum<?> i) {
		return true;
	}

	public void onDragOver() {
		if(tile.getType() == RegionPolygon.TYPE_LAND)
			tile.excludeType(RegionPolygon.TYPE_LAND);
		tile.setType(Tile.currentType);
	}

	public void onDelete() {
		
	}

	@Override
	public Drawable getModelCopy() {
		return null;
	}

	@Override
	public void releaseCopy() {}

	@Override
	public String getName() {
		return null;
	}

}
