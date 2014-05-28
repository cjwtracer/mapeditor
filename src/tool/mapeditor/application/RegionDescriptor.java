package tool.mapeditor.application;

import java.util.List;


import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Graphics;

import tool.mapeditor.Drawable;
import tool.mapeditor.application.MapDescriptor.LayerDescriptor;
import tool.mapeditor.application.MapDescriptorUtil.OPERS;
import tool.model.RegionPolygon;
import tool.util.ByteUtil;
import tool.util.Constants;
import tool.util.GLUtil;
import tool.util.ViewUtil;

/**
 * Descriptor of model polygon region.
 * @author caijw
 *
 */
public class RegionDescriptor extends Descriptor implements Drawable {
	final static Enum<?>[] list = new Enum<?>[]{
		OPERS.碰撞, OPERS.遮挡, OPERS.传送, OPERS.安全区, 
		OPERS.SEP, OPERS.删除, OPERS.SEP, OPERS.属性};
	
	LayerDescriptor layer;
	RegionPolygon region;
	boolean focused;
	float alphaF = 1;
	int[] mapping;
	int tileWidth, tileHeight;
	
	RegionDescriptor(RegionPolygon region, LayerDescriptor layer){
		if(region == null || region.getVertices().isEmpty())
			throw new IllegalArgumentException("Regoin can't be empty!");
		this.layer = layer;
		this.region = region;
	}

	public Rectangle getBounds() {
		return region.getBounds();
	}

	public void paint(GC gc, int destX, int destY, int destWidth, int destHeight, float trans) {

	}

	public void paint(Graphics graphics, int x, int y, int width, int height, float trans) {
	}
	
	void paint(int offx, int offy){
		float[] color = regionColor(region.getType());
		GL11.glColor4f(color[0], color[1], color[2], alphaF);
		GL11.glTranslatef(offx, offy, 0);
		if(mapping != null){
			int w = tileWidth / 2;
			int h = tileHeight / 2;
			int wit = region.getMappingTileRight() - region.getMappingTileLeft() + 1;
			for(int i = 0; i < mapping.length; ++i){
				int col = i % wit;
				int row = i / wit;
				int x = (region.getMappingTileLeft() + col) * tileWidth;
				int y = (region.getMappingTileTop() + row) * tileHeight;
				for(int k = 0; k < 4; ++k){
					int x1 = x + ((k == 0 || k == 3) ? 0 : w);
					int y1 = y + ((k == 0 || k == 1) ? 0 : h);
					if((mapping[i] & (1 << k)) != 0){
						GLUtil.fillRectangle(x1, y1, w, h);
					}
				}
			}
		}else{
			GLUtil.fillPolygon(region.getVertices());
			GL11.glColor4f(0, 1, 0, 1);
			for(int[] p : region.getVertices()){
				GLUtil.drawRectangle(p[0] - 2, p[1] - 2, 4, 4);
			}
		}
		GL11.glTranslatef(-offx, -offy, 0);
	}
	
	/**
	 * Get the color cooresponding to different types of region. 
	 * @param regionType
	 * @return
	 */
	float[] regionColor(int regionType){
		float[] clr = GLUtil.RED;
		if(region.isType(RegionPolygon.TYPE_COLLISION)){
			clr =  GLUtil.RED;
		}else if(region.isType(RegionPolygon.TYPE_SHUTTER)){
			clr =  GLUtil.BLUE;
		}else if(region.isType(RegionPolygon.TYPE_TELEPORT)){
			clr = GLUtil.PURPLE;
		}else if(region.isType(RegionPolygon.TYPE_LAND)){
			clr = GLUtil.YELLOW;
		}else if(region.isType(RegionPolygon.TYPE_SAFE)){
			clr = GLUtil.GREEN;
		}
		if (focused)
			alphaF = 0.8f;
		else
			alphaF = 0.4f;
		return clr;
	}

	public void setLocation(int x, int y) {

	}

	public void setRotation(int v) {

	}

	public void setScale(float v) {

	}

	public void setAlpha(int v) {

	}
	
	/**
	 * Return wheather the region contains the given point.
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean contains(int x, int y){
		boolean contain = false;
		Region area = new Region();
		int[] points = getPointArray(region.getVertices());
		area.add(points);
		contain = area.contains(x, y);
		area.dispose();
		return contain;
	}
	
	static int[] getPointArray(List<int[]> vertices){
		int[] points = new int[vertices.size() * 2];
		int i = 0;
		for(int[] p : vertices){
			points[i++] = p[0];
			points[i++] = p[1];
		}
		return points;
	}

	public List<int[]> getVertices() {
		return region.getVertices();
	}

	/**
	 * Select the vertex according to the given position.
	 * @param point
	 * @return
	 */
	public int[] getSelectedVertex(Point point) {
		for(int[] p : region.getVertices()){
			if(Math.abs(p[0] - point.x) < 4 && Math.abs(p[1] - point.y) < 4){
				return p;
			}
		}
		return null;
	}

	/**
	 * Reset the vertex of index i to the given position.
	 */
	public void resetVertex(int i, int x, int y) {
		int[] v = region.getVertices().get(i);
		v[0] = x;
		v[1] = y;
	}

	public void addPoint(Point p) {
		region.sortPointArray(new int[]{p.x, p.y});
		
	}

	/**
	 * Delete the vertex around the given position.
	 * @param p
	 * @return
	 */
	public boolean deletePoint(Point p) {
		return region.deletePoint(p.x, p.y);
	}

	public void setType(int type) {
		region.setType(type);
	}

	/**
	 * Map the the polygon region onto the ground tiles of the same layer if the
	 * flag is true, while false cancel the mapping.
	 * 
	 * @param isMapping
	 * @param tw
	 * @param th
	 */
	void mappingRegion(boolean isMapping, int tw, int th) {
		if(!isMapping){
			mapping = null;
			return;
		}
		tileWidth = tw;
		tileHeight = th;
		Region area = new Region();
		int[] points = getPointArray(region.getVertices());
		area.add(points);
		Rectangle b = area.getBounds();
		int tileJ = b.x / tw;
		int tileJ1 = (b.x + b.width) / tw;
		int tileI = b.y / th;
		int tileI1 = (b.y + b.height) / th;
		int baseArea = Math.min(ViewUtil.computeIntersectArea(area, b), tw * th / 4);
		mapping = region.generateMapping(tileJ, tileJ1, tileI, tileI1);
		int w = tileJ1 - tileJ + 1;
		for(int j = tileJ; j <= tileJ1; ++j){
			for(int i = tileI; i <= tileI1; ++i){
				Rectangle[] rects = ViewUtil.splitRectangle(j * tw, i * th, tw, th);
				int val = 0;
				for(int k = 0; k < 4; ++k){
					if(area.intersects(rects[k])){
						int interArea = ViewUtil.computeIntersectArea(area, rects[k]);
						if(interArea > baseArea * Constants.INTERSECT_FACTOR){
							val |= ByteUtil.r2(k);
						}
					}
				}
				mapping[(i - tileI) * w + (j - tileJ)] = val;
			}
		}
		area.dispose();
	}

	public void reset() {
		
	}

	public Enum<?>[] getOperationList() {
		return list;
	}

	public boolean operate(Enum<?> i) {
		boolean b = false;
		switch(Enum.valueOf(MapDescriptorUtil.OPERS.class, i.name())){
		case 碰撞:
			region.setType(RegionPolygon.TYPE_COLLISION);
			b = true;
			break;
		case 遮挡:
			region.setType(RegionPolygon.TYPE_SHUTTER);
			b = true;
			break;
		case 传送:
			region.setType(RegionPolygon.TYPE_TELEPORT);
			b = true;
			break;
		case 安全区:
			region.setType(RegionPolygon.TYPE_SAFE);
			b = true;
			break;
		case 删除:
			onDelete();
			break;
		case 属性:
			mainApp.showPropertyEditDialog(null, region, "区域属性");
			break;
		}
		return b;
	}

	public boolean getEditabilities(Enum<?> i) {
		return true;
	}

	public void onDragOver() {
		
	}

	public void onDelete() {
		layer.layer.getRegions().remove(region);
		layer.regions.remove(this);
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
