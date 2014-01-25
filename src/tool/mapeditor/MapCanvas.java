package tool.mapeditor;

import java.util.List;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.lwjgl.opengl.GL11;

import tool.util.GLUtil;


public class MapCanvas extends ScrollImageCanvas{
	private static MapCanvas instance;
	MapView container;
	WorldMapPainter mapPainter;
	WorldMapEdit mapEdit;
	Drawable firedUnit;
	Drawable movedUnit;
	Drawable firedRegion;
	int[] firedVertex;
	Point[] gaps;
	int gapX, gapY;
	Rectangle highlight = new Rectangle(0, 0, 0, 0);

	private MapCanvas(Composite parent, GLData data) {
		super(parent, data);
	}
	
	public static MapCanvas getInstance(Composite parent){
		if(parent == null)
			return null;
		if(instance == null){
			GLData data = new GLData();
			data.doubleBuffer = true;
			instance = new MapCanvas(parent, data);
		}
		return instance;
	}
	
	protected void paint(){
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		if(mapPainter == null)
			return;
		mapPainter.paintMap(graphics, origin.x, origin.y);
		mapPainter.paintGrid(origin.x, origin.y);
		if(firedUnit != null){
			GL11.glColor3f(1, 0, 0);
			Rectangle b = firedUnit.getBounds();
			if(b != null){
				GLUtil.drawRectangle(b.x + origin.x, b.y + origin.y, b.width, b.height);
			}
		}
		Drawable d = container.harbor.getCurrentResource();
		if(firedUnit != null && firedUnit.getModelCopy() != null)
			d = firedUnit.getModelCopy();
		if(d != null){
			Rectangle b = d.getBounds();
			mapPainter.paintHovering(graphics, mousePos.x, mousePos.y, b.width, b.height, d);
		}
		if(!highlight.isEmpty()){
			GL11.glColor4f(0, 1, 1, 0.5f);
			GLUtil.fillRectangle(highlight.x + origin.x, highlight.y + origin.y, highlight.width, highlight.height);
		}
	}
	
	protected void onMouseDown(MouseEvent e){
		super.onMouseDown(e);
		highlight.width = 0;
		highlight.height = 0;
		if(mapEdit == null)
			return;
		container.popupMenu.setVisible(false);
		if(e.button == 1){
			if(mapEdit.isMode(WorldMapEdit.MODE_PLACE)){
				Drawable d = container.harbor.getCurrentResource();
				if(firedUnit != null && firedUnit.getModelCopy() != null)
					d = firedUnit.getModelCopy();
				byte tp = mapEdit.getEditType();
				int tw = mapEdit.getTileWidth();
				int th = mapEdit.getTileHeight();
				if(d != null){
					if(validLocation()){
						if(tp == WorldMapEdit.TYPE_TILE){
							mapEdit.placeTile(absPos.y / th, absPos.x / tw, d);
						}else if(tp == WorldMapEdit.TYPE_UNIT){
							mapEdit.placeUnit(absPos.x, absPos.y, d);
						}
					}
				}else{
					firedUnit = mapEdit.select(absPos.x, absPos.y);
				}
			}else if(mapEdit.isMode(WorldMapEdit.MODE_MOVE)){
				Drawable d = mapEdit.select(absPos.x, absPos.y);
				if(d != null){
					movedUnit = firedUnit = d;
					Rectangle b = d.getBounds();
					gapX = absPos.x - b.x;
					gapY = absPos.y - b.y;
				}
			}else if(mapEdit.isMode(WorldMapEdit.MODE_REGION_RECT)){
				firedRegion = mapEdit.createRegion(absPos.x, absPos.y, absPos.x, absPos.y);
			}else if(mapEdit.isMode(WorldMapEdit.MODE_SHAPE_REGION)){
				int[] point = mapEdit.getSelectedVertex(absPos);
				if(point != null){
					firedVertex = point;
				}else{
					firedRegion = mapEdit.selectRegion(absPos.x, absPos.y);
					if(firedRegion != null){
						List<int[]> vs = firedRegion.getVertices();
						gaps = new Point[vs.size()];
						for(int i = 0; i < gaps.length; ++i){
							int[] p = vs.get(i);
							gaps[i] = new Point(absPos.x - p[0], absPos.y - p[1]);
						}
					}
				}
			}
		}else if(e.button == 3){
			if(container.harbor.isPlacing()){
				container.harbor.releaseCurrentResource();
			}else if(mapEdit.isMode(WorldMapEdit.MODE_SHAPE_REGION)){
				firedRegion = mapEdit.selectRegion(absPos.x, absPos.y);
				container.showPopupMenu(firedRegion);
			}else{
				firedUnit = mapEdit.select(absPos.x, absPos.y);
				container.showPopupMenu(firedUnit);
			}
		}
		redraw();
	}
	
	protected void onMouseMove(MouseEvent e){
		super.onMouseMove(e);
		if(mapEdit == null)
			return;
		container.statusLine.setMessage(mapEdit.getStatusLineMessage(e.x - origin.x, e.y - origin.y));
		if(mouseDownL){
			if(mapEdit.isMode(WorldMapEdit.MODE_MOVE)){
				if(movedUnit != null){
					movedUnit.setLocation(absPos.x - gapX, absPos.y - gapY);
				}
			}else if(mapEdit.isMode(WorldMapEdit.MODE_REGION_RECT)){
				int[] p = firedRegion.getVertices().get(0);
				firedRegion.resetVertex(1, absPos.x, p[1]);
				firedRegion.resetVertex(2, absPos.x, absPos.y);
				firedRegion.resetVertex(3, p[0], absPos.y);
			}else if(mapEdit.isMode(WorldMapEdit.MODE_SHAPE_REGION)){
				if(firedVertex != null){
					firedVertex[0] = absPos.x;
					firedVertex[1] = absPos.y;
				}else if(firedRegion != null){
					int i = 0;
					int pw = mapPainter.getPixelWidth();
					int ph = mapPainter.getPixelHeight();
					for(int[] p : firedRegion.getVertices()){
						p[0] = absPos.x - gaps[i].x;
						if(p[0] >= pw)
							p[0] = pw - 1;
						if(p[0] < 0)
							p[0] = 0;
						p[1] = absPos.y - gaps[i].y;
						if(p[1] >= ph)
							p[1] = ph - 1;
						if(p[1] < 0)
							p[1] = 0;
						i++;
					}
				}
			}else{
				Drawable d = mapEdit.getOveredDrawable(absPos.x, absPos.y);
				if(d != null)
					d.onDragOver();
			}
		}
		redraw();
	}
	
	protected void onMouseUp(MouseEvent e){
		super.onMouseUp(e);
		if(mapEdit == null)
			return;
		movedUnit = null;
		if(mapEdit.isMode(WorldMapEdit.MODE_MOVE)){
			if(firedUnit != null)
				firedUnit.reset();
		}else if(mapEdit.isMode(WorldMapEdit.MODE_REGION_RECT)){
			if(rectSelect.width < 5 || rectSelect.height < 5){
				mapEdit.deleteRegion(firedRegion);
			}
		}else if(mapEdit.isMode(WorldMapEdit.MODE_VERTEX_ADD)){
			if(firedRegion != null){
				mapEdit.addVertex(absPos, firedRegion);
			}
		}else if(mapEdit.isMode(WorldMapEdit.MODE_VERTEX_DEL)){
			if(firedRegion != null){
				mapEdit.deleteVertex(absPos, firedRegion);
			}
		}else if(mapEdit.isMode(WorldMapEdit.MODE_SHAPE_REGION)){
			gaps = null;
			firedVertex = null;
		}
		redraw();
	}
	
	protected void onMouseDoubleClick(MouseEvent e){
		firedUnit = mapEdit.select(absPos.x, absPos.y);
		if(firedUnit != null){
			firedUnit.onDelete();
			redraw();
		}
	}
	
	/**
	 * Check whether the position of mouse is within the bounds of map.
	 * @return
	 */
	boolean validLocation(){
		return absPos.x >= 0 && absPos.x < mapPainter.getPixelWidth() 
		&& absPos.y >= 0 && absPos.y < mapPainter.getPixelHeight();
	}

	void drawHighLight(int x, int y, int width, int height) {
		highlight.x = x;
		highlight.y = y;
		highlight.width = width;
		highlight.height = height;
	}

}
